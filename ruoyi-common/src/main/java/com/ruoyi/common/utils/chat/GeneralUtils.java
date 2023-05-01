package com.ruoyi.common.utils.chat;

import com.ruoyi.common.cache.LoginCacheHandler;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.chat.CommonContent;
import com.ruoyi.common.core.domain.model.chat.MesEvent;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.redis.RedisLock;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.SpringContextUtil;
import io.netty.channel.Channel;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.function.Supplier;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/23,14:42
 * @return:
 **/
public class GeneralUtils {
   
    /**
     * 更新当前用户登录缓存
     * @return
     */
    public static LoginCacheHandler loginCacheHandler(){
        return loginCacheHandler(true);
    }
    /**
     * 跟新非当前的前台用户的登录缓存;
     */
    public static LoginCacheHandler loginCacheHandler(boolean self){
        if (self){
            return  SpringContextUtil.getBean("selfLoginCacheHandler");
        }
        return  SpringContextUtil.getBean("otherLoginCacheHandler");
    }

    public static String seqToVo(String seqDate,Long seq){
        String s = String.valueOf(seq);
        int seqLength = s.length();
        int seqDateLength = seqDate.length();
        StringBuilder builder = new StringBuilder();
        builder.append(seqDate);
        int paddingLength=19-seqDateLength+seqLength;
        if (paddingLength>0){
            for (int i = 0; i < paddingLength; i++) {
                builder.append("0");
            }
        }
        builder.append(s);
        return builder.toString();
    }
    /**
     * 串行化id生成工具;
     * @param key redisKey
     * @param dataIfAbsent 若果redis 里没有
     * @return
     */
    public static Long getNextMessageSeq(String key, Supplier<Long> dataIfAbsent){
        RedisCache bean = SpringContextUtil.getBean(RedisCache.class);
        Assert.test(bean==null,"system.error");
        Object seq = bean.getCacheObject(key);
        if (seq==null){
            try {
                if (RedisLock.tryLock(key+":Lock",60)){
                    if (bean.getCacheObject(key)==null){
                        return bean.incr(key,dataIfAbsent.get()+1);
                    }else {
                        return bean.incr(key,1);
                    }
                }else {
                    throw new ServiceException("message.send.error");
                }
            }finally {
                RedisLock.unLock();
            }

        }else {
            return bean.incr(key,1);
        }
    }


    public static Long getNextMessageSeqByScript(String key,Supplier<Long> dataIfAbsent){
        RedisCache bean = SpringContextUtil.getBean(RedisCache.class);
        Assert.test(bean==null,"system.error");
        Long nextSeq = bean.existAndIncr(key);
        if (nextSeq==-1L){
            try {
                if (RedisLock.tryLock(key+":Lock",60)){
                    if ((nextSeq=bean.existAndIncr(key))==-1L){
                        return bean.incr(key,dataIfAbsent.get()+1);
                    }else {
                        return nextSeq;
                    }
                }else {
                    throw new ServiceException("message.send.error");
                }
            }finally {
                RedisLock.unLock();
            }
        }else {
            return nextSeq;
        }
    }




    public static Channel getCurrentChannel(){
        return ChatContext.CHANNEL_CONTEXT.get();
    }

    @SuppressWarnings("unchecked")
    public static <T extends LoginUser> T getCurrentLoginUser(){
        LoginUser loginUser;
        if (checkContext()){
            loginUser = ChatContext.LOGIN_USER_CONTEXT.get();
        }else {
            loginUser =  SecurityUtils.getLoginUser();
        }

        return (T)loginUser;
    }


    public static boolean isPlayer(LoginUser loginUser){
        return loginUser instanceof LoginMember&& Constants.USER_TYPE_PLAYER.equals(loginUser.getUserType());
    }


    public static String getLoginUserAttr(String key){
        LoginUser currentLoginUser = getCurrentLoginUser();
        return getLoginUserAttr(currentLoginUser,key);
    }

    public static String getLoginUserAttr(LoginUser loginUser,String key){
        if (loginUser==null){
            return "";
        }
        switch (key){
            case "photo":
                return  loginUser.getPhoto();
            case "nickName":
                return  loginUser.getNickName();
            case "IP":
                return loginUser.getIpaddr();
            default:
                throw new ServiceException("unknown key");
        }
    }

    public static CommonContent createCommon(MesEvent mesEvent , String topic){
        CommonContent commonContent = new CommonContent();
        commonContent.setExtra(topic);
        commonContent.setDate(new Date());
        commonContent.setMessage(mesEvent.getContent());
        commonContent.setCode(200);
        commonContent.setSenderIdentifier(mesEvent.getSendIdentifier());
        return commonContent;
    }

    static boolean checkContext() {
        Channel channel = ChatContext.CHANNEL_CONTEXT.get();
        return channel != null;
    }

    /**
     * 获取当前服务的ServerId 也是当websocket加入聊天室的时候，所连接的runID;
     * @return
     */
    public static Long getRunId(){

        RunIdGetter runIdGetter = SpringContextUtil.getBean("runIdGetter");
        return runIdGetter==null?0:runIdGetter.get();

    }
}

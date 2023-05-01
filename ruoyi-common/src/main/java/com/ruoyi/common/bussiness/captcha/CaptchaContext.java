package com.ruoyi.common.bussiness.captcha;

import com.alibaba.fastjson.JSONArray;
import com.ruoyi.common.bussiness.ContextListener;
import com.ruoyi.common.bussiness.RequestContext;
import com.ruoyi.common.constant.CacheConstant;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/6,15:50
 * @return:
 **/

public class CaptchaContext extends DecryptContext {

    @Resource
    private RedisCache redisCache;



    /**
     * 准备解密的数据，比如私钥，偏移，
     */
    @Override
    public void startContext(){
        HttpServletRequest request = ServletUtils.getRequest();
        Cookie[] cookies = request.getCookies();
        String value=null;
        boolean emp=false;
        for (Cookie cookie : cookies) {
            if ("emp-id".equals(cookie.getName())){
                value = cookie.getValue();
                RequestContext.setParam("emp-id",value);
                emp=true;
                break;
            }
        }
        if (!emp){
            throw new ServiceException(MessageUtils.message("captcha.out.time"),1000);
        }
        String status = redisCache.getCacheObject(CacheConstant.CAPTCHA_STATUS + value);

        if (!CacheConstant.CAPTCHA_STATUS_AWAIT.equals(status)){
            throw new ServiceException(MessageUtils.message("captcha.out.time"),1000);
        }

        RotateCaptchaParams map = redisCache.getCacheObject(CacheConstant.CAPTCHA_INFO + value);
        map.setCurrentStatus(status);
        RequestContext.setParam("CaptContext",map);
        RequestContext.setParam("CaptContextID",value);

    }

    @Override
    public Object decrypt(String encrypt, Field field) {
        if (StringUtils.isBlank(encrypt)){
            return encrypt;
        }
        RotateCaptchaParams captcha = RequestContext.getParam("CaptContext");
        String privateKey = getPrivateKey(captcha);
        try {
            return RSAUtil.decrypt(encrypt,privateKey);
        } catch (Exception e) {
            throw new ServiceException(MessageUtils.message("system.error"));
        }
    }

    @Override
    public void afterDecryptFields(Object o) {
        Class<?> aClass = o.getClass();
        ContextListener contextListener = getListener(aClass);
        String captContextID = RequestContext.getParam("CaptContextID");
        Long times = redisCache.redisTemplate.opsForValue().increment("captchaWrongTimes:" + captContextID);
        redisCache.expire("captchaWrongTimes:" + captContextID,60, TimeUnit.SECONDS);
        if (times!=null&&times>3){
            throw new ServiceException(MessageUtils.message("captcha.out.time"),1000);
        }
        if (contextListener!=null){
            try {
                contextListener.afterDecryptField(o,this);
            }catch (Exception e){
                if (times!=null&&times>=3){
                    redisCache.deleteObject("captchaWrongTimes:"+captContextID);
                }
                throw new ServiceException(MessageUtils.message("captcha.wrong.time") +':'+times,1001);
            }
        }
        if (!redisCache.compareAndSwap(CacheConstant.CAPTCHA_STATUS + captContextID,
                CacheConstant.CAPTCHA_STATUS_AWAIT,
                CacheConstant.CAPTCHA_STATUS_COMPLETE)){
            throw new ServiceException(MessageUtils.message("captcha.out.time"),1000);
        };
    }

    @Override
    public <T> T getObject() {
        return RequestContext.getParam("CaptContext");
    }

    //=============================================================================================


    private String getPrivateKey(RotateCaptchaParams captcha){
        String pointer=captcha.getPointer();
        JSONArray keyPair = redisCache.getCacheObject(CacheConstant.RSA_KEY_STORE + pointer);
        captcha.setKeyPair(keyPair);
        return keyPair.getString(0);
    }
}

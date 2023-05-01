package com.ruoyi.framework.security.handle;

import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/9/6,14:58
 * @return:
 **/
@Slf4j
public class LoginUpdateHandler4Other extends LoginUpdateHandler {

    @Resource
    private RedisCache redisCache;

    private static final ThreadLocal<SessionCache> CURRENT_HANDLED_LOGIN_USER =new ThreadLocal<>();

    @Override
    public void kickLoginUser(Long userId) {
        log.info("踢"+userId+"下线:by:"+ SecurityUtils.getUsername());
        //将人踢下线;
        List<String> tokens = redisCache.getCacheList("t_player_user:singleLogin:" + userId);
        if (tokens!=null&&tokens.size()>0){
            for (String token : tokens) {
                String loginToken="user_login_tokens:"+token;
                redisCache.deleteObject(loginToken);
            }
        }
    }

    @Override
    public LoginUser getLoginUser(Long userId) {
        Assert.test(userId==null,"system.error");
        SessionCache sessionCache = CURRENT_HANDLED_LOGIN_USER.get();
        if (sessionCache!=null){
            return sessionCache.loginUser;
        }
        log.info("开始更新登录缓存:"+userId);
        List<String> tokens = redisCache.getCacheList("t_player_user:singleLogin:" + userId);
        if (tokens!=null&&tokens.size()>0){
            String token = tokens.get(0);
            String loginToken="user_login_tokens:"+token;
            LoginMember loginMember = redisCache.getCacheObject(loginToken);
            if (loginMember!=null){
                CURRENT_HANDLED_LOGIN_USER.set(new SessionCache(loginToken,loginMember));
            }
            return loginMember;
        }
        return null;
    }


    @Override
    public void flush() {
        log.info("更新登录缓存刷入缓存管理");
        try {
            SessionCache sessionCache = CURRENT_HANDLED_LOGIN_USER.get();
            if (sessionCache==null){
                return;
            }
            redisCache.setCacheObject(sessionCache.loginToken,sessionCache.loginUser,1800, TimeUnit.SECONDS);
            log.info("更新登录缓存完成:"+sessionCache.loginUser.getUserId());
        }finally {

            CURRENT_HANDLED_LOGIN_USER.remove();
        }
    }


    private class SessionCache{

        private String loginToken;

        private LoginUser loginUser;

        private SessionCache(String loginToken,LoginUser loginUser){
            this.loginToken=loginToken;
            this.loginUser=loginUser;
        }
    }
}

package com.ruoyi.framework.security.provider;

import com.ruoyi.common.bussiness.RequestContext;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.auth.ApiAuthenticationToken;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.old.OldIpUtils;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.framework.security.service.ApiUserDetailService;
import com.ruoyi.framework.web.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/19,16:05
 * @return:
 **/
@Component
@Slf4j
public class ApiProvider implements AuthenticationProvider {

    private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    @Resource
    private ApiUserDetailService apiDetailService;
    @Resource
    private TokenService tokenService;
    @Resource
    private RedisCache redisCache;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ApiAuthenticationToken token = (ApiAuthenticationToken) authentication;
        Object principal = token.getPrincipal();

        RequestContext.setParam("userType", Constants.USER_TYPE_PLAYER);
        RequestContext.setParam("loginType", Constants.LOGIN_API);
        //这里并没有用本地缓存,每次查询要么查数redis缓存,要么查数据库

        UserDetails userDetails = apiDetailService.loadUserByUsername((String) principal, token.getData());

        userDetailsChecker.check(userDetails);

        //检查完毕后

        return createAuthentication(userDetails,token);
    }


    private Authentication createAuthentication(UserDetails userDetails,ApiAuthenticationToken token){
        String userType = RequestContext.getParam("userType");
        LoginMember loginMember = (LoginMember) userDetails;
        loginMember.setUserType(userType);


        String tokenServiceToken = tokenService.creatApiToken(loginMember);

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(tokenServiceToken, token.getCredentials(), userDetails.getAuthorities());
        authenticationToken.setDetails(token.getDetails());

        String apiOnceToken = StringUtils.apiOnceToken(loginMember.getToken());

        redisCache.setIfAbsent(apiOnceToken,"onceToken",60,TimeUnit.SECONDS);


        //AsyncFactory.kickUser(tokenService,false, loginMember);
        return authenticationToken;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

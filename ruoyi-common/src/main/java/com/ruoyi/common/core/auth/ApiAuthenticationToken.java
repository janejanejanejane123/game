package com.ruoyi.common.core.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/19,14:47
 * @return:
 **/
public class ApiAuthenticationToken extends AbstractAuthenticationToken {


    private final Object principal;


    private Object credentials;

    private Map<ApiAuthKey,Object> data;

    public ApiAuthenticationToken(Object principal,Map<ApiAuthKey,Object> data){
        super(null);
        this.principal=principal;
        this.data=data;
        super.setAuthenticated(false);
    }

    public Map<ApiAuthKey,Object> getData(){
        return data;
    }



    public ApiAuthenticationToken(Object principal,Collection<? extends GrantedAuthority> authorities){
        super(authorities);
        this.principal=principal;
        super.setAuthenticated(true);
    }


    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}

package com.ruoyi.framework.security.filter;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.framework.security.context.AuthenticationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/28,14:21
 * @return:
 **/
@Slf4j
public class UserTypeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Object principal = AuthenticationContextHolder.getContext().getPrincipal();
        if (principal instanceof LoginUser){
            Collection<? extends GrantedAuthority> authorities = ((LoginUser) principal).getAuthorities();

        }
    }
}

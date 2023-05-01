package com.ruoyi.framework.security.proxy;

import com.ruoyi.common.bussiness.RequestContext;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.GlobalException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/20,14:07
 * @return:
 **/
@Service("proxy")
public class UserDetailProxy implements UserDetailsService {


    @Resource
    private UserDetailsService userDetailService;

    @Resource
    private UserDetailsService memberDetailService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String userType = RequestContext.getParam("userType");
        if (Constants.USER_TYPE_PLAYER.equals(userType)){
            return memberDetailService.loadUserByUsername(username);
        }else if (Constants.USER_TYPE_SYSTEM_USER.equals(userType)){
            return userDetailService.loadUserByUsername(username);
        }
        throw new GlobalException("系统错误");
    }
}

package com.ruoyi.framework.security.service;

import com.ruoyi.common.core.auth.ApiAuthKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/20,12:48
 * @return:
 **/
public interface ApiUserDetailService {

    UserDetails loadUserByUsername(String username, Map<ApiAuthKey,Object> data) throws UsernameNotFoundException;
}

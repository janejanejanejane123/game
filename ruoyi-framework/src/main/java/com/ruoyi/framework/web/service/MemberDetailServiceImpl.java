package com.ruoyi.framework.web.service;

import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/19,12:51
 * @return:
 **/
@Service("memberDetailService")
public class MemberDetailServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(MemberDetailServiceImpl.class);



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TUser tUser = null;
        if (tUser==null){
            throw new ServiceException(MessageUtils.message("member.login.unknownUsername"));
        }
        if (tUser.getDisabled()==0){
            throw new ServiceException(MessageUtils.message("member.login.disabled"));
        }

        return new LoginMember(tUser);
    }
}

package com.ruoyi.framework.web.service;

import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.core.auth.ApiAuthKey;
import com.ruoyi.framework.security.service.ApiUserDetailService;
import com.ruoyi.member.service.ITUserService;
import com.ruoyi.system.service.IMemberService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/20,10:41
 * @return:
 **/
@Service("apiDetailService")
public class ApiDetailServiceImpl implements ApiUserDetailService {
    @Resource
    private IMemberService memberService;
    /**
     * api 用户查询
     * 有则查询，没有则新建;
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username, Map<ApiAuthKey,Object> data) throws UsernameNotFoundException {

        Assert.test(data.get(ApiAuthKey.MERCHANT_ID)==null,"system.error");
        Assert.test(data.get(ApiAuthKey.MERCHANT_NUMBER)==null,"system.error");

        String merchantNumber = (String) data.get(ApiAuthKey.MERCHANT_NUMBER);
        String apiUsername = StringUtils.apiUsername(username, merchantNumber);


        TUser tUser = memberService.queryApiMemberByUsername(apiUsername);

        if (tUser==null){
            throw new UsernameNotFoundException(MessageUtils.message("user.not.exists"));
        }


        return new LoginMember(tUser);
    }
}

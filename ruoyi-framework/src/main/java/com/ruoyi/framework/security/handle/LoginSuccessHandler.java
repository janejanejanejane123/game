package com.ruoyi.framework.security.handle;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.bussiness.RequestContext;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.member.mapper.TUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/25,13:43
 * @return:
 **/
@Configuration
@Slf4j
public class LoginSuccessHandler implements ApplicationListener<AuthenticationSuccessEvent> {

    @Resource
    private TokenService tokenService;
    @Resource
    private TUserMapper mapper;
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse response;

    private void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        if (Constants.LOGIN_API.equals(RequestContext.getParam("loginType"))){
            return;
        };

        //生成token
        String userType = RequestContext.getParam("userType");


        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        loginUser.setUserType(userType);

        String token = tokenService.createToken(loginUser);
        //踢人;
        AsyncFactory.kickUser(tokenService,false,authentication);
        //返回token
        AjaxResult success = AjaxResult.success();
        success.put(Constants.TOKEN, token);

        if (userType.equals(Constants.USER_TYPE_PLAYER)){
            AsyncFactory.loginRecord(mapper,loginUser.getUserId());
        }
        HttpUtils.writeAjaxMsg(request,response,null, JSON.toJSONString(success));

        log.info(loginUser.getUsername()+": 登录成功");
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication source = (Authentication)event.getSource();
        try {
            this.onAuthenticationSuccess(request,response,source);
        } catch (IOException e) {
            throw new ServiceException("system error");
        }
    }


}

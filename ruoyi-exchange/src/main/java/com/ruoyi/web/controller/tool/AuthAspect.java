package com.ruoyi.web.controller.tool;

import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/5/9,15:29
 * @return:
 **/

@Aspect
@Configuration
@Slf4j
public class AuthAspect {


    @Before("@annotation(authorizeLogin)")
    public void doBefore(JoinPoint point, AuthorizeLogin authorizeLogin) throws Throwable{
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Assert.test(!(loginUser instanceof LoginMember),
                "member.auth.Forbidden",403);
        String perm = authorizeLogin.value();

        Set<String> permissions = loginUser.getPermissions();

        Assert.test(CollectionUtils.isEmpty(permissions)
                        || StringUtils.isEmpty(perm),
                "member.auth.Forbidden",403);

        Assert.test(!permissions.contains(perm),
                "member.auth.Forbidden",403);

        HttpServletRequest request = ServletUtils.getRequest();
        String ipAddr = IpUtils.getIpAddr(request);


    }
}

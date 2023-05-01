package com.ruoyi.framework.security.filter;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.ip.old.OldIpUtils;
import com.ruoyi.framework.web.service.TokenService;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * token过滤器 验证token有效性
 *
 * @author ruoyi
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter
{
    @Autowired
    private TokenService tokenService;
    @Resource
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser!=null){
            if (loginUser.getToken().startsWith("API:")&&needToCompleteApiToken(loginUser)){
                verifyApiLoginUser((LoginMember)loginUser,request);
            }else {
                UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
                if (!verifyIp(loginUser.getIpaddr(),IpUtils.getIpAddr(request))||
                !verifyHost(loginUser.getHost(), OldIpUtils.getWebsiteHost(request))||
                !verifyBrowser(loginUser.getBrowser(),userAgent)||
                !verifyOs(loginUser.getOs(),userAgent)){
                    AjaxResult networkchange = new AjaxResult(HttpStatus.UNAUTHORIZED, "networkchange");
                    HttpUtils.writeAjaxMsg(request,response,null, JSON.toJSONString(networkchange));
                    return;
                }
            }
        }
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication()))
        {
            tokenService.verifyToken(loginUser);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        chain.doFilter(request, response);
    }

    private boolean needToCompleteApiToken(LoginUser loginUser){
        return StringUtils.isBlank(loginUser.getBrowser())
                ||StringUtils.isBlank(loginUser.getOs())
                ||StringUtils.isBlank(loginUser.getIpaddr())
                ||StringUtils.isBlank(loginUser.getHost());
    }


    private void verifyApiLoginUser(LoginMember loginUser, HttpServletRequest request) {
        String onceToken = redisCache.getAndDelete(StringUtils.apiOnceToken(loginUser.getToken()));
        if (StringUtils.isNotBlank(onceToken)){
            tokenService.setUserAgent(loginUser,request);
            tokenService.refreshToken(loginUser);
        }
    }

    private boolean verifyOs(String os, UserAgent userAgent) {
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        String name = operatingSystem.getName();
        return os.equals(name);
    }

    private boolean verifyBrowser(String  br, UserAgent userAgent) {
        Browser browser = userAgent.getBrowser();
        String name = browser.getName();
        return br.equals(name);


    }

    private boolean verifyHost(String cacheHost,String reqHost) {
        return cacheHost.equals(reqHost);
    }


    private boolean verifyIp(String loginIp,String reqIp){
        return loginIp.equals(reqIp);
    }
}

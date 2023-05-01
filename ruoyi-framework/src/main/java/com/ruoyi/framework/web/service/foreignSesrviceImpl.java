package com.ruoyi.framework.web.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.bussiness.RequestContext;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Encrypt;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.framework.security.context.AuthenticationContextHolder;
import com.ruoyi.game.domain.Account;
import com.ruoyi.game.domain.Agent;
import com.ruoyi.game.domain.request.LoginChildRequest;
import com.ruoyi.game.domain.request.LoginRequest;
import com.ruoyi.game.domain.response.D;
import com.ruoyi.game.domain.response.LoginResponse;
import com.ruoyi.game.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2023-04-28
 */
@Service
public class foreignSesrviceImpl implements IForeignSesrvice {

    @Autowired
    private IGameConfigService iGameConfigService;

    @Autowired
    private IGameUserService iGameUserService;

    @Autowired
    private IAgentService iAgentService;

    @Autowired
    private IUserThirdAuthService iUserThirdAuthService;

    @Autowired
    private IAccountService iAccountService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public AjaxResult channelHandle(LoginRequest loginRequest, HttpServletRequest httpServletRequest) throws Exception {
        String agent = loginRequest.getAgent();
        String key = loginRequest.getKey();
        String DESKey = stringRedisTemplate.opsForValue().get("game.DESKey");
        if (DESKey == null) {
            QueryWrapper<Agent> agentQueryWrapper = new QueryWrapper<>();
            agentQueryWrapper.eq("agent", agent);
            agentQueryWrapper.eq("md5_key", key);
            Agent one = iAgentService.getOne(agentQueryWrapper);
            DESKey = one.getApiKey();
        }
        String param = Encrypt.AESDecrypt(loginRequest.getParam(), DESKey, true);
        LoginChildRequest loginChildRequest = JSON.parseObject(param, LoginChildRequest.class);
        //校验用户和apiKey
        String account = loginChildRequest.getAccount();
        QueryWrapper<Account> accountQueryWrapper = new QueryWrapper<>();
        accountQueryWrapper.eq("account", account);
        accountQueryWrapper.eq("agent", agent);
        Account account1 = iAccountService.getOne(accountQueryWrapper);
        if (account1 == null) {
            return AjaxResult.error();
        }
        String requestURI = httpServletRequest.getRequestURI();
        String randomString = Encrypt.getRandomString(16);
        String token = Encrypt.AESEncrypt(account, randomString);
        String gameToken = randomString + " " + token;
        stringRedisTemplate.opsForValue().set("game.token", gameToken);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setS(100);
        loginResponse.setM("1");
        D d = new D();
        d.setUrl(requestURI + "?" + "token=" + token);
        loginResponse.setD(d);


        String username=account,password=randomString;
        // 用户验证
        Authentication authentication = null;
        try {
            RequestContext.setParam("userType", Constants.USER_TYPE_SYSTEM_USER);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
//                throw new UserPasswordNotMatchException();
                throw new ServiceException("用户不存在/密码错误");
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));



        return AjaxResult.success(loginResponse);
    }

    public static void main(String[] args) throws Exception {
        LoginChildRequest loginChildRequest = new LoginChildRequest();
        loginChildRequest.setAccount("1");
        loginChildRequest.setIp("1");
        loginChildRequest.setS("1");
        loginChildRequest.setLineCode("1");
        loginChildRequest.setKindID("1");
        loginChildRequest.setOrderid("1");
        String randomString = Encrypt.getRandomString(16);
        System.out.println(randomString);
        String s = Encrypt.AESEncrypt(JSON.toJSONString(loginChildRequest), randomString);
        System.out.println(s);
    }
}
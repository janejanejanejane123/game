package com.ruoyi.framework.web.service;

import com.ruoyi.common.constant.CacheConstant;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.CookiesUtils;
import com.ruoyi.common.utils.ip.AddressUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.ip.old.OldIpUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * token验证处理
 *
 * @author ruoyi
 */
@Component
public class TokenService
{
    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;

    @Value("${token.redisLoginKey}")
    private String redisLoginKey;
    /**
     * 最大token数量
     */
    @Value("${token.maxtoken}")
    private Long maxtoken;


    private RedisScript multiTokenScript;

    /**
     * 控制最大登录数量的脚本
     * @throws BeansException
     */
    @PostConstruct
    public void init() throws BeansException {
        multiTokenScript=RedisScript.of(
                "local length = redis.call(\"llen\",KEYS[1]) ;\n"+
                        "if length ~= nil \n"+
                        "then \n"+
                        "   if length >= "+ this.maxtoken+" \n"+
                        "   then \n"+
                        "       local lastToken = redis.call(\"rpop\",KEYS[1]); \n" +
                        "       redis.call(\"lpush\",KEYS[1], ARGV[1] ); \n" +
                        "       return lastToken ; \n"+
                        "   else \n"+
                        "       redis.call(\"lpush\",KEYS[1], ARGV[1] ); \n" +
                        "       return nil; \n"+
                        "   end \n"+
                        "else \n" +
                        "   redis.call(\"lpush\",KEYS[1], ARGV[1] ); \n"+
                        "   return nil \n"+
                        "end ",String.class);
    }

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    @Autowired
    private RedisCache redisCache;


    /**
     * 添加 redis Uid和token映射;
     * @param loginUser
     * @return
     */
    public String setUidTokenMapping(LoginUser loginUser){
        String redisKickKey = loginUser.redisKickKey();
        return redisCache.kickLastAfterLogin(redisKickKey, redisLoginKey, loginUser.getToken());
    }


    /**
     * 添加uid 与token 隐射集合（支持单账号多登陆）
     * @param loginUser
     * @return
     */
    public String setUidTokenListMapping(LoginUser loginUser){
        return redisCache.execute(multiTokenScript, Collections.singletonList(loginUser.redisKickKey()),loginUser.getToken());
    }

    /**
     * 删除redis指向;
     * @param redisKey
     */
    public void delUidTokenMapping(String redisKey){
        redisCache.deleteObject(redisKey);
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request)
    {
        String token;
        // 获取请求携带的令牌
        if(request.getRequestURI().startsWith("/websocket")){
            token = request.getParameter("token");
        }else{
            token = getToken(request);
        }
        if (StringUtils.isNotEmpty(token))
        {
            try
            {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                String userKey = getTokenKey(uuid);
                LoginUser user = redisCache.getCacheObject(userKey);
                return user;
            }
            catch (Exception e)
            {
            }
        }
        return null;
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(String token)
    {
        try {
            Claims claims = parseToken(token);
            // 解析对应的权限以及用户信息
            String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
            String userKey = getTokenKey(uuid);
            LoginUser user = redisCache.getCacheObject(userKey);
            return user;
        }catch (Exception e){
            System.out.println("获取用户异常");
        }
        return null;
    }


    public void updateLoginCache(){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        this.setLoginUser(loginUser);
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser)
    {
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken()))
        {
            refreshToken(loginUser);
        }
    }

    public void logoutUser(LoginUser loginUser){
        String token = loginUser.getToken();
        redisCache.logout(getTokenKey(token),loginUser.redisKickKey(),token);

    }


    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token)
    {
        if (StringUtils.isNotEmpty(token))
        {
            String userKey = getTokenKey(token);
            redisCache.deleteObject(userKey);

        }
    }


    public String creatApiToken(LoginMember loginMember){
        String token = "API:"+IdUtils.fastUUID();
        loginMember.setToken(token);
        refreshToken(loginMember);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUser loginUser)
    {
        String token = IdUtils.fastUUID();


        loginUser.setHost(OldIpUtils.getWebsiteHost(ServletUtils.getRequest()));
        loginUser.setToken(token);
        setUserAgent(loginUser);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser
     * @return 令牌
     */
    public void verifyToken(LoginUser loginUser)
    {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN)
        {
            if (loginUser instanceof LoginMember){
                reMemberToken((LoginMember) loginUser);
            }else {
                refreshToken(loginUser);
            }

        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser)
    {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }


    public void reMemberToken(LoginMember loginMember){
        String userKey = getTokenKey(loginMember.getToken());
        redisCache.expire(userKey,expireTime,TimeUnit.MINUTES);
    }

    public void setUserAgent(LoginUser loginUser,HttpServletRequest request)
    {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr(request);
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }
    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    public void setUserAgent(LoginUser loginUser)
    {
        setUserAgent(loginUser,ServletUtils.getRequest());
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims)
    {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token)
    {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token)
    {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private String getToken(HttpServletRequest request)
    {
        String token = request.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX))
        {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    public String getTokenKey(String uuid)
    {
        return redisLoginKey +":"+ uuid;
    }

    public void checkCaptchaStatus() {

        HttpServletRequest request = ServletUtils.getRequest();
        String cookie = CookiesUtils.getCookie(request, "emp-id");
        Assert.test(StringUtils.isBlank(cookie),"captcha.status.out.time",1002);

        String status=redisCache.getAndDelete(CacheConstant.CAPTCHA_STATUS + cookie);

        Assert.test(StringUtils.isBlank(status),"captcha.status.null",1003);

        Assert.test(CacheConstant.CAPTCHA_STATUS_AWAIT.equals(status),"captcha.status.not.complete",1004);

        Assert.test(!CacheConstant.CAPTCHA_STATUS_COMPLETE.equals(status),"system.error");

    }



}

//package com.ruoyi.framework.web.service;
//
//import com.ruoyi.common.constant.Constants;
//import com.ruoyi.common.core.domain.model.LoginUser;
//import com.ruoyi.common.core.domain.model.member.MemberLoginBody;
//import com.ruoyi.common.core.redis.RedisCache;
//import com.ruoyi.common.utils.StringUtils;
//import com.ruoyi.common.utils.ip.AddressUtils;
//import com.ruoyi.common.utils.ip.IpUtils;
//import com.ruoyi.common.utils.uuid.IdUtils;
//import eu.bitwalker.useragentutils.UserAgent;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
///**
// * @param
// * @Author: sddd
// * @Date: 2022/3/21,13:03
// * @return:
// **/
//
//@Component
//public class Token4MemberService {
//
//    @Value("${token4member.header}")
//    private String header;
//    @Value("${token4member.secret}")
//    private String secret;
//    @Value("${token4member.expireTime}")
//    private int expireTime;
//    @Resource
//    private HttpServletRequest request;
//
//    @Resource
//    private RedisCache redisCache;
//
//    private static final long MILLIS_SECOND = 1000;
//
//    private static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
//
//    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;
//
//
//    public MemberLoginBody getLoginUser()
//    {
//        String token;
//        // 获取请求携带的令牌
//        if(request.getRequestURI().startsWith("/websocket")){
//            token = request.getParameter("token");
//        }else{
//            token = getToken();
//        }
//        if (StringUtils.isNotEmpty(token))
//        {
//            try
//            {
//                Claims claims = parseToken(token);
//                // 解析对应的权限以及用户信息
//                String uuid = (String) claims.get(Constants.LOGIN_MEMBER_KEY);
//                String userKey = getTokenKey(uuid);
//                 ;
//                return redisCache.getCacheObject(userKey);
//            }
//            catch (Exception e)
//            {
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
//     *
//     *
//     */
//
//    public void verifyToken(MemberLoginBody memberLoginBody)
//    {
//        long expireTime = memberLoginBody.getExpTime();
//        long currentTime = System.currentTimeMillis();
//        if (expireTime - currentTime <= MILLIS_MINUTE_TEN)
//        {
//            refreshToken(memberLoginBody);
//        }
//    }
//    /**
//     * 创建令牌
//     *
//     * @param loginMember 用户信息
//     * @return 令牌
//     */
//    public String createToken(MemberLoginBody loginMember)
//    {
//        String token = IdUtils.fastUUID();
//        loginMember.setToken(token);
//        setUserAgent(loginMember);
//        refreshToken(loginMember);
//
//        Map<String, Object> claims = new HashMap<>(2);
//        claims.put(Constants.LOGIN_USER_KEY, token);
//        return createToken(claims);
//    }
//
//    /**
//     * 设置用户代理信息
//     *
//     * @param loginMember 登录信息
//     */
//    public void setUserAgent(MemberLoginBody loginMember)
//    {
//        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
//        String ip = IpUtils.getIpAddr(request);
//        loginMember.setAttribute("login",ip);
//        loginMember.setAttribute("location",AddressUtils.getRealAddressByIP(ip));
//        loginMember.setAttribute("browser",userAgent.getBrowser().getName());
//        loginMember.setAttribute("OS",userAgent.getOperatingSystem().getName());
//        refreshToken(loginMember);
//    }
//
//
//    /**
//     * 从数据声明生成令牌
//     *
//     * @param claims 数据声明
//     * @return 令牌
//     */
//    private String createToken(Map<String, Object> claims)
//    {
//        return Jwts.builder()
//                .setClaims(claims)
//                .signWith(SignatureAlgorithm.HS512, secret).compact();
//    }
//    /**
//     * 设置用户身份信息
//     */
//    public void setLoginMember(MemberLoginBody loginMember)
//    {
//        if (StringUtils.isNotNull(loginMember) && StringUtils.isNotEmpty(loginMember.getToken()))
//        {
//            refreshToken(loginMember);
//        }
//    }
//
//    /**
//     * 刷新令牌有效期
//     *
//     * @param loginMember 登录信息
//     */
//    public void refreshToken(MemberLoginBody loginMember)
//    {
//        loginMember.setLoginTime(System.currentTimeMillis());
//        loginMember.setExpTime(loginMember.getLoginTime() + expireTime * MILLIS_MINUTE);
//        String userKey = getTokenKey(loginMember.getToken());
//        redisCache.setCacheObject(userKey, loginMember, expireTime, TimeUnit.MINUTES);
//    }
//
//    private String getTokenKey(String token) {
//        return Constants.LOGIN_TOKEN_KEY_4_MEMBER + token;
//    }
//
//
//    /**
//     * 从令牌中获取数据声明
//     *
//     * @param token 令牌
//     * @return 数据声明
//     */
//    private Claims parseToken(String token)
//    {
//        return Jwts.parser()
//                .setSigningKey(secret)
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    /**
//     * 获取请求token
//     *
//     * @return token
//     */
//    private String getToken()
//    {
//        String token = request.getHeader(header);
//        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX4M))
//        {
//            token = token.replace(Constants.TOKEN_PREFIX4M, "");
//        }
//        return token;
//    }
//}

package com.ruoyi.common.constant;

import io.jsonwebtoken.Claims;

/**
 * 通用常量信息
 * 
 * @author ruoyi
 */
public class Constants
{
    public static final String LOGIN_API="0";
    public static final String REGISTER_FRONT="0";
    public static final String REGISTER_BACK="1";
    public static final String REGISTER_API="2";
    /**
     * 通用状态
     */
    public static final short CLOSE=0;
    public static final short OPEN=1;
    /**
     * 实名认证
     */
    public static final short UNDO=0;

    public static final short DONE=1;

    /**
     * 银行卡类型_银行卡
     */
    public static final short CREDIT_TYPE_CARD=0;
    /**
     * 银行卡类型_微信
     */
    public static final short CREDIT_TYPE_VX=1;
    /**
     * 银行卡类型_支付宝
     */
    public static final short CREDIT_TYPE_ZFB=2;

    /**
     * qq
     */
    public static final short CREDIT_TYPE_QQ=3;

    public static final short CREDIT_TYPE_ZFBA=4;

    public static final Long CREDIT_DELETE = 0L;

    public static final Long CREDIT_ON_USE = 1L;

    /**
     * 系统登陆类型
     */
    public static final String USER_TYPE_SYSTEM_USER="1";

    /**
     * 玩家登陆类型
     */
    public static final String USER_TYPE_PLAYER="0";

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 通用成功标识
     */
    public static final String SUCCESS = "0";

    /**
     * 通用失败标识
     */
    public static final String FAIL = "1";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 令牌
     */
    public static final String TOKEN = "token";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌前缀redis
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 用户ID
     */
    public static final String JWT_USERID = "userid";

    /**
     * 用户名称
     */
    public static final String JWT_USERNAME = Claims.SUBJECT;

    /**
     * 用户头像
     */
    public static final String JWT_AVATAR = "avatar";

    /**
     * 创建时间
     */
    public static final String JWT_CREATED = "created";

    /**
     * 用户权限
     */
    public static final String JWT_AUTHORITIES = "authorities";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * RMI 远程方法调用
     */
    public static final String LOOKUP_RMI = "rmi:";

    /**
     * LDAP 远程方法调用
     */
    public static final String LOOKUP_LDAP = "ldap:";

    /**
     * LDAPS 远程方法调用
     */
    public static final String LOOKUP_LDAPS = "ldaps:";

    /**
     * 定时任务白名单配置（仅允许访问的包名，如其他需要可以自行添加）
     */
    public static final String[] JOB_WHITELIST_STR = { "com.ruoyi" };

    /**
     * 定时任务违规的字符
     */
    public static final String[] JOB_ERROR_STR = { "java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml",
            "org.springframework", "org.apache", "com.ruoyi.common.utils.file" };


    public static final Short APPLY_WAIT = 0;

    public static final Short APPLY_PASS = 1;

    public static final String PUSH_MESSAGE_INNER_QUEUE = "signal/channel/PUSH_MESSAGE_INNER_QUEUE";

    public static final String BIND_MESSAGE_INNER_QUEUE = "signal/channel/BIND_MESSAGE_INNER_QUEUE";

    public static final String ONLINE_WEB_USER_KEY = "ONLINE:WEB:USER";

    public static final Long MERCHANT_ID_DEFAULT = 0L;
}

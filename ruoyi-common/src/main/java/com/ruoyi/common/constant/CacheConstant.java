package com.ruoyi.common.constant;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/1,16:05
 * @return:
 **/
public class CacheConstant {

    public static final String SENSITIVE_WORD_IDENTIFIER="E_PAY_SENSITIVE";

    public static final String RSA_KEY="RSA-KEY";

    public static final String RSA_KEY_STORE="VALIDATE:RSA:STORE:";

    public static final String CAPTCHA_INFO = "CAPTCHA_INFO:";


    public static final String CAPTCHA_STATUS = "CAPTCHA_STATUS:";
    public static final String CAPTCHA_STATUS_AWAIT = "0";
    public static final String CAPTCHA_STATUS_COMPLETE = "1";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

}

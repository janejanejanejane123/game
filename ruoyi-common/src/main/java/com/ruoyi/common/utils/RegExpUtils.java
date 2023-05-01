package com.ruoyi.common.utils;

import java.util.regex.Pattern;

/**
 * 正则表达式 工具类
 * 可以直接调用 最后的校验方法
 * @author nn
 */
public class RegExpUtils
{
    /**
     * 纯数字正则
     */
    public static final String REGEX_PURE_NUMBER = "(^[0-9]{8,25}$)";

    /**
     * 姓名正则
     */
    public static final String REGEX_NAME = "(^[\\u4E00-\\u9FA5a-zA-Z]+)";

    /**
     * 用户名
     */
    public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,20}$";

    /**
     * 密码正则
     */
    public static final String REGEX_PASSWORD = "(^(\\w){6,20}$)";

    /**
     * 账号正则
     */
    public static final String REGEX_ACCOUNT = "(^[a-zA-Z][a-zA-Z0-9_]{5,19}$)";

    /**
     * 手机号
     */
    public static final String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

    /**
     * 邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 汉字
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";

    /**
     * 身份证
     */
    public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";

    /**
     * URL
     */
    public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

    /**
     * IP地址
     */
    public static final String REGEX_IP_ADDRESS = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";


    //========================================== 校验方法 ============================

    /**
     * 校验账号
     *
     * @param account
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isAccount(String account) {
        return Pattern.matches(REGEX_ACCOUNT, account);
    }

    /**
     * 校验姓名
     *
     * @param name
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isName(String name) {
        return Pattern.matches(REGEX_NAME, name);
    }

    /**
     * 校验纯数字
     *
     * @param number
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPureNumber(String number) {
        return Pattern.matches(REGEX_PURE_NUMBER, number);
    }

    /**
     * 校验用户名
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }

    /**
     * 校验密码
     *
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    /**
     * 校验邮箱
     *
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    /**
     * 校验汉字
     *
     * @param chinese
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isChinese(String chinese) {
        return Pattern.matches(REGEX_CHINESE, chinese);
    }

    /**
     * 校验身份证
     *
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }

    /**
     * 校验URL
     *
     * @param url
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }

    /**
     * 校验IP地址
     *
     * @param ipAddress
     * @return  校验通过返回true，否则返回false
     */
    public static boolean isIPAddress(String ipAddress) {
        return Pattern.matches(REGEX_IP_ADDRESS, ipAddress);
    }
}

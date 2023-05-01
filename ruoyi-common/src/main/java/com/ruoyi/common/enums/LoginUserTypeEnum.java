package com.ruoyi.common.enums;

import com.ruoyi.common.exception.ServiceException;

import java.util.HashMap;
import java.util.Map;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/9/13,11:28
 * @return:
 **/
public enum  LoginUserTypeEnum {
    //管理员
    SYSTEM("00","1"),
    //商户
    MERCHANT("11","1"),
    //代付
    PROXY_PAY("22","2"),
    //支付
    PAY("33","3");

    public static final Map<String,LoginUserTypeEnum> LOGIN_USER_TYPE_ENUM_MAP=new HashMap<>();

    static {
        for (LoginUserTypeEnum value : values()) {
            LOGIN_USER_TYPE_ENUM_MAP.put(value.userType,value);
        }
    }

    LoginUserTypeEnum(String userType,String siteCode){
        this.userType=userType;
        this.siteCode=siteCode;
    }

    private String userType;

    private String siteCode;

    public static void test(String userType,Object principal,String siteCode){
        LoginUserTypeEnum loginUserTypeEnum = LOGIN_USER_TYPE_ENUM_MAP.get(userType);
        if (loginUserTypeEnum==null||!loginUserTypeEnum.siteCode.equals(siteCode)){
            throw new ServiceException("登录用户：" + principal + " 不存在");
        }
    }
}

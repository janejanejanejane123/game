package com.ruoyi.common.bussiness.constants;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/23,12:00
 * @return:
 **/
public class Constant {

    public final static String USERNAME_PATTEN= "[a-zA-Z0-9]{6,20}";


    public final static String PASSWORD_PATTEN ="(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$";


    public static final String EMAIL_PATTEN = "^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,4}$";

    public static final String PHONE_NUMBER_REG = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";


    public final static String REAL_NAME="^[\\u4E00-\\u9FA5][•·\\u4e00-\\u9fa5]{1,20}";
}

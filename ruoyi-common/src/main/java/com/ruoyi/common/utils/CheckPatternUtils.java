package com.ruoyi.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/23,12:08
 * @return:
 **/
public class CheckPatternUtils {

    /**
     * 匹配方法
     */
    public static boolean match(String args,String rule) {

        Pattern r = Pattern.compile(rule);
        Matcher m = r.matcher(args);
        return  m.matches();
    }
}

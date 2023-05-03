package com.ruoyi.member.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/5/17,13:07
 * @return:
 **/
public class Util {
    private static final Logger log = LoggerFactory.getLogger(Util.class);
    private static StringBuilder sb = new StringBuilder();

    /**
     * 字节数组IP转String
     *
     * @param ip ip的字节数组形式
     * @return 字符串形式的ip
     */
    public static String getIpStringFromBytes(byte[] ip) {
        sb.delete(0, sb.length());
        sb.append(ip[0] & 0xFF);
        sb.append('.');
        sb.append(ip[1] & 0xFF);
        sb.append('.');
        sb.append(ip[2] & 0xFF);
        sb.append('.');
        sb.append(ip[3] & 0xFF);
        return sb.toString();
    }


    /**
     * 生成6位验证码
     */
    public static String randomCode() {
        String sources = UUID.randomUUID().toString().replaceAll("-", "");
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(sources.charAt(random.nextInt(31)) + "");
        }
        return str.toString();
    }
    /**
     * 生成6位数字验证码
     */
    public static String randomNum() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    /**
     * 根据某种编码方式将字节数组转换成字符串
     *
     * @param b        字节数组
     * @param offset   要转换的起始位置
     * @param len      要转换的长度
     * @param encoding 编码方式
     * @return 如果encoding不支持，返回一个缺省编码的字符串
     */
    public static String getString(byte[] b, int offset, int len, String encoding) {
        try {
            return new String(b, offset, len, encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(b, offset, len);
        }
    }





}

package com.ruoyi.common.utils.http;

import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/6,15:12
 * @return:
 **/
public class CookiesUtils {


    public static String trCreate(){
        String cookie = getCookie(ServletUtils.getRequest(), "emp-id");
        if (StringUtils.isBlank(cookie)){
            cookie = UUID.randomUUID().toString();
        }
        return cookie;
    }


    public static void setHeader(String name,String value){
        HttpServletResponse response = ServletUtils.getResponse();
        response.setHeader(name,value);
    }


    public static void setCookie(String key,String value,int time) throws UnsupportedEncodingException {

        HttpServletResponse response = ServletUtils.getResponse();
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        URLEncoder.encode(value, "utf-8");
        cookie.setMaxAge(time);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        Cookie cookie = cookieMap.get(name);
        if (cookie==null) {
            return null;
        }
        return cookie.getValue();
    }
}

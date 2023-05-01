package com.ruoyi.common.bussiness;


import com.ruoyi.common.utils.ServletUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/25,16:23
 * @return:
 **/
public class RequestContext {

    public static void setParam(String key,Object param){
        HttpServletRequest request = ServletUtils.getRequest();
        request.setAttribute(key,param);
    }
    @SuppressWarnings("unchecked")
    public static <T> T getParam(String key){
        HttpServletRequest request = ServletUtils.getRequest();
        return (T)request.getAttribute(key);
    }

}

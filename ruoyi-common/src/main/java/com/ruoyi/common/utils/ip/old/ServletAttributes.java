package com.ruoyi.common.utils.ip.old;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @description: 获取Request
 *
 * @create: 2019-07-23 14:48
 **/
public class ServletAttributes {
    private static int type = 0;
    private static final int SpringServlet=1;
    private static Class requestContext;


    static {
        init();
    }

    private static void init() {
        try {
            requestContext = Class.forName("org.springframework.web.context.request.RequestContextHolder");
            Method method = requestContext.getMethod("getRequestAttributes");
            method.invoke(null);
            type = SpringServlet;
            return;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        }
    }

    public static ServletRequest getServletRequest(){
        if(type == 0){
            return null;
        }
        try {
            switch (type){
                case SpringServlet:
                    requestContext = Class.forName("org.springframework.web.context.request.RequestContextHolder");
                    Method method = requestContext.getMethod("getRequestAttributes");
                    Object springAttributes = method.invoke(null);
                    if(springAttributes == null){
                        return  null;
                    }
                    Class servletRequestAttributes = Class.forName("org.springframework.web.context.request.ServletRequestAttributes");
                    method = servletRequestAttributes.getMethod("getRequest");
                    return (ServletRequest) method.invoke(springAttributes);
                 default:
                     return null;

            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
        }
        return null;
    }

    public static ServletResponse getServletResponse(){
        if(type == 0){
            return null;
        }
        try {
            switch (type){
                case SpringServlet:
                    requestContext = Class.forName("org.springframework.web.context.request.RequestContextHolder");
                    Method method = requestContext.getMethod("getRequestAttributes");
                    Object springAttributes = method.invoke(null);
                    if(springAttributes == null){
                        return  null;
                    }
                    Class servletRequestAttributes = Class.forName("org.springframework.web.context.request.ServletRequestAttributes");
                    method = servletRequestAttributes.getMethod("getResponse");
                    return (ServletResponse) method.invoke(springAttributes);
                default:
                    return null;

            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
        }
        return null;
    }
}

package com.ruoyi.chat.component.handler;

import com.ruoyi.chat.component.handler.annotation.AppAsync;
import com.ruoyi.chat.component.handler.annotation.SubEventPublish;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/9,14:51
 * @return:
 **/
public class MethodHandler {
    private final Class<?> returnType;

    private boolean async;

    private final Method method;

    private final Object bean;

    private final Boolean requirePerm;


    MethodHandler(Method method,Object bean,boolean defaultAsync){
        this.method=method;
        this.requirePerm=true;
        this.bean=bean;
        this.returnType=method.getReturnType();
        initAsync(method,defaultAsync);
    }

    private void initAsync(Method method,boolean defaultAsync) {
        if (method.isAnnotationPresent(SubEventPublish.class)){
            throw new ServiceException("不规范的注解:SubEventPublish 不能用在AppController下");
        }
        if (method.isAnnotationPresent(AppAsync.class)){
            this.async=true;
        }else {
            this.async=defaultAsync;
        }

    }

    boolean isAsync(){
        return async;
    }

    Method getMethod(){
        return method;
    }


    boolean checkPerm(LoginMember loginMember){
        if (loginMember != null){
            if (!requirePerm){
                return true;
            }

            return true;
        }else {
            return false;
        }
    }

    Object invoke(Object...args){
        return ReflectionUtils.invokeMethod(method, bean, args);

    }

    public Class<?> getReturnType() {
        return returnType;
    }
}

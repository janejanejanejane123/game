package com.ruoyi.framework.aspectj;

import com.ruoyi.common.annotation.Overtime;
import com.ruoyi.common.cache.OvertimeContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.util.Stack;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/9,11:29
 * @return:
 **/
@Order(-1)
@Aspect
@Configuration
@Slf4j
public class OvertimeAspect {






    @Pointcut("@annotation(com.ruoyi.common.annotation.Overtime)")
    public void otPointCut()
    {

    }


    @Around("otPointCut()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        boolean annotationPresent = method.isAnnotationPresent(Cacheable.class);
        Stack<Overtime> stack=null;
        try {
            //只有在Cacheable存在时才能生效
            if (annotationPresent){
                //将过期时间存入上下文栈中;
                stack = OvertimeContext.getStack();
                Overtime overtime = AnnotationUtils.findAnnotation(method, Overtime.class);
                stack.push(overtime);
            }
            return point.proceed();
        }finally {
            if (annotationPresent){
                OvertimeContext.popStack(stack);
            }
        }
    }


}

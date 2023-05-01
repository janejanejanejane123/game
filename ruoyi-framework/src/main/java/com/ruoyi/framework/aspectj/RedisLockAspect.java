package com.ruoyi.framework.aspectj;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.ruoyi.common.annotation.RedisLockOperate;
import com.ruoyi.common.core.redis.RedisLock;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/26,13:51
 * @return:
 **/
@Aspect
@Configuration
@Slf4j
public class RedisLockAspect {

    private static AviatorEvaluatorInstance aviatorEvaluatorInstance = AviatorEvaluator.newInstance();

    @Pointcut("@annotation(com.ruoyi.common.annotation.RedisLockOperate)")
    public void rLPointCut()
    {

    }


    @Around("rLPointCut()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        RedisLockOperate lock = getAnnotation(point);
        String generateKey = generateKey(point,lock);
        log.info("RedisLock生成的Key是:"+generateKey);
        try {
            if (RedisLock.lock(generateKey,lock.seconds())){
                return point.proceed();
            }else {
                throw new ServiceException(MessageUtils.message(lock.messageCode()));
            }
        }finally {
            RedisLock.unLock();
        }
    }


    private String generateKey(ProceedingJoinPoint point,RedisLockOperate annotation){

        String key = annotation.key();
        MethodSignature signature = (MethodSignature) point.getSignature();
        String methodName = signature.getMethod().getName();
        Class declaringType = signature.getDeclaringType();
        String clzName = declaringType.getSimpleName();
        //如果未设key 值 ,则返回 类名+方法名+uid 或者  类名+方法名+ip 取决于这个接口是否已经登录;
        if (StringUtils.isBlank(key)){
            String uniqueKey;
            if (SecurityUtils.isLogin()){
                uniqueKey= SecurityUtils.getLoginUser().getUserId()+"";
            }else {
                HttpServletRequest request = ServletUtils.getRequest();
                uniqueKey = IpUtils.getIpAddr(request);
            }

            return clzName+"."+methodName+":"+uniqueKey;
        }

        String[] parameterNames = signature.getParameterNames();
        Object[] args = point.getArgs();
        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            standardEvaluationContext.setVariable(parameterNames[i],args[i]);
        }
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        return  (String)spelExpressionParser.parseRaw(key).getValue(standardEvaluationContext);
    }

    private RedisLockOperate getAnnotation(ProceedingJoinPoint point){
        MethodSignature signature = (MethodSignature) point.getSignature();
        RedisLockOperate redisLock = AnnotationUtils.findAnnotation(signature.getMethod(), RedisLockOperate.class);
        if (Objects.nonNull(redisLock))
        {
            return redisLock;
        }

        return AnnotationUtils.findAnnotation(signature.getDeclaringType(), RedisLockOperate.class);
    }
}

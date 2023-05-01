package com.ruoyi.common.annotation;

import com.ruoyi.common.bussiness.captcha.CaptchaContext;
import com.ruoyi.common.utils.DecryptContext;

import java.lang.annotation.*;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/1,14:01
 * @return:
 **/
@Target({ElementType.TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Detector {
    Class<? extends DecryptContext> preContext() default CaptchaContext.class;
}

package com.ruoyi.chat.component.handler.annotation;

import java.lang.annotation.*;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/13,19:38
 * @return:
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AppRequestBody {
    String value() default "";
}

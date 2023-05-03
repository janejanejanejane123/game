package com.ruoyi.chat.component.handler.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/9,10:38
 * @return:
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface AppController {
    String value() default "";
}
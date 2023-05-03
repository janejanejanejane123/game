package com.ruoyi.chat.component.handler.annotation;

import java.lang.annotation.*;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/9,15:00
 * @return:
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AppRequires {
}

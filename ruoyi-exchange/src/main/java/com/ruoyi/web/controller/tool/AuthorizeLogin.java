package com.ruoyi.web.controller.tool;

import java.lang.annotation.*;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/5/9,15:27
 * @return:
 **/

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AuthorizeLogin {

    String value() default "ROLE_PLAYER";
}

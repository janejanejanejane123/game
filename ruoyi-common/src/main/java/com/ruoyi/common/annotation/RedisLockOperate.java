package com.ruoyi.common.annotation;

import java.lang.annotation.*;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/26,13:41
 * @return:
 **/
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLockOperate {
    //boolean login() default true;

    String key() default "";

    long seconds() default 1;

    String messageCode() default "no.operate.frequently";

}

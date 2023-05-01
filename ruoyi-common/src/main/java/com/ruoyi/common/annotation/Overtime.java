package com.ruoyi.common.annotation;

import java.lang.annotation.*;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/9,11:24
 * @return:
 **/
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Overtime {
    /**
     *
     * @return 过期时间 秒
     */
    long value() default 3600L;
}

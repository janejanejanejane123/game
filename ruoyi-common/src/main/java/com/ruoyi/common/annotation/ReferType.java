package com.ruoyi.common.annotation;

import com.alibaba.fastjson.JSONObject;

import java.lang.annotation.*;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/13,17:58
 * @return:
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReferType {

    Class<?> value() default JSONObject.class;
}

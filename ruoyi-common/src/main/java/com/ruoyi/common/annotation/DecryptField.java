package com.ruoyi.common.annotation;

import java.lang.annotation.*;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/1,12:58
 * @return:
 **/
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptField {
}

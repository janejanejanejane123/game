package com.ruoyi.chat.component.handler.annotation;

import com.ruoyi.chat.netty.RequestMethod;
import com.ruoyi.chat.netty.RequestType;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AppRequestMapping {
    String value() default "";

    RequestType[] requestType() default {};

    RequestMethod[] method() default {};

    String[] headers() default {};

    String[] consumes() default {"application/json;charset=UTF-8"};

    String[] produces() default {};
}

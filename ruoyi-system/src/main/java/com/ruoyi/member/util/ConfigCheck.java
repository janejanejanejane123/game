package com.ruoyi.member.util;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/5,13:18
 * @return:
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = ConfigCheckImpl.class)
public @interface ConfigCheck {
    String confKey();

    String message() default "{javax.validation.constraints.Pattern.message}";


    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

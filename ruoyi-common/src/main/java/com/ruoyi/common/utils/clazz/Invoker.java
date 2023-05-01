package com.ruoyi.common.utils.clazz;

/**
 * 获取对象属性Invoker
 *
 * @version v 1.0
 * @ClassName: Invoker
 */
import java.lang.reflect.InvocationTargetException;

public interface Invoker {
    String getName();
    Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException;
}

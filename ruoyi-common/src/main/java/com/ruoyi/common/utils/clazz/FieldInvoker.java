package com.ruoyi.common.utils.clazz;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class FieldInvoker {
    private Field field;

    public FieldInvoker(Field field) {
        this.field = field;
    }

    public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
        field.set(target, args[0]);
        return null;
    }
    public Object invoke(Object target) throws IllegalAccessException, InvocationTargetException {
        return field.get(target);
    }
    public Class<?> getType() {
        return field.getType();
    }
}

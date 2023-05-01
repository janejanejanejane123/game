package com.ruoyi.common.utils.clazz;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 方法Invoker
 */
public class MethodInvoker implements Invoker {

  private Method method;
  private String name;

  public MethodInvoker(Method method) {
    this.method = method;
    this.name = method.getName();
  }

  public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
    return method.invoke(target, args);
  }

  public Method getMethod() {
    return method;
  }

  public String getName() {
    return name;
  }
}

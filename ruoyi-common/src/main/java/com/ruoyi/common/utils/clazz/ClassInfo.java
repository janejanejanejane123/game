/*
 *  Copyright 2004 Clinton Begin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ruoyi.common.utils.clazz;

import com.ruoyi.common.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;


/**
 *
 * class信息类，该类表示一组缓存的类定义信息
 * 允许在属性名称和getter / setter方法之间轻松映射。
 * @version  v 1.0
 */
public class ClassInfo {
    private static Logger logger = LoggerFactory.getLogger(ClassInfo.class);

    private static boolean cacheEnabled = true;//为了性能默认启动缓存机制
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final Set SIMPLE_TYPE_SET = new HashSet();
    private static final Map CLASS_INFO_MAP = Collections.synchronizedMap(new HashMap());

    private String className;
    private String[] readablePropertyNames = EMPTY_STRING_ARRAY;
    private String[] writeablePropertyNames = EMPTY_STRING_ARRAY;
    private HashMap setMethods = new HashMap();
    private HashMap getMethods = new HashMap();
    private HashMap setTypes = new HashMap();
    private HashMap getTypes = new HashMap();
    private Constructor defaultConstructor;
    private List<Type> superClassGenricList;
    static {
        SIMPLE_TYPE_SET.add(String.class);
        SIMPLE_TYPE_SET.add(Byte.class);
        SIMPLE_TYPE_SET.add(Short.class);
        SIMPLE_TYPE_SET.add(Character.class);
        SIMPLE_TYPE_SET.add(Integer.class);
        SIMPLE_TYPE_SET.add(Long.class);
        SIMPLE_TYPE_SET.add(Float.class);
        SIMPLE_TYPE_SET.add(Double.class);
        SIMPLE_TYPE_SET.add(Boolean.class);
        SIMPLE_TYPE_SET.add(Date.class);
        SIMPLE_TYPE_SET.add(Class.class);
        SIMPLE_TYPE_SET.add(BigInteger.class);
        SIMPLE_TYPE_SET.add(BigDecimal.class);

        SIMPLE_TYPE_SET.add(Collection.class);
        SIMPLE_TYPE_SET.add(Set.class);
        SIMPLE_TYPE_SET.add(Map.class);
        SIMPLE_TYPE_SET.add(List.class);
        SIMPLE_TYPE_SET.add(HashMap.class);
        SIMPLE_TYPE_SET.add(TreeMap.class);
        SIMPLE_TYPE_SET.add(ArrayList.class);
        SIMPLE_TYPE_SET.add(LinkedList.class);
        SIMPLE_TYPE_SET.add(HashSet.class);
        SIMPLE_TYPE_SET.add(TreeSet.class);
        SIMPLE_TYPE_SET.add(Vector.class);
        SIMPLE_TYPE_SET.add(Hashtable.class);
        SIMPLE_TYPE_SET.add(Enumeration.class);
    }

    private ClassInfo(Class clazz) {
        className = clazz.getName();
        addDefaultConstructor(clazz);
        addGetMethods(clazz);
        addSetMethods(clazz);
        addFields(clazz);
        readablePropertyNames = (String[]) getMethods.keySet().toArray(new String[getMethods.keySet().size()]);
        writeablePropertyNames = (String[]) setMethods.keySet().toArray(new String[setMethods.keySet().size()]);
        superClassGenricList = GenericsUtils.getSuperClassGenric(clazz);//设置枚举类

    }

    public List<Type> getSuperClassGenricList() {
        return superClassGenricList;
    }

    /**
     * 添加默认构造函数
     * @param clazz
     */
    private void addDefaultConstructor(Class clazz) {
        Constructor[] consts = clazz.getDeclaredConstructors();
        for (int i = 0; i < consts.length; i++) {
            Constructor constructor = consts[i];
            if (constructor.getParameterTypes().length == 0) {
                if (canAccessPrivateMethods()) {
                    try {
                        constructor.setAccessible(true);
                    } catch (Exception e) {
                        logger.error("添加默认构造函数失败{}",e);
                    }
                }
                if (constructor.isAccessible()) {
                    this.defaultConstructor = constructor;
                }
            }
        }
    }

    /**
     * 添加get方法信息
     * @param cls
     */
    private void addGetMethods(Class cls) {
        Method[] methods = getClassMethods(cls);
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String name = method.getName();
            if (name.startsWith("get") && name.length() > 3) {
                if (method.getParameterTypes().length == 0) {
                    name = dropCase(name);
                    addGetMethod(name, method);
                }
            } else if (name.startsWith("is") && name.length() > 2) {
                if (method.getParameterTypes().length == 0) {
                    name = dropCase(name);
                    addGetMethod(name, method);
                }
            }
        }
    }
    /**
     * 添加get方法信息
     * @param name
     * @param method
     */
    private void addGetMethod(String name, Method method) {
        getMethods.put(name, new MethodInvoker(method));
        getTypes.put(name, method.getReturnType());
    }

    /**
     * 添加set方法信息
     * @param cls
     */
    private void addSetMethods(Class cls) {
        Map conflictingSetters = new HashMap();
        Method[] methods = getClassMethods(cls);
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String name = method.getName();
            if (name.startsWith("set") && name.length() > 3) {
                if (method.getParameterTypes().length == 1) {
                    name = dropCase(name);
                    ///------------
                    addSetterConflict(conflictingSetters, name, method);
                    // addSetMethod(name, method);
                    ///------------
                }
            }
        }
        resolveSetterConflicts(conflictingSetters);
    }

    private void addSetterConflict(Map conflictingSetters, String name, Method method) {
        List list = (List) conflictingSetters.get(name);
        if (list == null) {
            list = new ArrayList();
            conflictingSetters.put(name, list);
        }
        list.add(method);
    }

    private void resolveSetterConflicts(Map conflictingSetters) {
        for (Iterator propNames = conflictingSetters.keySet().iterator(); propNames.hasNext();) {
            String propName = (String) propNames.next();
            List setters = (List) conflictingSetters.get(propName);
            Method firstMethod = (Method) setters.get(0);
            if (setters.size() == 1) {
                addSetMethod(propName, firstMethod);
            } else {
                Class expectedType = (Class) getTypes.get(propName);
                if (expectedType == null) {
                    throw new ServiceException("Illegal overloaded setter method with ambiguous type for property "
                            + propName + " in class " + firstMethod.getDeclaringClass() + ".  This breaks the JavaBeans " +
                            "specification and can cause unpredicatble results.");
                } else {
                    Iterator methods = setters.iterator();
                    Method setter = null;
                    while (methods.hasNext()) {
                        Method method = (Method) methods.next();
                        if (method.getParameterTypes().length == 1
                                && expectedType.equals(method.getParameterTypes()[0])) {
                            setter = method;
                            break;
                        }
                    }
                    if (setter == null) {
                        throw new ServiceException("Illegal overloaded setter method with ambiguous type for property "
                                + propName + " in class " + firstMethod.getDeclaringClass() + ".  This breaks the JavaBeans " +
                                "specification and can cause unpredicatble results.");
                    }
                    addSetMethod(propName, setter);
                }
            }
        }
    }

    /**
     *
     * @Description: 添加set方法
     * @Title: addSetMethod
     * @param name
     * @param method
     * @return void
     */
    private void addSetMethod(String name, Method method) {
        setMethods.put(name, new MethodInvoker(method));
        setTypes.put(name, method.getParameterTypes()[0]);
    }
    /**
     *
     * @Description: 添加字段
     * @Title:
     * @param clazz
     * @return
     */
    private void addFields(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (canAccessPrivateMethods()) {
                try {
                    field.setAccessible(true);
                } catch (Exception e) {
                    // Ignored. This is only a final precaution, nothing we can do.
                }
            }
            if (field.isAccessible()) {
                if (!setMethods.containsKey(field.getName())) {
                    addSetField(field);
                }
                if (!getMethods.containsKey(field.getName())) {
                    addGetField(field);
                }
            }
        }
        if (clazz.getSuperclass() != null) {
            addFields(clazz.getSuperclass());
        }
    }

    private void addSetField(Field field) {
        setMethods.put(field.getName(), new FieldInvoker(field));
        setTypes.put(field.getName(), field.getType());
    }

    private void addGetField(Field field) {
        getMethods.put(field.getName(), new FieldInvoker(field));
        getTypes.put(field.getName(), field.getType());
    }

    /**
     * 此方法返回一个包含所有方法的数组
     * 在这个类和任何超类中声明。
     * 我们使用这种方法，而不是简单的Class.getMethods（），
     * 因为我们也想寻找私人方法。
     *
     * @param cls The class
     * @return 一个包含此类中所有方法的数组
     */
    private Method[] getClassMethods(Class cls) {
        HashMap uniqueMethods = new HashMap();
        Class currentClass = cls;
        while (currentClass != null) {
            addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());

            // we also need to look for interface methods -
            // because the class may be abstract
            Class[] interfaces = currentClass.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                addUniqueMethods(uniqueMethods, interfaces[i].getMethods());
            }

            currentClass = currentClass.getSuperclass();
        }

        Collection methods = uniqueMethods.values();

        return (Method[]) methods.toArray(new Method[methods.size()]);
    }

    private void addUniqueMethods(HashMap uniqueMethods, Method[] methods) {
        for (Method currentMethod : methods) {
            if (!currentMethod.isBridge()) {
                String signature = getSignature(currentMethod);
                // check to see if the method is already known
                // if it is known, then an extended class must have
                // overridden a method
                if (!uniqueMethods.containsKey(signature)) {
                    if (canAccessPrivateMethods()) {
                        try {
                            currentMethod.setAccessible(true);
                        } catch (Exception e) {
                            // Ignored. This is only a final precaution, nothing we can do.
                        }
                    }

                    uniqueMethods.put(signature, currentMethod);
                }
            }
        }
    }

    private String getSignature(Method method) {
        StringBuffer sb = new StringBuffer();
        sb.append(method.getName());
        Class[] parameters = method.getParameterTypes();

        for (int i = 0; i < parameters.length; i++) {
            if (i == 0) {
                sb.append(':');
            } else {
                sb.append(',');
            }
            sb.append(parameters[i].getName());
        }

        return sb.toString();
    }

    private static String dropCase(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else {
            throw new ServiceException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
        }

        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.US) + name.substring(1);
        }

        return name;
    }

    private static boolean canAccessPrivateMethods() {
        try {
            SecurityManager securityManager = System.getSecurityManager();
            if (null != securityManager) {
                securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
            }
        } catch (SecurityException e) {
            return false;
        }
        return true;
    }

    /**
     * 获取类的名称
     *
     * @return The class name
     */
    public String getClassName() {
        return className;
    }

    public Object instantiateClass() {
        if (defaultConstructor != null) {
            try {
                return defaultConstructor.newInstance(null);
            } catch (Exception e) {
                throw new RuntimeException("Error instantiating class. Cause: " + e, e);
            }
        } else {
            throw new RuntimeException("Error instantiating class.  There is no default constructor for class " + className);
        }
    }

    /**
     * 获取属性作为Method对象的setter
     *
     * @param propertyName - the property
     * @return The Method
     */
    public Method getSetter(String propertyName) {
        Invoker method = (Invoker) setMethods.get(propertyName);
        if (method == null) {
            throw new ServiceException("There is no WRITEABLE property named '" + propertyName + "' in class '" + className + "'");
        }
        if (!(method instanceof MethodInvoker)) {
            throw new ServiceException("Can't get setter method because '" + propertyName + "' is a field in class '" + className + "'");
        }
        return ((MethodInvoker) method).getMethod();
    }

    /**
     * 获取属性作为Method对象的getter
     *
     * @param propertyName - the property
     * @return The Method
     */
    public Method getGetter(String propertyName) {
        Invoker method = (Invoker) getMethods.get(propertyName);
        if (method == null) {
            throw new ServiceException("There is no READABLE property named '" + propertyName + "' in class '" + className + "'");
        }
        if (!(method instanceof MethodInvoker)) {
            throw new ServiceException("Can't get getter method because '" + propertyName + "' is a field in class '" + className + "'");
        }
        return ((MethodInvoker) method).getMethod();
    }

    public Invoker getSetInvoker(String propertyName) {
        Invoker method = (Invoker) setMethods.get(propertyName);
        if (method == null) {
            throw new ServiceException("There is no WRITEABLE property named '" + propertyName + "' in class '" + className + "'");
        }
        return method;
    }

    public Invoker getGetInvoker(String propertyName) {
        Invoker method = (Invoker) getMethods.get(propertyName);
        if (method == null) {
            throw new ServiceException("There is no READABLE property named '" + propertyName + "' in class '" + className + "'");
        }
        return method;
    }

    /**
     * 获取set方法的类型
     *
     * @param propertyName 属性名称
     * @return
     */
    public Class getSetterType(String propertyName) {
        Class clazz = (Class) setTypes.get(propertyName);
        if (clazz == null) {
            throw new ServiceException("There is no WRITEABLE property named '" + propertyName + "' in class '" + className + "'");
        }
        return clazz;
    }

    /**
     * Gets the type for a property getter
     *
     * @param propertyName - the name of the property
     * @return The Class of the propery getter
     */
    public Class getGetterType(String propertyName) {
        Class clazz = (Class) getTypes.get(propertyName);
        if (clazz == null) {
            throw new ServiceException("There is no READABLE property named '" + propertyName + "' in class '" + className + "'");
        }
        return clazz;
    }

    /**
     * 获取对象的可读属性数组
     *
     * @return The array
     */
    public String[] getReadablePropertyNames() {
        return readablePropertyNames;
    }

    /**
     * 获取对象的可写属性数组
     *
     * @return The array
     */
    public String[] getWriteablePropertyNames() {
        return writeablePropertyNames;
    }

    /**
     * 检查一个类是否具有可写属性的名称
     *
     * @param propertyName - 属性名称
     * @return
     */
    public boolean hasWritableProperty(String propertyName) {
        return setMethods.containsKey(propertyName);
    }

    /**
     * 检查一个类是否具有名称可读属性
     *
     * @param propertyName 属性名称
     * @return 如果对象具有名称可读属性，则为true
     */
    public boolean hasReadableProperty(String propertyName) {
        return getMethods.keySet().contains(propertyName);
    }

    /**
     * 告诉我们传入的类是否是已知的常见类型
     *
     * @param clazz
     * @return
     */
    public static boolean isKnownType(Class clazz) {
        if (SIMPLE_TYPE_SET.contains(clazz)) {
            return true;
        } else if (Collection.class.isAssignableFrom(clazz)) {
            return true;
        } else if (Map.class.isAssignableFrom(clazz)) {
            return true;
        } else if (List.class.isAssignableFrom(clazz)) {
            return true;
        } else if (Set.class.isAssignableFrom(clazz)) {
            return true;
        } else if (Iterator.class.isAssignableFrom(clazz)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取类的信息实例
     *
     * @param clazz 要获取的类信息的class
     * @return
     */
    public static ClassInfo getInstance(Class clazz) {
        if (cacheEnabled) {//是否启用缓存
            synchronized (clazz) {
                ClassInfo cached = (ClassInfo) CLASS_INFO_MAP.get(clazz);
                if (cached == null) {
                    cached = new ClassInfo(clazz);
                    CLASS_INFO_MAP.put(clazz, cached);
                }
                return cached;
            }
        } else {
            return new ClassInfo(clazz);
        }
    }

    public static void setCacheEnabled(boolean cacheEnabled) {
        ClassInfo.cacheEnabled = cacheEnabled;
    }

    /**
     * 检查一个Throwable对象，并得到它的根本原因
     *
     * @param t
     * @return
     */
    public static Throwable unwrapThrowable(Throwable t) {
        Throwable t2 = t;
        while (true) {
            if (t2 instanceof InvocationTargetException) {
                t2 = ((InvocationTargetException) t).getTargetException();
            } else if (t instanceof UndeclaredThrowableException) {
                t2 = ((UndeclaredThrowableException) t).getUndeclaredThrowable();
            } else {
                return t2;
            }
        }
    }

    /**
     *
     * @Description: 根据属性列表获取class类型
     * @Title: getTypes
     * @param info class信息类
     * @param propertyNames 属性列表
     * @return java.lang.Class[]
     */
    protected static Class[] getTypes(ClassInfo info,String[] propertyNames) {
        Class[] types = new Class[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++) {
            types[i] = info.getGetterType(propertyNames[i]);
        }
        return types;
    }
    
    /**
     *
     * @Description: 根据属性列表获取set方法名称
     * @Title: getSetterNames
     * @param info class信息类
     * @param propertyNames 属性列表
     * @return java.lang.String[]
     */
    protected static String[] getSetterNames(ClassInfo info,String[] propertyNames) {
        String[] names = new String[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++) {
            names[i] = info.getSetter(propertyNames[i]).getName();
        }
        return names;
    }
    /**
     *
     * @Description: 根据属性列表获取get方法名称
     * @Title: getGetterNames
     * @param info class信息类
     * @param propertyNames 属性列表
     * @return java.lang.String[]
     */
    protected static String[] getGetterNames(ClassInfo info,String[] propertyNames) {
        String[] names = new String[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++) {
            names[i] = info.getGetter(propertyNames[i]).getName();
        }
        return names;
    }
}




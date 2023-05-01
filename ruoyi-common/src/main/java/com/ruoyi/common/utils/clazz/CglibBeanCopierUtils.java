package com.ruoyi.common.utils.clazz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.cglib.beans.BulkBean;

import java.beans.PropertyDescriptor;
import java.util.*;


/**
 *
 * 将beancopier做成静态类，方便拷贝,赋值的类必须实现Serializable
 * @version  v 1.0
 */
public class CglibBeanCopierUtils {
    private static Logger logger = LoggerFactory.getLogger(CglibBeanCopierUtils.class);
    /**
     *
     */
    public static Map<String, BeanCopier> beanCopierMap = new HashMap<String, BeanCopier>();

    /**
     * @param source 资源类
     * @param target 目标类
     * @Title: copyProperties
     * @Description: bean属性转换
     */
    public static void copyProperties( Object target, Object source) {
        String beanKey = generateKey(source.getClass(), target.getClass());
        BeanCopier copier = null;
        if (!beanCopierMap.containsKey(beanKey)) {
            copier = BeanCopier.create(source.getClass(), target.getClass(), false);
            beanCopierMap.put(beanKey, copier);
        } else {
            copier = beanCopierMap.get(beanKey);
        }
        copier.copy(source, target, null);
    }

    private static String generateKey(Class<?> class1, Class<?> class2) {
        return class1.toString() + class2.toString();
    }
    /*注：
    (1)相同属性名，且类型不匹配时候的处理，ok，但是未满足的属性不拷贝；
    (2)get和set方法不匹配的处理，创建拷贝的时候报错，无法拷贝任何属性(当且仅当sourceClass的get方法超过set方法时出现)
    (3)BeanCopier
    初始化例子：BeanCopier copier = BeanCopier.create(Source.class, Target.class, useConverter=true)
    第三个参数userConverter,是否开启Convert,默认BeanCopier只会做同名，同类型属性的copier,否则就会报错.
    copier = BeanCopier.create(source.getClass(), target.getClass(), false);
    copier.copy(source, target, null);
    (4)修复beanCopier对set方法强限制的约束
    改写net.sf.cglib.beans.BeanCopier.Generator.generateClass(ClassVisitor)方法
    将133行的
    MethodInfo write = ReflectUtils.getMethodInfo(setter.getWriteMethod());
    预先存一个names2放入
     109        Map names2 = new HashMap();
     110        for (int i = 0; i < getters.length; ++i) {
     111          names2.put(setters[i].getName(), getters[i]);
                }
    调用这行代码前判断查询下，如果没有改writeMethod则忽略掉该字段的操作，这样就可以避免异常的发生。*/




    /**
     * @param source        要复制属性到的对象
     * @param target        原对象
     * @param propertyNames 要复制对象的属性名称，当isInequality为false时候，用:号分隔两个不同的属性,前面的是target的属性，后面的是source属性
     * @return void
     * @Description: 复制所有属性，并复制特殊属性
     * @Title: copyAllProperties
     */
    public static void copyAllProperties(Object target, Object source, String[] propertyNames) {
        copyProperties(target, source);
        copyProperties(target, source, propertyNames);
    }

    /**
     * 复制指定对象属性
     *
     * @param source        要复制属性到的对象
     * @param target        原对象
     * @param propertyNames 要复制对象的属性名称，当isInequality为false时候，用:号分隔两个不同的属性,前面的是target的属性，后面的是source属性
     */
    public static void copyProperties(Object target, Object source,  String[] propertyNames) {

            ClassInfo sourceInfo = ClassInfo.getInstance(source.getClass());
            ClassInfo targetInfo = ClassInfo.getInstance(target.getClass());
            String[] getNames = new String[propertyNames.length];
            String[] setNames = new String[propertyNames.length];
            Class[] types = new Class[propertyNames.length];
            String[] sourceGetNames = new String[propertyNames.length];
            String[] sourceSetNames = new String[propertyNames.length];
            Class[] sourceTypes = new Class[propertyNames.length];

            for (int i = 0; i < propertyNames.length; i++) {
                String property = propertyNames[i];
                if (property.indexOf(":") == -1) {
                    String getName = sourceInfo.getGetter(property).getName();
                    String setName = sourceInfo.getSetter(property).getName();
                    getNames[i] = getName;
                    setNames[i] = setName;
                    types[i] = targetInfo.getGetterType(property);

                    sourceGetNames[i] = getName;
                    sourceSetNames[i] = setName;
                    sourceTypes[i] = sourceInfo.getGetterType(property);
                } else {
                    String[] names = property.split(":");
                    getNames[i] = targetInfo.getGetter(names[0]).getName();
                    setNames[i] = targetInfo.getSetter(names[0]).getName();
                    types[i] = targetInfo.getGetterType(names[0]);

                    sourceGetNames[i] = sourceInfo.getGetter(names[1]).getName();
                    sourceSetNames[i] = sourceInfo.getSetter(names[1]).getName();
                    sourceTypes[i] = sourceInfo.getGetterType(names[1]);
                }

            }
            BulkBean sourceBean = BulkBean.create(source.getClass(), sourceGetNames, sourceSetNames, sourceTypes);
            Object[] objs = sourceBean.getPropertyValues(source);


            for(int i = 0 ; i < objs.length ; i++){
                objs[i] = ClassUtil.formatObject(objs[i],true,types[i]);
            }

            BulkBean targetBean = BulkBean.create(target.getClass(), getNames, setNames, types);
            targetBean.setPropertyValues(target, objs);
    }

    public static Object mapToBean(Object  target, Map source, String[] propertyNames) {
        ClassInfo sourceInfo = ClassInfo.getInstance(target.getClass());
        String[] sourceGetNames = new String[propertyNames.length];
        String[] sourceSetNames = new String[propertyNames.length];
        Class[] sourceTypes = new Class[propertyNames.length];
        Object[] objs = new Object[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++) {
            String property = propertyNames[i];
            String key = null;
            if (property.indexOf(":") == -1) {
                String getName = sourceInfo.getGetter(property).getName();
                String setName = sourceInfo.getSetter(property).getName();
                Class clazz = sourceInfo.getGetterType(property);
                sourceGetNames[i] = getName;
                sourceSetNames[i] = setName;
                sourceTypes[i] = clazz;
                key = property;
            } else {
                String[] names = property.split(":");
                sourceGetNames[i] = sourceInfo.getGetter(names[0]).getName();
                sourceSetNames[i] = sourceInfo.getSetter(names[0]).getName();
                sourceTypes[i] = sourceInfo.getGetterType(names[0]);
                key = names[1];
            }
            objs[i] = ClassUtil.formatObject(source.get(key),true,sourceTypes[i]);
        }
        BulkBean sourceBean = BulkBean.create(target.getClass(), sourceGetNames, sourceSetNames, sourceTypes);
        sourceBean.setPropertyValues(target, objs);
        return target;
    }

    /**
     * 复制指定对象属性
     *
     * @param targetClass 要复制属性到的对象
     * @param source        原对象
     * @param propertyNames 要复制对象的属性名称
     */
    public static <T> T mapToBean(Class<T>  targetClass, Map source, String[] propertyNames) {
        try {
            return (T) mapToBean(targetClass.newInstance(),source,propertyNames);
        } catch (InstantiationException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * bean对象转Map对象
     * @param bean
     * @return
     */
    public static Map<String, Object> beanToMap(Object bean) {
        Map<String, Object> beanMap = new HashMap<>(16);
        if (bean != null) {
            BeanMap tempMap = BeanMap.create(bean);
            for (Object key : tempMap.keySet()) {
                String putKey = String.valueOf(key);
                Object putValue = tempMap.get(key);
                beanMap.put(putKey, putValue);
            }
        }
        return beanMap;
    }

    /**
     * bean对象转Map对象
     * @param bean
     * @return
     */
    public static Map<String, String> beanToStringMap(Object bean) {
        Map<String, String> beanMap = new HashMap<>(16);
        if (bean != null) {
            BeanMap tempMap = BeanMap.create(bean);
            for (Object key : tempMap.keySet()) {
                String putKey = String.valueOf(key);
                Object putValue = tempMap.get(key);
                beanMap.put(putKey, ClassUtil.objToString(putValue));
            }
        }
        return beanMap;
    }
    /**
     *
     * @Title: beanListToMapList
     * @Description: List<T>转换为List<Map<String,Object>>
     * @param beanList
     * @return
     */
    public static List<Map<String, Object>> beanToMapBatch(List beanList) {
        List<Map<String, Object>> beanMapList = new ArrayList<>(16);
        if (beanList != null && beanList.size() > 0) {
            for (Object bean : beanList) {
                Map<String, Object> beanMap = beanToMap(bean);
                beanMapList.add(beanMap);
            }
        }
        return beanMapList;
    }

    /**
     *
     * @Title: mapToBean
     * @Description: map转javabean
     * @param beanMap
     * @param beanClass
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> T mapToBean(Map<String, Object> beanMap, Class<T> beanClass)throws InstantiationException, IllegalAccessException {
        T bean = beanClass.newInstance();
        return mapToBean(beanMap,bean);
    }

    /**
     *
     * @Title: mapToBean
     * @Description: map转javabean
     * @param beanMap
     * @param bean
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> beanMap, T bean) {
        ClassInfo targetInfo = ClassInfo.getInstance(bean.getClass());
        Set<Map.Entry<String, Object>> mapSet = beanMap.entrySet();
        Map<String,Object> temp = new HashMap<>(beanMap.size());
        for(Map.Entry<String,Object> entry : mapSet){
            if(targetInfo.hasWritableProperty(entry.getKey())) {
                temp.put(entry.getKey(), ClassUtil.formatObject(entry.getValue(), targetInfo.getSetterType(entry.getKey())));
            }
        }
        BeanMap tempMap = BeanMap.create(bean);
        tempMap.putAll(temp);
        return bean;
    }


    /**
     *
     * @Title: mapToBeanBatch
     * @Description: List<Map<String,Object>>转List<T>
     * @param beanMap
     * @param beanClass
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T> List<T> mapToBeanBatch(List<Map<String, Object>> beanMap, Class<T> beanClass)
            throws InstantiationException, IllegalAccessException {
        List<T> beanList = new ArrayList<>(16);
        if (beanMap != null && beanMap.size() > 0) {
            for (Map<String, Object> tempMap : beanMap) {
                T bean = mapToBean(tempMap, beanClass);
                beanList.add(bean);
            }
        }
        return beanList;
    }

    public static String[] getNullPropertyNames(Object source){
        final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Object propertyValue = beanWrapper.getPropertyValue(propertyDescriptor.getName());
            if (propertyValue==null){
                emptyNames.add(propertyDescriptor.getName());
            }
        }
        String[] strings = new String[emptyNames.size()];
        return emptyNames.toArray(strings);
    }

    public static void copyPropertiesIgnoreNull(Object target,Object source){
        BeanUtils.copyProperties(source,target,getNullPropertyNames(source));
    };
}


package com.ruoyi.common.utils.clazz;

import com.ruoyi.common.annotation.DecryptField;
import com.ruoyi.common.annotation.Detector;
import com.ruoyi.common.utils.DecryptContext;
import com.ruoyi.common.utils.SpringContextUtil;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/1,13:08
 * @return:
 **/
public class ClassFieldMetaInfoWithDecrypter {

    private Map<String,Field> map=new HashMap<>();
    private List<Field> fieldsWithAnnotation=new LinkedList<>();

    public static void isDetectorPresent(Object o, MethodParameter parameter){

        Detector detecor;
        if (o.getClass().isAnnotationPresent(Detector.class)){
            detecor=o.getClass().getAnnotation(Detector.class);
        }else if (parameter.hasParameterAnnotation(Detector.class)){
            detecor=parameter.getParameterAnnotation(Detector.class);
        }else {
            return ;
        }
        assert detecor != null;

        Class<?> aClass = detecor.preContext();
        DecryptContext context = (DecryptContext)SpringContextUtil.getBean(aClass);

        assert context != null;
        if (!context.needDecrypt(o,parameter)){
            return;
        }
        context.startDecrypt(o);
    }




    public ClassFieldMetaInfoWithDecrypter(Class<?> clz){
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            map.put(field.getName(),field);
            if (field.isAnnotationPresent(DecryptField.class)){
                field.setAccessible(true);
                fieldsWithAnnotation.add(field);
            }
        }
    }


    public void decryptFields(Object o,DecryptContext context) throws Exception {

        for (Field field : this.fieldsWithAnnotation) {

            Object decrypt = context.decrypt((String) field.get(o),field);
            field.set(o,decrypt);
        }
    }

    public Field getFieldByName(String name) {
        return map.get(name);
    }

}

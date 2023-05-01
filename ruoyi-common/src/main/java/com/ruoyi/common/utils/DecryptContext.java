package com.ruoyi.common.utils;

import com.ruoyi.common.bussiness.ContextListener;
import com.ruoyi.common.utils.clazz.ClassFieldMetaInfoWithDecrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/6,16:10
 * @return:
 **/
public class DecryptContext {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ConcurrentHashMap<Class<?>, ClassFieldMetaInfoWithDecrypter> classMetaInfo=new ConcurrentHashMap<>();

    private Map<Class<?>, ContextListener> listeners;

    public DecryptContext(){
        listeners=new HashMap<>(4);
    }



    public void addListener(Class<?> clz,ContextListener contextListener){
        this.listeners.put(clz,contextListener);
    }

    protected ContextListener getListener(Class<?> clz){
        return this.listeners.get(clz);
    }

    public boolean needDecrypt(Object o, MethodParameter parameter){
        return true;
    };
    /**
     * 准备解密的上下文
     */
    public void startContext(){};

    /**
     * 开始对字段解密
     * @param encrypt 密文
     * @param field 字段
     * @return 明文
     */
    public  Object decrypt(String encrypt, Field field){
        return encrypt;
    };

    /**
     *解密之后要处理的内容
     * @param o 已经对字段解密后的对象;
     */
    public void afterDecryptFields(Object o){

    };


    public <T> T getObject(){
        return null;
    }


    public void addMetaInfo(Class<?> clz){
        classMetaInfo.put(clz,new ClassFieldMetaInfoWithDecrypter(clz));
    }
    /**
     * 开始进行字段解密
     * @param body
     */
    public void startDecrypt(Object body){
        ContextListener listener = getListener(body.getClass());
        if (listener!=null){
            listener.beforeDecrypt(body,this);
        }
        //准备上下文
        startContext();
        //解析类信息
        ClassFieldMetaInfoWithDecrypter classFieldMetaInfoWithDecrypter;
        if (!classMetaInfo.containsKey(body.getClass())){
            classFieldMetaInfoWithDecrypter = new ClassFieldMetaInfoWithDecrypter(body.getClass());
            classMetaInfo.put(body.getClass(),classFieldMetaInfoWithDecrypter);
        }else {
            classFieldMetaInfoWithDecrypter = classMetaInfo.get(body.getClass());
        }

        //开始解密
        try {
            classFieldMetaInfoWithDecrypter.decryptFields(body,this);

        } catch (Exception e) {
            logger.error("解密字段的时候失出现错误",e);
            return;
        }

        //结束解密;
        afterDecryptFields(body);
    }

}

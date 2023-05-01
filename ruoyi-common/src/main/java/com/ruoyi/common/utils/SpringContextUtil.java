package com.ruoyi.common.utils;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @description: 上下文工具类
 * @create: 2019-02-20 15:00
 **/
@Lazy(false)
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static Logger logger = LoggerFactory.getLogger(SpringContextUtil.class);
    private static ApplicationContext context = null;

    /* (non Javadoc)
     * @Title: setApplicationContext
     * @Description: spring获取bean工具类
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        logger.info("当前环境变量是：", JSON.toJSONString(applicationContext.getEnvironment().getActiveProfiles()));
        this.context = applicationContext;
    }
    public static void setContext(ApplicationContext applicationContext)
            throws BeansException {
        logger.info("当前环境变量是：", JSON.toJSONString(applicationContext.getEnvironment().getActiveProfiles()));
        context = applicationContext;
    }
    /*获取spring 的 ApplicationContext*/
    public static ApplicationContext getApplicationContext(){
        return context;
    }

    /*获取Annotation 注解的类*/
    public static String[] getBeanNamesForAnnotation(Class<? extends Annotation>  clazz){
        return getApplicationContext().getBeanNamesForAnnotation(clazz);
    }

    // 传入线程中
    public static <T> T getBean(String beanName) {
        Object result = context.getBean(beanName);
        if(result != null && result.getClass().getName().equals("org.springframework.beans.factory.support.NullBean")){
            return null;
        }
        return (T) result;
    }

    // 传入线程中
    public static <E> E getBean(Class<E> clazz) {
        Object result = context.getBean(clazz);
        if(result != null && result.getClass().getName().equals("org.springframework.beans.factory.support.NullBean")){
            return null;
        }
        return (E) result;
    }

    // 国际化使用
    public static String getMessage(String key) {
        return context.getMessage(key, null, Locale.getDefault());
    }

    // 获取当前环境
    public static String[] getActiveProfiles() {
        return context.getEnvironment().getActiveProfiles();
    }

    // 判断当前环境是否为test/local
    public static boolean isTestEnv(){
        String[] activeProfiles = getActiveProfiles();
        if (activeProfiles.length<1){
            return false;
        }

        for (String activeProfile : activeProfiles) {
            if (StringUtils.equals(activeProfile,"local")||
                    StringUtils.equals(activeProfile,"dev") || StringUtils.equals(activeProfile,"dev2")
                    || StringUtils.equals(activeProfile,"test")){
                return true;
            }
        }
        return false;
    }

    public static boolean containsBean(String name) {
        return context.containsBean(name);
    }

    public static void registerBean(String beanName, Class clazz){
        registerBean(beanName,null,clazz);
    }

    public static void registerBean(String beanName, Map<String,Object> parameter, Class clazz){
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) context;
        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        // 通过BeanDefinitionBuilder创建bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        if(parameter != null && !parameter.isEmpty()) {
            Set<Map.Entry<String,Object>> entrySet = parameter.entrySet();
            for(Map.Entry<String,Object> entry : entrySet) {
                beanDefinitionBuilder.addPropertyValue(entry.getKey(), entry.getValue());
            }
        }
        // 注册bean
        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());
    }
}

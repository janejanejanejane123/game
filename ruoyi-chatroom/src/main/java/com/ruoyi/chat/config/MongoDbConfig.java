package com.ruoyi.chat.config;

import com.ruoyi.common.utils.SpringContextUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

import javax.annotation.Resource;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/12/6,16:16
 * @return:
 **/
@Configuration
public class MongoDbConfig {


    @Bean("mongoTransactionManager")
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory mongoDatabaseFactory){
        return new MongoTransactionManager(mongoDatabaseFactory);
    }


}

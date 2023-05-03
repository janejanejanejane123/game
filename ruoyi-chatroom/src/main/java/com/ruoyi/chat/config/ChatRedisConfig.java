package com.ruoyi.chat.config;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.SpringContextUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.Resource;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/9/25,13:02
 * @return:
 **/
@Configuration
public class ChatRedisConfig {

    @Resource
    private BootstrapConfig config;


    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                       MessageListener pushMessageListener,
                                                                       MessageListener bindMessageListener){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(pushMessageListener,new ChannelTopic(Constants.PUSH_MESSAGE_INNER_QUEUE));
        container.addMessageListener(bindMessageListener,new ChannelTopic(Constants.BIND_MESSAGE_INNER_QUEUE));
        return container;
    }

}

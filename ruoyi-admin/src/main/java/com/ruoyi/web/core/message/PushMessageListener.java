package com.ruoyi.web.core.message;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;


/**
 * 集群环境下，监听redis队列，广播消息到每个实例进行推送
 * 如果使用MQ的情况也，最好替换为MQ消息队列
 */
@Component
public class PushMessageListener implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onMessage(Message redisMessage, byte[] bytes) {
        logger.info("收到消息{}", JSON.toJSONString(redisMessage));
    }
}

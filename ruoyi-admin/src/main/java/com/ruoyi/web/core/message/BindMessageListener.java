package com.ruoyi.web.core.message;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * 集群环境下，监控多设备登录情况，控制是否其余终端下线的逻辑
 */
@Component
public class BindMessageListener implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onMessage(Message redisMessage, byte[] bytes) {
        logger.info("收到消息{}", JSON.toJSONString(redisMessage));
    }
}

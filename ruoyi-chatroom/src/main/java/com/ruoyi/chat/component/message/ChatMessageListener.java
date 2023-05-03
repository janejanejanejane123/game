package com.ruoyi.chat.component.message;

import com.ruoyi.chat.component.group.MessageReceiveService;
import com.ruoyi.common.core.domain.model.chat.MesEvent;
import com.ruoyi.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;

/**
 * 集群环境下的广播消息监听器
 * @param
 * @Author: sddd
 * @Date: 2022/7/10,13:28
 * @return:
 **/
@Slf4j
@Lazy(false)
@RocketMQMessageListener(messageModel= MessageModel.BROADCASTING,topic = "chatMessage", selectorExpression = "messEvent", consumerGroup = "chat_group")
public class ChatMessageListener implements RocketMQListener<MesEvent> {


    @Resource
    private MessageReceiveService messageReceiveService;

    @Override
    public void onMessage(MesEvent message) {
        log.info("聊天收到消息:{}",message);

        messageReceiveService.receiveAndHandleMassage(message);

    }
}

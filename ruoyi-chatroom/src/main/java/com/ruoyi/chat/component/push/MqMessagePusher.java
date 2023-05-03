package com.ruoyi.chat.component.push;

import com.alibaba.fastjson.JSON;
import com.ruoyi.chat.component.message.FriendChatAckProcess;
import com.ruoyi.common.core.domain.model.chat.MesEvent;
import com.ruoyi.common.core.domain.model.chat.OfflineAck;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/10,14:02
 * @return:
 **/
@Component
@Slf4j
public class MqMessagePusher {

    @Resource
    private RocketMQTemplate rocketMQTemplate;


    public void sendMessage(MesEvent msg){
        //通过MQ广播消息到所有节点;
        SendResult result = rocketMQTemplate.syncSend("chatMessage:messEvent", msg);
        if(!SendStatus.SEND_OK.equals(result.getSendStatus())){
            Assert.error("message.send.error");
        }else {
            log.info("发送消息成功 :{}",JSON.toJSONString(msg) );
        }
    }

}

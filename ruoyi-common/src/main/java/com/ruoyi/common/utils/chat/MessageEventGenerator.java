package com.ruoyi.common.utils.chat;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.constant.ChatRoomConstants;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.chat.MesEvent;
import com.ruoyi.common.core.domain.model.chat.OfflineAck;
import com.ruoyi.common.core.domain.model.chat.enums.MessageTypeEnum;
import com.ruoyi.common.enums.ChatTopic;
import com.ruoyi.common.enums.PayResponEnums;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/23,20:26
 * @return:
 **/
@Slf4j
public class MessageEventGenerator {

    public static void sendOnWebContext(MesEvent msg){
        RocketMQTemplate rocketMQTemplate = SpringContextUtil.getBean(RocketMQTemplate.class);
        assert rocketMQTemplate!=null;

        SendResult result = rocketMQTemplate.syncSend("chatMessage:messEvent",msg);
        if(!SendStatus.SEND_OK.equals(result.getSendStatus())){
            Assert.error("message.send.error");
        }else {
            log.info("发送消息成功 on web:{}", JSON.toJSONString(msg));
        }
    }

    public static void offlineAck(OfflineAck offlineAck){
        MesEvent mesEvent = new MesEvent();
        List<MesEvent> mesEvents = getMesEventChain();
        mesEvent.setContent(offlineAck);
        mesEvent.setType(MessageTypeEnum.ACK);
        mesEvents.add(mesEvent);
        tryPublish(mesEvent);
    }
    /**
     * 给自己发消息;
     * @param msg 内容
     */
    public static void message2All(Object msg, ChatTopic topic){
        List<MesEvent> mesEvents = getMesEventChain();
        MesEvent mesEvent = new MesEvent();
        mesEvent.setContent(msg);
        mesEvent.setTopic(topic.value());
        mesEvent.setSendTime(new Date());
        mesEvent.setType(MessageTypeEnum.ALL);
        mesEvent.setRecipient(ChatRoomConstants.MESSAGE_RECIPIENT_ALL);
        mesEvents.add(mesEvent);
        tryPublish(mesEvent);
    }
    /**
     * 给自己发消息;
     * @param msg 内容
     */
    public static void message2Self(Object msg,ChatTopic topic){
        List<MesEvent> mesEvents = getMesEventChain();
        MesEvent mesEvent = new MesEvent();
        mesEvent.setTopic(topic.value());
        mesEvent.setContent(msg);
        mesEvent.setSendTime(new Date());
        mesEvent.setType(MessageTypeEnum.SELF);
        mesEvents.add(mesEvent);
        tryPublish(mesEvent);
    }

    /**
     * @param senderIdentifier 发送者的标识
     * @param friendIdentifier 接收者的标识
     * @param msg 内容
     */
    public static void message2Somebody(String senderIdentifier,String friendIdentifier, Object msg,ChatTopic topic,Long nodeId){
        MesEvent mesEvent = new MesEvent();
        mesEvent.setContent(msg);
        mesEvent.setTopic(topic.value());
        mesEvent.setSendTime(new Date());
        mesEvent.setSendIdentifier(senderIdentifier);
        mesEvent.setRecipient(friendIdentifier);
        mesEvent.setNodeServer(nodeId);
        mesEvent.setType(MessageTypeEnum.PRIVATE);

        tryPublish(mesEvent);
    }


    private static List<MesEvent> getMesEventChain(){
        LinkedList<MesEvent> mesEvents = ChatContext.MES_EVENT_CHAIN.get();
        Assert.test(mesEvents==null,"chat.context.error",304);
        return mesEvents;
    }

    private static void tryPublish(MesEvent mesEvent){
        if (GeneralUtils.checkContext()){
            List<MesEvent> mesEventChain = getMesEventChain();
            mesEventChain.add(mesEvent);
        }else {
            Dispatcher dispatcher = SpringContextUtil.getBean(Dispatcher.class);
            if (dispatcher!=null){
                dispatcher.dispatch(mesEvent);
            }
        }
    }



    /**
     * 给聊天室发送消息;
     * @param msg 内容
     * @param typeEnum 类型
     * @param topic 主题

     * @see MessageEventGenerator#(Object, Long)
     * 后台直接向聊天室广播消息，不必输入当前用户
     *
     */
    public static void broadCastMessage2ChatRoom(Object msg, MessageTypeEnum typeEnum, ChatTopic topic){
        MesEvent mesEvent = new MesEvent();
        mesEvent.setType(typeEnum);
        mesEvent.setContent(msg);
        mesEvent.setTopic(topic.value());
        mesEvent.setSendTime(new Date());
        tryPublish(mesEvent);
    }

}

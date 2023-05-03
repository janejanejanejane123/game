package com.ruoyi.chat.component.handler.dispatch;

import com.ruoyi.common.core.domain.model.chat.enums.MessageTypeEnum;
import com.ruoyi.chat.component.group.ChatSessionGroupManager;
import com.ruoyi.chat.component.message.FriendChatAckProcess;
import com.ruoyi.chat.component.push.MqMessagePusher;
import com.ruoyi.common.core.domain.model.chat.ChatMessage;
import com.ruoyi.common.core.domain.model.chat.MesEvent;
import com.ruoyi.common.utils.chat.Dispatcher;
import com.ruoyi.common.utils.chat.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import javax.annotation.Resource;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/10,20:16
 * @return:
 **/
@Slf4j
public class ClusterMessageDispatcher implements Dispatcher {

    @Resource
    private MqMessagePusher mqMessagePusher;
    @Resource
    private ChatSessionGroupManager chatSessionGroupManager;
    @Resource
    private FriendChatAckProcess chatAckProcess;

    @Override
    public Logger getLogger() {
        return log;
    }

    /**
     * 给自己发送消息
     * @param mesEvent
     */
    @Override
    public void sendMsgToSelf(MesEvent mesEvent){
        ChatMessage chatMessage = covert(mesEvent);
        chatMessage.setType(2);
        chatSessionGroupManager.sendMsgToSelf(chatMessage);
    }


    /**
     * 给别人发送消息， 也给自己复制一份同样的消息;
     * @param mesEvent 消息对象
     */
    @Override
    public void sendPrivate(MesEvent mesEvent){
        ChatMessage chatMessage = covert(mesEvent);
        chatMessage.setType(2);
        //如果发送方和接收方在同一个节点
        if (GeneralUtils.getRunId().equals(mesEvent.getNodeServer())){
            //给自己和对面都发送
            chatSessionGroupManager.sendMes(chatMessage,mesEvent.getRecipient(),mesEvent.getSendIdentifier());
        }else {
            //一份给自己返回;
            chatSessionGroupManager.sendMsgToSelf(chatMessage);
            //一份给别的服务器;
            mqMessagePusher.sendMessage(mesEvent);
        }
    }

    @Override
    public void sendAck(MesEvent mesEvent) {
        try {
            chatAckProcess.chatAck(mesEvent);
        } catch (Exception e) {
            getLogger().error("本地ack消息确认失败",e);
        }
    }

    @Override
    public void sendBroadcasting(MesEvent mesEvent) {
        mqMessagePusher.sendMessage(mesEvent);
    }

    @Override
    public void sendAll(MesEvent mesEvent) {
        mqMessagePusher.sendMessage(mesEvent);
    }

}

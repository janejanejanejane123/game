package com.ruoyi.chat.component.handler.dispatch;

import com.ruoyi.chat.component.group.ChatSessionGroupManager;
import com.ruoyi.chat.component.message.FriendChatAckProcess;
import com.ruoyi.common.core.domain.model.chat.ChatMessage;
import com.ruoyi.common.core.domain.model.chat.MesEvent;
import com.ruoyi.common.core.domain.model.chat.enums.MessageTypeEnum;
import com.ruoyi.common.utils.chat.Dispatcher;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import javax.annotation.Resource;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/18,20:03
 * @return:
 **/
@Slf4j
public class MessageDispatcher implements Dispatcher {

    @Resource
    private ChatSessionGroupManager chatSessionGroupManager;
    @Resource
    private FriendChatAckProcess chatAckProcess;

    @Override
    public Logger getLogger() {
        return log;
    }


    @Override
    public void sendMsgToSelf(MesEvent mesEvent) {
        ChatMessage chatMessage = covert(mesEvent);
        chatSessionGroupManager.sendMsgToSelf(chatMessage);
    }

    @Override
    public void sendPrivate(MesEvent mesEvent) {
        ChatMessage chatMessage = covert(mesEvent);
        chatSessionGroupManager.sendMes(chatMessage,mesEvent.getRecipient(),mesEvent.getSendIdentifier());
    }

    @Override
    public void sendAck(MesEvent mesEvent) {
        try {
            chatAckProcess.chatAck(mesEvent);
        } catch (Exception e) {
            getLogger().error("单机 本地ack队列消息确认失败",e);
        }
    }

    @Override
    public void sendBroadcasting(MesEvent mesEvent) {
        ChatMessage chatMessage = covert(mesEvent);
        chatSessionGroupManager.sendMsgAllMerchant(chatMessage,null);
    }

    @Override
    public void sendAll(MesEvent mesEvent) {
        ChatMessage chatMessage = covert(mesEvent);
        chatSessionGroupManager.sendAll(chatMessage,null);
    }

}

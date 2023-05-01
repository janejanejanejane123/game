package com.ruoyi.common.utils.chat;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.core.domain.model.chat.ChatMessage;
import com.ruoyi.common.core.domain.model.chat.CommonContent;
import com.ruoyi.common.core.domain.model.chat.MesEvent;
import com.ruoyi.common.core.domain.model.chat.enums.MessageTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Assert;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/2,16:19
 * @return:
 **/
public interface Dispatcher {
    /**
     * 获取当前日志对象
     * @return Logger
     */
    Logger getLogger();

    /**
     * 转成返回前端的消息;
     * @param mesEvent mesEvent
     * @return ChatMessage
     */
    default ChatMessage covert(MesEvent mesEvent){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(2);
        CommonContent commonContent = GeneralUtils.createCommon(mesEvent, mesEvent.getTopic());
        chatMessage.setContent(JSON.toJSONString(commonContent));
        return chatMessage;
    }

    /**
     * 转发消息
     * @param mesEvent 消息对象
     */

    default void dispatch(MesEvent mesEvent){
        Assert.test(mesEvent.getType()==null,"system.error");

        switch (mesEvent.getType()) {
            case ACK:
                sendAck(mesEvent);
                break;
            case SELF:
                //传给自己;
                sendMsgToSelf(mesEvent);

                break;
            case PRIVATE:
                //私聊;
                sendPrivate(mesEvent);
                break;
            case CHATROOM:

                sendBroadcasting(mesEvent);
                break;
            case ALL:
                sendBroadcasting(mesEvent);
                break;
            default:
                break;
        }
    };

    default void data2Self(Object data){
        data2Self(data,"data_attach");
    };


    default void data2Self(Object data,String topic){
        MesEvent mesEvent = new MesEvent();
        mesEvent.setContent(data);
        mesEvent.setType(MessageTypeEnum.SELF);
        mesEvent.setTopic(topic);
        sendMsgToSelf(mesEvent);
    };

    default void dispatch(List<MesEvent> mesEvents){
        if (mesEvents.size()>0){
            for (MesEvent mesEvent : mesEvents) {
                dispatch(mesEvent);
            }
        }
    };


    default void errorMessage(Exception e){
        getLogger().error("chartRoom error",e);
        ChatMessage chatMessage = new ChatMessage();
        CommonContent commonContent = new CommonContent();
        chatMessage.setType(2);
        commonContent.setExtra("error_message");
        commonContent.setDate(new Date());
        if (e instanceof ServiceException){
            commonContent.setCode(((ServiceException) e).getCode());
            commonContent.setMessage(e.getMessage());
        }else {
            commonContent.setCode(500);
            commonContent.setMessage("系统错误!请联系管理员");
        }
        chatMessage.setContent(JSON.toJSONString(commonContent));
        GeneralUtils.getCurrentChannel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatMessage)));
    }


    void sendMsgToSelf(MesEvent mesEvent);

    void sendPrivate(MesEvent mesEvent);

    void sendAck(MesEvent mesEvent);

    void sendBroadcasting(MesEvent mesEvent);

    void sendAll(MesEvent mesEvent);
}

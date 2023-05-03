package com.ruoyi.chat.component.group;

import com.alibaba.fastjson.JSON;
import com.ruoyi.chat.config.CIMConfig;
import com.ruoyi.common.core.domain.model.chat.ChatMessage;
import com.ruoyi.common.core.domain.model.chat.CommonContent;
import com.ruoyi.common.core.domain.model.chat.MesEvent;
import com.ruoyi.common.core.domain.model.chat.enums.MessageTypeEnum;
import com.ruoyi.common.utils.chat.GeneralUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/10,15:11
 * @return:
 **/
@Component
public class MessageReceiveService  {


    @Resource
    private ChatSessionGroupManager chatSessionGroupManager;

    /**
     * 发送聊天室消息
     * @param mesEvent
     */
    public void receiveAndHandleMassage(MesEvent mesEvent){
        MessageTypeEnum action = mesEvent.getType();
        ChatMessage chatMessage = new ChatMessage();



        CommonContent commonContent = GeneralUtils.createCommon(mesEvent, mesEvent.getTopic());
        chatMessage.setContent(JSON.toJSONString(commonContent));
        chatMessage.setType(2);
        switch (action) {
            //发送聊天室消息
            case CHATROOM:

                chatSessionGroupManager.sendMsgAllMerchant(chatMessage,null);
                break;
            case ALL:
                chatSessionGroupManager.sendAll(chatMessage,null);
                break;
            case PRIVATE:
                if (mesEvent.getNodeServer()==null ||
                        !GeneralUtils.getRunId().equals(mesEvent.getNodeServer())) {
                    return;
                }
                //私聊发送
                chatSessionGroupManager.sendMes(mesEvent.getRecipient(), chatMessage);
                break;
            default:
        }
    }


}

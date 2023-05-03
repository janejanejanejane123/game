package com.ruoyi.chat.controller.socket;

import com.ruoyi.chat.component.handler.annotation.AppAsync;
import com.ruoyi.chat.component.handler.annotation.AppController;
import com.ruoyi.chat.component.handler.annotation.AppRequestMapping;
import com.ruoyi.chat.netty.AppResponse;
import com.ruoyi.chatroom.db.domain.ChatUserInfo;
import com.ruoyi.chatroom.service.ChatFriendRecordService;
import com.ruoyi.chatroom.service.ChatUserInfoService;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.chat.OfflineAck;
import com.ruoyi.common.core.domain.model.chat.enums.MessageTypeEnum;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.chat.MessageEventGenerator;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 好友聊天(客服跟会员)
 * @author: nn
 * @create: 2022-07-19 11:19
 **/
@AppController
@AppRequestMapping("/chatFriend")
public class ChatFriendMessageController {

    @Resource
    private ChatFriendRecordService chatFriendRecordService;
    @Resource
    private ChatUserInfoService chatUserInfoService;


    @AppRequestMapping("/connect")
    public AppResponse connect(Byte msgType, LoginUser loginUser, String friendIdentifier){
        Long sessionId= chatFriendRecordService.getSessionId(loginUser,friendIdentifier);
        return AppResponse.newInstance(MessageTypeEnum.SELF).put("sessionId",sessionId);
    }
    /**
     *
     * @param loginMember 当前用户的UserInfo
     * @param msg 发送消息
     * @throws Exception 异常;
     */
    @AppRequestMapping("/sendMsgToMyFriend")
    public void sendMsgToMyCustomerService(Byte msgType, LoginMember loginMember, String friendIdentifier, Object msg) throws Exception {


        ChatUserInfo friendInfo = chatUserInfoService.getChatUserInfoByUserIdentifier(friendIdentifier);

        Assert.test(friendInfo==null,"chat.message.target.error");

        //消息记录存入Mongo
        chatFriendRecordService.saveChatFriendRecord(msgType,friendIdentifier,msg,loginMember,friendInfo);


    }



    @AppRequestMapping("/ack")
    @AppAsync
    public void ack(List<Long> msgIds){
        MessageEventGenerator.offlineAck(OfflineAck.of(msgIds));
    }
}

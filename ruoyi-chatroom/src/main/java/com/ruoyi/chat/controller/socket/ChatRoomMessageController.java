package com.ruoyi.chat.controller.socket;

import com.ruoyi.chat.component.bootstrap.BusinessChannelAttr;
import com.ruoyi.chat.component.group.ChatSessionGroupManager;
import com.ruoyi.chat.component.handler.annotation.AppController;
import com.ruoyi.chat.component.handler.annotation.AppRequestMapping;
import com.ruoyi.chatroom.db.domain.ChatUserInfo;
import com.ruoyi.chatroom.db.repository.ChatUserInfoRepository;
import com.ruoyi.chatroom.db.vo.ChatRoomRecordVo;
import com.ruoyi.chatroom.service.ChatRoomRecordService;
import com.ruoyi.chatroom.service.IChatServeCustomerService;
import com.ruoyi.chatroom.service.impl.ChatroomFacade;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.ChatMessageType;
import com.ruoyi.common.enums.ChatTopic;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.chat.GeneralUtils;
import com.ruoyi.common.utils.chat.MessageEventGenerator;
import io.netty.channel.Channel;

import javax.annotation.Resource;
import java.util.List;


/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/9,14:56
 * @return:
 **/
@AppController
@AppRequestMapping("/chatRoom")
public class ChatRoomMessageController {

    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;
    @Resource
    private ChatSessionGroupManager chatSessionGroupManager;
    @Resource
    private ChatroomFacade chatroomFacade;
    @Resource
    private ChatRoomRecordService chatRoomRecordService;
    @Resource
    private IChatServeCustomerService chatServeCustomerService;
    @Resource
    private ChatUserInfoRepository chatUserInfoRepository;

    /**
     * 加入聊天室
     * @param channel
     * @param loginUser
     * @return
     */
    @AppRequestMapping("/join")
    public void joinChatRoom(ChatUserInfo chatUserInfo, Channel channel, LoginUser loginUser){
        if (chatUserInfo==null){
            chatUserInfo = new ChatUserInfo();
            chatUserInfo.setShortUrl(loginUser.getShortUrl());
            chatUserInfo.setLargeHead(loginUser.getPhoto());
            chatUserInfo.setId(snowflakeIdUtils.nextId());
            chatUserInfo.setUserId(loginUser.getUserId());
            chatUserInfo.setNikeName(loginUser.getNickName());
            chatUserInfo.setUserName(loginUser.getUsername());
            chatUserInfo.setMerchantId(loginUser.getMerchantId());
            chatUserInfo.setUserIdentifier(chatroomFacade.getUserIdentifier(loginUser.getUserId(),loginUser.getUsername()));
            chatUserInfo.setRunId(GeneralUtils.getRunId());
            chatUserInfoRepository.saveChatUserInfo(chatUserInfo);
        }else {
            chatUserInfo.setRunId(GeneralUtils.getRunId());
            chatUserInfoRepository.updateChatUserInfo(chatUserInfo);
        }
        //聊天室维护;
        chatroomFacade.checkChatRoomMaintenance();
        chatroomFacade.checkMerchantClose(loginUser.getMerchantId());
        chatroomFacade.checkCashLock(loginUser.getUserId());
        //判断是否在组里

        Long merchantIdAttr = channel.attr(BusinessChannelAttr.MERCHANT_ID_KEY).get();

        if (chatSessionGroupManager.inGroup(merchantIdAttr,channel)){
            return ;
        }

        //加入聊天室
        chatSessionGroupManager.addChannelToGroup(
                merchantIdAttr,
                chatroomFacade.getUserIdentifier(loginUser.getUserId(),loginUser.getUsername()),
                channel);

        //获取聊天室记录;
        List<ChatRoomRecordVo> result = chatRoomRecordService.getChatRoomRecordVoList(null, 20, loginUser);
        //给自己返回聊天记录
        MessageEventGenerator.message2Self(result, ChatTopic.CHAT_ROOM_RECORDS);


    }

    /**
     * 聊天室发消息;
     * @param channel
     * @param msg
     * @return
     * @throws Exception
     */
    @AppRequestMapping("/sendMessageToAll")
    public void sendMessageToAll(Channel channel,Byte msgType,Object msg,LoginUser loginUser) throws Exception {
        //聊天室维护;
        chatroomFacade.checkChatRoomMaintenance();
        chatroomFacade.checkMerchantClose(loginUser.getMerchantId());
        chatroomFacade.checkCashLock(loginUser.getUserId());
        Long merchantId = channel.attr(BusinessChannelAttr.MERCHANT_ID_KEY).get();

        if (!chatSessionGroupManager.inGroup(merchantId,channel)){
            throw new ServiceException("您不在聊天室中！",12345);
        }

        if (chatroomFacade.isForbiddenToTalk(loginUser.getUserId())){
            throw new ServiceException("您已经被禁言！",12346);
        };
        //最多10秒内发言5次;
        chatroomFacade.limitedChat("chatRoom:limited:"+loginUser.getUserId());
        //敏感词过滤
        Object replaceMsg = chatroomFacade.processingMsgType(msg,
                ChatMessageType.getMessageTypeByType(msgType),null);
        chatRoomRecordService.saveChatRoomRecord(replaceMsg);
    }

}

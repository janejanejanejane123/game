package com.ruoyi.chatroom.service;

import com.ruoyi.chatroom.db.domain.ChatFriendRecord;
import com.ruoyi.chatroom.db.domain.ChatUserInfo;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.ChatMessageType;
import io.netty.channel.Channel;

import javax.xml.ws.Response;
import java.util.Date;

/**
 * @description: 朋友聊天记录业务接口定义
 * @author: nn
 * @create: 2022-07-19 16:44
 **/
public interface ChatFriendRecordService {

    /**
     * @Description: 保存消息
     * @param chatFriendRecord
     * @return void
     */
    void saveChatFriendRecord(ChatFriendRecord chatFriendRecord);

    /**
     * @Description: 保存消息
     * @param msgType
     * @param friendIdentifier
     * @param msg
     * @param loginUser
     * @param friendChatUserInfo
     * @throws Exception
     */
    void saveChatFriendRecord(Byte msgType, String friendIdentifier, Object msg, LoginUser loginUser, ChatUserInfo friendChatUserInfo ) throws Exception;

    /**
     * @Description: 撤回消息
     * @param friendChatUserInfo
     * @param messageId 消息ID
     * @param loginMember 登陆用户
     */
    AjaxResult retractMessage(ChatUserInfo friendChatUserInfo, Long messageId, LoginMember loginMember);


    void advisoryCustomerService(Byte msgType,Object msg, Response response, ChatUserInfo friendChatUserInfo, Response response1,  Channel channel) throws Exception;

    void sendMsgToTourist(ChatMessageType chatMessageType, Object msg,  String touristIdentifier, String touristNikeName, Channel touristChannel, ChatUserInfo myChatUserInfo, Response response) throws Exception;

    /**
     * @Description: 举报消息
     * @param messageId 消息ID
     * @param remarks 备注
     * @param reportType 举报类型
     * @return
     */
    void reportMessage(Long messageId, LoginMember loginMember, String remarks, String reportType);

    /**
     * 定时删除私聊消息记录.
     * @param day   天数
     * @return
     */
    public AjaxResult deleteOnTimeChatFriendRecord(int day);

    Long getSessionId(LoginUser loginUser, String friendIdentifier);
}

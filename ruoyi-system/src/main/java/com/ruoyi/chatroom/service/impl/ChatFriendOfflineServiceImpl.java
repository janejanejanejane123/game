package com.ruoyi.chatroom.service.impl;

import com.alibaba.fastjson.JSON;
import com.ruoyi.chatroom.db.domain.ChatFriendOffline;
import com.ruoyi.chatroom.db.domain.ChatFriendRecord;
import com.ruoyi.chatroom.db.domain.ChatUserInfo;
import com.ruoyi.chatroom.db.repository.ChatFriendOfflineRepository;
import com.ruoyi.chatroom.db.repository.ChatFriendRecodRepository;
import com.ruoyi.chatroom.db.vo.ChatFriendOfflineVo;
import com.ruoyi.chatroom.db.vo.ChatFriendRecordVo;
import com.ruoyi.chatroom.service.ChatFriendOfflineService;
import com.ruoyi.chatroom.service.ChatUserInfoService;
import com.ruoyi.chatroom.service.IChatServeCustomerService;
import com.ruoyi.common.constant.TopicConstants;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.chat.ChatMessage;
import com.ruoyi.common.core.domain.model.chat.CommonContent;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.enums.ChatTopic;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @description: 好友离线消息实现
 * @author: nn
 * @create: 2022-07-19 18:59
 **/
@Service
public class ChatFriendOfflineServiceImpl implements ChatFriendOfflineService {
    private Logger logger = LoggerFactory.getLogger(ChatFriendOfflineServiceImpl.class);

    @Resource
    private ChatFriendOfflineRepository chatFriendOfflineRepository;
    @Resource
    private ChatroomFacade chatroomFacade;
    @Resource
    private ChatFriendRecodRepository chatFriendRecodRepository;
    @Resource
    private IChatServeCustomerService chatServeCustomerService;
    @Resource
    private ChatUserInfoService chatUserInfoService;


    @Override
    public void startCheckAck() { }

    @Override
    @Async("taskExecutor")
    @SuppressWarnings("unchecked")
    public void loadChatFriendOffline(Channel channel, LoginMember loginMember) {
        boolean serveCustomer = chatServeCustomerService.isServeCustomer(null, loginMember.getUserId());

        if (serveCustomer){
            ChatMessage chatMessage = new ChatMessage();

            CommonContent commonContent = new CommonContent();

            commonContent.setCode(200);
            chatMessage.setType(2);
            List<Map> maps = chatFriendOfflineRepository.customerServerOfflineMessageCount(loginMember.getUserId());
            List<Map> offlineInfo = new ArrayList<>(maps.size());
            for (Map map : maps) {
                Long friendId = Convert.toLong(map.get("_id"));
                map.remove("_id");
                ChatUserInfo friendInfo = chatUserInfoService.getChatUserInfoByUserId(friendId);
                if (friendInfo==null){
                    continue;
                }
                map.put("nickName",friendInfo.getNikeName());
                map.put("identifier",friendInfo.getUserIdentifier());
                offlineInfo.add(map);
            }
            commonContent.setExtra(ChatTopic.CUSTOMER_OFFLINE_MESSAGE.value());
            commonContent.setMessage(offlineInfo);
            if (channel.isActive() && channel.isWritable()){
                chatMessage.setContent(JSON.toJSONString(commonContent));
                channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatMessage)));
            }else {
                channel.close();
            }
        }else {
            this.doChatOfflineSend(channel,loginMember,null);
        }
    }

    @Override
    @Async("taskExecutor")
    public void doChatOfflineSend(Channel channel,LoginMember loginMember,String friendIdentifier){
        ChatMessage chatMessage = new ChatMessage();
        CommonContent commonContent = new CommonContent();
        commonContent.setCode(200);
        chatMessage.setType(2);

        Long friendId=null;
        if (StringUtils.isNotBlank(friendIdentifier)){
            friendId=chatUserInfoService.getChatUserInfoByUserIdentifier(friendIdentifier).getUserId();
        }

        long count = chatFriendOfflineRepository.queryMyOfflineRecordCount(loginMember.getUserId(),friendId);
        int pageSize = 100;
        if(count > 0 ){
            Long lastId=null;
            int pageCount = count % pageSize != 0 ? (int) Math.floor((count * 1.0d) / pageSize) + 1 :(int) Math.floor((count * 1.0d) / pageSize);
            commonContent.setExtra(ChatTopic.PLAYER_OFFLINE_MESSAGE.value());
            for (int i = 0; i < pageCount; i++) {
                List<ChatFriendOffline> friendChatOfflineList = chatFriendOfflineRepository.queryMyOfflineRecordList(loginMember.getUserId(), lastId, pageSize,friendId);
                if (!CollectionUtils.isEmpty(friendChatOfflineList)){
                    lastId = friendChatOfflineList.get(friendChatOfflineList.size() - 1).getMessageId();
                    List<Long> msgIds = friendChatOfflineList.stream().map(ChatFriendOffline::getMessageId).collect(Collectors.toList());
                    List<ChatFriendRecord> chatFriendRecords = chatFriendRecodRepository.queryFriendChatRecordByIds(msgIds);
                    List<ChatFriendRecordVo> collect = chatFriendRecords.stream().map((o) -> {
                        ChatFriendRecordVo chatFriendRecordVo = new ChatFriendRecordVo();
                        CglibBeanCopierUtils.copyProperties(chatFriendRecordVo, o);
                        chatFriendRecordVo.setSendUserId(null);
                        chatFriendRecordVo.setSendUserName(null);
                        chatFriendRecordVo.setFriendUserName(null);
                        chatFriendRecordVo.setFriendUserId(null);
                        return chatFriendRecordVo;
                    }).collect(Collectors.toList());
                    if (channel.isOpen()&&channel.isWritable()){
                        commonContent.setMessage(collect);
                        chatMessage.setContent(JSON.toJSONString(commonContent));
                        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatMessage)));
                    }else {
                        channel.close();
                        return;
                    }
                }
            }
        }
    }


    @Override
    public void chatAck(Long userId, Long messageId) throws InterruptedException {

    }
}

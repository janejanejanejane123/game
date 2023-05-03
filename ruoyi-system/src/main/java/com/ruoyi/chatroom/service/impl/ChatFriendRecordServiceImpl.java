package com.ruoyi.chatroom.service.impl;

import com.ruoyi.chatroom.db.domain.*;
import com.ruoyi.chatroom.db.repository.ChatFriendOfflineRepository;
import com.ruoyi.chatroom.db.repository.ChatFriendRecodRepository;
import com.ruoyi.chatroom.db.repository.ChatSessionRepository;
import com.ruoyi.chatroom.db.repository.ReportMessageRepository;
import com.ruoyi.chatroom.db.vo.ChatFriendRecordVo;
import com.ruoyi.chatroom.service.ChatFriendRecordService;
import com.ruoyi.chatroom.service.ChatUserInfoService;
import com.ruoyi.chatroom.service.IChatServeCustomerService;
import com.ruoyi.chatroom.service.MessageRecallRecordService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.redis.RedisLock;
import com.ruoyi.common.enums.*;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.chat.MessageEventGenerator;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.ws.Response;
import java.util.Calendar;
import java.util.Date;

/**
 * @description: 朋友聊天记录业务接口实现
 * @author: nn
 * @create: 2019-11-12 16:45
 **/
@Service
public class ChatFriendRecordServiceImpl implements ChatFriendRecordService {
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;
    @Resource
    private ChatFriendRecodRepository chatFriendRecodRepository;
    @Resource
    private ChatFriendOfflineRepository friendOfflineRepository;
    @Resource
    private ChatUserInfoService chatUserInfoService;
    @Resource
    private ChatroomFacade chatroomFacade;
    @Resource
    private IChatServeCustomerService chatServeCustomerService;
    @Resource
    private ReportMessageRepository reportMessageRepository;
    @Resource
    private MessageRecallRecordService messageRecallRecordService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private ChatSessionRepository chatSessionRepository;


    public void sendMessage(ChatFriendRecord chatFriendRecord){
        ChatFriendRecordVo chatFriendRecordVo = new ChatFriendRecordVo();
        CglibBeanCopierUtils.copyProperties(chatFriendRecordVo,chatFriendRecord);

        chatFriendRecordVo.setFriendUserName(null);
        chatFriendRecordVo.setFriendUserId(null);
        chatFriendRecordVo.setSendUserId(null);
        chatFriendRecordVo.setSendUserName(null);
        MessageEventGenerator.message2Somebody(chatFriendRecord.getSendUserIdentifier(),chatFriendRecord.getFriendIdentifier(),chatFriendRecordVo, ChatTopic.PRIVATE_CHAT,chatFriendRecord.getFriendRunId());
    }


    @Override
    @Transactional(transactionManager = "mongoTransactionManager",rollbackFor = Exception.class)
    public void saveChatFriendRecord(ChatFriendRecord chatFriendRecord) {
        chatFriendRecodRepository.saveChatFriendRecord(chatFriendRecord);

        ChatFriendOffline chatFriendOffline = new ChatFriendOffline();
        chatFriendOffline.setId(snowflakeIdUtils.nextId());
        chatFriendOffline.setMessageId(chatFriendRecord.getMessageId());
        chatFriendOffline.setSendTime(chatFriendRecord.getSendTime());

        chatFriendOffline.setReceiverUid(chatFriendRecord.getFriendUserId());
        chatFriendOffline.setSenderUid(chatFriendRecord.getSendUserId());
        chatFriendOffline.setReceiverUserType(chatFriendRecord.getFriendPlayerType());

        friendOfflineRepository.saveChatFriendOfflineRecord(chatFriendOffline);


        chatFriendOffline.setId(snowflakeIdUtils.nextId());
        chatFriendOffline.setReceiverUid(chatFriendRecord.getSendUserId());
        chatFriendOffline.setSenderUid(chatFriendRecord.getFriendUserId());
        chatFriendOffline.setReceiverUserType(chatFriendRecord.getUserPlayerType());

        friendOfflineRepository.saveChatFriendOfflineRecord(chatFriendOffline);

    }

    @Override
    @Transactional(transactionManager = "mongoTransactionManager",rollbackFor = Exception.class)
    public void saveChatFriendRecord(Byte msgType, String friendIdentifier, Object msg, LoginUser loginUser, ChatUserInfo friendChatUserInfo ) throws Exception {


        //判断类型，会员->客服，客服->会员，客服->客服
        int sendType=sendType(loginUser.getUserId(),friendIdentifier);
        Long sessionId;
        if (sendType==1){
            sessionId= sessionId(friendIdentifier,chatroomFacade.getUserIdentifier(loginUser.getUserId(),loginUser.getUsername()));
        }else {
            sessionId= sessionId(chatroomFacade.getUserIdentifier(loginUser.getUserId(),loginUser.getUsername()),friendIdentifier);
        }

        ChatMessageType chatMessageType = ChatMessageType.getMessageTypeByType(msgType);
        Assert.test(chatMessageType==null,"chat.message.type.error");

        ChatUserInfo chatUserInfo = chatUserInfoService.getChatUserInfoByUserId(loginUser.getUserId());
        Assert.test(chatUserInfo==null,"system.error");


        ChatFriendRecord chatFriendRecord = new ChatFriendRecord();
        chatFriendRecord.setSessionId(sessionId);
        chatFriendRecord.setFriendRunId(friendChatUserInfo.getRunId());
        //发送者信息
        chatFriendRecord.setSendUserId(loginUser.getUserId());
        chatFriendRecord.setSendUserIdentifier(chatUserInfo.getUserIdentifier());
        chatFriendRecord.setSendUserIp(loginUser.getIpaddr());
        chatFriendRecord.setSendUserNickName(loginUser.getNickName());
        chatFriendRecord.setSendUserName(loginUser.getUsername());
        chatFriendRecord.setSendUserPortrait(loginUser.getPhoto());
        chatFriendRecord.setUserPlayerType(sendType==2?
                PlayerTypeEnum.PLAYER_USER.getPlayerType() :
                PlayerTypeEnum.CUSTOMER_SERVICE.getPlayerType());
        //发送对象信息
        chatFriendRecord.setFriendIdentifier(friendIdentifier);
        chatFriendRecord.setFriendUserId(friendChatUserInfo.getUserId());
        chatFriendRecord.setFriendNickName(friendChatUserInfo.getNikeName());
        chatFriendRecord.setFriendUserName(friendChatUserInfo.getUserName());
        chatFriendRecord.setFriendPortrait(friendChatUserInfo.getLargeHead());
        chatFriendRecord.setFriendPlayerType(sendType>=2?
                PlayerTypeEnum.CUSTOMER_SERVICE.getPlayerType():
                PlayerTypeEnum.PLAYER_USER.getPlayerType());
        //发送消息信息
        chatFriendRecord.setMsgType(chatMessageType.getType());
        chatFriendRecord.setMessageId(snowflakeIdUtils.nextId());
        chatFriendRecord.setMsgBody(chatroomFacade.processingMsgType(msg,chatMessageType,null));
        chatFriendRecord.setMsgStatus(MsgStatusEnum.NORMAL.getStatus());

        //额外信息
        chatFriendRecord.setSendTime(new Date());
        chatFriendRecord.setReceiveFlag((byte) 0);
        chatFriendRecord.setReport(false);
        this.saveChatFriendRecord(chatFriendRecord);

        sendMessage(chatFriendRecord);
    }

    private PlayerTypeEnum getPlayerTypeEnum(boolean isServeCustomer , boolean isTrialAccount, LoginUser loginUser, ChatUserInfo friendChatUserInfo){
        if(isServeCustomer){
            return PlayerTypeEnum.CUSTOMER_SERVICE;
        }else {
            return PlayerTypeEnum.PLAYER_USER;
        }
    }


    private Long nextId(){
        return null;
    }

    @Override
    @Transactional(transactionManager = "mongoTransactionManager",rollbackFor = Exception.class)
    public void advisoryCustomerService(Byte msgType,Object msg, Response response, ChatUserInfo friendChatUserInfo, Response response1,  Channel channel) throws Exception {

    }

    @Override
    @Transactional(transactionManager = "mongoTransactionManager",rollbackFor = Exception.class)
    public void sendMsgToTourist(ChatMessageType chatMessageType, Object msg, String touristIdentifier, String touristNikeName, Channel touristChannel, ChatUserInfo myChatUserInfo, Response response) throws Exception {

    }

    /**
     * 举报消息.
     * @param messageId 消息ID
     * @param remarks 备注
     * @param reportType 举报类型
     */
    @Override
    @Transactional(transactionManager = "mongoTransactionManager",rollbackFor = Exception.class)
    public void reportMessage( Long messageId,LoginMember loginMember,  String remarks, String reportType) {

        ChatFriendRecord originalChatFriendRecord = chatFriendRecodRepository.getChatFriendRecordById(messageId);
        if(originalChatFriendRecord == null || originalChatFriendRecord.getFriendUserId().longValue() != loginMember.getUserId().longValue()){
            throw new ServiceException("没有找到要举报的消息!");
        }else if(originalChatFriendRecord.getSendUserId().longValue() == loginMember.getUserId().longValue()){
            throw new ServiceException("举报自己?");
        }
        Long informerId = loginMember.getUserId();
        String informerName = loginMember.getUsername();
        String informerIdentifier = chatroomFacade.getUserIdentifier(informerId,informerName);
        chatFriendRecodRepository.reportMessage(messageId);
        ReportMessage reportMessage = new ReportMessage();
        reportMessage.setReportId(snowflakeIdUtils.nextId());
        reportMessage.setMessageId(messageId);
        reportMessage.setReportSource(ReportSourceEnum.FRIENDS_MESSAGE.getReportSource());
        reportMessage.setReportType(reportType);
        //举报人.
        reportMessage.setInformerId(informerId);
        reportMessage.setInformerName(informerName);
        reportMessage.setInformerIdentifier(informerIdentifier);
        //被举报人.
        reportMessage.setUserId(originalChatFriendRecord.getSendUserId());
        reportMessage.setUserName(originalChatFriendRecord.getSendUserName());
        reportMessage.setUserIdentifier(originalChatFriendRecord.getSendUserIdentifier());
        reportMessage.setReportTime(new Date());
        reportMessage.setRemarks(remarks);
        reportMessage.setIsDelete((byte)0);
        reportMessageRepository.saveReportMessage(reportMessage);
    }

    /**
     * 撤回消息
     * @param friendChatUserInfo
     * @param messageId 消息ID
     * @param loginMember 登陆用户
     * @return
     */
    @Override
    @Transactional(transactionManager = "mongoTransactionManager",rollbackFor = Exception.class)
    public AjaxResult retractMessage(ChatUserInfo friendChatUserInfo, Long messageId, LoginMember loginMember) {

        Date sendTime = new Date();
        ChatUserInfo chatUserInfo = chatUserInfoService.getChatUserInfoByUserId(loginMember.getUserId());
        ChatFriendRecordVo chatFriendRecordVo = new ChatFriendRecordVo();
        chatFriendRecordVo.setMsgType(ChatMessageType.RETRACT_MESSAGE.getType());
        chatFriendRecordVo.setMsgBody(messageId);
        chatFriendRecordVo.setMessageId(snowflakeIdUtils.nextId());
        chatFriendRecordVo.setMsgStatus(MsgStatusEnum.NORMAL.getStatus());
        chatFriendRecordVo.setSendTime(sendTime);
        chatFriendRecordVo.setSendUserIdentifier(chatUserInfo.getUserIdentifier());//当前用户发送标识
        chatFriendRecordVo.setFriendIdentifier(friendChatUserInfo.getUserIdentifier());//朋友标识
        chatFriendRecordVo.setFriendNikeName(friendChatUserInfo.getNikeName());
        chatFriendRecordVo.setFriendPortrait(friendChatUserInfo.getSmallHead());
        chatFriendRecordVo.setIsSelf((byte) 1);
        ChatFriendRecord originalFriendChatRecord = chatFriendRecodRepository.getChatFriendRecordById(messageId, loginMember.getUserId(),friendChatUserInfo.getUserId());
        Date nowTime = new Date();
        if(originalFriendChatRecord == null || (!originalFriendChatRecord.getSendUserIdentifier().equals(chatUserInfo.getUserIdentifier()) && originalFriendChatRecord.getSendUserIdentifier().equals(friendChatUserInfo.getUserIdentifier()))){
            throw new ServiceException("没有找到要撤回的消息!!!");
        }else if(originalFriendChatRecord.getMsgStatus() == MsgStatusEnum.RETRACT.getStatus()){
            throw new ServiceException("该消息已经撤回!!!");
        }else if (!DateUtils.isBetween(originalFriendChatRecord.getSendTime(), DateUtils.addMinute(nowTime,-2), nowTime,DateUtils.LEFT_OPEN_RIGHT_OPEN, DateUtils.COMP_MODEL_DATETIME)) {
            throw new ServiceException("发送消息超过2分钟不能被撤回!!!");
        }
        //发送消息的用户类型设置
        chatFriendRecodRepository.updateMessage(messageId, loginMember.getUserId(),friendChatUserInfo.getUserId(), MsgStatusEnum.RETRACT);
        boolean isServeCustomer = chatServeCustomerService.isServeCustomer(null, loginMember.getUserId());
        chatFriendRecordVo.setUserPlayerType(isServeCustomer ? PlayerTypeEnum.CUSTOMER_SERVICE.getPlayerType() : PlayerTypeEnum.PLAYER_USER.getPlayerType());

        PlayerTypeEnum playerTypeEnum = getPlayerTypeEnum(isServeCustomer,false,null,friendChatUserInfo);
        chatFriendRecordVo.setFriendPlayerType(playerTypeEnum.getPlayerType());

        ChatFriendRecord chatFriendRecord = new ChatFriendRecord();
        CglibBeanCopierUtils.copyProperties(chatFriendRecord, chatFriendRecordVo);

        chatFriendRecord.setSendUserIp(loginMember.getIpaddr());
        chatFriendRecord.setSendUserId(loginMember.getUserId());
        chatFriendRecord.setSendUserName(chatUserInfo.getUserName());
        chatFriendRecord.setSendUserName(chatUserInfo.getNikeName());
        chatFriendRecord.setSendUserPortrait(chatUserInfo.getSmallHead());
        chatFriendRecord.setReceiveFlag((byte) 0);
        chatFriendRecord.setFriendUserId(friendChatUserInfo.getUserId());
        chatFriendRecord.setMsgStatus(MsgStatusEnum.NORMAL.getStatus());
        chatFriendRecord.setFriendUserName(friendChatUserInfo.getUserName());

        this.saveChatFriendRecord(chatFriendRecord);//保存朋友聊天内容
        //准备给好友发送消息
        chatFriendRecordVo.setSendUserIdentifier(friendChatUserInfo.getUserIdentifier());//朋友标识
        chatFriendRecordVo.setFriendIdentifier(chatUserInfo.getUserIdentifier());//用户标识
        chatFriendRecordVo.setFriendNikeName(chatUserInfo.getNikeName());
        chatFriendRecordVo.setFriendPortrait(chatUserInfo.getSmallHead());
        chatFriendRecordVo.setFriendPlayerType(chatFriendRecord.getUserPlayerType());
        chatFriendRecordVo.setIsSelf((byte) 0);
        chatFriendRecordVo.setUserPlayerType(chatFriendRecord.getFriendPlayerType());
//        response.setData(chatFriendRecordVo);
//        MySessionManager.sendDesignationUser(tokenVo.getSiteCode(), (short) 0, friendChatUserInfo.getUserId(), null, new TextWebSocketFrame(JSON.toJSONString(response)));

        chatFriendRecordVo.setSendUserIdentifier(chatUserInfo.getUserIdentifier());
        chatFriendRecordVo.setFriendPlayerType(chatFriendRecord.getFriendPlayerType());
        chatFriendRecordVo.setUserPlayerType(chatFriendRecord.getUserPlayerType());
        chatFriendRecordVo.setFriendIdentifier(friendChatUserInfo.getUserIdentifier());
        chatFriendRecordVo.setFriendNikeName(friendChatUserInfo.getNikeName());
        chatFriendRecordVo.setFriendPortrait(friendChatUserInfo.getSmallHead());
        chatFriendRecordVo.setIsSelf((byte) 1);

        //保存私聊消息撤回记录.
        MessageRecallRecord messageRecallRecord = new MessageRecallRecord();
        messageRecallRecord.setId(snowflakeIdUtils.nextId());
        messageRecallRecord.setMessageId(messageId);
        messageRecallRecord.setMsgType(originalFriendChatRecord.getMsgType());
        messageRecallRecord.setMsgBody(originalFriendChatRecord.getMsgBody());
        messageRecallRecord.setSendUserId(originalFriendChatRecord.getSendUserId());
        messageRecallRecord.setSendUserName(originalFriendChatRecord.getSendUserName());
        messageRecallRecord.setSendNickName(originalFriendChatRecord.getSendUserName());
        messageRecallRecord.setRecallUserId(loginMember.getUserId());
        messageRecallRecord.setRecallUserName(loginMember.getUsername());
        messageRecallRecord.setRecallType((byte)2);
        messageRecallRecord.setRecallSource((byte)3);
        messageRecallRecord.setRecallTime(new Date());
        messageRecallRecord.setRecallRemark("前端撤回私聊消息!");
        messageRecallRecordService.addMessageRecallRecord(messageRecallRecord);
        return new AjaxResult();
    }


    /**
     * 定时删除私聊消息.
     * @return
     */
    @Override
    public AjaxResult deleteOnTimeChatFriendRecord(int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -day);
        Date time = calendar.getTime();

        chatFriendRecodRepository.deleteOnTimeChatFriendRecord(time);
        return new AjaxResult();
    }

    @Override
    public Long getSessionId(LoginUser loginUser, String friendIdentifier) {
        String myIdentifier = chatroomFacade.getUserIdentifier(loginUser.getUserId(), loginUser.getUsername());
        int sendType = sendType(loginUser.getUserId(), friendIdentifier);
        String userIdentifier;
        String customerIdentifier;
        if (sendType==1){
            userIdentifier=friendIdentifier;
            customerIdentifier=myIdentifier;
        }else {
            userIdentifier=myIdentifier;
            customerIdentifier=friendIdentifier;
        }

        return sessionId(userIdentifier,customerIdentifier);
    }

    private Long sessionId(String userIdentifier, String customerIdentifier) {


        ChatSession session = chatSessionRepository.getSession(userIdentifier, customerIdentifier);
        if (session==null){
            try {
                if (RedisLock.tryLock("tryCreateChatSession:"+userIdentifier+":"+customerIdentifier,60)){
                    session= chatSessionRepository.getSession(userIdentifier, customerIdentifier);
                    if (session==null){
                        return chatSessionRepository.addSession(userIdentifier,customerIdentifier).getId();
                    }else {
                        return session.getId();
                    }
                }
            }finally {
                RedisLock.unLock();
            }

        }

        return null;
    }


    private int sendType(Object id1,Object id2){
        int sendType = count(id1, 1) + count(id2, 2);
        Assert.test(sendType==0||sendType==3,"system.error");
        return sendType;
    }



    private int count(Object id1,int weight){
        int result=0;
        boolean cus=false;
        if (id1 instanceof String){
            cus=chatServeCustomerService.isServeCustomer((String) id1,null);
        }else if (id1 instanceof Long){
            cus=chatServeCustomerService.isServeCustomer(null,(Long)id1);
        }
        if (cus){
            result+=weight;
        }
        return result;

    }

}

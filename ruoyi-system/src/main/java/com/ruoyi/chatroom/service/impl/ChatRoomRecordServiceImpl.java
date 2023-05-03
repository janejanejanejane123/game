package com.ruoyi.chatroom.service.impl;

import com.ruoyi.chatroom.db.domain.ChatRoomRecord;
import com.ruoyi.chatroom.db.domain.MessageRecallRecord;
import com.ruoyi.chatroom.db.domain.ReportMessage;
import com.ruoyi.chatroom.db.repository.ChatRoomRecordRepository;
import com.ruoyi.chatroom.db.repository.ReportMessageRepository;
import com.ruoyi.chatroom.db.vo.ChatRoomMessageVo;
import com.ruoyi.chatroom.db.vo.ChatRoomRecordVo;
import com.ruoyi.chatroom.db.vo.RetractMessageVo;
import com.ruoyi.chatroom.service.ChatMessageProcess;
import com.ruoyi.chatroom.service.ChatRoomRecordService;
import com.ruoyi.chatroom.service.MessageRecallRecordService;
import com.ruoyi.common.constant.TopicConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.chat.enums.MessageTypeEnum;
import com.ruoyi.common.enums.ChatMessageType;
import com.ruoyi.common.enums.ChatTopic;
import com.ruoyi.common.enums.MsgStatusEnum;
import com.ruoyi.common.enums.UserTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.chat.GeneralUtils;
import com.ruoyi.common.utils.chat.MessageEventGenerator;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;
import com.ruoyi.member.service.ITUserNicknameBlackListService;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @description: 大厅聊天记录业务实现类.
 * @author: nn
 * @create: 2020-06-08 11:42
 **/
@Service
@Lazy
public class ChatRoomRecordServiceImpl implements ChatRoomRecordService {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;
    @Resource
    private ChatRoomRecordRepository chatRoomRecordRepository;
    @Resource
    private ReportMessageRepository reportMessageRepository;
    @Resource
    private MessageRecallRecordService messageRecallRecordService;
    @Resource
    private ChatroomFacade chatroomFacade;
    @Resource
    private ITUserNicknameBlackListService nicknameBlackListService;

    /**
     * @Description: 获取消息列表
     * @param messageId 消息ID
     * @param pageSize 分页大小
     * @param loginUser 登陆用户信息
     * @return java.util.List<com.cloud.chatroom.service.server.db.domain.GroupChatRecord>
     */
    @Override
    public List<ChatRoomRecordVo> getChatRoomRecordVoList(Long messageId, int pageSize, LoginUser loginUser){

        Query query = new Query();
        Criteria criteria = Criteria.where("msgStatus").in(MsgStatusEnum.NORMAL.getStatus()
                ,MsgStatusEnum.RETRACT.getStatus());
        criteria.and("messageId").lte(messageId != null ? messageId : Long.MAX_VALUE);
        query.addCriteria(criteria);
        query.limit(pageSize).with(Sort.by(Sort.Direction.DESC, "messageId"));
        List<ChatRoomRecord> chatRoomRecordList = chatRoomRecordRepository.queryChatRoomRecordList(query);
        List<ChatRoomRecordVo> chatRoomRecordVoList = new ArrayList<>(pageSize);
        for(ChatRoomRecord chatRoomRecord :chatRoomRecordList){
            ChatRoomRecordVo chatRoomRecordVo = new ChatRoomRecordVo();
            CglibBeanCopierUtils.copyProperties(chatRoomRecordVo, chatRoomRecord);
            chatRoomRecordVoList.add(chatRoomRecordVo);
        }
        return chatRoomRecordVoList;
    }

    /**
     * 举报消息
     * @param messageId 消息Id
     * @param remarks 备注
     * @param reportType 举报类型
     */
    @Override
    @Transactional(transactionManager = "mongoTransactionManager",rollbackFor = Exception.class)
    public void reportMessage(Long messageId,String remarks, String reportType,LoginMember loginMember,byte reportSource){
        ChatRoomRecord chatRoomRecord = chatRoomRecordRepository.getChatRoomRecord(messageId,  null);
        if(chatRoomRecord == null){
            throw new ServiceException("举报的消息已失效!");
        }

        chatRoomRecordRepository.reportMessage(messageId);
        //被举报人.
        Long userId = chatRoomRecord.getUserId();
        String userName = chatRoomRecord.getUserName();
        String userIdentifier = chatRoomRecord.getUserIdentifier();

        //举报人.
        Long informerId = loginMember.getUserId();
        String informerName = loginMember.getUsername();
        String informerIdentifier = chatroomFacade.getUserIdentifier(informerId,informerName);
        if(userId.longValue() == informerId.longValue()){
            throw new ServiceException("举报自己?");
        }

        ReportMessage reportMessage = new ReportMessage();
        reportMessage.setReportId(snowflakeIdUtils.nextId());
        reportMessage.setMessageId(messageId);
        reportMessage.setReportSource(reportSource);
        reportMessage.setReportType(reportType);
        //举报人.
        reportMessage.setInformerId(informerId);
        reportMessage.setInformerName(informerName);
        reportMessage.setInformerIdentifier(informerIdentifier);
        //被举报人.
        reportMessage.setUserId(userId);
        reportMessage.setUserName(userName);
        reportMessage.setUserIdentifier(userIdentifier);
        reportMessage.setReportTime(new Date());
        reportMessage.setRemarks(remarks);
        reportMessage.setIsDelete((byte)0);
        reportMessageRepository.saveReportMessage(reportMessage);
    }

    /**
     * 撤回消息.
     * @param messageId 消息ID
     * @param isManager 是否管理员撤回，0:否,1:是
     * @param loginIp
     * @param loginUser 撤回用户
     */
    @Override
    @Transactional(transactionManager = "mongoTransactionManager",rollbackFor = Exception.class)
    public void retractMessage(Long messageId, byte isManager, String loginIp, LoginUser loginUser, UserTypeEnum userTypeEnum) {

        Date nowTime = new Date();
        String yyyyMM = DateUtils.parseDateToStr("yyyyMM", nowTime);
        ChatRoomRecord retractChatRoomRecord = chatRoomRecordRepository.getChatRoomRecord(messageId, userTypeEnum == UserTypeEnum.PLAYER ? loginUser.getUserId() : null);
        if(retractChatRoomRecord == null){
            throw new ServiceException("撤回消息不存在!");
        }else if(retractChatRoomRecord.getMsgStatus() == MsgStatusEnum.RETRACT.getStatus()){
            throw new ServiceException("该消息已经撤回!");
        }else if (userTypeEnum == UserTypeEnum.PLAYER && !DateUtils.isBetween(retractChatRoomRecord.getSendTime(), DateUtils.addMinute(nowTime,-2), nowTime,DateUtils.LEFT_OPEN_RIGHT_OPEN, DateUtils.COMP_MODEL_DATETIME)) {
            throw new ServiceException("发送消息超过2分钟不能被撤回!");
        }

        RetractMessageVo retractMessageVo = new RetractMessageVo();
        retractMessageVo.setMessageId(messageId);
        retractMessageVo.setMsgType(retractChatRoomRecord.getMsgType());
        retractMessageVo.setIsManager(isManager);
        ChatMessageType chatMessageType = ChatMessageType.RETRACT_MESSAGE;

        Long retractMessageId = nextId();
        ChatRoomMessageVo chatRoomMessageVo = new ChatRoomMessageVo();
        chatRoomMessageVo.setMessageId(retractMessageId);
        chatRoomMessageVo.setMsgType(chatMessageType.getType());
        chatRoomMessageVo.setMsgBody(retractMessageVo);
        chatRoomMessageVo.setSendTime(new Date());
        chatRoomMessageVo.setUserId(loginUser.getUserId());
        chatRoomMessageVo.setUserIdentifier(chatroomFacade.getUserIdentifier(loginUser.getUserId(),loginUser.getUsername()));
        String userNickname =  loginUser.getUser().getNickName();
        chatRoomMessageVo.setUserName(StringUtils.isBlank(userNickname) ? loginUser.getUsername() : userNickname);
        chatRoomMessageVo.setSendType((byte) 1);
        chatRoomMessageVo.setReport(false);
        chatRoomMessageVo.setReportNumber(0);
        chatRoomMessageVo.setMsgStatus(MsgStatusEnum.NORMAL.getStatus());

        ChatRoomRecord chatRoomRecord = new ChatRoomRecord();

        CglibBeanCopierUtils.copyProperties(chatRoomRecord, chatRoomMessageVo);
        chatRoomRecord.setSendUserIp(loginIp);
        chatRoomRecord.setUserName(loginUser.getUsername());
        chatRoomRecord.setSeqDate(yyyyMM);
        chatRoomRecord.setSeq(nextSeq());
        chatRoomRecordRepository.saveChatRoomRecord((chatRoomRecord));
        retractChatRoomRecord.setMsgStatus(MsgStatusEnum.RETRACT.getStatus());
        //TODO 更新消息记录为测回
        chatRoomRecordRepository.updateMsgStatus( messageId, (byte)2);

        //保存大厅消息撤回记录.
        MessageRecallRecord messageRecallRecord = new MessageRecallRecord();
        messageRecallRecord.setId(snowflakeIdUtils.nextId());
        messageRecallRecord.setMessageId(messageId);
        messageRecallRecord.setMsgType(retractChatRoomRecord.getMsgType());
        messageRecallRecord.setMsgBody(retractChatRoomRecord.getMsgBody());
        messageRecallRecord.setSendUserId(retractChatRoomRecord.getUserId());
        messageRecallRecord.setSendUserName(retractChatRoomRecord.getUserName());
        messageRecallRecord.setSendNickName(retractChatRoomRecord.getNickName());
        messageRecallRecord.setRecallUserId(loginUser.getUserId());
        messageRecallRecord.setRecallUserName(loginUser.getUsername());
        messageRecallRecord.setRecallSource((byte)1);
        messageRecallRecord.setRecallTime(new Date());
        if(userTypeEnum == UserTypeEnum.MANAGE){
            messageRecallRecord.setRecallType((byte)1);
            messageRecallRecord.setRecallRemark("后台管理员撤回大厅消息!");
        }else if(userTypeEnum == UserTypeEnum.PLAYER){
            messageRecallRecord.setRecallType((byte)2);
            messageRecallRecord.setRecallRemark("前端会员撤回大厅消息!");
        }

        messageRecallRecordService.addMessageRecallRecord(messageRecallRecord);

        chatRoomMessageVo.setUserName(StringTools.realNameChange(chatRoomMessageVo.getUserName()));
        if(isManager == 0){
            chatRoomMessageVo.setIsSelf((byte) 1);
        }else {
            chatRoomMessageVo.setIsSelf((byte) 0);
        }

        sendMessage(chatRoomMessageVo,ChatTopic.CHAT_ROOM_MESSAGE);
    }


    private Long nextId(){
        return snowflakeIdUtils.nextId();
    }

    /**
     * 定时删除大厅消息.
     * @return
     */
    @Override
    public AjaxResult deleteOnTimeChatRoomRecord(int day) {

        //七天前.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -day);
        Date time = calendar.getTime();

        chatRoomRecordRepository.deleteOnTimeChatFriendRecord(time);
        return new AjaxResult();
    }

    @Override
    public void saveChatRoomRecord(Object replaceMsg) {
        LoginMember member = GeneralUtils.getCurrentLoginUser();

        ChatRoomRecord chatRoomRecord = new ChatRoomRecord();
        chatRoomRecord.setSendUserIp(GeneralUtils.getLoginUserAttr("IP"));
        chatRoomRecord.setMessageId(nextId());
        chatRoomRecord.setUserIdentifier(chatroomFacade.getUserIdentifier(member.getUserId()
                ,member.getUsername()));
        chatRoomRecord.setUserName(member.getUsername());

        if (nicknameBlackListService.checkBlackList(member)){
            chatRoomRecord.setNickName("用户"+member.getShortUrl());
        }else {
            chatRoomRecord.setNickName(member.getNickName());
        }
        chatRoomRecord.setMsgBody(replaceMsg);
        chatRoomRecord.setUserId(member.getUserId());
        chatRoomRecord.setUserPortrait(member.getPhoto());
        chatRoomRecord.setMsgStatus(MsgStatusEnum.NORMAL.getStatus());
        ChatRoomRecordService currentProxy = (ChatRoomRecordService)AopContext.currentProxy();
        currentProxy.saveChatRoomRecord(chatRoomRecord);
    }

    @Override
    public void saveChatRoomRecord(ChatRoomRecord chatRoomRecord) {
        ChatMessageProcess bean = SpringContextUtil.getBean(ChatMessageProcess.class);
        if (bean==null){
            throw new ServiceException(MessageUtils.message("system.error"));
        }
        bean.saveChatRoomRecord(chatRoomRecord);
    }




    private Long nextSeq() {

        return GeneralUtils.getNextMessageSeq("chatRoomMessageSeq:",
                ()-> chatRoomRecordRepository.getMaxSeq());
    }



    public void sendMessage(ChatRoomMessageVo msg,ChatTopic topic) {
        msg.setUserId(null);
        msg.setUserName(null);
        MessageEventGenerator.broadCastMessage2ChatRoom(msg, MessageTypeEnum.CHATROOM,topic);
    }
}

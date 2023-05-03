package com.ruoyi.chat.component.message;

import com.ruoyi.chat.component.bootstrap.BootstrapServer;
import com.ruoyi.chat.component.message.task.ChatRoomMessageTask;
import com.ruoyi.chatroom.db.domain.ChatRoomRecord;
import com.ruoyi.chatroom.db.repository.ChatRoomRecordRepository;
import com.ruoyi.chatroom.db.vo.ChatRoomMessageVo;
import com.ruoyi.chatroom.service.ChatMessageProcess;
import com.ruoyi.common.constant.TopicConstants;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.chat.enums.MessageTypeEnum;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.ChatTopic;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.chat.GeneralUtils;
import com.ruoyi.common.utils.chat.MessageEventGenerator;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 在缓存中的seq 被清除,或者跨月份seq重置之后，聊天室的message通过 这个类中的队列进行插入库以及发送消息
 * 直到seq重新被建立并且 队列中的消息小于5 ，各个
 * @param
 * @Author: sddd
 * @Date: 2022/8/16,12:43
 * @return:
 **/

@Component
@Slf4j
public class ChatRoomMessageProcess implements ChatMessageProcess {

    private final static LinkedBlockingQueue<ChatRoomMessageTask> CHAT_ROOM_QUEUE = new LinkedBlockingQueue<>();

    private final static long MAX_SIZE=1000;

    @Resource
    private ChatRoomRecordRepository chatRoomRecordRepository;
    @Resource
    private RedisCache redisCache;
    @Resource
    private BootstrapServer bootstrapServer;

    @Override
    public void saveChatRoomRecord(ChatRoomRecord chatRoomRecord){
        Date date = new Date();
        String yyyyMM = DateUtils.parseDateToStr("yyyyMM",date );
        chatRoomRecord.setSeqDate(yyyyMM);
        chatRoomRecord.setSendTime(date);
        Long nextSeq = redisCache.existAndIncr("chatRoomMessageSeq:");
        //如果缓存中没有消息序列的缓存，或者执行队列中还有多于5条以上的数据，那么消息都走队列，避免出现太多后发先到的情况;
        if (nextSeq==-1L) {
            if (CHAT_ROOM_QUEUE.size()>=MAX_SIZE){
                throw new ServiceException(MessageUtils.message("system.busy"));
            }
            goQueue(chatRoomRecord);
        }else if (CHAT_ROOM_QUEUE.size() > 5){
            chatRoomRecord.setSeq(nextSeq);
            goQueue(chatRoomRecord);
        }else {
            //如果一切正常，则直接存库并发消息;
            chatRoomRecord.setSeq(nextSeq);
            saveAndSendMessage(chatRoomRecord,GeneralUtils.getCurrentLoginUser());
        }
    }

    private void goQueue (ChatRoomRecord chatRoomRecord){
        ChatRoomMessageTask chatRoomMessageTask = new ChatRoomMessageTask();
        chatRoomMessageTask.setRoomRecord(chatRoomRecord);
        chatRoomMessageTask.setLoginMember(GeneralUtils.getCurrentLoginUser());
        try {
            CHAT_ROOM_QUEUE.put(chatRoomMessageTask);
        } catch (InterruptedException e) {
            log.error("聊天室消息本地队列[CHAT_ROOM_QUEUE]添加失败",e);
        }
    }


    private Long tryNextSeq(){
        return GeneralUtils.getNextMessageSeqByScript("chatRoomMessageSeq:",
                ()-> chatRoomRecordRepository.getMaxSeq());
    }

    /**
     * 消费本地队列中的聊天室消息，存库以及发送消息
     */
    @Async("taskExecutor")
    @Override
    public void waitToHandle() {
        log.info("[CHAT_ROOM_QUEUE] process start ...{}",Thread.currentThread().getName());
        boolean startFlag=false;
        while (!startFlag||bootstrapServer.isRun()){
            if (!startFlag&&bootstrapServer.isRun()){
                startFlag=true;
            }
            try {
                ChatRoomMessageTask poll = CHAT_ROOM_QUEUE.poll(3, TimeUnit.SECONDS);
                if (poll!=null){
                    ChatRoomRecord roomRecord = poll.getRoomRecord();
                    if (roomRecord.getSeq()==null||roomRecord.getSeq()==0L){
                        roomRecord.setSeq(tryNextSeq());
                    }
                    saveAndSendMessage(roomRecord,poll.getLoginMember());
                }
            }catch (Exception e){
                log.error("聊天室消息本地队列[CHAT_ROOM_QUEUE]消费出现错误",e);
            }
        }
    }



    private void saveAndSendMessage(ChatRoomRecord chatRoomRecord,LoginMember loginMember){
        //存库
        chatRoomRecordRepository.saveChatRoomRecord(chatRoomRecord);
        ChatRoomMessageVo chatRoomMessageVo = new ChatRoomMessageVo();
        chatRoomMessageVo.setSeq(GeneralUtils.seqToVo(chatRoomRecord.getSeqDate(),chatRoomRecord.getSeq()));
        CglibBeanCopierUtils.copyProperties(chatRoomMessageVo,chatRoomRecord);
        sendMessage(chatRoomMessageVo, loginMember);
    }




    private void sendMessage(ChatRoomMessageVo msg, LoginMember loginMember) {
        msg.setUserId(null);
        msg.setUserName(null);
        MessageEventGenerator.broadCastMessage2ChatRoom(msg, MessageTypeEnum.CHATROOM, ChatTopic.CHAT_ROOM_MESSAGE);
    }
}

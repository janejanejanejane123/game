package com.ruoyi.chatroom.db.repository;

import com.mongodb.client.result.DeleteResult;
import com.ruoyi.chatroom.db.domain.ChatRoomRecord;
import com.ruoyi.common.enums.MsgStatusEnum;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @description: 大厅聊天记录Mogo
 * @author: nn
 * @create: 2019-04-08 11:42
 **/
@Service
@Lazy
public class ChatRoomRecordRepository {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 获取消息集合.
     * @param query  查询对象.
     * @return
     */
    public List<ChatRoomRecord> queryChatRoomRecordList(Query query){
        List<ChatRoomRecord> chatFriendsList = mongoTemplate.find(query, ChatRoomRecord.class);
        return chatFriendsList;
    }

    /**
     * @Description: 保存消息
     * @param chatRoomRecord 消息
     * @return void
     * @author nn
     * @date 2020/1/13 15:40
     */
    public void saveChatRoomRecord(ChatRoomRecord chatRoomRecord){
        mongoTemplate.insert(chatRoomRecord);
    }

    /**
     * @Description: 举报消息
     * @param messageId 消息id
     * @return void
     */
    public void reportMessage(Long messageId){
        Query query = new Query(Criteria.where("messageId").is(messageId));
        Update update = new Update();
        update.set("isReport",true).inc("reportNumber",1);
        mongoTemplate.updateFirst(query,update, ChatRoomRecord.class);
    }

    /**
     * 删除消息.
     * @param messageId
     * @return
     */
    public ChatRoomRecord deleteChatRoomRecord(Long messageId) {
        Criteria criteria = Criteria.where("messageId").is(messageId);
        Update update = new Update();
        update.set("msgStatus",(byte) 0);
        Query query = new Query(criteria);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.remove(false);
        options.returnNew(false);
        options.upsert(false);
       return mongoTemplate.findAndModify(query,update,options, ChatRoomRecord.class);
    }

    public ChatRoomRecord getChatRoomRecord(Long messageId, Long userId) {
        Criteria criteria = Criteria.where("messageId").is(messageId).and("msgStatus").in(MsgStatusEnum.NORMAL.getStatus()
                ,MsgStatusEnum.RETRACT.getStatus());
        if(userId != null){
            criteria.and("userId").is(userId);
        }
       return mongoTemplate.findOne(new Query(criteria), ChatRoomRecord.class);
    }

    /**
     * 获取下一条消息.
     * @param messageId
     * @return
     */
    public ChatRoomRecord getLastChatRoomRecord(Long messageId) {
        Criteria criteria = Criteria.where("messageId").lt(messageId);
        Query query = new Query();
        query.addCriteria(criteria);
        query.with(Sort.by(
                Sort.Order.desc("messageId")
        ));
        return mongoTemplate.findOne(query, ChatRoomRecord.class);
    }

    /**
     * 修改消息状态
     * @param messageId
     * @return
     */
    public ChatRoomRecord updateMsgStatus(Long messageId, byte msgStatus) {
        Criteria criteria = Criteria.where("messageId").is(messageId);
        Update update = new Update();
        update.set("msgStatus",msgStatus);
        Query query = new Query(criteria);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.remove(false);
        options.returnNew(false);
        options.upsert(false);
        return mongoTemplate.findAndModify(query,update,options, ChatRoomRecord.class);
    }

    /**
     * 获取大厅最后一条消息的MessageId
     * @return
     */
    public Long getLastMessageId() {
        Query query = new Query();
        Criteria criteria = new Criteria();
        query.addCriteria(criteria);
        query.with(Sort.by(
                Sort.Order.desc("_id")
        ));
        ChatRoomRecord chatRoomRecord = mongoTemplate.findOne(query, ChatRoomRecord.class);
        if(chatRoomRecord != null){
            return chatRoomRecord.getMessageId();
        }
        return 0L;
    }

    /**
     * 定时删除大厅消息.
     * @return
     */
    public Long deleteOnTimeChatFriendRecord(Date sendTime){
        Query query = new Query();
        Criteria criteria = Criteria.where("sendTime").lt(sendTime);
        query.addCriteria(criteria);
        DeleteResult deleteResult = mongoTemplate.remove(query, ChatRoomRecord.class);
        return deleteResult.getDeletedCount();
    }

    /**
     * 消息的序列;
     * @return
     */
    public Long getMaxSeq() {
        Query query = new Query();
        query.with(Sort.by(
                Sort.Order.desc("seq")
        ));
        ChatRoomRecord one = mongoTemplate.findOne(query, ChatRoomRecord.class);
        if (one==null){
            return 0L;
        }
        return one.getSeq();
    }
}

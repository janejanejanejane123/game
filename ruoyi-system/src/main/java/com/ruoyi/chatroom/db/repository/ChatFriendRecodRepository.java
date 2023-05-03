package com.ruoyi.chatroom.db.repository;

import com.mongodb.client.result.DeleteResult;
import com.ruoyi.chatroom.db.domain.ChatFriendRecord;
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
 * @description: 朋友聊天记录
 * @author: nn
 * @create: 2022-07-19 18:58
 **/
@Service
@Lazy
public class ChatFriendRecodRepository {
    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 保存细聊记录.
     * @param chatFriendRecord
     */
    public void saveChatFriendRecord(ChatFriendRecord chatFriendRecord) {
        mongoTemplate.insert(chatFriendRecord);
    }

    public ChatFriendRecord getChatFriendRecordById(Long messageId){
        return mongoTemplate.findById(messageId,ChatFriendRecord.class);
    }

    public ChatFriendRecord getChatFriendRecordById(Long messageId,Long userId, Long myFrendId){
        Criteria criteria = Criteria.where("messageId").is(messageId);
        if(userId != null){
            criteria.and("sendUserId").is(userId);
        }
        if(myFrendId != null){
            criteria.and("myFrendId").is(myFrendId);
        }
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query,ChatFriendRecord.class);
    }


    public List<ChatFriendRecord> queryFriendChatRecordByIds(List<Long> msgIdLst) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(msgIdLst));
        query.with(Sort.by(Sort.Direction.ASC, "_id"));
        return mongoTemplate.find(query, ChatFriendRecord.class);
    }


    public List<ChatFriendRecord> queryChatFriendRecordByIds(List<Long> msgIdLst) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(msgIdLst));
        query.with(Sort.by(
                Sort.Order.asc("_id")
        ));
        return mongoTemplate.find(query, ChatFriendRecord.class);
    }

    public ChatFriendRecord updateMessage( Long messageId,Long userId, Long myFrendId, MsgStatusEnum msgStatusEnum) {
        Criteria criteria = Criteria.where("messageId").is(messageId);
        if(userId != null){
            criteria.and("sendUserId").is(userId);
        }
        if(myFrendId != null){
            criteria.and("myFrendId").is(myFrendId);
        }
        Query query = new Query(criteria);
        Update update = new Update();
        update.set("msgStatus", msgStatusEnum.getStatus());
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.remove(false);
        options.returnNew(false);
        options.upsert(false);
        return mongoTemplate.findAndModify(query,update,options, ChatFriendRecord.class);
    }


    public ChatFriendRecord getMyChatFriendRecordById(Long userId, Long originalMessageId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(originalMessageId).andOperator( new Criteria().orOperator(
                Criteria.where("myFrendId").is(userId),
                Criteria.where("sendUserId").is(userId)
        )));
        return mongoTemplate.findOne(query, ChatFriendRecord.class);
    }


    /**
     * 举报消息.
     * @param messageId
     */
    public void reportMessage(Long messageId) {
        Query query = new Query(Criteria.where("messageId").is(messageId));
        Update update = new Update();
        update.set("isReport",true).inc("reportNumber",1);
        mongoTemplate.updateFirst(query,update, ChatFriendRecord.class);
    }

    /**
     * 定时删除私聊消息.
     * @return
     */
    public Long deleteOnTimeChatFriendRecord(Date sendTime){
        Query query = new Query();
        Criteria criteria = Criteria.where("sendTime").lt(sendTime);
        query.addCriteria(criteria);
        DeleteResult deleteResult = mongoTemplate.remove(query, ChatFriendRecord.class);
        return deleteResult.getDeletedCount();
    }
}

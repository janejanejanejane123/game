package com.ruoyi.chatroom.db.repository;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.ruoyi.chatroom.db.domain.MessageRecallRecord;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 消息撤回记录
 * @author: nn
 * @create: 2022-07-19 11:42
 **/
@Service
@Lazy
public class MessageRecallRecordRepository {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 查询消息撤回记录集合.
     * @param query  查询对象.
     * @return
     */
    public List<MessageRecallRecord> queryMessageRecallRecordList(Query query){
        List<MessageRecallRecord> messageRecallRecordList = mongoTemplate.find(query, MessageRecallRecord.class);
        return messageRecallRecordList;
    }

    /**
     * 查询消息撤回记录数.
     * @param query  查询条件对象.
     * @return
     */
    public int queryMessageRecallRecordCount(Query query){
        long count = mongoTemplate.count(query, MessageRecallRecord.class);
        return (int)count;
    }

    /**
     * 查询消息撤回记录.
     * @param query  查询对象.
     * @return
     */
    public MessageRecallRecord getMessageRecallRecord(Query query){
        MessageRecallRecord messageRecallRecord = mongoTemplate.findOne(query, MessageRecallRecord.class);
        return messageRecallRecord;
    }

    /**
     * 保存消息撤回记录.
     * @param messageRecallRecord  保存对象.
     * @return
     */
    public MessageRecallRecord saveMessageRecallRecord(MessageRecallRecord messageRecallRecord){
        return mongoTemplate.insert(messageRecallRecord);
    }

    /**
     * 修改消息撤回记录.
     * @param query  查询条件对象.
     * @param update 修改赋值对象.
     * @return
     */
    public Long updateMessageRecallRecord(Query query,Update update){
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update, MessageRecallRecord.class);
        return updateResult.getModifiedCount();
    }

    /**
     * 删除消息撤回记录
     * @param query
     */
    public Long deleteMessageRecallRecord(Query query){
        DeleteResult deleteResult = mongoTemplate.remove(query, MessageRecallRecord.class);
        return deleteResult.getDeletedCount();
    }

    /**
     * 根据id查询消息撤回记录.
     * @param id  id.
     * @return
     */
    public MessageRecallRecord getMessageRecallRecordById(Long id){
        MessageRecallRecord messageRecallRecord = mongoTemplate.findById(id, MessageRecallRecord.class);
        return messageRecallRecord;
    }
}

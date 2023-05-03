package com.ruoyi.chatroom.db.repository;

import com.mongodb.client.result.UpdateResult;
import com.ruoyi.chatroom.db.domain.ChatFriendRecord;
import com.ruoyi.chatroom.db.domain.ChatRoomRecord;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 消息管理.
 * @author nn
 * @date 2022/07/29
 */
@Service
public class MessageManagementRepostory {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 查询记录数(大厅).
     * @param query  查询条件对象.
     * @return
     */
    public int selectChatRoomRecordCount(Query query){
        long count = mongoTemplate.count(query, ChatRoomRecord.class);
        return (int)count;
    }

    /**
     * 查询记录集合(大厅).
     * @param query  查询对象.
     * @return
     */
    public List<ChatRoomRecord> selectChatRoomRecordList(Query query){
        List<ChatRoomRecord> chatRoomRecordList = mongoTemplate.find(query, ChatRoomRecord.class);
        return chatRoomRecordList;
    }

    /**
     *  修改(大厅).
     * @param query 查询条件对象.
     * @param update 修改参数对象.
     * @return
     */
    public Long updateChatRoomRecord(Query query, Update update) {
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update, ChatRoomRecord.class);
        return  updateResult.getModifiedCount();
    }

    /**
     * 查询记录数(私聊).
     * @param query  查询条件对象.
     * @return
     */
    public int selectChatFriendRecordCount(Query query){
        long count = mongoTemplate.count(query, ChatFriendRecord.class);
        return (int)count;
    }

    /**
     * 查询记录集合(私聊).
     * @param query  查询对象.
     * @return
     */
    public List<ChatFriendRecord> selectChatFriendRecordList(Query query){
        List<ChatFriendRecord> friendChatRecordList = mongoTemplate.find(query, ChatFriendRecord.class);
        return friendChatRecordList;
    }

    /**
     *  修改(私聊).
     * @param query 查询条件对象.
     * @param update 修改参数对象.
     * @return
     */
    public Long updateChatFriendRecord(Query query, Update update) {
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update, ChatFriendRecord.class);
        return  updateResult.getModifiedCount();
    }
}

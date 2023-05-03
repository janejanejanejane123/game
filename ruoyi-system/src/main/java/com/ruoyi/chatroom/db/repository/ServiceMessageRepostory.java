package com.ruoyi.chatroom.db.repository;

import com.ruoyi.chatroom.db.domain.ChatFriendRecord;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author nn
 * @date 2022/07/31
 */
@Service
public class ServiceMessageRepostory {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 查询记录数(私聊).
     * @param query  查询条件对象.
     * @return
     */
    public Long selectChatFriendRecordCount(Query query){
        long count = mongoTemplate.count(query, ChatFriendRecord.class);
        return count;
    }

    /**
     * 查询记录(私聊).
     * @param query  查询对象.
     * @return
     */
    public ChatFriendRecord getChatFriendRecord(Query query){
        ChatFriendRecord friendChatRecord = mongoTemplate.findOne(query, ChatFriendRecord.class);
        return friendChatRecord;
    }

    /**
     * 查询记录集合(私聊).
     * @param query  查询对象.
     * @return
     */
    public List<ChatFriendRecord> selectFriendChatRecordList(Query query){
        List<ChatFriendRecord> friendChatRecordList = mongoTemplate.find(query, ChatFriendRecord.class);
        return friendChatRecordList;
    }

    /**
     * 查询记录集合(私聊).
     * @param aggregation  查询对象.
     * @return
     */
    public AggregationResults<ChatFriendRecord> selectAggregationResults(Aggregation aggregation){
        AggregationResults<ChatFriendRecord> aggregationResults = mongoTemplate.aggregate(aggregation ,ChatFriendRecord.class,ChatFriendRecord.class);
        return aggregationResults;
    }

    /**
     * 查询记录集合(记录数).
     * @param aggregation  查询对象.
     * @return
     */
    public int selectAggregationResultsConut(Aggregation aggregation){
        AggregationResults<ChatFriendRecord> aggregationResults = mongoTemplate.aggregate(aggregation ,ChatFriendRecord.class,ChatFriendRecord.class);
        return aggregationResults.getMappedResults().size();
    }
}

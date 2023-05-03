package com.ruoyi.chatroom.db.repository;

import com.ruoyi.chatroom.db.domain.ChatFriendOffline;
import com.ruoyi.chatroom.db.vo.OfflineCountVo;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 离校消息DB
 * @author: j
 * @create: 2019-11-12 17:40
 **/
@Component
public class ChatFriendOfflineRepository {
    @Resource
    private MongoTemplate mongoTemplate;
    /**
     * @Description: 保存离线消息
     * @param chatFriendOffline
     * @return void
     * @author nn
     * @date 2022/07/19 17:42
     */
    public void saveChatFriendOfflineRecord(ChatFriendOffline chatFriendOffline) {
        mongoTemplate.insert(chatFriendOffline);
    }

    public void deleteChatFriendOffline(Criteria...chatFriendOfflinerray) {
        Query query = new Query();
        Criteria criteria = new Criteria().orOperator(chatFriendOfflinerray);
        query.addCriteria(criteria);
        mongoTemplate.remove(query, ChatFriendOffline.class);
    }

    public long queryMyOfflineRecordCount(Long userId,Long friendId) {
        Query query = new Query();
        Criteria receiverUid = Criteria.where("receiverUid").is(userId);
        if (friendId!=null){
            receiverUid.and("senderUid").is(friendId);
        }
        query.addCriteria(receiverUid);
        return mongoTemplate.count(query, ChatFriendOffline.class);
    }

    public List<ChatFriendOffline> queryMyOfflineRecordList(Long userId, Long lastMessageId, int pageSize,Long friendId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("receiverUid").is(userId);
        criteria.size(pageSize);
        if(lastMessageId != null){
            criteria.and("messageId").gt(lastMessageId);
        }
        if (friendId!=null){
            criteria.and("senderUid").is(friendId);
        }
        query.addCriteria(criteria);
        query.with(Sort.by(
                Sort.Order.asc("_id")
        ));
        return mongoTemplate.find(query, ChatFriendOffline.class);
    }

    public List<Map>  customerServerOfflineMessageCount(Long userId){
        Criteria criteria = Criteria.where("receiverUid").is(userId);
        //match
        MatchOperation match = Aggregation.match(criteria);
        //group count
        GroupOperation count = Aggregation.group("senderUid").count().as("offlineCount");
        //sort
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "offlineCount");
        TypedAggregation<ChatFriendOffline> aggregation = Aggregation.newAggregation(ChatFriendOffline.class,
                match,
                count,
                sort);

        AggregationResults<Map> aggregate = mongoTemplate.aggregate(aggregation, Map.class);
        return aggregate.getMappedResults();

    }

}

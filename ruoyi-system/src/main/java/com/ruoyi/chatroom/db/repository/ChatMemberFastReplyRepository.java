package com.ruoyi.chatroom.db.repository;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.ruoyi.chatroom.db.domain.ChatMemberFastReply;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 会员快捷回复Mogo
 * @author: nn
 * @create: 2022-07-19 11:42
 **/
@Service
@Lazy
public class ChatMemberFastReplyRepository {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     *  查询我的快捷回复.
     * @param myUserId  我的userId.
     * @return
     */
    public List<ChatMemberFastReply> queryChatMemberFastReplyListByUserId(Long myUserId){
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(myUserId));
        query.with(Sort.by(
                Sort.Order.desc("id")
        ));
        List<ChatMemberFastReply> chatMemberFastReplyList = mongoTemplate.find(query, ChatMemberFastReply.class);
        return chatMemberFastReplyList;
    }

    /**
     * 查询我的快捷记录数.
     * @param query  查询条件对象.
     * @return
     */
    public Long selectChatMemberFastReplyCount(Query query){
        return mongoTemplate.count(query, ChatMemberFastReply.class);
    }

    /**
     * 保存回复内容.
     * @param chatMemberFastReply  保存对象.
     * @return
     */
    public ChatMemberFastReply saveChatMemberFastReply(ChatMemberFastReply chatMemberFastReply){
        return mongoTemplate.insert(chatMemberFastReply);
    }

    /**
     *  修改回复内容.
     * @param query   查询条件对象.
     * @param update  修改赋值对象.
     * @return
     */
    public Long updateChatMemberFastReply(Query query, Update update){
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update, ChatMemberFastReply.class);
        return updateResult.getModifiedCount();
    }

    /**
     *  删除回复内容.
     * @param query  查询条件对象.
     * @return
     */
    public Long delectChatMemberFastReply(Query query){
        DeleteResult deleteResult = mongoTemplate.remove(query, ChatMemberFastReply.class);
        return deleteResult.getDeletedCount();
    }

}

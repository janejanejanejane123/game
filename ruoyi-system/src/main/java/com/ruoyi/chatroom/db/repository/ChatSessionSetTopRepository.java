package com.ruoyi.chatroom.db.repository;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.ruoyi.chatroom.db.domain.ChatSessionSetTop;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 会话置顶Mogo
 * @author: nn
 * @create: 2020-01-17 11:42
 **/
@Service
@Lazy
public class ChatSessionSetTopRepository {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 查询会话置顶集合(返回指定的字段).
     * @param query  查询对象.
     * @return
     */
    public List<ChatSessionSetTop> mySessionSetTopList(Query query){
        List<ChatSessionSetTop> chatSessionSetTopList = mongoTemplate.findDistinct(query,"conversationId",ChatSessionSetTop.class,ChatSessionSetTop.class);
        return chatSessionSetTopList;
    }

    /**
     * 获取会话置顶集合.
     * @param query  查询条件对象.
     * @return
     */
    public List<ChatSessionSetTop> queryChatSessionSetTopList(Query query){
        List<ChatSessionSetTop> chatSessionSetTopList = mongoTemplate.find(query, ChatSessionSetTop.class);
        return chatSessionSetTopList;
    }

    /**
     * 查询会话置顶.
     * @param query  查询条件对象.
     * @return
     */
    public ChatSessionSetTop getChatSessionSetTop(Query query){
        return mongoTemplate.findOne(query,ChatSessionSetTop.class);
    }

    /**
     * 根据userId查询信息.
     * @param
     * @return
     */
    public List<ChatSessionSetTop>  getChatSessionSetTopByUserId(Long userId) {

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        List<ChatSessionSetTop>  chatSessionSetTopList = mongoTemplate.find(query, ChatSessionSetTop.class);
        return chatSessionSetTopList;
    }

    /**
     * 保存会话置顶.
     * @param chatSessionSetTop  保存对象.
     * @return
     */
    public ChatSessionSetTop saveChatSessionSetTop(ChatSessionSetTop chatSessionSetTop){
        return mongoTemplate.insert(chatSessionSetTop);
    }

    /**
     * 修改会话置顶.
     * @param query   查询条件对象.
     * @param update  修改参数对象.
     * @return
     */
    public Long updateChatSessionSetTop(Query query,Update update){
        UpdateResult updateResult = mongoTemplate.updateFirst(query,update, ChatSessionSetTop.class);
        return updateResult.getModifiedCount();
    }

    /**
     *  删除会话置顶.
     * @param query  查询条件对象.
     * @return
     */
    public Long deleteChatSessionSetTop(Query query){
        DeleteResult deleteResult = mongoTemplate.remove(query, ChatSessionSetTop.class);
        return deleteResult.getDeletedCount();
    }

}

package com.ruoyi.chatroom.db.repository;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.ruoyi.chatroom.db.domain.ChatUserInfo;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @description: 用户信息Mogo
 * @author: nn
 * @create: 2022-07-19 11:42
 **/
@Service
@Lazy
public class ChatUserInfoRepository {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 根据userId查询信息.
     * @param
     * @return
     */
    public ChatUserInfo getChatUserInfoByUserId(Long userId) {

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        ChatUserInfo chatUserInfo = mongoTemplate.findOne(query, ChatUserInfo.class);
        return chatUserInfo;
    }


    /**
     * 获取用户信息集合.
     * @param query  查询条件对象.
     * @return
     */
    public List<ChatUserInfo> queryChatUserInfoList(Query query){
        List<ChatUserInfo> chatTeams = mongoTemplate.find(query, ChatUserInfo.class);
        return chatTeams;
    }

    /**
     * 保存用户.
     * @param chatUserInfo  保存对象.
     * @return
     */
    public ChatUserInfo saveChatUserInfo(ChatUserInfo chatUserInfo){
        return mongoTemplate.insert(chatUserInfo);
    }

    /**
     * 查询用户.
     * @param query  查询条件对象.
     * @return
     */
    public ChatUserInfo getChatUserInfo(Query query){
        return mongoTemplate.findOne(query,ChatUserInfo.class);
    }

    /**
     * 修改用户.
     * @param query   查询条件对象.
     * @param update  修改参数对象.
     * @return
     */
    public Long updateChatUserInfo(Query query, Update update){
        UpdateResult updateResult = mongoTemplate.updateFirst(query,update, ChatUserInfo.class);
        return updateResult.getModifiedCount();
    }

    public Long updateChatUserInfo(ChatUserInfo chatUserInfo){
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(chatUserInfo.getId()));
        Update update = new Update();
        update.set("runId",chatUserInfo.getRunId());
        UpdateResult updateResult = mongoTemplate.updateFirst(query,update, ChatUserInfo.class);
        return updateResult.getModifiedCount();
    }


    /**
     * 删除用户信息对象.
     * @param query  查询条件对象.
     * @return
     */
    public Long deleteChatUserInfo(Query query){
        DeleteResult deleteResult = mongoTemplate.remove(query, ChatUserInfo.class);
        return deleteResult.getDeletedCount();
    }

    /**
     * 删除用户信息对象.
     * @return
     */
    public Long deleteChatUserInfoByDate(Date createTime){
        Query query = new Query();
        Criteria criteria = Criteria.where("createTime").lt(createTime);
        query.addCriteria(criteria);
        DeleteResult deleteResult = mongoTemplate.remove(query, ChatUserInfo.class);
        return deleteResult.getDeletedCount();
    }
}

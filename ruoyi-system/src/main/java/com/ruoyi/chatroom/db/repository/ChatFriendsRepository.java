package com.ruoyi.chatroom.db.repository;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.ruoyi.chatroom.db.domain.ChatFriends;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 好友记录Mogo
 * @author: nn
 * @create: 2022-07-19 11:42
 **/
@Service
@Lazy
public class ChatFriendsRepository {

    @Resource
    private MongoTemplate mongoTemplate;


    /**
     * 查询好友记录集合.
     * @param query  查询对象.
     * @return
     */
    public List<ChatFriends> queryChatFriendsList(Query query){
        List<ChatFriends> chatFriendsList = mongoTemplate.find(query, ChatFriends.class);
        return chatFriendsList;
    }

    /**
     * 查询好友记录数.
     * @param query  查询条件对象.
     * @return
     */
    public Long selectChatFriendsCount(Query query){
        long count = mongoTemplate.count(query, ChatFriends.class);
        return count;
    }

    /**
     *  查询好友信息.
     * @param query 查询条件对象.
     * @return
     */
    public ChatFriends getChatFriend(Query query) {
        return mongoTemplate.findOne(query,ChatFriends.class);
    }

      /**
     *  保存好友信息.
     * @param chatFriends 保存对象.
     * @return
     */
    public ChatFriends saveChatFriend(ChatFriends chatFriends) {
        return  mongoTemplate.insert(chatFriends);
    }

    /**
     *  修改好友信息.
     * @param query 查询条件对象.
     * @param update 修改参数对象.
     * @return
     */
    public Long updateChatFriend(Query query,Update update) {
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update, ChatFriends.class);
        return  updateResult.getModifiedCount();
    }

    /**
     *  删除好友信息.
     * @param query 查询条件对象.
     * @return
     */
    public Long deleteChatFriend(Query query) {
        DeleteResult deleteResult = mongoTemplate.remove(query, ChatFriends.class);
        return  deleteResult.getDeletedCount();
    }

    /**
     * 查询好友分組集合.
     * @param query  查询对象.
     * @return
     */
    public List<ChatFriends> myGroupList(Query query){
        List<ChatFriends> chatFriendsList = mongoTemplate.findDistinct(query,"groupName",ChatFriends.class,ChatFriends.class);
        return chatFriendsList;
    }

    /**
     * 查询我的所有好友记录集合.
     * @param query  查询对象.
     * @return
     */
    public List<ChatFriends> queryMyFriendsList(Query query){
        List<ChatFriends> chatFriendsList = mongoTemplate.find(query, ChatFriends.class);
        return chatFriendsList;
    }
}



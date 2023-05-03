package com.ruoyi.chatroom.service.impl;

import com.ruoyi.chatroom.db.domain.ChatSessionSetTop;
import com.ruoyi.chatroom.db.repository.ChatSessionSetTopRepository;
import com.ruoyi.chatroom.service.ChatSessionSetTopService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description: 用户信息业务实现类.
 * @author: nn
 * @create: 2022-07-19 11:42
 **/
@Service
@Lazy
public class ChatSessionSetTopServiceImpl implements ChatSessionSetTopService {

    @Resource
    private ChatroomFacade chatroomFacade;

    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;

    @Resource
    private ChatSessionSetTopRepository chatSessionSetTopRepository;

    /**
     * 1.获取我的所有会话置顶.
     * @param myUserId  我的userId
     * @return
     */
    @Override
    public List<String> mySessionSetTopList(Long myUserId) {

        Query query = new Query();
        Criteria criteria = Criteria.where("userId").is(myUserId);
        query.with(Sort.by(Sort.Order.desc("id")));
        query.addCriteria(criteria);
        List<ChatSessionSetTop> chatSessionSetTopList = chatSessionSetTopRepository.queryChatSessionSetTopList(query);

        List<String> conversationIdList = new ArrayList<>();
        if(chatSessionSetTopList != null && chatSessionSetTopList.size() >0){
            for(ChatSessionSetTop chatSessionSetTop : chatSessionSetTopList){
                conversationIdList.add(chatSessionSetTop.getConversationId());
            }
        }

        return conversationIdList;
    }

    /**
     * 2.置顶
     * @param conversationId 会话ID
     * @param loginUser  当前登录用户信息
     * @return
     */
    @Override
    @Transactional(transactionManager = "mongoTransactionManager")
    public AjaxResult setTop(String conversationId, LoginUser loginUser) {

        //我的用户标识
        Long myUserId = loginUser.getUserId();
        String myUserIdentifier = chatroomFacade.getUserIdentifier(loginUser.getUserId(),loginUser.getUsername());
        List<ChatSessionSetTop> chatSessionSetTopList = chatSessionSetTopRepository.getChatSessionSetTopByUserId(myUserId);
        if(chatSessionSetTopList != null && chatSessionSetTopList.size() > 10){
            throw new ServiceException("对不起,您最多只能将10个会话置顶!");
        }

        Query query = new Query();
        Criteria criteria = Criteria.where("userId").is(myUserId).and("conversationId").is(conversationId);
        query.addCriteria(criteria);
        ChatSessionSetTop chatSessionSetTop = chatSessionSetTopRepository.getChatSessionSetTop(query);
        //已置顶，删除，最新置顶
        if(chatSessionSetTop != null){
            chatSessionSetTopRepository.deleteChatSessionSetTop(query);
        }
        chatSessionSetTop = new ChatSessionSetTop();
        chatSessionSetTop.setId(snowflakeIdUtils.nextId());
        chatSessionSetTop.setConversationId(conversationId);
        chatSessionSetTop.setUserId(myUserId);
        chatSessionSetTop.setUserIdentifier(myUserIdentifier);
        chatSessionSetTop.setSetTopTime(new Date());
        chatSessionSetTopRepository.saveChatSessionSetTop(chatSessionSetTop);

        return new AjaxResult();
    }

    /**
     * 3.取消置顶
     * @param conversationId 会话ID
     * @param myUserId  我的userId
     * @return
     */
    @Override
    public AjaxResult cancelTop(String conversationId, Long myUserId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("userId").is(myUserId).and("conversationId").is(conversationId);
        query.addCriteria(criteria);
        chatSessionSetTopRepository.deleteChatSessionSetTop(query);
        return new AjaxResult();
    }

}

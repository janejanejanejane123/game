package com.ruoyi.chatroom.db.repository;

import com.ruoyi.chatroom.db.domain.ChatSession;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class ChatSessionRepository {


    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;

    public ChatSession getSession(String userIdentifier,String customerIdentifier){
        Query query = new Query();
        Criteria criteria = Criteria.where("userIdentifier").is(userIdentifier)
                .and("customerIdentifier").is(customerIdentifier);
        query.addCriteria(criteria);

        return mongoTemplate.findOne(query,ChatSession.class);
    }



    public ChatSession addSession(String userIdentifier,String customerIdentifier){
        ChatSession chatSession = new ChatSession();
        chatSession.setDate(new Date());
        chatSession.setCustomerIdentifier(customerIdentifier);
        chatSession.setUserIdentifier(userIdentifier);
        chatSession.setId(snowflakeIdUtils.nextId());
        return mongoTemplate.insert(chatSession);
    }


}

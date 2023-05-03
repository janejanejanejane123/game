package com.ruoyi.chatroom.service.impl;

import com.ruoyi.chatroom.db.domain.ChatMemberFastReply;
import com.ruoyi.chatroom.db.repository.ChatMemberFastReplyRepository;
import com.ruoyi.chatroom.db.vo.ChatMemberFastReplyVo;
import com.ruoyi.chatroom.service.ChatMemberFastReplyService;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.sensitive.service.SensitiveWordService;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @description: 会员快捷回复业务实现类.
 * @author: nn
 * @create: 2020-03-31 11:42
 **/
@Service
@Lazy
public class ChatMemberFastReplyServiceImpl implements ChatMemberFastReplyService {

    @Resource
    private ChatroomFacade chatroomFacade;

    @Lazy
    @Resource
    private SensitiveWordService sensitiveWordService;

    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;

    @Resource
    private ChatMemberFastReplyRepository chatMemberFastReplyRepository;

    @Override
    public List<ChatMemberFastReplyVo> queryChatMemberFastReplyVoListByUserId(Long userId) {
        List<ChatMemberFastReply> chatMemberFastReplyList = chatMemberFastReplyRepository.queryChatMemberFastReplyListByUserId(userId);
        List<ChatMemberFastReplyVo> listVo = new CopyOnWriteArrayList<>();
        if(chatMemberFastReplyList != null && chatMemberFastReplyList.size() > 0){
            for(ChatMemberFastReply chatMemberFastReply : chatMemberFastReplyList) {
                ChatMemberFastReplyVo chatMemberFastReplyVo = new ChatMemberFastReplyVo();
                CglibBeanCopierUtils.copyProperties(chatMemberFastReplyVo, chatMemberFastReply);
                listVo.add(chatMemberFastReplyVo);
            }
        }
        return listVo;
    }

    /**
     * 保存会员快捷回复 回复内容
     * @param loginUser
     * @param replyContent
     * @return
     */
    @Override
    public Long saveChatMemberFastReply(LoginUser loginUser, String replyContent) throws Exception{

        replyContent = sensitiveWordService.checkSensitiveMsg((byte) 2, "快捷回复包含敏感关键词(%s)!", replyContent);
        //我的用户标识
        Long myUserId = loginUser.getUserId();
        String myUserIdentifier = chatroomFacade.getUserIdentifier(loginUser.getUserId(),loginUser.getUsername());

        //判断快捷回复不能超过30条,不然太多加载数据慢
        List<ChatMemberFastReplyVo> list = queryChatMemberFastReplyVoListByUserId(myUserId);
        if(list != null && list.size() > 100){
            throw new ServiceException("快捷回复过多,请先删除一些不常用的,再新增!");
        }

        //判断重复添加.
        Query query = new Query();
        Criteria criteria = Criteria.where("userId").is(myUserId).and("replyContent").is(replyContent);
        query.addCriteria(criteria);
        int count = chatMemberFastReplyRepository.selectChatMemberFastReplyCount(query).intValue();
        if(count > 0){
            throw new ServiceException("该快捷回复已存在你的列表中!");
        }

        ChatMemberFastReply chatMemberFastReply = new ChatMemberFastReply();
        chatMemberFastReply.setId(snowflakeIdUtils.nextId());
        chatMemberFastReply.setUserId(myUserId);
        chatMemberFastReply.setUserIdentifier(myUserIdentifier);
        chatMemberFastReply.setReplyContent(replyContent);
        chatMemberFastReply.setCreateTime(new Date());
        chatMemberFastReplyRepository.saveChatMemberFastReply(chatMemberFastReply);
        return null;
    }

    /**
     * 修改会员快捷回复.
     * @param id  Id
     * @param replyContent  回复内容
     * @param myUserId  我的userId
     * @return
     */
    @Override
    public Long updateChatMemberFastReply(Long id, String replyContent, Long myUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(myUserId).and("id").is(id));
        Update update = new Update();
        update.set("replyContent",replyContent);
        return chatMemberFastReplyRepository.updateChatMemberFastReply(query,update);
    }

    /**
     * 删除会员快捷回复.
     * @param id  Id
     * @param myUserId  我的userId
     * @return
     */
    @Override
    public Long deleteChatMemberFastReply(Long id, Long myUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(myUserId).and("id").is(id));
        return chatMemberFastReplyRepository.delectChatMemberFastReply(query);
    }
}

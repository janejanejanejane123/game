package com.ruoyi.chat.mq;

import com.ruoyi.chatroom.db.domain.ChatUserInfo;
import com.ruoyi.chatroom.db.repository.ChatUserInfoRepository;
import com.ruoyi.chatroom.service.ChatUserInfoService;
import com.ruoyi.common.vo.MemberMessageVo;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description: 更新会员信息.
 * @author: nn
 * @create: 2019-10-18 11:47
 **/
@Service
@Lazy(false)
@RocketMQMessageListener(topic = "update_member_info_to_chat", selectorExpression = "update_member_info", consumerGroup = "update_member_info_group")
public class UpdateMemberInfoToChatConsumer implements RocketMQListener<MemberMessageVo> {

    private static Logger logger = LoggerFactory.getLogger(UpdateMemberInfoToChatConsumer.class);

    @Resource
    private ChatUserInfoService chatUserInfoService;

    @Resource
    private ChatUserInfoRepository chatUserInfoRepository;

    @Override
    public void onMessage(MemberMessageVo memberMessageVo) {

        if(memberMessageVo != null){
            ChatUserInfo chatUserInfo = chatUserInfoRepository.getChatUserInfoByUserId(memberMessageVo.getUserId());
            if(chatUserInfo != null){
                long count = chatUserInfoService.updateChatUserInfo(memberMessageVo,chatUserInfo.getUserIdentifier());
                if(count < 1){
                    logger.info("修改聊天用户信息失败!");
                }
            }else {
                logger.info("修改聊天用户信息失败! chatUserInfo == null");
            }
        }else {
            logger.info("修改聊天用户信息失败! memberMessageVo == null");
        }
    }
}

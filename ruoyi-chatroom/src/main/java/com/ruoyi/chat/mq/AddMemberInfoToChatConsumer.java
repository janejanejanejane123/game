package com.ruoyi.chat.mq;

import com.ruoyi.chatroom.db.domain.ChatUserInfo;
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
 * @description: 新增会员信息.
 * @author: nn
 * @create: 2022-07-19 11:47
 **/
@Service
@Lazy(false)
@RocketMQMessageListener(topic = "register_member_info_to_chat", selectorExpression = "register_member_info", consumerGroup = "register_member_info_group")
public class AddMemberInfoToChatConsumer implements RocketMQListener<MemberMessageVo> {

    private static Logger logger = LoggerFactory.getLogger(AddMemberInfoToChatConsumer.class);

    @Resource
    private ChatUserInfoService chatUserInfoService;

    @Override
    public void onMessage(MemberMessageVo memberMessageVo) {

        if(memberMessageVo != null){
            if(memberMessageVo.getType() == 1){
                ChatUserInfo chatUserInfo = chatUserInfoService.addChatUserInfo(memberMessageVo);
                if(chatUserInfo == null){
                    logger.info("创建聊天用户信息失败! chatUserInfo == null");
                }
            }else {
                logger.info("类型不对,创建聊天用户信息失败! type = " + memberMessageVo.getType());
            }
        }else {
            logger.info("创建聊天用户信息失败! memberMessageVo == null");
        }
    }
}

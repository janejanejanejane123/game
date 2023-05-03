package com.ruoyi.member.util;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.bussiness.constants.enums.TopicEnum;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.enums.UserInfoApplyEnums;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.system.service.ISysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/6/6,12:20
 * @return:
 **/

@Component
@Slf4j
public class RocketMqService {

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private ISysMenuService sysMenuService;


    private List<Long> queryIdByPerm(String perm){
        
        return sysMenuService.roleIds(perm);
    }
    private String listToString(List<Long> ids){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            if (i<ids.size()-1){
                builder.append(id);
                builder.append(",");
            }else {
                builder.append(id);
            }
        }
        return builder.toString();
    }
//    @Async("threadPoolTaskExecutor")
    public void applyToAdmin(UserInfoApplyEnums applyEnums,Object data){
        List<Long> ids = queryIdByPerm(applyEnums.getPerm());
        if (ids==null||ids.size()==0){
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("data",data);
        map.put("time",new Date());
        UserMailBox.UserMailBoxBuilder builder = UserMailBox.builder();
        builder.userType(Integer.valueOf(Constants.USER_TYPE_SYSTEM_USER))
                .topic(applyEnums.getTopic())
                .userIds(listToString(ids))
                .content(JSON.toJSONString(map));

        UserMailBox build = builder.build();
        sendMsgToWeb(build);
    }

//    @Async("threadPoolTaskExecutor")
    public void applyComplete(String code,Long uid,String typeCode,TopicEnum topicEnum){
        String message = MessageUtils.message("message.mq.content",
                MessageUtils.message(typeCode),
                MessageUtils.message(code));
        UserMailBox.UserMailBoxBuilder builder = UserMailBox.builder();


        Map<String, String> map = new HashMap<>();
        map.put("code","message.mq.pass".equals(code)?"200":"201");
        map.put("msg",message);


        UserMailBox userMailBox = builder.content(JSON.toJSONString(map))
                .topic(topicEnum.getTopic())
                .userIds(String.valueOf(uid))
                .userType(Integer.valueOf(Constants.USER_TYPE_PLAYER)).build();
        sendMsgToWeb(userMailBox);
    }


    public void sendMsgToWeb(UserMailBox mailBox){
        MessageBuilder builder1 = MessageBuilder.withPayload(mailBox);
        SendResult result = rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder1.build());
        if(!SendStatus.SEND_OK.equals(result.getSendStatus())){
            Assert.error("system.error");
        }else {
            log.info("发送消息成功:{}",JSON.toJSONString(mailBox));
        }
    }
}

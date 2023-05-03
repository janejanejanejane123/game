package com.ruoyi.chat.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.farsunset.cim.constant.ChannelAttr;
import com.farsunset.cim.group.SessionGroup;
import com.farsunset.cim.model.Message;
import com.ruoyi.chat.component.push.DefaultMessagePusher;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import io.netty.channel.Channel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;

@Component
@RocketMQMessageListener(topic = "ECOIN_MSG_TO_WEB",selectorExpression="*",consumerGroup = "webSocketMsg")
public class WebMsgListener implements RocketMQListener<Object> {

    @Resource
    private DefaultMessagePusher defaultMessagePusher;

    @Resource
    SessionGroup sessionGroup;

    @Override
    public void onMessage(Object obj) {
        JSONObject object = JSON.parseObject(JSON.toJSONString(obj));
        String userIds = object.getString("userIds");
        String userType = object.getString("userType");
        Message message = new Message();
        String keyId = object.getString("remark");
        message.setSender(StringUtils.isEmpty(keyId)?"system":keyId);
        message.setAction("2");
        message.setContent(object.getString("content"));
        message.setFormat("0");
        message.setTitle(object.getString("title"));
        message.setExtra(object.getString("topic"));
        if(StringUtils.isEmpty(userIds)){
            Collection<Collection<Channel>> values = sessionGroup.values();

            values.forEach(item -> {
                item.stream().filter(x -> x.attr(ChannelAttr.UID).get().startsWith(userType + "_")).forEach(x -> {
                        message.setId(System.currentTimeMillis());
                        x.writeAndFlush(message);
                    }
                );
            });
        }else{
            String[] strings = userIds.split(",");
            for (String s : strings) {
                Message temp = new Message();
                BeanUtils.copyBeanProp(temp,message);
                temp.setReceiver(userType + "_" + s);
                temp.setId(System.currentTimeMillis());
                defaultMessagePusher.push(temp);
            }
        }
    }
}
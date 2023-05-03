package com.ruoyi.chat.component.message;

import com.farsunset.cim.constant.ChannelAttr;
import com.farsunset.cim.group.SessionGroup;
import com.farsunset.cim.model.Message;
import com.ruoyi.common.utils.JSONUtils;
import com.ruoyi.system.domain.TSession;
import io.netty.channel.Channel;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 集群环境下，监控多设备登录情况，控制是否其余终端下线的逻辑
 */
@Component
public class BindMessageListener implements MessageListener {

    private static final String FORCE_OFFLINE_ACTION = "999";

    private static final String SYSTEM_ID = "0";

    /*
     一个账号只能在同一个类型的终端登录
     如: 多个android或ios不能同时在线
         一个android或ios可以和web，桌面同时在线
     */
    private final Map<String,String[]> conflictMap = new HashMap<>();

    @Resource
    private SessionGroup SessionGroup;

    public BindMessageListener(){
        conflictMap.put(TSession.CHANNEL_ANDROID,new String[]{TSession.CHANNEL_ANDROID,TSession.CHANNEL_IOS});
        conflictMap.put(TSession.CHANNEL_IOS,new String[]{TSession.CHANNEL_ANDROID,TSession.CHANNEL_IOS});
        conflictMap.put(TSession.CHANNEL_WINDOWS,new String[]{TSession.CHANNEL_WINDOWS,TSession.CHANNEL_WEB,TSession.CHANNEL_MAC});
        conflictMap.put(TSession.CHANNEL_WEB,new String[]{TSession.CHANNEL_WINDOWS,TSession.CHANNEL_WEB,TSession.CHANNEL_MAC});
        conflictMap.put(TSession.CHANNEL_MAC,new String[]{TSession.CHANNEL_WINDOWS,TSession.CHANNEL_WEB,TSession.CHANNEL_MAC});
    }

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message redisMessage, byte[] bytes) {

        TSession TSession = JSONUtils.fromJson(redisMessage.getBody(), TSession.class);
        String uid = TSession.getUid();
        String[] conflictChannels = conflictMap.get(TSession.getChannel());

        Collection<Channel> channelList = SessionGroup.find(uid,conflictChannels);

        channelList.removeIf(channel -> TSession.getNid().equals(channel.attr(ChannelAttr.ID).get()));

        /*
         * 获取到其他在线的终端连接，提示账号再其他终端登录
         */
        channelList.forEach(channel -> {

            if (Objects.equals(TSession.getDeviceId(),channel.attr(ChannelAttr.DEVICE_ID).get())){
                return;
            }

            Message message = new Message();
            message.setAction(FORCE_OFFLINE_ACTION);
            message.setReceiver(uid);
            message.setSender(SYSTEM_ID);
            message.setContent(TSession.getDeviceName());
            channel.writeAndFlush(message);
            channel.close();
        });


    }
}

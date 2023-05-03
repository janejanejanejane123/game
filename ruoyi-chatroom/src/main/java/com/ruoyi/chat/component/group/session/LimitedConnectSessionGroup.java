package com.ruoyi.chat.component.group.session;

import com.farsunset.cim.constant.ChannelAttr;
import com.farsunset.cim.group.SessionGroup;
import com.farsunset.cim.model.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/9/16,14:02
 * @return:
 **/
public class LimitedConnectSessionGroup extends SessionGroup {

    private int maxSessionSize;


    private final transient ChannelFutureListener remover = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) {
            future.removeListener(this);
            LimitedConnectSessionGroup.this.remove(future.channel());
        }
    };

    @Override
    public void add(Channel channel) {
        String uid = this.getKey(channel);
        if (uid != null && channel.isActive()) {
            channel.closeFuture().addListener(remover);
            Collection<Channel> collections = this.putIfAbsent(uid, new ConcurrentLinkedQueue<>(Collections.singleton(channel)));
            if (collections != null) {

                collections.add(channel);
            }
            if (!channel.isActive()) {
                channel.close();
            }
        }
    }


    @Override
    protected String getKey(Channel channel) {
        return super.getKey(channel);
    }
}

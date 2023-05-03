package com.ruoyi.chat.component.group;

import com.alibaba.fastjson.JSON;
import com.ruoyi.chat.component.bootstrap.BusinessChannelAttr;
import com.ruoyi.common.constant.ChatRoomConstants;
import com.ruoyi.common.core.domain.model.chat.ChatMessage;
import com.ruoyi.chat.component.redis.ChatNodeServerManager;
import com.ruoyi.common.utils.chat.GeneralUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/10,11:19
 * @return:
 **/
@Component
public class ChatSessionGroupManager  {

    /**
     * 聊天群组;
     */
    private  Map<Long, ChannelGroup> groups=new ConcurrentHashMap<>();


    private ChatSessionManager chatSession;



    public ChatSessionGroupManager(){
        this.chatSession=new ChatSessionManager();
    }

    public void sendMes(String identifier,ChatMessage msg){
        Channel channel = chatSession.findChannel(identifier);
        if (channel!=null){
            if (channel.isOpen() && channel.isActive() && channel.isWritable() ){
                channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
            }else {
                channel.close();
            }
        }
    }

    /**
     *
     * @param msg
     * @param identifiers
     */
    public void sendMes(Object msg,String...identifiers){
        TextWebSocketFrame wMsg = new TextWebSocketFrame(JSON.toJSONString(msg));
        for (String identifier : identifiers) {
            Channel channel = chatSession.findChannel(identifier);
            if (channel!=null){
                if (channel.isOpen() && channel.isActive() && channel.isWritable() ){
                    channel.writeAndFlush(safeDuplicate(wMsg));
                }else {
                    channel.close();
                }
            }
        }
        ReferenceCountUtil.release(wMsg);
    }


    public boolean isTargetChannelBindThisNode(String identifier){
        return chatSession.identifierSessions.containsKey(identifier);
    }



    private final transient ChannelFutureListener chatRoomRemover = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) {
            future.removeListener(this);
            Attribute<Long> attr = future.channel().attr(BusinessChannelAttr.MERCHANT_ID_KEY);
            Long merchantId = attr.get();
            ChatSessionGroupManager.this.removeChannel(merchantId,future.channel());

        }
    };

    public  ChannelGroup createGroup(Long merchantId) {
        ChannelGroup channelGroup = groups.get(merchantId);
        if (channelGroup == null) {
            ChannelGroup tempChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            channelGroup = groups.putIfAbsent(merchantId, tempChannelGroup);
            if (channelGroup == null) {
                channelGroup = tempChannelGroup;
            }
        }
        return channelGroup;
    }


    public boolean inGroup(Long merchantId,Channel channel ){
        ChannelGroup channelGroup = groups.get(merchantId);
        if (channelGroup==null){
            return false;
        }else {
           return   channelGroup.find(channel.id())!=null;
        }
    }


    public void addChannelToGroup(Long merchantId,String identifier, Channel channel) {
        ChannelGroup channelGroup = createGroup(merchantId);
        channelGroup.add(channel);
        chatSession.addSession(identifier,channel);
        channel.closeFuture().addListener(this.chatRoomRemover);
    }



    public void customerOnline(){

    }


    /**
     * 给特定商户下的用户发送消息
     * @param create 是否创建新的商户组
     * @param channel 当前用户对应的channel
     * @param matcher 匹配筛选器
     * @param merchantId 商户对应ID
     * @param chatMessage 聊天消息
     */
    public void sendMsg(boolean create, Channel channel, ChannelMatcher matcher, Long merchantId, ChatMessage chatMessage) {
        ChannelGroup channelGroup = getGroupByName(merchantId,create);
        if (channelGroup != null) {
            if(channel != null && channelGroup.find(channel.id()) == null) {
                channelGroup.add(channel);
            }
            WebSocketFrame webSocketFrame = new TextWebSocketFrame(JSON.toJSONString(chatMessage));
            if(matcher == null) {
                channelGroup.writeAndFlush(webSocketFrame);
            }else {
                channelGroup.writeAndFlush(webSocketFrame,matcher);
            }
        }
    }

    public void sendMsgToSelf(Object o){
        Channel currentChannel = GeneralUtils.getCurrentChannel();
        WebSocketFrame webSocketFrame = new TextWebSocketFrame(JSON.toJSONString(o));
        currentChannel.writeAndFlush(webSocketFrame);
    }

    /**
     * 给特定商户下的用户发送消息
     * @param create
     * @param matcher
     * @param merchantId
     * @param chatMessage
     */
    public void sendMsg(boolean create,ChannelMatcher matcher, Long merchantId, ChatMessage chatMessage){
        sendMsg(true,null,matcher,merchantId,chatMessage);
    }


    private ChannelGroup getGroupByName(Long merchantId, boolean create) {
        if (create){
            return createGroup(merchantId);
        }
        return groups.get(merchantId);
    }


    public  void removeChannel(Long merchantId, Channel channel) {
        ChannelGroup channelGroup = getGroupByName(merchantId,false);
        if(channelGroup != null && channel != null){
            channelGroup.remove(channel);
        }
    }


    public void sendMsgAllMerchant(ChatMessage chatMessage, ChannelMatcher matcher){
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(JSON.toJSONString(chatMessage));
        Set<Long> excludes = findExcludes();
        groups.forEach((merchantId,group)->{
            if (excludes==null||!excludes.contains(merchantId)){
                if (matcher==null){
                    group.writeAndFlush(safeDuplicate(textWebSocketFrame));
                }else {
                    group.writeAndFlush(safeDuplicate(textWebSocketFrame),matcher);
                }

            }
        });
        ReferenceCountUtil.release(textWebSocketFrame);
    }

    private Set<Long> findExcludes() {
        return null;
    }


    private static Object safeDuplicate(Object message) {
        if (message instanceof ByteBuf) {
            return ((ByteBuf)message).retainedDuplicate();
        } else {
            return message instanceof ByteBufHolder ? ((ByteBufHolder)message).retainedDuplicate() : ReferenceCountUtil.retain(message);
        }
    }

    public void sendAll(ChatMessage chatMessage,ChannelMatcher channelMatcher) {
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(JSON.toJSONString(chatMessage));
        this.chatSession.identifierSessions.forEach((s,c)->{
            if (channelMatcher!=null){
                if (channelMatcher.matches(c)){
                    c.writeAndFlush(safeDuplicate(textWebSocketFrame));
                };
            }else {
                c.writeAndFlush(safeDuplicate(textWebSocketFrame));
            }
        });
        ReferenceCountUtil.release(textWebSocketFrame);
    }

    private class ChatSessionManager{

        private final AttributeKey<String> IDENTIFIER=AttributeKey.valueOf("IDENTIFIER");
        /**
         * 聊天Channel;
         */
        private  Map<String, Channel> identifierSessions = new ConcurrentHashMap<>();

        private final transient ChannelFutureListener sessionRemover = new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                future.removeListener(this);
                Channel channel = future.channel();
                ChatSessionManager.this.removeSession(channel.attr(IDENTIFIER).get());

            }
        };

        private void addSession(String identifier,Channel channel){
            channel.attr(IDENTIFIER).set(identifier);
            if (!identifierSessions.containsKey(identifier)){
                identifierSessions.put(identifier,channel);
                channel.closeFuture().addListener(this.sessionRemover);
            }

        }


        private Channel findChannel(String identifier){
            return identifierSessions.get(identifier);
        }


        private void removeSession(String identifier){
            identifierSessions.remove(identifier);
        }


    }

}

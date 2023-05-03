package com.ruoyi.chatroom.service;

import com.ruoyi.common.core.domain.model.LoginMember;
import io.netty.channel.Channel;

/**
 * @description: 好友离线消息业务接口定义
 * @author: nn
 * @create: 2022-07-19 18:56
 **/
public interface ChatFriendOfflineService {

    /**
     * @Description: //批量消费ACK，尽量减少单条记录的删除，否者服务器压力会变得巨大
     * @param
     * @return void
     */
    void startCheckAck();
    /**
     * @Description: 加载好友离线消息
     * @param ctx
     * @return void
     */
    void loadChatFriendOffline(Channel ctx, LoginMember chatUserInfo);




    void doChatOfflineSend(Channel channel,LoginMember loginMember,String friendIdentifier);
    /**
     * @Description:
     * @param userId 用户ID
     * @param messageId 消息ID
     * @return void
     */
    void chatAck(Long userId, Long messageId) throws InterruptedException;

}

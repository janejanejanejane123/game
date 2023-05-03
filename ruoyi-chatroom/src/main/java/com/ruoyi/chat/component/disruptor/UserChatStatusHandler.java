package com.ruoyi.chat.component.disruptor;

import com.ruoyi.chat.component.bootstrap.BusinessChannelAttr;
import com.ruoyi.chat.component.group.ChatSessionGroupManager;
import com.ruoyi.chat.component.handler.annotation.SubEventPublish;
import com.ruoyi.chatroom.service.ChatFriendOfflineService;
import com.ruoyi.chatroom.service.IChatServeCustomerService;
import com.ruoyi.chatroom.service.impl.ChatroomFacade;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.ChatTopic;
import com.ruoyi.common.utils.SpringContextUtil;
import com.ruoyi.common.utils.chat.MessageEventGenerator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Attribute;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserChatStatusHandler {

    @Resource
    private ChatFriendOfflineService chatFriendOfflineService;



    private boolean testUser(LoginUser loginUser){
        return true;
    }

    @SubEventPublish
    public void online(LoginUser loginUser, Channel channel){
        if (testUser(loginUser)){
            if (Constants.USER_TYPE_PLAYER.equals(loginUser.getUserType())){
                //商户ID
                Long merchantId = loginUser.getMerchantId();
                if (merchantId==null){
                    merchantId=Constants.MERCHANT_ID_DEFAULT;
                }
                channel.attr(BusinessChannelAttr.MERCHANT_ID_KEY).set(merchantId);

                //离线消息
                chatFriendOfflineService.loadChatFriendOffline(channel,(LoginMember) loginUser);
            }

            channel.closeFuture().addListener((ChannelFutureListener) channelFuture -> {
                offline(loginUser);
            });
            if (isCustomer(loginUser.getUserId())){
                String message= "客服 "+loginUser.getNickName()+" 上线";
                MessageEventGenerator.message2All(message, ChatTopic.CUSTOMER_ONLINE);
            }
        }

    }

    private boolean isCustomer(Long uid){
        IChatServeCustomerService service = SpringContextUtil.getBean(IChatServeCustomerService.class);
        if (service==null){
            return false;
        }
        return service.isServeCustomer(null, uid);
    }


    public void offline(LoginUser loginUser){
        if (isCustomer(loginUser.getUserId())) {
            String message= "客服 "+loginUser.getNickName()+" 下线";
            MessageEventGenerator.message2All(message, ChatTopic.CUSTOMER_OFFLINE);
        }
    }

}

package com.ruoyi.common.utils.chat;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.chat.MesEvent;
import io.netty.channel.Channel;

import java.util.LinkedList;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/24,20:25
 * @return:
 **/
public class ChatContext {

    public static final ThreadLocal<Channel> CHANNEL_CONTEXT =new ThreadLocal<>();
    public static final ThreadLocal<LoginUser> LOGIN_USER_CONTEXT =new ThreadLocal<>();
    public static final ThreadLocal<LinkedList<MesEvent>> MES_EVENT_CHAIN =new ThreadLocal<>();
}

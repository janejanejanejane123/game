package com.ruoyi.chat.component.handler;

import com.alibaba.fastjson.JSON;
import com.farsunset.cim.constant.ChannelAttr;
import com.farsunset.cim.group.SessionGroup;
import com.farsunset.cim.handler.CIMRequestHandler;
import com.farsunset.cim.model.ReplyBody;
import com.farsunset.cim.model.SentBody;
import com.ruoyi.chat.component.bootstrap.BusinessChannelAttr;
import com.ruoyi.chat.component.disruptor.UserChatStatusHandler;
import com.ruoyi.chat.component.handler.annotation.CIMHandler;
import com.ruoyi.chat.component.redis.SignalRedisTemplate;
import com.ruoyi.chatroom.service.ChatFriendOfflineService;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.TSession;
import com.ruoyi.system.service.ITSessionService;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 客户长连接 账户绑定实现
 */
@CIMHandler(key = "client_bind")
public class BindHandler implements CIMRequestHandler {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	TokenService tokenService;

	@Resource
	RedisTemplate redisTemplate;
	@Resource
	private ITSessionService sessionService;

	@Resource
	private SessionGroup sessionGroup;

	@Resource
	private SignalRedisTemplate signalRedisTemplate;


	@Resource
	private UserChatStatusHandler userChatStatusHandler;


	@Override
	public void process(Channel channel, SentBody body) {

		ReplyBody reply = new ReplyBody();
		reply.setKey(body.getKey());
		reply.setCode(HttpStatus.OK.value());
		reply.setTimestamp(System.currentTimeMillis());
		LoginUser loginUser = tokenService.getLoginUser(body.get("uid"));
		String channelKey = loginUser.getUserType() + "_" + loginUser.getUserId();
		String name = loginUser.getUsername();
		TSession session = new TSession();
		session.setUid(channelKey);
		session.setNid(channel.attr(ChannelAttr.ID).get());
		session.setDeviceId(body.get("deviceId"));
		session.setChannel(body.get("channel"));
		session.setDeviceName(body.get("deviceName"));
		session.setAppVersion(body.get("appVersion"));
		session.setOsVersion(body.get("osVersion"));
		session.setLanguage(body.get("language"));
		session.setLocation(name);
		try {
			String host = InetAddress.getLocalHost().getHostAddress();
			session.setHost(host);
		} catch (UnknownHostException e) {
			logger.error("获取host异常",e);
		}
		channel.attr(ChannelAttr.UID).set(channelKey);

		channel.attr(ChannelAttr.CHANNEL).set(session.getChannel());
		channel.attr(ChannelAttr.DEVICE_ID).set(session.getDeviceId());
		channel.attr(ChannelAttr.LANGUAGE).set(session.getLanguage());
		channel.attr(ReadWriteHandlerProxy.TOKEN).set(body.get("uid"));
		channel.attr(ReadWriteHandlerProxy.USER_TYPE).set(loginUser.getUserType());

		/*
		 *存储到数据库
		 */
		sessionService.add(session);

		/*
		 * 添加到内存管理
		 */
		sessionGroup.add(channel);

		/*
		 *向客户端发送bind响应
		 */
		channel.writeAndFlush(reply);

		/*
		 * 发送上线事件到集群中的其他实例，控制其他设备下线
		 */
		signalRedisTemplate.bind(session);

		redisTemplate.opsForHash().put(Constants.ONLINE_WEB_USER_KEY,channelKey, JSON.toJSONString(loginUser));


		userChatStatusHandler.online(loginUser,channel);

	}




}

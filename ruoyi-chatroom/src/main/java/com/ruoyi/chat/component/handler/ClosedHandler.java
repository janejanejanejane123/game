package com.ruoyi.chat.component.handler;

import com.farsunset.cim.constant.ChannelAttr;
import com.farsunset.cim.group.SessionGroup;
import com.farsunset.cim.handler.CIMRequestHandler;
import com.farsunset.cim.model.SentBody;
import com.ruoyi.chat.component.group.ChatSessionGroupManager;
import com.ruoyi.chat.component.handler.annotation.CIMHandler;
import com.ruoyi.common.bussiness.constants.Constant;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.system.service.ITSessionService;
import io.netty.channel.Channel;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * 连接断开时，更新用户相关状态
 */
@CIMHandler(key = "client_closed")
public class ClosedHandler implements CIMRequestHandler {

	@Resource
	private ITSessionService sessionService;

	@Resource
	private SessionGroup sessionGroup;

	@Resource
	private RedisTemplate redisTemplate;
	@Resource
	private ChatSessionGroupManager chatSessionGroupManager;

	@Override
	public void process(Channel channel, SentBody message) {

		String channelKey = channel.attr(ChannelAttr.UID).get();

		if (channelKey == null){
			return;
		}

		sessionGroup.remove(channel);
		sessionService.deleteTSessionByUid(channelKey);

		redisTemplate.opsForHash().delete(Constants.ONLINE_WEB_USER_KEY,channelKey);
	}

}

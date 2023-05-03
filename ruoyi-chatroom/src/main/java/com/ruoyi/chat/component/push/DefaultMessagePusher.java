package com.ruoyi.chat.component.push;

import com.farsunset.cim.model.Message;
import com.ruoyi.chat.component.redis.SignalRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/*
 * 消息发送实现类
 * 
 */
@Component
public class DefaultMessagePusher implements CIMMessagePusher {

	@Resource
	private SignalRedisTemplate signalRedisTemplate;

	/**
	 * 向用户发送消息
	 *
	 * @param message
	 */
	@Override
	public final void push(Message message) {

		/*
		 * 通过发送redis广播，到集群中的每台实例，获得当前UID绑定了连接并推送
		 * @see com.farsunset.hoxin.component.message.PushMessageListener
		 */
		signalRedisTemplate.push(message);

	}

}

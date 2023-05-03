package com.ruoyi.chat.component.redis;

import com.farsunset.cim.model.Message;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.JSONUtils;
import com.ruoyi.system.domain.TSession;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SignalRedisTemplate extends StringRedisTemplate {

	public SignalRedisTemplate(LettuceConnectionFactory connectionFactory) {
		super(connectionFactory);
		connectionFactory.setValidateConnection(true);
	}

	/**
	 * 消息发送到 集群中的每个实例，获取对应长连接进行消息写入
	 * @param message
	 */
	public void push(Message message) {
		super.convertAndSend(Constants.PUSH_MESSAGE_INNER_QUEUE, JSONUtils.toJSONString(message));
	}

	/**
	 * 消息发送到 集群中的每个实例，解决多终端在线冲突问题
	 * @param session
	 */
	public void bind(TSession session) {
		super.convertAndSend(Constants.BIND_MESSAGE_INNER_QUEUE, JSONUtils.toJSONString(session));
	}
}

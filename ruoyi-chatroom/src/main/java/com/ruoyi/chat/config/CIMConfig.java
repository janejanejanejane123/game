package com.ruoyi.chat.config;

import com.farsunset.cim.acceptor.AppSocketAcceptor;
import com.farsunset.cim.acceptor.config.AppSocketConfig;
import com.farsunset.cim.acceptor.config.WebsocketConfig;
import com.farsunset.cim.group.SessionGroup;
import com.farsunset.cim.handler.CIMRequestHandler;
import com.farsunset.cim.model.SentBody;
import com.ruoyi.chat.component.bootstrap.BootstrapServer;
import com.ruoyi.chat.component.handler.annotation.CIMHandler;
import com.ruoyi.chat.component.handler.dispatch.ClusterMessageDispatcher;
import com.ruoyi.chat.component.handler.dispatch.MessageDispatcher;
import com.ruoyi.chat.component.message.FriendChatAckProcess;
import com.ruoyi.chat.component.predicate.HandshakePredicate;
import com.ruoyi.chat.config.properties.CIMAppSocketProperties;
import com.ruoyi.chat.config.properties.CIMWebsocketProperties;
import com.ruoyi.chatroom.service.ChatMessageProcess;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.chat.Dispatcher;
import com.ruoyi.common.utils.chat.RunIdGetter;
import com.ruoyi.system.service.ITSessionService;
import com.sun.org.apache.regexp.internal.RE;
import io.netty.channel.Channel;
import org.apache.ibatis.annotations.Case;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EnableConfigurationProperties({
		CIMWebsocketProperties.class,
		CIMAppSocketProperties.class,
		BootstrapConfig.class})
@Configuration
public class CIMConfig implements CIMRequestHandler, ApplicationListener<ApplicationStartedEvent> {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Long nodeId ;

	public Long getNodeId() {
		return nodeId;
	}

	public CIMConfig(){
		nodeId = new SnowflakeIdUtils().nextId();
	}

	@Resource
	private ApplicationContext applicationContext;

	@Resource
	private ITSessionService sessionService;

	@Resource
	private FriendChatAckProcess friendOfflineAck;
	@Resource
	private ChatMessageProcess chatMessageProcess;

	private final HashMap<String,CIMRequestHandler> handlerMap = new HashMap<>();

	@Bean("runIdGetter")
	public RunIdGetter runIdGetter(){
		return () -> nodeId;
	}


	@Bean(name = "sessionGroup")
	public SessionGroup sessionGroup() {
		return new SessionGroup();
	}

	@Bean(destroyMethod = "destroy",initMethod = "bind")
	@ConditionalOnProperty(name = {"cim.websocket.enable"},matchIfMissing = true)
	public BootstrapServer websocketAcceptor(CIMWebsocketProperties properties,
											 HandshakePredicate handshakePredicate,
											 BootstrapConfig bootstrapConfig) {
		WebsocketConfig config = new WebsocketConfig();
		config.setHandshakePredicate(handshakePredicate);
		config.setPath(properties.getPath());
		config.setPort(properties.getPort());
		config.setProtocol(properties.getProtocol());
		config.setOuterRequestHandler(this);
		config.setEnable(properties.isEnable());
		return new BootstrapServer(config,bootstrapConfig);
	}

	@Bean(destroyMethod = "destroy",initMethod = "bind")
	@ConditionalOnProperty(name = {"cim.app.enable"},matchIfMissing = true)
	public AppSocketAcceptor appSocketAcceptor(CIMAppSocketProperties properties) {

		AppSocketConfig config = new AppSocketConfig();
		config.setPort(properties.getPort());
		config.setOuterRequestHandler(this);
		config.setEnable(properties.isEnable());

		return new AppSocketAcceptor(config);
	}


	@Bean(name = "messageDispatcher")
	public Dispatcher messageDispatcher(BootstrapConfig bootstrapConfig){
		String mode = bootstrapConfig.getMode();
		switch (mode){
			case "cluster":
				return new ClusterMessageDispatcher();
			case "singleton":
			default:
				return new MessageDispatcher();
		}
	}



	@Override
	public void process(Channel channel, SentBody body) {
		
        CIMRequestHandler handler = handlerMap.get(body.getKey());
		
		if(handler == null) {return ;}
		
		handler.process(channel, body);
		
	}
	/*
	 * springboot启动完成之后再启动cim服务的，避免服务正在重启时，客户端会立即开始连接导致意外异常发生.
	 */
	@Override
	public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {

		Map<String, CIMRequestHandler> beans =  applicationContext.getBeansOfType(CIMRequestHandler.class);

		for (Map.Entry<String, CIMRequestHandler> entry : beans.entrySet()) {

			CIMRequestHandler handler = entry.getValue();

			CIMHandler annotation = handler.getClass().getAnnotation(CIMHandler.class);

			if (annotation != null){
				handlerMap.put(annotation.key(),handler);
			}
		}
		try {
			String host = InetAddress.getLocalHost().getHostAddress();
			sessionService.deleteLocalhost(host);
		} catch (UnknownHostException e) {
			logger.error("获取host异常",e);
		}
		//启动离线消息确认线程
		friendOfflineAck.ackHandle();
		//启动聊天室消息消费队列线程
		chatMessageProcess.waitToHandle();
	}
}
package com.ruoyi.chat.component.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
//	private final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HB",CharsetUtil.UTF_8));

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
			IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
			if (idleStateEvent.state() == IdleState.READER_IDLE || idleStateEvent.state() == IdleState.WRITER_IDLE) {
				Channel channel	= ctx.channel();
				if(channel.isOpen() && channel.isActive()) {
					//send ping
					channel.writeAndFlush(new PingWebSocketFrame());
				}else{
					ctx.close();
				}
			}else if (idleStateEvent.state() == IdleState.ALL_IDLE) {
//				ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
				ctx.close();
			}
		}else {
			super.userEventTriggered(ctx, evt);
		}
	}
}
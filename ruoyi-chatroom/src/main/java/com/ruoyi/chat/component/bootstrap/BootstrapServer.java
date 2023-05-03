package com.ruoyi.chat.component.bootstrap;

import com.farsunset.cim.acceptor.config.WebsocketConfig;
import com.farsunset.cim.coder.json.TextMessageDecoder;
import com.farsunset.cim.coder.json.TextMessageEncoder;
import com.farsunset.cim.coder.protobuf.WebMessageDecoder;
import com.farsunset.cim.coder.protobuf.WebMessageEncoder;
import com.farsunset.cim.constant.WebsocketProtocol;
import com.farsunset.cim.handler.IllegalRequestHandler;
import com.farsunset.cim.handshake.HandshakeHandler;
import com.ruoyi.chat.component.handler.HeartbeatHandler;
import com.ruoyi.chat.config.BootstrapConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/11,13:26
 * @return:
 **/
@ChannelHandler.Sharable
public class BootstrapServer extends NioSocketAcceptor{


    private BootstrapConfig bootstrapConfig;

    private EventExecutorGroup eventExecutors;



    private static final String JSON_BANNER = "\n\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n*                                                                                   *\n*                                                                                   *\n*              Websocket Server started on port {} for [JSON] mode.              *\n*                                                                                   *\n*                                                                                   *\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";
    private static final String PROTOBUF_BANNER = "\n\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n*                                                                                   *\n*                                                                                   *\n*             Websocket Server started on port {} for [protobuf] mode.           *\n*                                                                                   *\n*                                                                                   *\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";
    private final WebsocketConfig config;
    private final HandshakeHandler handshakeHandler;
    private final ChannelHandler illegalRequestHandler = new IllegalRequestHandler();

    public BootstrapServer(WebsocketConfig config,BootstrapConfig bootstrapConfig) {
        super(config.getOuterRequestHandler());
        this.config = config;
        this.bootstrapConfig=bootstrapConfig;
        this.handshakeHandler = new HandshakeHandler(config.getHandshakePredicate());
        this.eventExecutors=new DefaultEventExecutorGroup(bootstrapConfig.getBusinessThreadCount());
    }

    public void bind() {
        if (this.config.isEnable()) {
            ServerBootstrap bootstrap = this.createServerBootstrap();
            options(bootstrap);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new HttpServerCodec());
                    ch.pipeline().addLast(new ChunkedWriteHandler());
                    ch.pipeline().addLast(new HttpObjectAggregator(bootstrapConfig.getMaxContext()));
                    ch.pipeline().addLast(new WebSocketServerProtocolHandler(BootstrapServer.this.config.getPath(), true));
                    ch.pipeline().addLast(BootstrapServer.this.loggingHandler);
                    ch.pipeline().addLast(BootstrapServer.this.handshakeHandler);
                    if (BootstrapServer.this.config.getProtocol() == WebsocketProtocol.JSON) {
                        ch.pipeline().addLast(new TextMessageDecoder());
                        ch.pipeline().addLast(new TextMessageEncoder());
                    } else {
                        ch.pipeline().addLast(new WebMessageDecoder());
                        ch.pipeline().addLast(new WebMessageEncoder());
                    }

                    ch.pipeline().addLast(new IdleStateHandler(
                            BootstrapServer.this.bootstrapConfig.getReadDuration(),
                            BootstrapServer.this.bootstrapConfig.getWriteDuration(),
                            0L, TimeUnit.SECONDS));
                    ch.pipeline().addLast("heartbeatHandler",new HeartbeatHandler());
                    ch.pipeline().addLast(eventExecutors,"businessHandler",BootstrapServer.this);
                    ch.pipeline().addLast(BootstrapServer.this.illegalRequestHandler);
                }
            });
            ChannelFuture channelFuture = bootstrap.bind(this.config.getPort()).syncUninterruptibly();
            channelFuture.channel().newSucceededFuture().addListener((future) -> {
                if (this.config.getProtocol() == WebsocketProtocol.JSON) {
                    this.logger.info("\n\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n*                                                                                   *\n*                                                                                   *\n*              Websocket Server started on port {} for [JSON] mode.              *\n*                                                                                   *\n*                                                                                   *\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n", this.config.getPort());
                }

                if (this.config.getProtocol() == WebsocketProtocol.PROTOBUF) {
                    this.logger.info("\n\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n*                                                                                   *\n*                                                                                   *\n*             Websocket Server started on port {} for [protobuf] mode.           *\n*                                                                                   *\n*                                                                                   *\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n", this.config.getPort());
                }

            });
            channelFuture.channel().closeFuture().addListener((future) -> {
                this.destroy();
                this.eventExecutors.shutdownGracefully();
            });
            this.isRun=true;
        }
    }

    private void options(ServerBootstrap bootstrap) {
        if (useEpoll()){
            bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
        }
        bootstrap.option(ChannelOption.SO_BACKLOG,bootstrapConfig.getBacklog());
        bootstrap.option(ChannelOption.SO_RCVBUF,bootstrapConfig.getSoRcvbuf());
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);


    }
}

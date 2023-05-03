package com.ruoyi.chat.component.bootstrap;

import com.farsunset.cim.constant.ChannelAttr;
import com.farsunset.cim.handler.CIMRequestHandler;
import com.farsunset.cim.handler.LoggingHandler;
import com.farsunset.cim.model.Ping;
import com.farsunset.cim.model.SentBody;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ThreadFactory;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/11,13:48
 * @return:
 **/
public abstract class NioSocketAcceptor extends SimpleChannelInboundHandler<SentBody> {

    private static final int PONG_TIME_OUT_COUNT = 3;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final ChannelHandler loggingHandler = new LoggingHandler();
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final CIMRequestHandler outerRequestHandler;
    public final Duration writeIdle = Duration.ofSeconds(45L);
    public final Duration readIdle = Duration.ofSeconds(60L);

    protected volatile boolean isRun;

    protected NioSocketAcceptor(CIMRequestHandler outerRequestHandler) {
        this.outerRequestHandler = outerRequestHandler;
        ThreadFactory bossThreadFactory = (r) -> {
            Thread thread = new Thread(r);
            thread.setName("nio-boss-");
            return thread;
        };
        ThreadFactory workerThreadFactory = (r) -> {
            Thread thread = new Thread(r);
            thread.setName("nio-worker-");
            return thread;
        };
        if (this.isLinuxSystem()) {
            this.bossGroup = new EpollEventLoopGroup(bossThreadFactory);
            this.workerGroup = new EpollEventLoopGroup(workerThreadFactory);
        } else {
            this.bossGroup = new NioEventLoopGroup(bossThreadFactory);
            this.workerGroup = new NioEventLoopGroup(workerThreadFactory);
        }

    }

    public void destroy() {
        if (this.bossGroup != null && !this.bossGroup.isShuttingDown() && !this.bossGroup.isShutdown()) {
            try {
                this.bossGroup.shutdownGracefully();
            } catch (Exception var3) {
            }
        }

        if (this.workerGroup != null && !this.workerGroup.isShuttingDown() && !this.workerGroup.isShutdown()) {
            try {
                this.workerGroup.shutdownGracefully();
            } catch (Exception var2) {
            }
        }
        this.isRun=false;
    }

    protected ServerBootstrap createServerBootstrap() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(this.bossGroup, this.workerGroup);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.channel(this.isLinuxSystem() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
        return bootstrap;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SentBody body) {
        if (this.outerRequestHandler != null) {
            this.outerRequestHandler.process(ctx.channel(), body);
        }

    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.channel().attr(ChannelAttr.ID).set(ctx.channel().id().asShortText());
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (ctx.channel().attr(ChannelAttr.UID) != null) {
            if (this.outerRequestHandler != null) {
                SentBody body = new SentBody();
                body.setKey("client_closed");
                this.outerRequestHandler.process(ctx.channel(), body);
            }
        }
    }
//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
//        if (evt instanceof IdleStateEvent) {
//            IdleStateEvent idleEvent = (IdleStateEvent)evt;
//            String uid = (String)ctx.channel().attr(ChannelAttr.UID).get();
//            if (idleEvent.state() == IdleState.WRITER_IDLE && uid == null) {
//                ctx.close();
//            } else {
//                Integer pingCount;
//                if (idleEvent.state() == IdleState.WRITER_IDLE && uid != null) {
//                    pingCount = (Integer)ctx.channel().attr(ChannelAttr.PING_COUNT).get();
//                    ctx.channel().attr(ChannelAttr.PING_COUNT).set(pingCount == null ? 1 : pingCount + 1);
//                    ctx.channel().writeAndFlush(Ping.getInstance());
//                } else {
//                    pingCount = (Integer)ctx.channel().attr(ChannelAttr.PING_COUNT).get();
//                    if (idleEvent.state() == IdleState.READER_IDLE && pingCount != null && pingCount >= 3) {
//                        ctx.close();
//                        this.logger.info("{} pong timeout.", ctx.channel());
//                    }
//
//                }
//            }
//        }
//    }

    private boolean isLinuxSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("linux");
    }

    public  boolean useEpoll(){
        return isLinuxSystem() && Epoll.isAvailable();
    }

    public boolean isRun(){
        return isRun;
    }
}

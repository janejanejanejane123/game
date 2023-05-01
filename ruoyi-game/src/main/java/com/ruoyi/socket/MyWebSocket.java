package com.ruoyi.socket;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.game.domain.request.BetRequest;
import com.ruoyi.game.domain.request.BetsTakeEffectRequest;
import com.ruoyi.game.domain.request.ChangeBetMoneyRequest;
import com.ruoyi.game.domain.response.BetResponse;
import com.ruoyi.game.domain.response.BetsTakeEffectResponse;
import com.ruoyi.game.domain.response.ChangeBetMoneyResponse;
import com.ruoyi.game.service.IGameConfigService;
import com.ruoyi.game.service.IGameUserService;
import com.ruoyi.websocket.message.MessageOperateType;
import com.ruoyi.websocket.message.OperateMessage;
import com.ruoyi.websocket.message.SocketMessageOpType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Description: websocket简单入门。此类可以向个人发送消息。但是不适合向，因为这里无法适合业务。可以自行改造Map来控制
 * @Author: Gentle
 * @date 2018/9/5  18:43
 */

@Component
@ServerEndpoint(value = "/ws/websocket")
@Slf4j
public class MyWebSocket {
    //每个客户端都会有相应的session,服务端可以发送相关消息
    private Session session;

    //J.U.C包下线程安全的类，主要用来存放每个客户端对应的webSocket连接，为什么说他线程安全。在文末做简单介绍
    private static CopyOnWriteArraySet<MyWebSocket> copyOnWriteArraySet = new CopyOnWriteArraySet<MyWebSocket>();

    @Autowired
    private IGameConfigService iGameConfigService;

    @Autowired
    private IGameUserService iGameUserService;


    /**
     * 打开连接。进入页面后会自动发请求到此进行连接
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        copyOnWriteArraySet.add(this);
        log.info("websocket有新的连接, 总数:"+ copyOnWriteArraySet.size());

    }

    /**
     * 用户关闭页面，即关闭连接
     */
    @OnClose
    public void onClose() {
        copyOnWriteArraySet.remove(this);
        log.info("websocket连接断开, 总数:"+ copyOnWriteArraySet.size());
    }

    /**
     * 测试客户端发送消息，测试是否联通
     * @param message
     */

    public BetResponse bet(BetRequest betRequest) {
        BetResponse betResponse = new BetResponse();
        {
            int score = 0;
            Random random = new Random();
            int a = random.nextInt(10);
            int b = random.nextInt(10);
            int c = random.nextInt(10);
            if (a == 0 && b == 0 && c == 0) {
                score = 800;
            }
            if (a == 4 && b == 4 && c == 4) {
                score = 400;
            }
            if (a == 6 && b == 6 && c == 6) {
                score = 250;
            }
            if (a == 8 && b == 8 && c == 8) {
                score = 100;
            }
            if (a == 2 && b == 2 && c == 2) {
                score = 100;
            }
            if ((a == 2 && b == 2) || (c == 2 && b == 2) || (c == 2 && a == 2)) {
                score = 30;
            }
            if (a == 2 || b == 2 || c == 2) {
                score = 15;
            }
            //Encrypt.AESDecrypt("1","1",true);
        }
        return betResponse;
    }


    public BetsTakeEffectResponse betsTakeEffect(BetsTakeEffectRequest betsTakeEffectRequest) {
        BetsTakeEffectResponse betsTakeEffectResponse = new BetsTakeEffectResponse();
        {
            int score = 0;
            Random random = new Random();
            int a = random.nextInt(10);
            int b = random.nextInt(10);
            int c = random.nextInt(10);
            if (a == 0 && b == 0 && c == 0) {
                score = 800;
            }
            if (a == 4 && b == 4 && c == 4) {
                score = 400;
            }
            if (a == 6 && b == 6 && c == 6) {
                score = 250;
            }
            if (a == 8 && b == 8 && c == 8) {
                score = 100;
            }
            if (a == 2 && b == 2 && c == 2) {
                score = 100;
            }
            if ((a == 2 && b == 2) || (c == 2 && b == 2) || (c == 2 && a == 2)) {
                score = 30;
            }
            if (a == 2 || b == 2 || c == 2) {
                score = 15;
            }
            //Encrypt.AESDecrypt("1","1",true);
        }
        return betsTakeEffectResponse;
    }

    public ChangeBetMoneyResponse changeBetMoney(ChangeBetMoneyRequest changeBetMoneyRequest) {
        return null;
    }


    /**
     * 收到客户端发来消息
     *
     * @param message 消息对象
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        /** 前端发送空消息, do nothing */
        if (StringUtils.isBlank(message)) {
            return;
        }
        /** ping <->  pong(heartbeat) */
        OperateMessage opMsg = JSON.parseObject(message, OperateMessage.class);
        if (SocketMessageOpType.PING.getCode().equals(opMsg.getOpType())) {
            try {
                if (session.isOpen()) {

                    session.getAsyncRemote().sendText(JSON.toJSONString(
                            new MessageOperateType(SocketMessageOpType.PONG.getCode())));
                }
            } catch (Exception e) {
                // ignore
            }
            return;
        }
        if (SocketMessageOpType.CHANGEBETMONEY.getCode().equals(opMsg.getOpType())) {
            try {
                if (session.isOpen()) {

                    session.getAsyncRemote().sendText(JSON.toJSONString(changeBetMoney(JSON.parseObject(opMsg.getMessage().toString(),ChangeBetMoneyRequest.class))));
                }
            } catch (Exception e) {
                // ignore
            }
            return;
        }
        if (SocketMessageOpType.BET.getCode().equals(opMsg.getOpType())) {
            try {
                if (session.isOpen()) {

                    session.getAsyncRemote().sendText(JSON.toJSONString(bet(JSON.parseObject(opMsg.getMessage().toString(),BetRequest.class))));
                }
            } catch (Exception e) {
                // ignore
            }
            return;
        }
        if (SocketMessageOpType.BETSTAKEEFFECT.getCode().equals(opMsg.getOpType())) {
            try {
                if (session.isOpen()) {

                    session.getAsyncRemote().sendText(JSON.toJSONString(betsTakeEffect(JSON.parseObject(opMsg.getMessage().toString(),BetsTakeEffectRequest.class))));
                }
            } catch (Exception e) {
                // ignore
            }
            return;
        }
    }


    /**
     * 出现错误
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误：" + error.getMessage(), session.getId());
        error.printStackTrace();
    }

    /**
     * 用于发送给客户端消息（群发）
     * @param message
     */

    public void sendMessage(String message) {


        //遍历客户端
        for (MyWebSocket webSocket : copyOnWriteArraySet) {
            log.info("websocket广播消息：" + message);
            try {
                //服务器主动推送
                webSocket.session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 用于发送给指定客户端消息,这里写的不好。。不管了
     *
     * @param message
     */
    public void sendMessage(String sessionId, String message) throws IOException {
        Session session = null;
        MyWebSocket tempWebSocket = null;
        for (MyWebSocket webSocket : copyOnWriteArraySet) {
            if (webSocket.session.getId().equals(sessionId)) {
                tempWebSocket = webSocket;
                session = webSocket.session;
                break;
            }
        }
        if (session != null) {
            tempWebSocket.session.getBasicRemote().sendText(message);
        } else {
            log.warn("没有找到你指定ID的会话：{}", sessionId);
        }
    }

    /**
     * springboot内置tomcat的话，需要配一下这个。。如果没有这个对象，无法连接到websocket
     * 别问为什么。。很坑。。。
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        //这个对象说一下，貌似只有服务器是tomcat的时候才需要配置,具体我没有研究
        return new ServerEndpointExporter();
    }


}

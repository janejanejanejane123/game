package com.ruoyi.chat.component.message;

import com.alibaba.fastjson.JSON;
import com.ruoyi.chat.component.bootstrap.BootstrapServer;
import com.ruoyi.chatroom.db.repository.ChatFriendOfflineRepository;
import com.ruoyi.common.core.domain.model.chat.MesEvent;
import com.ruoyi.common.core.domain.model.chat.OfflineAck;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/12,12:17
 * @return:
 **/

@Component
@Slf4j
public class FriendChatAckProcess  {

    private final static LinkedBlockingQueue<MesEvent> QUEUE = new LinkedBlockingQueue<>();
    @Resource
    private ChatFriendOfflineRepository chatFriendOfflineRepository;
    @Resource
    private BootstrapServer bootstrapServer;



    public void chatAck(MesEvent mesEvent) throws Exception {
        int maxSize = 50000;
        if (QUEUE.size() <= maxSize) {
            QUEUE.put(mesEvent);
        }else{
            Assert.error("system.busy");
        }
    }

    @Async("taskExecutor")
    public void ackHandle() {
        log.info("[process] start ...{}",Thread.currentThread().getName());
        List<Long> delData = new ArrayList<>(100);
        long lastTime= System.currentTimeMillis();
        boolean startFlag=false;
        while (!startFlag||bootstrapServer.isRun()) {
            if (!startFlag&&bootstrapServer.isRun()){
                startFlag=true;
            }
            try {
                MesEvent mesEvent = QUEUE.poll(3000, TimeUnit.MILLISECONDS);
                long timeInterval = System.currentTimeMillis() - lastTime;
                if(mesEvent != null) {
                    OfflineAck content = (OfflineAck) mesEvent.getContent();
                    Collection<Long> offlineIds = content.getOfflineIds();
                    delData.addAll(offlineIds);
                }
                boolean delFlag = (timeInterval >= 3000 && delData.size() > 0);
                if(delFlag||delData.size()>100) {
                    lastTime=System.currentTimeMillis();
                    Criteria criteria = Criteria.where("messageId").in(delData);
                    chatFriendOfflineRepository.deleteChatFriendOffline(criteria);
                    delData.clear();
                    log.info("[process] delOfflineMessageSuccess id in {}...",delData);
                }
            } catch (Exception e) {
                log.info("[process] delOfflineMessageFail exception:{},data{}",e,delData);
            }
        }
    }
}

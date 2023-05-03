package com.ruoyi.chat.component.group;

import com.ruoyi.chat.component.disruptor.subevent.SubEventRunnable;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用于执行disruptor 消费者任务
 */
@Component
@Log4j2
public class SubEventService {

    @Async("dispatcherExecutor")
    public void execute(SubEventRunnable runnable){
        try {
            runnable.run();
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(),throwable);
        }
    }

}

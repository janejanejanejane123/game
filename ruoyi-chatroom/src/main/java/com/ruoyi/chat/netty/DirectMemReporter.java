package com.ruoyi.chat.netty;


import com.google.common.util.concurrent.MoreExecutors;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author st  //作者
 * @ClassName : DirectMemReporter  //类名
 * @Description: 堆外内存打印
 * @date 2022-08-05 13:11  //时间
 */
@Slf4j
@Component
public class DirectMemReporter {
    private AtomicLong directMem = new AtomicLong();
    private ScheduledExecutorService executor = MoreExecutors.getExitingScheduledExecutorService(
            new ScheduledThreadPoolExecutor(1), 10, TimeUnit.SECONDS);
    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public DirectMemReporter() {
        Field field = ReflectionUtils.findField(PlatformDependent.class, "DIRECT_MEMORY_COUNTER");
        field.setAccessible(true);
        try {
            directMem = (AtomicLong) field.get(PlatformDependent.class);
        } catch (IllegalAccessException e) {}
    }

    @PostConstruct
    public void startReport() {
        executor.scheduleWithFixedDelay(() -> {
            log.info("netty direct memory size:{}b, max:{}", directMem.get(), PlatformDependent.maxDirectMemory());
        }, 0, 5, TimeUnit.SECONDS);

    }
    @PreDestroy
    public void shutdown() {
        log.info("group chat record ack executorService shutdown");
        executor.shutdown();
        log.info("group chat record ack executorService shutdown");
        scheduledExecutorService.shutdown();
        log.info("group chat record ack executorService shutdown finish");
    }

}

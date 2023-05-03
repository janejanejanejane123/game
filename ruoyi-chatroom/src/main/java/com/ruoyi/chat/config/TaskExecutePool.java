package com.ruoyi.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/17,15:00
 * @return:
 **/
@EnableAsync
@Configuration
public class TaskExecutePool {
    @Value("${spring.concurrent.corePoolSize}")
    private int corePoolSize;
    @Value("${spring.concurrent.maxPoolSize}")
    private int maxPoolSize;
    @Value("${spring.concurrent.keepAliveSeconds}")
    private int keepAliveSeconds;
    @Value("${spring.concurrent.queueCapacity}")
    private int queueCapacity;


    @Bean(name = "taskExecutor")
    public Executor myTaskAsyncPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
package com.ruoyi.chat.config;

import com.ruoyi.chat.component.disruptor.subevent.SubEventDispatcher;
import com.ruoyi.chat.component.disruptor.subevent.SubEventHandler;
import com.ruoyi.chat.config.properties.DispatchProperties;
import com.ruoyi.common.exception.ServiceException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Aspect
public class DispatcherConfig {
    @Resource
    private DispatchProperties dispatcherProperties;
    @Resource
    private SubEventDispatcher subEventDispatcher;

    @Bean(name = "dispatcherExecutor")
    public Executor dispatcherExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(dispatcherProperties.getCorePoolSize());
        executor.setMaxPoolSize(dispatcherProperties.getMaxPoolSize());
        executor.setQueueCapacity(dispatcherProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(dispatcherProperties.getKeepAliveSeconds());

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadGroupName("dispatcherExecutor");
        executor.setThreadPriority(Thread.NORM_PRIORITY);
        executor.initialize();
        return executor;
    }

    @Bean(name = "subEventHandler")
    public SubEventHandler subEventHandler(){
        return new SubEventHandler();
    }

    @Bean(name = "subEventDispatcher",initMethod = "start",destroyMethod = "stop")
    public SubEventDispatcher subEventDispatcher(SubEventHandler subEventHandler){
        if("PHASEDBACKOFF".equals(dispatcherProperties.getFallbackStrategy())){
            throw new ServiceException("1489615645460151","dispatcher fallback strategy can't be PHASEDBACKOFF");
        }
        SubEventDispatcher messageDispatcher = new SubEventDispatcher(dispatcherProperties,subEventHandler);
        return messageDispatcher;
    }


    @Pointcut("@annotation(com.ruoyi.chat.component.handler.annotation.SubEventPublish)")
    protected void pointCutOperate() {

    }


    @Around("pointCutOperate()")
    public Object doOperate(ProceedingJoinPoint pjp) throws Throwable {
        subEventDispatcher.onData(pjp::proceed);
        return null;
    }
}

package com.ruoyi.chat.config.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dispatcher")
@Data
public class DispatchProperties {


    private int ringBufferSize;//队列最大长度,必须为2的幂
    private String exceptionHandlerClass;//异常处理handler
    private long shutdownTimeout;//关闭超时时间，单位毫秒，默认0毫秒

    /** strategy parameter config start*/
    private String strategy;//队列策略，默认TIMEOUT
    /**TIMEOUT 策略参数*/
    private long timeoutMillis=10L;//如果strategy是TIMEOUT的话，就是请求的超时的时间,单位毫秒，默认10毫秒
    /**PHASEDBACKOFF 策略参数*/
    private long spinTimeout;//
    private long yieldTimeout;//
    private String fallbackStrategy;//后备策略
    /**SLEEP 策略参数*/
    private int retries = 200;
    private long sleepTimeNs = 100;

    /** strategy parameter config end*/


    /** thread  parameter config start*/
    private String threadName;//线程名称
    private int corePoolSize = 10;//线程池维护线程的最少数量
    private int maxPoolSize = 200;//线程池维护线程的最大数量
    private int keepAliveSeconds = 200; // 允许的空闲时间
    private int queueCapacity = 20;// 缓存队列
    /** thread parameter config end*/

}

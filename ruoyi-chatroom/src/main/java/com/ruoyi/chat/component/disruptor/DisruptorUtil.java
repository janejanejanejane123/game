package com.ruoyi.chat.component.disruptor;

import ch.qos.logback.core.util.Loader;
import com.lmax.disruptor.*;
import com.ruoyi.chat.component.disruptor.subevent.SubEvent;
import com.ruoyi.chat.component.disruptor.subevent.SubEventExceptionHandler;
import com.ruoyi.common.exception.ServiceException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;


import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @description: Disruptor 工具类
 * 
 * @create: 2019-10-08 14:49
 **/
@Log4j2
public class DisruptorUtil {

    public static WaitStrategy createWaitStrategy(String strategy, final long timeoutMillis, long spinTimeout,long yieldTimeout,String fallbackStrategy) {
        strategy = StringUtils.isBlank(strategy) ? "TIMEOUT" : strategy;
        log.trace("property {}={}", strategy, timeoutMillis);
        final String strategyUp = strategy.toUpperCase(Locale.ROOT);
        switch (strategyUp) {
            case "SLEEP":
                /**
                 * 说明：CPU友好型策略. 会在循环中不断等待数据. 可通过参数设置,首先进行自旋等待, 若不成功, 则使用Thread.yield()让出CPU, 并使用LockSupport.parkNanos(1)进行线程睡眠, 通过线程调度器重新调度；或一直自旋等待, 所以, 此策略数据处理数据可能会有较高的延迟, 适合用于对延迟不敏感的场景, 优点是对生产者线程影响小, 典型应用场景是异步日志
                 * 适用场景：性能和CPU资源之间有很好的折中. 延迟不均匀
                 */
                return new SleepingWaitStrategy();
            case "YIELD":
                /**
                 * 说明：低延时策略. 消费者线程会不断循环监控RingBuffer的变化, 在循环内部使用Thread.yield()让出CPU给其他线程, 通过线程调度器重新调度
                 * 适用场景：性能和CPU资源之间有很好的折中. 延迟比较均匀
                 */
                return new YieldingWaitStrategy();
            case "BLOCK":
                /**
                 * 说明：默认等待策略. 和BlockingQueue的实现很类似, 通过使用锁和条件（Condition）进行线程阻塞的方式, 等待生产者唤醒(线程同步和唤醒). 此策略对于线程切换来说, 最节约CPU资源, 但在高并发场景下性能有限
                 * 适用场景：CPU资源紧缺, 吞吐量和延迟并不重要的场景
                 */
                return new BlockingWaitStrategy();
            case "BUSYSPIN":
                /**
                 * 说明：死循环策略. 消费者线程会尽最大可能监控缓冲区的变化, 会占用所有CPU资源,线程一直自旋等待, 比较耗CPU
                 * 适用场景：通过不断重试, 减少切换线程导致的系统调用, 而降低延迟. 推荐在线程绑定到固定的CPU的场景下使用
                 */
                return new BusySpinWaitStrategy();
            case "LITEBLOCKING":
                /**
                 * 说明：通线程阻塞等待生产者唤醒，与BlockingWaitStrategy相比，区别在signalNeeded.getAndSet,如果两个线程同时访问一个访问waitfor,一个访问signalAll时，可以减少lock加锁次数.
                 * 适用场景：这个策略在基准性能测试上是会表现出一些性能提升，但是作者还不能完全证明程序的正确性。
                 */
                return new LiteBlockingWaitStrategy();
            case "PHASEDBACKOFF":
                /**
                 * 说明：根据指定的时间段参数和指定的等待策略决定采用哪种等待策略
                 * 适用场景：多种策略的综合，阶段性等待事件处理器完成的屏障，此策略适合吞吐量和低延迟的并不重要，重要的是CPU资源，其在自旋后再应用BlockingWaitStrategy策略
                 */
                WaitStrategy fallbackWaitStrategy = createWaitStrategy(fallbackStrategy,timeoutMillis,0l,0l,null);
                return new PhasedBackoffWaitStrategy(spinTimeout, yieldTimeout, TimeUnit.MILLISECONDS,fallbackWaitStrategy);
            case "TIMEOUT":
                /**
                 * 说明：通过参数设置阻塞时间, 如果超时则抛出异常
                 * 适用场景：CPU资源紧缺, 吞吐量和延迟并不重要的场景
                 */
                return new TimeoutBlockingWaitStrategy(timeoutMillis, TimeUnit.MILLISECONDS);
            default:
                return new TimeoutBlockingWaitStrategy(timeoutMillis, TimeUnit.MILLISECONDS);
        }
    }

    public static ExceptionHandler<SubEvent> getAsyncConfigExceptionHandler(final String cls) {
        if (cls == null) {
            return new SubEventExceptionHandler<>();
        }
        try {
            @SuppressWarnings("unchecked")
            final Class<? extends ExceptionHandler<SubEvent>> klass =
                    (Class<? extends ExceptionHandler<SubEvent>>) Loader.loadClass(cls);
            return klass.newInstance();
        } catch (final Exception e) {
            throw new ServiceException("23456789036128","Invalid MessageDispatcher.ExceptionHandler value: error creating "+cls,e);
        }
    }
}

package com.ruoyi.chat.component.disruptor.subevent;

import com.lmax.disruptor.ExceptionHandler;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 异常消息处理器
 * 
 * @create: 2019-10-08 16:58
 * AbstractAsyncExceptionHandler
 **/
@Log4j2
public class SubEventExceptionHandler<T> implements ExceptionHandler<T> {
    @Override
    public void handleEventException(Throwable ex, long sequence, T event) {
      /*  if(event instanceof DispatchEventWrapper){

        }*/
        log.error("MessageDispatcher error starting,sequence is {},event is{}",sequence,event,ex);
    }

    @Override
    public void handleOnStartException(final Throwable throwable) {
        log.error("MessageDispatcher error starting:",throwable);
    }

    @Override
    public void handleOnShutdownException(final Throwable throwable) {
        log.error("MessageDispatcher error shutting down:",throwable);
    }
}

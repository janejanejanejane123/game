package com.ruoyi.chat.component.disruptor.subevent;

import com.lmax.disruptor.LifecycleAware;
import com.lmax.disruptor.WorkHandler;
import com.ruoyi.chat.component.group.SubEventService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;

@Log4j2
public class SubEventHandler implements WorkHandler<SubEvent>, LifecycleAware {

    @Resource
    private SubEventService subEventService;

    @Override
    public void onEvent(SubEvent event) throws Exception {
        if (event.getRunnable()==null){
            return;
        }
        subEventService.execute(event.getRunnable());
    }

    @Override
    public void onStart() {
       log.info("SubEventHandler onStart");
    }

    @Override
    public void onShutdown() {
        log.info("SubEventHandler shut down");
    }
}

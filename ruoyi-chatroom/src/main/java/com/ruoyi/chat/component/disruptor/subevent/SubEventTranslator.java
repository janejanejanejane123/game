package com.ruoyi.chat.component.disruptor.subevent;

import com.lmax.disruptor.EventTranslator;

public class SubEventTranslator implements EventTranslator<SubEvent> {


    private SubEventRunnable runnable;

    public SubEventTranslator( SubEventRunnable runnable){
        this.runnable=runnable;
    }

    @Override
    public void translateTo(SubEvent event, long sequence) {
        event.setRunnable(runnable);
    }
}

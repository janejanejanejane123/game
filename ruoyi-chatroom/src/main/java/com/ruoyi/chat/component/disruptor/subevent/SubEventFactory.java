package com.ruoyi.chat.component.disruptor.subevent;

import com.lmax.disruptor.EventFactory;

public class SubEventFactory implements EventFactory<SubEvent> {
    @Override
    public SubEvent newInstance() {
        return new SubEvent();
    }
}

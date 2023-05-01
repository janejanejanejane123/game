package com.ruoyi.common.core.domain.model.chat;

import lombok.Data;

import java.util.Collection;
import java.util.List;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/1,14:19
 * @return:
 **/
@Data
public class OfflineAck {

    Collection<Long> offlineIds;

    public OfflineAck(){
    }

    private OfflineAck(Collection<Long> offlineIds){
        this.offlineIds=offlineIds;
    }

    public static OfflineAck of(Collection<Long> messageIds){
        return new OfflineAck(messageIds);
    }
}

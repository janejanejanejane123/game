package com.ruoyi.chatroom.service;

import com.ruoyi.chatroom.db.domain.MessageRecallRecord;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.PageUtils;

import java.util.Map;

/**
 * @description: 消息撤回记录 业务类
 * @author: nn
 * @create: 2022-07-19 11:42
 **/
public interface MessageRecallRecordService {

    /**
     * 1:消息撤回记录.
     * @return
     */
    TableDataInfo queryPage(Map<String, Object> params);

    /**
     * 2.保存.
     * @param messageRecallRecord
     * @return
     */
    MessageRecallRecord addMessageRecallRecord(MessageRecallRecord messageRecallRecord);
}

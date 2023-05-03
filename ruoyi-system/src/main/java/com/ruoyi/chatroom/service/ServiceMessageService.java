package com.ruoyi.chatroom.service;

import com.ruoyi.chatroom.db.domain.ChatFriendRecord;
import com.ruoyi.chatroom.db.vo.ChatFriendRecordVo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 客服消息业务类
 * @author nn
 * @date 2022/07/26
 */
public interface ServiceMessageService {

    /**
     * 分页查询客服消息
     * @param params
     * @return
     */
    TableDataInfo queryPage(Map<String, Object> params);

    /**
     * 客服对话列表
     * @param params
     * @return
     */
    List<ChatFriendRecord> dialogueList(Map<String, Object> params);

}

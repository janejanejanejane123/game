package com.ruoyi.quartz.task;

import com.ruoyi.chatroom.service.ChatFriendRecordService;
import com.ruoyi.chatroom.service.ChatRoomRecordService;
import com.ruoyi.settings.service.ISysMaintenanceService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 聊天室定时任务调度
 * 
 * @author nn
 */
@Component("chatRoomTask")
public class ChatRoomTask {

    @Resource
    private ChatRoomRecordService chatRoomRecordService;

    @Resource
    private ChatFriendRecordService chatFriendRecordService;

    /**
     * 定时删除大厅记录.
     */
    public void deleteChatRoomRecord(Integer day) {
        chatRoomRecordService.deleteOnTimeChatRoomRecord(day);
    }

    /**
     * 定时删除私聊记录.
     */
    public void deleteChatFriendRecord(Integer day) {
        chatFriendRecordService.deleteOnTimeChatFriendRecord(day);
    }

}

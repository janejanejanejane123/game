package com.ruoyi.chat.component.message.task;

import com.ruoyi.chatroom.db.domain.ChatRoomRecord;
import com.ruoyi.common.bussiness.task.MessageTask;
import lombok.Data;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/16,15:00
 * @return:
 **/
public class ChatRoomMessageTask extends MessageTask {

    private ChatRoomRecord roomRecord;

    public ChatRoomRecord getRoomRecord() {
        return roomRecord;
    }

    public void setRoomRecord(ChatRoomRecord roomRecord) {
        this.roomRecord = roomRecord;
    }
}

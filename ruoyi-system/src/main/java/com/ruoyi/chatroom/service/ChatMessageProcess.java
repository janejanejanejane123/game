package com.ruoyi.chatroom.service;

import com.ruoyi.chatroom.db.domain.ChatRoomRecord;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/16,16:04
 * @return:
 **/
public interface ChatMessageProcess {

    void saveChatRoomRecord(ChatRoomRecord chatRoomRecord);


    void waitToHandle();
}

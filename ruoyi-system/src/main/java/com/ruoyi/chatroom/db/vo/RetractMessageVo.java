package com.ruoyi.chatroom.db.vo;

import com.ruoyi.common.enums.ChatMessageType;
import lombok.Data;

/**
 * @author nn
 * @Description: 撤回消息vo
 * @date 2022-07-24 09:39
 */
@Data
public class RetractMessageVo {

    /**撤回消息ID*/
    private Long messageId;
    /**
     * 撤回消息类型
     * @see ChatMessageType
     */
    private byte msgType;
    /**是否管理员撤回，0:否,1:是*/
    private byte isManager;
}

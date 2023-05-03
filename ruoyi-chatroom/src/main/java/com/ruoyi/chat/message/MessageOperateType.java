/*
 * Copyright (C) 2020 Baidu, Inc. All Rights Reserved.
 */
package com.ruoyi.chat.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageOperateType implements Serializable {
    private static final long serialVersionUID = 5744599721648521251L;
    private String opType = SocketMessageOpType.EDITING.getCode();
    private Long msgId;

    public MessageOperateType(String opType) {
        this.opType = opType;
    }

    public MessageOperateType() {
    }

    public String getMsgId() {
        return String.valueOf(msgId);
    }

    public Long getNumMsgId() {
        return msgId;
    }
}

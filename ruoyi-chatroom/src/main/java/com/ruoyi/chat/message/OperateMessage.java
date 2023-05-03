/*
 * Copyright (C) 2020 Baidu, Inc. All Rights Reserved.
 */
package com.ruoyi.chat.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class OperateMessage implements Serializable {
    private static final long serialVersionUID = 5882032917965873119L;
    /**
     * 操作类型
     * @see
     */
    private String opType;
    // doc_init时为doc对象/其他情况为OP集合
    private Object message;
    // opId 用于消息推送ack
    private String opId;
}

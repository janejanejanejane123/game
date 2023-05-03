/*
 * Copyright (C) 2020 Baidu, Inc. All Rights Reserved.
 */
package com.ruoyi.chat.message;


import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * WS前后端信息交互中opType类型
 */
public enum SocketMessageOpType {
    /**
     * DOC_INIT: doc创建后的初始化(tips: 由前端推送至后台)
     * EDIT_INIT: 进入doc编辑页面获取基础doc文档信息
     * JOIN: 编辑人员第一次进入编辑页面后向其他在线编辑人员推送加入信息
     * LEAVE: 编辑人员最后一次退出编辑后向其他在线编辑人员推送退出信息
     * EDITING: 普通的doc编辑产生的信息(tips: 由前端推送至后台)
     * ACCEPT: 普通的doc编辑产生的OP信息在后台实际处理之后的响应(伪ack)
     * PUBLISH: 编辑过程中发布doc(tips: 由前端推送至后台)
     * EDITOR_REQ: 建立WS连接时向其他实例推送获取同时在线编辑人员信息
     * EDITOR_RES: EDITOR_REQ的响应信息
     * LC_CREATE: 行间评论创建
     * LC_CREATE_BC: 行间评论创建消息广播
     * LC_DEL: 行间评论移除
     * LC_DEL_BC: 行间评论移除消息广播
     * SESSION_SYNC: 同步
     * PRE_JOIN: 预加入
     * PRE_RECEIVE: 预接收
     * ERROR: WS建立连接或交互过程产生异常(tips: 后台推送这种类型的消息后,将会关闭会话)
     */
    DOC_INIT("DOC_INIT"), EDIT_INIT("EDIT_INIT"), JOIN("JOIN"), LEAVE("LEAVE"),
    EDITING("EDITING"), ACCEPT("ACCEPT"), PUBLISH("PUBLISH"), EDITOR_REQ("EDITOR_REQ"),
    EDITOR_RES("EDITOR_RES"), LC_CREATE("LC_CREATE"), LC_CREATE_BC("LC_CREATE_BC"), LC_DEL("LC_DEL"),
    LC_DEL_BC("LC_DEL_BC"), SESSION_SYNC("SESSION_SYNC"), PRE_JOIN("PRE_JOIN"), PRE_RECEIVE("PRE_RECEIVE"),
    ERROR("ERROR"), PING("PING"), PONG("PONG"), CHANGEBETMONEY("CHANGEBETMONEY"), BET("BET"), BETSTAKEEFFECT("BETSTAKEEFFECT");

    private String code;

    SocketMessageOpType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static SocketMessageOpType getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (SocketMessageOpType opType : SocketMessageOpType.values()) {
            if (opType.getCode().equals(code)) {
                return opType;
            }
        }
        return null;
    }

    public static Set<SocketMessageOpType> getOtherType() {
        return Sets.newHashSet(JOIN, LEAVE, EDITING, PUBLISH, DOC_INIT);
    }
}

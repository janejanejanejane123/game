package com.ruoyi.chatroom.db.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: mongo数据模型
 * @author: j
 * @create: 2019-03-05 09:22
 **/

@Data
public class ChatRoomRecordVo implements Serializable {
    private static final long serialVersionUID = -5271455283858465928L;

    private String seq;
    /**消息ID*/
    private Long messageId;
    /**上一次的消息ID*/
    private Long lastMessageId;
    /** 消息类型，具体参照枚举ChatMessageType*/
    private byte msgType;
    /** 消息对应上面消息枚举*/
    private  String msgTypeDesc;
    /**消息体*/
    private Object msgBody;
    /**发送时间*/
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date sendTime;
    /**我自己的ID标识*/
    private String userIdentifier;
    /**发送消息的用户ID*/
    private Long userId;
    /** 用户名称*/
    private String userName;
    /**用户昵称*/
    private String nickName;
    /**用户头像*/
    private String userPortrait;
    /** 0:系统消息，1:用户消息  */
    private byte sendType;
    /**用户等级*/
    private String userLevel;
    /**发送用户IP*/
    private String sendUserIp;
    /**是否被举报*/
    private boolean isReport;
    /**举报次数*/
    private Integer reportNumber;
    /**消息状态,0:已经删除,1:正常消息,2:撤回消息,*/
    private byte msgStatus;

}

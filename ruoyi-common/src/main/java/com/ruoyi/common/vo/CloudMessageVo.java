package com.ruoyi.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @description: 消息载体
 * @create: 2022-04-04 11:12
 **/
@Data
public class CloudMessageVo implements Serializable {
    private static final long serialVersionUID = -9116165367307937369L;
    /**消息ID*/
    private Long messageId;
    /**数据分组*/
    private String dataGroup;
    /**消息标题*/
    private String title;
    /**消息体，消息体不支持直接对象保存，不然会引起序列化异常，如果使用对象想转Map，
     * 使用CglibBeanCopierUtils.beanToMap(Object object)*/
    private Map body;
    /**发送时间*/
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "GMT+8")
    private Date sendTime;
    /**发送消息的用户ID*/
    private Long userId;
    /**用户类型 （发送者） 1：管理员用户，0：前端用户消 */
    private Short userType;
    /**指定sessionId*/
    private String recipientSessionId;
    /**接收者用户ID*/
    private Long recipientId;
    /**接收者用户类型  1：后台用户，0：前端用户*/
    private Short recipientType;
    /**发送类型 ,对应枚举类SendMessageType*/
    private byte sendType;
    /**消息头*/
    private Map header;
    /**页面上的topic*/
    private String topic;
    /**读取标识*/
    private byte readFlag;
    /**读取时间*/
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "GMT+8")
    private Date readTime;
    /**读取类型，对应枚举类MsgReadType 0:默认模型，1:即收到就是*/
    private byte readType;
    /**是否存储,0:不用存储，1：存储到数据库*/
    private boolean isSave;
}

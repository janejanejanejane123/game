package com.ruoyi.chatroom.db.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author nn
 * @ClassName : ReportMessageVo  //类名
 * @Description: 被举报信息
 * @date 2020-06-08 11:12  //时间
 */
@Data
public class ReportMessageVo {
    /**举报ID*/
    private Long reportId;
    /**被举报人消息ID*/
    private Long messageId;
    /**举报来源(1:聊天室,2：好友消息,3:群消息,4:好友申请,5:群申请)*/
    private byte reportSource;
    /**举报类型(1.色情,2.政治,3.暴恐,4.民生,5.反动,6.贪腐,7.骂人,8.其他（多个用英文逗号隔开）*/
    private String reportType;
    /**被举报人ID*/
    private Long userId;
    /**被举报人登录账号*/
    private String userName;
    /**被举报人标识*/
    private String userIdentifier;
    /**举报人用户ID*/
    private Long informerId;
    /**举报人名称*/
    private String informerName;
    /**举报人标识*/
    private String informerIdentifier;
    /**举报时间*/
    private Date reportTime;
    /**举报内容备注*/
    private String remarks;
    /**来源Id,聊天室大厅没有Id*/
    private Long sourceId;

}

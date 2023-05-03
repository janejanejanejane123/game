package com.ruoyi.chatroom.db.domain;

import com.ruoyi.common.enums.ReportSourceEnum;
import com.ruoyi.common.enums.ReportTypeEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author nn
 * @ClassName : ReportMessage  //类名
 * @Description: 被举报信息
 * @date 2020-06-08 11:12  //时间
 */
@Data
@Document(collection="t_report_message")
public class ReportMessage {
    /**举报ID*/
    @Id
    private Long reportId;
    /**站点编码 - 预留字段*/
    private String siteCode = "1";
    /**消息ID*/
    private Long messageId;
    /**
     * 举报来源
     * @see ReportSourceEnum
     */
    private byte reportSource;
    /**
     * 举报类型
     * @see ReportTypeEnum
     */
    private String reportType;
    /**被举报人ID*/
    private Long userId;
    /**被举报人名称*/
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
    /**是否删除(逻辑删除:0:未删除  1:已删除)*/
    private byte isDelete;
}

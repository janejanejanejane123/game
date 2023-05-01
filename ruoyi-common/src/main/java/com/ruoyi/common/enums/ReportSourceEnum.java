package com.ruoyi.common.enums;

/**
 * @author nn
 * @ClassName : ReportSourceEnum  //类名
 * @Description: 举报来源枚举类
 * @date 2022-07-19 10:33  //时间
 */
public enum ReportSourceEnum {
    CHATROOM((byte)1,"聊天室大厅"),
    FRIENDS_MESSAGE((byte)2, "好友消息"),;
    private byte reportSource;
    private String desc;

    ReportSourceEnum(byte reportSource, String desc) {
        this.reportSource = reportSource;
        this.desc = desc;
    }

    public byte getReportSource() {
        return reportSource;
    }

    public String getDesc() {
        return desc;
    }
}

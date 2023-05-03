package com.ruoyi.chatroom.db.vo;

import lombok.Data;

/**
 * @description: 图片消息vo
 * @author: st
 * @create: 2022-08-05 16:11
 **/
@Data
public class ImageMessageVo {
    /*消息内容*/
    private String message;
    /*图片ID*/
    private Long imageId;
    /*文件分组*/
    private String group;
    /*文件2路径，图片就是缩略图路径*/
    private String thumbnailPath;
    /*缩略图高度*/
    private int thumbnailsHeight;
    /*缩略图寬度*/
    private int thumbnailsWidth;
    /*文件路径*/
    private String filePath;
}

package com.ruoyi.chatroom.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 表情包对象 t_chat_emoji
 * 
 * @author nn
 * @date 2022-07-13
 */
public class ChatEmoji extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表id */
    private Long emojiId;

    /** 表情包名称 */
    @Excel(name = "表情包名称")
    private String emojiName;

    /** 表情包类型 */
    @Excel(name = "表情包类型")
    private String emojiType;

    /** 上传文件id */
    @Excel(name = "上传文件id")
    private Long fileId;

    /** 文件名称 */
    @Excel(name = "文件名称")
    private String fileName;

    /** 文件服务器路径 */
    @Excel(name = "文件服务器路径")
    private String filePath;

    /** 缩略图路径  */
    @Excel(name = "缩略图路径 ")
    private String thumbnailPath;

    /** 上传人id */
    @Excel(name = "上传人id")
    private Long uploadId;

    /** 上传人 */
    @Excel(name = "上传人")
    private String uploadName;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date uploadTime;

    /** 上传ip */
    @Excel(name = "上传ip")
    private String uploadIp;

    /** 状态（0:启用 1:禁用） */
    @Excel(name = "状态", readConverterExp = "0=:启用,1=:禁用")
    private String status;

    public void setEmojiId(Long emojiId) 
    {
        this.emojiId = emojiId;
    }

    public Long getEmojiId() 
    {
        return emojiId;
    }
    public void setEmojiName(String emojiName) 
    {
        this.emojiName = emojiName;
    }

    public String getEmojiName() 
    {
        return emojiName;
    }
    public void setEmojiType(String emojiType) 
    {
        this.emojiType = emojiType;
    }

    public String getEmojiType() 
    {
        return emojiType;
    }
    public void setFileId(Long fileId) 
    {
        this.fileId = fileId;
    }

    public Long getFileId() 
    {
        return fileId;
    }
    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }
    public void setFilePath(String filePath) 
    {
        this.filePath = filePath;
    }

    public String getFilePath() 
    {
        return filePath;
    }
    public void setThumbnailPath(String thumbnailPath) 
    {
        this.thumbnailPath = thumbnailPath;
    }

    public String getThumbnailPath() 
    {
        return thumbnailPath;
    }
    public void setUploadId(Long uploadId) 
    {
        this.uploadId = uploadId;
    }

    public Long getUploadId() 
    {
        return uploadId;
    }
    public void setUploadName(String uploadName) 
    {
        this.uploadName = uploadName;
    }

    public String getUploadName() 
    {
        return uploadName;
    }
    public void setUploadTime(Date uploadTime) 
    {
        this.uploadTime = uploadTime;
    }

    public Date getUploadTime() 
    {
        return uploadTime;
    }
    public void setUploadIp(String uploadIp) 
    {
        this.uploadIp = uploadIp;
    }

    public String getUploadIp() 
    {
        return uploadIp;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("emojiId", getEmojiId())
            .append("emojiName", getEmojiName())
            .append("emojiType", getEmojiType())
            .append("fileId", getFileId())
            .append("fileName", getFileName())
            .append("filePath", getFilePath())
            .append("thumbnailPath", getThumbnailPath())
            .append("uploadId", getUploadId())
            .append("uploadName", getUploadName())
            .append("uploadTime", getUploadTime())
            .append("uploadIp", getUploadIp())
            .append("status", getStatus())
            .append("remark", getRemark())
            .toString();
    }
}

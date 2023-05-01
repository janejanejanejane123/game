package com.ruoyi.game.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户对象 user
 *
 * @author ruoyi
 * @date 2023-04-28
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName("user")
public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId
    private Long userId;

    /**
     * 用户登录的token
     */
    @Excel(name = "用户登录的token")
    private String token;

    /**
     * token过期时间
     */
    @Excel(name = "token过期时间")
    private String expireIn;

    /**
     * 登录失败次数
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "登录失败次数", width = 30, dateFormat = "yyyy-MM-dd")
    private Date tryTimes;
}

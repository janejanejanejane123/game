package com.ruoyi.game.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 account
 *
 * @author ruoyi
 * @date 2023-04-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName("account")
public class Account extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 账号
     */
    @Excel(name = "账号")
    private String account;

    /**
     * 余额
     */
    @Excel(name = "余额")
    private String balance;

    /**
     * 代理唯一标识
     */
    @Excel(name = "代理唯一标识")
    private String agent;

}

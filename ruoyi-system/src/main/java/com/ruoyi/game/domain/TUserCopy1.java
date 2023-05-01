package com.ruoyi.game.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户对象 t_user_copy1
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName("tuser_copy1")
public class TUserCopy1 extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId
    private Long uid;

    /** 最近一次登录IP */
    @Excel(name = "最近一次登录IP")
    private String lastLoginIp;

    /** 最近登录城市 */
    @Excel(name = "最近登录城市")
    private String lastLoginCity;

    /** 商户ID */
    @Excel(name = "商户ID")
    private Long merchantId;
}

package com.ruoyi.game.domain;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 game_config
 *
 * @author ruoyi
 * @date 2023-04-28
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName("game_cofig")
public class GameConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId
    private Long id;

    /**
     * 图案
     */
    @Excel(name = "图案")
    private String pattern;

    /**
     * 组合
     */
    @Excel(name = "组合")
    private String combination;

    /**
     * 金额
     */
    @Excel(name = "金额")
    private BigDecimal money;

    /**
     * 币种
     */
    @Excel(name = "币种")
    private String currency;
}

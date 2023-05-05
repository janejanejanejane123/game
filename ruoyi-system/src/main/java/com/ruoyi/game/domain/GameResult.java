package com.ruoyi.game.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * 【请填写功能名称】对象 game_result
 *
 * @author ruoyi
 * @date 2023-04-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName("game_result")
public class GameResult extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId
    private Long id;

    /**
     * 得分
     */
    @Excel(name = "得分")
    private String score;

    /**
     * 投注时间
     */
    @Excel(name = "betTime")
    private Date betTime;

    /**
     * 订单编号
     */
    @Excel(name = "订单编号")
    private Long orderNo;

    /**
     * 是否输赢
     */
    @Excel(name = "是否输赢")
    private Long whetherWin;

    /**
     * 投注金额
     */
    @Excel(name = "投注金额")
    private String money;

    /**
     * 账号
     */
    @Excel(name = "账号")
    private String account;

    /**
     * 货币
     */
    @Excel(name = "货币")
    private String currency;
}

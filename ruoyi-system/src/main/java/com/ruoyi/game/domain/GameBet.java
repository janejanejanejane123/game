package com.ruoyi.game.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * 【请填写功能名称】对象 game_bet
 *
 * @author ruoyi
 * @date 2023-04-28
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName("game_bet")
public class GameBet extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @TableId
    private Long id;

    /**
     * 投注时间
     */
    @Excel(name = "bet_time")
    private Date betTime;

    /**
     * 订单编号
     */
    @Excel(name = "order_no")
    private Long orderNo;

    /**
     * 投注金额
     */
    @Excel(name = "bet_money")
    private String betMoney;

    /**
     * 账号
     */
    @Excel(name = "account")
    private String account;

    /**
     * 货币
     */
    @Excel(name = "currency")
    private String currency;
}

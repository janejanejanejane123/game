package com.ruoyi.member.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 会员星级对象 t_user_rank
 * 
 * @author ry
 * @date 2022-09-09
 */
public class UserRank extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long uid;

    /** 会员名 */
    @Excel(name = "会员名")
    private String name;

    /** 交易人数id */
    @Excel(name = "交易人数id")
    private String transferUids;

    /** 累计交易金额 */
    @Excel(name = "累计交易金额")
    private BigDecimal transferMoney;

    /** 剩余人数 */
    @Excel(name = "剩余人数")
    private Integer leftPeople;

    /** 剩余金额 */
    @Excel(name = "剩余金额")
    private BigDecimal leftMoney;

    /** 剩余人数 */
    @Excel(name = "剩余人数")
    private Integer score;


    public void setUid(Long uid) 
    {
        this.uid = uid;
    }

    public Long getUid() 
    {
        return uid;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setTransferUids(String transferUids) 
    {
        this.transferUids = transferUids;
    }

    public String getTransferUids() 
    {
        return transferUids;
    }
    public void setTransferMoney(BigDecimal transferMoney) 
    {
        this.transferMoney = transferMoney;
    }

    public BigDecimal getTransferMoney() 
    {
        return transferMoney;
    }
    public void setLeftPeople(Integer leftPeople) 
    {
        this.leftPeople = leftPeople;
    }

    public Integer getLeftPeople() 
    {
        return leftPeople;
    }
    public void setLeftMoney(BigDecimal leftMoney) 
    {
        this.leftMoney = leftMoney;
    }

    public BigDecimal getLeftMoney() 
    {
        return leftMoney;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("uid", getUid())
            .append("name", getName())
            .append("transferUids", getTransferUids())
            .append("transferMoney", getTransferMoney())
            .append("leftPeople", getLeftPeople())
            .append("leftMoney", getLeftMoney())
            .append("score", getScore())
            .toString();
    }
}

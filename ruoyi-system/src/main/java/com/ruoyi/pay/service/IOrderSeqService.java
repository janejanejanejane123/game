package com.ruoyi.pay.service;


import com.ruoyi.pay.domain.OrderSeq;

import java.util.List;

/**
 * 序列号生成Service接口
 *
 * @author ry
 * @date 2022-09-02
 */
public interface IOrderSeqService
{
    /**
     * 查询序列号生成
     *
     * @param timestr 序列号生成主键
     * @return 序列号生成
     */
    public OrderSeq selectOrderSeqByTimestr(String timestr);

    /**
     * 查询序列号生成列表
     *
     * @param orderSeq 序列号生成
     * @return 序列号生成集合
     */
    public List<OrderSeq> selectOrderSeqList(OrderSeq orderSeq);

    /**
     * 新增序列号生成
     *
     * @param orderSeq 序列号生成
     * @return 结果
     */
    public int insertOrderSeq(OrderSeq orderSeq);

    /**
     * 修改序列号生成
     *
     * @param orderSeq 序列号生成
     * @return 结果
     */
    public int updateOrderSeq(OrderSeq orderSeq);

    /**
     * 批量删除序列号生成
     *
     * @param timestrs 需要删除的序列号生成主键集合
     * @return 结果
     */
    public int deleteOrderSeqByTimestrs(String[] timestrs);

    /**
     * 删除序列号生成信息
     *
     * @param timestr 序列号生成主键
     * @return 结果
     */
    public int deleteOrderSeqByTimestr(String timestr);

    String getSeqNo();
}

package com.ruoyi.pay.mapper;


import com.ruoyi.pay.domain.OrderSeq;

import java.util.List;

/**
 * 序列号生成Mapper接口
 *
 * @author ry
 * @date 2022-09-02
 */
public interface OrderSeqMapper
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
     * 删除序列号生成
     *
     * @param timestr 序列号生成主键
     * @return 结果
     */
    public int deleteOrderSeqByTimestr(String timestr);

    /**
     * 批量删除序列号生成
     *
     * @param timestrs 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOrderSeqByTimestrs(String[] timestrs);

    String selectSeq(String timestr);
}

package com.ruoyi.pay.service.impl;


import java.util.List;

import com.ruoyi.pay.domain.OrderSeq;
import com.ruoyi.pay.mapper.OrderSeqMapper;
import com.ruoyi.pay.service.IOrderSeqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 序列号生成Service业务层处理
 *
 * @author ry
 * @date 2022-09-02
 */
@Service
public class OrderSeqServiceImpl implements IOrderSeqService {
    @Autowired
    private OrderSeqMapper orderSeqMapper;

    /**
     * 查询序列号生成
     *
     * @param timestr 序列号生成主键
     * @return 序列号生成
     */
    @Override
    public OrderSeq selectOrderSeqByTimestr(String timestr) {
        return orderSeqMapper.selectOrderSeqByTimestr(timestr);
    }

    /**
     * 查询序列号生成列表
     *
     * @param orderSeq 序列号生成
     * @return 序列号生成
     */
    @Override
    public List<OrderSeq> selectOrderSeqList(OrderSeq orderSeq) {
        return orderSeqMapper.selectOrderSeqList(orderSeq);
    }

    /**
     * 新增序列号生成
     *
     * @param orderSeq 序列号生成
     * @return 结果
     */
    @Override
    public int insertOrderSeq(OrderSeq orderSeq) {
        return orderSeqMapper.insertOrderSeq(orderSeq);
    }

    /**
     * 修改序列号生成
     *
     * @param orderSeq 序列号生成
     * @return 结果
     */
    @Override
    public int updateOrderSeq(OrderSeq orderSeq) {
        return orderSeqMapper.updateOrderSeq(orderSeq);
    }

    /**
     * 批量删除序列号生成
     *
     * @param timestrs 需要删除的序列号生成主键
     * @return 结果
     */
    @Override
    public int deleteOrderSeqByTimestrs(String[] timestrs) {
        return orderSeqMapper.deleteOrderSeqByTimestrs(timestrs);
    }

    /**
     * 删除序列号生成信息
     *
     * @param timestr 序列号生成主键
     * @return 结果
     */
    @Override
    public int deleteOrderSeqByTimestr(String timestr) {
        return orderSeqMapper.deleteOrderSeqByTimestr(timestr);
    }

    @Transactional(rollbackFor = Exception.class)
    public String getSeqNo() {
        OrderSeq orderSeq = new OrderSeq();
        orderSeq.setTimestr("1");
        int i = this.updateOrderSeq(orderSeq);
        if (i == 0) {
            orderSeq.setOrderSn("1");
            this.insertOrderSeq(orderSeq);
        }
        return orderSeqMapper.selectSeq("1");
    }
}

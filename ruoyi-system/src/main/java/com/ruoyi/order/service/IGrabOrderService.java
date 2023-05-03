package com.ruoyi.order.service;

import com.ruoyi.order.domain.GrabOrder;
import com.ruoyi.order.domain.vo.GrabOrderVo;
import com.ruoyi.order.domain.vo.OrderVo;

import java.util.List;
import java.util.Map;

/**
 * grabService接口
 * 
 * @author ry
 * @date 2022-07-15
 */
public interface IGrabOrderService 
{
    /**
     * 查询grab
     * 
     * @param id grab主键
     * @return grab
     */
    public GrabOrder selectGrabOrderById(String id);

    /**
     * 查询grab列表
     * 
     * @param grabOrder grab
     * @return grab集合
     */
    public List<GrabOrder> selectGrabOrderList(GrabOrder grabOrder);

    /**
     * 新增grab
     * 
     * @param grabOrder grab
     * @return 结果
     */
    public int insertGrabOrder(GrabOrder grabOrder);

    /**
     * 修改grab
     * 
     * @param grabOrder grab
     * @return 结果
     */
    public int updateGrabOrder(GrabOrder grabOrder);

    /**
     * 批量删除grab
     * 
     * @param ids 需要删除的grab主键集合
     * @return 结果
     */
    public int deleteGrabOrderByIds(String[] ids);

    /**
     * 删除grab信息
     * 
     * @param id grab主键
     * @return 结果
     */
    public int deleteGrabOrderById(String id);

    int updateGrabOrderStatus(GrabOrder grabOrder);

    List<GrabOrderVo> selectMarketList(Map<String, Object> param);

    List<OrderVo> queryMyOrder(Map<String, Object> param);
}

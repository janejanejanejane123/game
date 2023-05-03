package com.ruoyi.order.mapper;

import com.ruoyi.order.domain.GrabOrder;

import java.util.List;
import java.util.Map;

/**
 * grabMapper接口
 * 
 * @author ry
 * @date 2022-07-15
 */
public interface GrabOrderMapper 
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
     * 删除grab
     * 
     * @param id grab主键
     * @return 结果
     */
    public int deleteGrabOrderById(String id);

    /**
     * 批量删除grab
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGrabOrderByIds(String[] ids);

    int updateGrabOrderStatus(GrabOrder grabOrder);

    List<GrabOrder> selectMarketList(Map<String, Object> param);

    List<GrabOrder> queryMyOrder(Map<String, Object> param);
}

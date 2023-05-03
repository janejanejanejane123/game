package com.ruoyi.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ruoyi.order.domain.UserOrder;

/**
 * orderService接口
 *
 * @author ruoyi
 * @date 2022-03-23
 */
public interface IUserOrderService
{
    /**
     * 查询order
     *
     * @param id order主键
     * @return order
     */
    public UserOrder selectUserOrderById(String id);

    /**
     * 查询order列表
     *
     * @param UserOrder order
     * @return order集合
     */
    public List<UserOrder> selectUserOrderList(UserOrder UserOrder);

    /**
     * 新增order
     *
     * @param UserOrder order
     * @return 结果
     */
    public int insertUserOrder(UserOrder UserOrder);

    /**
     * 修改order
     *
     * @param UserOrder order
     * @return 结果
     */
    public int updateUserOrder(UserOrder UserOrder);

    /**
     * 批量删除order
     *
     * @param ids 需要删除的order主键集合
     * @return 结果
     */
    public int deleteUserOrderByIds(String[] ids);

    /**
     * 删除order信息
     *
     * @param id order主键
     * @return 结果
     */
    public int deleteUserOrderById(String id);


    /**
     * 购买订单
     * @param referId
     * @return
     */
    public int changeOrderStatus(int afterStatus,String referId,int beforeStatus);

    List<Map<String,Object>> selectPool(Map<String, Object> param);

    List<Map<String,Object>> selectMyOrder(Map<String, Object> param);

    int updateUserOrderStatus(Map<String, String> map);

    int queryUnCheckNo();

    int cancelUserOrder(String primaryKey, Long userId);

    BigDecimal selectUserOrderSum(UserOrder userOrder);
}

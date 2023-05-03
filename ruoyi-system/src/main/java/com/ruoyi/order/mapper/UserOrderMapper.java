package com.ruoyi.order.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ruoyi.order.domain.UserOrder;
import org.apache.ibatis.annotations.Param;

/**
 * orderMapper接口
 *
 * @author ruoyi
 * @date 2022-03-23
 */
public interface UserOrderMapper
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
     * 删除order
     *
     * @param id order主键
     * @return 结果
     */
    public int deleteUserOrderById(String id);

    /**
     * 批量删除order
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserOrderByIds(String[] ids);

    public BigDecimal querySaleAmountByUid(Long userId);

    public int changeOrderStatus(@Param("afterStatus") int afterStatus, @Param("id")String id, @Param("beforeStatus")int beforeStatus);

    List<Map<String, Object>> selectPool(Map<String, Object> param);

    List<Map<String,Object>> selectMyOrder(Map<String, Object> param);

    int updateUserOrderStatus(Map<String, String> map);

    int queryUnCheckNo();

    int cancelUserOrder(@Param("id")String id, @Param("userId")Long userId);

    /*查询汇总*/
    Map selectUserOrderSum(UserOrder userOrder);
}

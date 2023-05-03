package com.ruoyi.order.mapper;

import com.ruoyi.order.domain.UserOrderDetail;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * orderMapper接口
 * 
 * @author mc_dog
 * @date 2022-03-20
 */
public interface UserOrderDetailMapper 
{
    /**
     * 查询order
     * 
     * @param primaryId order主键
     * @return order
     */
    public UserOrderDetail selectUserOrderDetailByPrimaryId(String primaryId);

    /**
     * 查询order列表
     * 
     * @param userOrderDetail order
     * @return order集合
     */
    public List<UserOrderDetail> selectUserList(UserOrderDetail userOrderDetail);

    /**
     * 新增order
     * 
     * @param userOrderDetail order
     * @return 结果
     */
    public int insertUserOrderDetail(UserOrderDetail userOrderDetail);

    /**
     * 修改order
     * 
     * @param userOrderDetail order
     * @return 结果
     */
    public int updateUserOrderDetail(UserOrderDetail userOrderDetail);

    /**
     * 删除order
     * 
     * @param primaryId order主键
     * @return 结果
     */
    public int deleteUserOrderDetailByPrimaryId(String primaryId);

    /**
     * 批量删除order
     * 
     * @param primaryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserOrderDetailByPrimaryIds(String[] primaryIds);

    public BigDecimal queryUnPaidOrderByUid(Long userId);

    int updateStatus(UserOrderDetail userOrderDetail);

    int reloadSuccessStatus(String primaryId);

    int reloadPauseStatus(String primaryId);

    int delSuccessImg(String primaryId);

    int delPauseImg(String primaryId);

    int cancelOrderByAdmin(UserOrderDetail userOrderDetail);

    int sendBalanceByAdmin(UserOrderDetail userOrderDetail);

    List<UserOrderDetail> selectMyBuyOrder(Map<String, Object> param);
}

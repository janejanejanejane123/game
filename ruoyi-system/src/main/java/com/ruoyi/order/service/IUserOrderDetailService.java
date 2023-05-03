package com.ruoyi.order.service;

import com.ruoyi.order.domain.UserOrderDetail;
import com.ruoyi.order.domain.vo.OrderDetailVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * orderService接口
 * 
 * @author mc_dog
 * @date 2022-03-20
 */
public interface IUserOrderDetailService 
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

    public List<OrderDetailVo> selectOrderDetailVolList(UserOrderDetail userOrderDetail);

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
     * 批量删除order
     * 
     * @param primaryIds 需要删除的order主键集合
     * @return 结果
     */
    public int deleteUserOrderDetailByPrimaryIds(String[] primaryIds);

    /**
     * 删除order信息
     * 
     * @param primaryId order主键
     * @return 结果
     */
    public int deleteUserOrderDetailByPrimaryId(String primaryId);

    BigDecimal queryUnPaidOrderByUid(Long userId);

    /**
     * 根据条件更改订单状态
     * @param userOrderDetail
     * @return
     */
    int updateStatus(UserOrderDetail userOrderDetail);

    int deleteImg(String primaryId, String type);

    int reUploadPic(String primaryId, String type);

    int updateImg(String primaryKey, String path, int i);

    OrderDetailVo selectById(String parimaryId, Long userId);

    int cancelOrderByAdmin(UserOrderDetail userOrderDetail);

    int sendBalanceByAdmin(UserOrderDetail orderDetail);

    List<OrderDetailVo> selectMyBuyOrder(Map<String, Object> param);
}

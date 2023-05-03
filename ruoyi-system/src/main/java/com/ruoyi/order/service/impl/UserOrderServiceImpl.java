package com.ruoyi.order.service.impl;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.enums.UserOrderConst;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.order.domain.UserOrder;
import com.ruoyi.order.mapper.UserOrderMapper;
import com.ruoyi.order.service.IUserOrderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * orderService业务层处理
 *
 * @author ruoyi
 * @date 2022-03-23
 */
@Service
public class UserOrderServiceImpl implements IUserOrderService {
    @Resource
    private UserOrderMapper userOrderMapper;

    @Resource
    RedisTemplate redisTemplate;

    /**
     * 查询order
     *
     * @param id order主键
     * @return order
     */
    @Override
    public UserOrder selectUserOrderById(String id) {
        return userOrderMapper.selectUserOrderById(id);
    }

    /**
     * 查询order列表
     *
     * @param UserOrder order
     * @return order
     */
    @Override
    public List<UserOrder> selectUserOrderList(UserOrder UserOrder) {
        return userOrderMapper.selectUserOrderList(UserOrder);
    }

    /**
     * 新增order
     *
     * @param UserOrder order
     * @return 结果
     */
    @Override
    public int insertUserOrder(UserOrder UserOrder) {
        UserOrder.setCreateTime(DateUtils.getNowDate());
        return userOrderMapper.insertUserOrder(UserOrder);
    }

    /**
     * 修改order
     *
     * @param UserOrder order
     * @return 结果
     */
    @Override
    public int updateUserOrder(UserOrder UserOrder) {
        UserOrder.setUpdateTime(DateUtils.getNowDate());
        return userOrderMapper.updateUserOrder(UserOrder);
    }

    /**
     * 批量删除order
     *
     * @param ids 需要删除的order主键
     * @return 结果
     */
    @Override
    public int deleteUserOrderByIds(String[] ids) {
        return userOrderMapper.deleteUserOrderByIds(ids);
    }

    /**
     * 删除order信息
     *
     * @param id order主键
     * @return 结果
     */
    @Override
    public int deleteUserOrderById(String id) {
        return userOrderMapper.deleteUserOrderById(id);
    }

    @Override
    public int changeOrderStatus(int afterStatus, String referId, int beforeStatus) {
        return userOrderMapper.changeOrderStatus(afterStatus, referId, beforeStatus);
    }

    @Override
    public List<Map<String, Object>> selectPool(Map<String, Object> param) {
        List<Map<String, Object>> res = new ArrayList<>();
        List<Map<String, Object>> list = userOrderMapper.selectPool(param);
        list.forEach(item -> {
            Object userId = item.remove("user_id");
            Boolean isOnline = redisTemplate.opsForHash().hasKey(Constants.ONLINE_WEB_USER_KEY, "0_" + userId);
            Boolean flag = UserOrderConst.MERCHANT_SALE.equals(Short.valueOf(item.get("is_split").toString()));
            item.put("online", (flag || isOnline) ? "0" : "1");
            res.add(item);
        });
        return res;
    }

    @Override
    public List<Map<String, Object>> selectMyOrder(Map<String, Object> param) {
        return userOrderMapper.selectMyOrder(param);
    }

    @Override
    public int updateUserOrderStatus(Map<String, String> map) {
        return userOrderMapper.updateUserOrderStatus(map);
    }

    @Override
    public int queryUnCheckNo() {
        return userOrderMapper.queryUnCheckNo();
    }

    @Override
    public int cancelUserOrder(String primaryKey, Long userId) {
        return userOrderMapper.cancelUserOrder(primaryKey, userId);
    }

    @Override
    public BigDecimal selectUserOrderSum(UserOrder userOrder) {
        Map map = userOrderMapper.selectUserOrderSum(userOrder);
        if (map != null && map.get("totalAmoutSum") != null) {
            return new BigDecimal(map.get("totalAmoutSum").toString());
        } else {
            return BigDecimal.ZERO;
        }
    }
}

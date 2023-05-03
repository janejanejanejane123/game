package com.ruoyi.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.enums.UserOrderConst;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.order.domain.UserOrderDetail;
import com.ruoyi.order.domain.vo.OrderDetailVo;
import com.ruoyi.order.mapper.UserOrderDetailMapper;
import com.ruoyi.order.service.IUserOrderDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * orderService业务层处理
 * 
 * @author mc_dog
 * @date 2022-03-20
 */
@Service
public class UserOrderDetailServiceImpl implements IUserOrderDetailService
{
    @Resource
    private UserOrderDetailMapper userOrderDetailMapper;

    /**
     * 查询order
     * 
     * @param primaryId order主键
     * @return order
     */
    @Override
    public UserOrderDetail selectUserOrderDetailByPrimaryId(String primaryId)
    {
        return userOrderDetailMapper.selectUserOrderDetailByPrimaryId(primaryId);
    }

    /**
     * 查询order列表
     * 
     * @param userOrderDetail order
     * @return order
     */
    @Override
    public List<UserOrderDetail> selectUserList(UserOrderDetail userOrderDetail)
    {
        return userOrderDetailMapper.selectUserList(userOrderDetail);
    }

    @Override
    public List<OrderDetailVo> selectOrderDetailVolList(UserOrderDetail userOrderDetail) {
        List<UserOrderDetail> userOrderDetails = userOrderDetailMapper.selectUserList(userOrderDetail);
        List<OrderDetailVo> result = new ArrayList<>();
        userOrderDetails.forEach(item->{
            result.add(getVo(item,userOrderDetail.getBuyerId()));
        });
        return result;
    }

    /**
     * 新增order
     * 
     * @param userOrderDetail order
     * @return 结果
     */
    @Override
    public int insertUserOrderDetail(UserOrderDetail userOrderDetail)
    {
        userOrderDetail.setCreateTime(DateUtils.getNowDate());
        return userOrderDetailMapper.insertUserOrderDetail(userOrderDetail);
    }

    /**
     * 修改order
     * 
     * @param userOrderDetail order
     * @return 结果
     */
    @Override
    public int updateUserOrderDetail(UserOrderDetail userOrderDetail)
    {
        return userOrderDetailMapper.updateUserOrderDetail(userOrderDetail);
    }

    /**
     * 批量删除order
     * 
     * @param primaryIds 需要删除的order主键
     * @return 结果
     */
    @Override
    public int deleteUserOrderDetailByPrimaryIds(String[] primaryIds)
    {
        return userOrderDetailMapper.deleteUserOrderDetailByPrimaryIds(primaryIds);
    }

    /**
     * 删除order信息
     * 
     * @param primaryId order主键
     * @return 结果
     */
    @Override
    public int deleteUserOrderDetailByPrimaryId(String primaryId)
    {
        return userOrderDetailMapper.deleteUserOrderDetailByPrimaryId(primaryId);
    }

    @Override
    public BigDecimal queryUnPaidOrderByUid(Long userId) {
        return userOrderDetailMapper.queryUnPaidOrderByUid(userId);
    }

    @Override
    public int updateStatus(UserOrderDetail userOrderDetail) {
        return userOrderDetailMapper.updateStatus(userOrderDetail);
    }

    @Override
    public int deleteImg(String primaryId, String type) {
        if ("0".equals(type)) {
            return userOrderDetailMapper.delSuccessImg(primaryId);
        }
        if ("1".equals(type)) {
            return userOrderDetailMapper.delPauseImg(primaryId);
        }
        return 0;
    }

    @Override
    public int reUploadPic(String primaryId, String type) {
        if ("0".equals(type)) {
            return userOrderDetailMapper.reloadSuccessStatus(primaryId);
        }
        if ("1".equals(type)) {
            return userOrderDetailMapper.reloadPauseStatus(primaryId);
        }
        return 0;
    }

    @Override
    public int updateImg(String primaryKey, String path, int i) {
        UserOrderDetail userOrderDetail = new UserOrderDetail();
        userOrderDetail.setPrimaryId(primaryKey);
        if(i == 0){
            userOrderDetail.setSuccessImg(path);
        }
        if(i == 1){
            userOrderDetail.setPauseImg(path);
        }
        return userOrderDetailMapper.updateUserOrderDetail(userOrderDetail);
    }


    @Override
    public int cancelOrderByAdmin(UserOrderDetail userOrderDetail) {
        return userOrderDetailMapper.cancelOrderByAdmin(userOrderDetail);
    }

    @Override
    public int sendBalanceByAdmin(UserOrderDetail userOrderDetail) {
        return userOrderDetailMapper.sendBalanceByAdmin(userOrderDetail);
    }

    @Override
    public OrderDetailVo selectById(String primary, Long userId) {
        UserOrderDetail item = userOrderDetailMapper.selectUserOrderDetailByPrimaryId(primary);
        return getVo(item,userId);
    }

    public OrderDetailVo getVo(UserOrderDetail item, Long userId){
        OrderDetailVo vo = new OrderDetailVo();
        BeanUtils.copyBeanProp(vo, item);
        //隐藏会员名和银行卡信息
        vo.setBuyerCardName(StringUtils.realNameChange(vo.getBuyerCardName()));
        vo.setBuyerCardAddress(StringUtils.realNameChange(vo.getBuyerCardAddress()));
        //如果是创建状态,买家只显示取消订单，卖家显示取消订单/卖家确认
        if (item.getPayStatus().equals(UserOrderConst.PAY_STATUS_CREATE)) {
            long l = DateUtils.addMinute(item.getCreateTime(), 10).getTime() - System.currentTimeMillis();
            vo.setCountTime(l/1000);
            vo.setOperation_1("取消订单");
            vo.setOperation_url_1("cancelOrder");
            if (userId.equals(item.getSalerId())) {
                vo.setOperation_2("卖家确认");
                vo.setOperation_url_2("preConfirm");
            }else{
                vo.setOperation_2("等待卖家确认");
            }
        }
        //如果是卖家确认,买家显示取消订单/完成付款，卖家只显示等待买家付款
        if (item.getPayStatus().equals(UserOrderConst.PAY_STATUS_CONFIRM)) {
            long l = DateUtils.addMinute(item.getSalerCheckTime(), 10).getTime() - System.currentTimeMillis();
            vo.setCountTime(l/1000);
            vo.setBuyerCardAddress(item.getBuyerCardAddress());
            if (userId.equals(item.getBuyerId())) {
                vo.setOperation_1("取消订单");
                vo.setOperation_url_1("cancelOrderByBuyer");
                vo.setOperation_3("完成付款");
                vo.setOperation_url_3("finishPay");
            }else{
                vo.setOperation_2("等待买家付款");
            }
        }
        //如果是完成付款,买家不显示任何操作，卖家只显示确认放蛋/暂停放蛋
        if (item.getPayStatus().equals(UserOrderConst.PAY_STATUS_PAID)) {
            vo.setSalerCardName(StringUtils.realNameChange(vo.getSalerCardName()));
            vo.setSalerCardAddress(StringUtils.realNameChange(vo.getSalerCardAddress()));
            if (userId.equals(item.getSalerId())) {
                vo.setOperation_1("确认收到款项");
                vo.setOperation_url_1("sendBalance");
                vo.setOperation_3("暂未收到款项");
                vo.setOperation_url_3("pauseOrder");
            }else{
                vo.setOperation_1("等待卖家放蛋");
                if("-1".equals(vo.getSuccessImg())){
                    vo.setSuccessImg(null);
                    vo.setOperation_3("重新上传付款凭证");
                    vo.setOperation_url_3("reloadSucPic");
                }
            }
        }
        //如果是上分成功,买卖双方均显示已完成放款
        if (item.getPayStatus().equals(UserOrderConst.PAY_STATUS_FINISH)) {
            vo.setSalerCardName(StringUtils.realNameChange(vo.getSalerCardName()));
            vo.setSalerCardAddress(StringUtils.realNameChange(vo.getSalerCardAddress()));
        }
        //如果是暂停放款,买家显示等待卖家上分，卖家只显示确认上分
        if (item.getPayStatus().equals(UserOrderConst.PAY_STATUS_PAUSE)) {
            vo.setSalerCardName(StringUtils.realNameChange(vo.getSalerCardName()));
            vo.setSalerCardAddress(StringUtils.realNameChange(vo.getSalerCardAddress()));
            if (userId.equals(item.getSalerId())) {
                if("-1".equals(vo.getPauseImg())){
                    vo.setPauseImg(null);
                    vo.setOperation_3("重新上传暂停凭证");
                    vo.setOperation_url_3("reloadPausePic");
                }else{
                    vo.setOperation_1("暂未收到款项");
                }
                vo.setOperation_2("确认收到款项");
                vo.setOperation_url_2("sendBalanceBySaler");
            }else{
                vo.setOperation_1("等待卖家放蛋");
                if("-1".equals(vo.getSuccessImg())){
                    vo.setSuccessImg(null);
                    vo.setOperation_3("重新上传付款凭证");
                    vo.setOperation_url_3("reloadSucPic");
                }
            }
        }
        //如果是取消订单,隐藏掉卖家信息
        if (!item.getPayStatus().equals(UserOrderConst.PAY_STATUS_CONFIRM)) {
            vo.setSalerCardName(null);
            vo.setSalerCardAddress(null);
            vo.setSalerCardRemark(null);
        }
        return vo;
    }

    @Override
    public List<OrderDetailVo> selectMyBuyOrder(Map<String, Object> param) {
        List<UserOrderDetail> list = userOrderDetailMapper.selectMyBuyOrder(param);
        List<OrderDetailVo> res = new ArrayList<>();
        list.forEach(item->{
            res.add(getVo(item,Long.parseLong(param.get("uid").toString())));
        });
        return res;
    }
}

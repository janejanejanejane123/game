package com.ruoyi.order.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.order.domain.GrabOrder;
import com.ruoyi.order.domain.vo.GrabOrderVo;
import com.ruoyi.order.domain.vo.OrderVo;
import com.ruoyi.order.mapper.GrabOrderMapper;
import com.ruoyi.order.service.IGrabOrderService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * grabService业务层处理
 * 
 * @author ry
 * @date 2022-07-15
 */
@Service
public class GrabOrderServiceImpl implements IGrabOrderService
{
    @Resource
    private GrabOrderMapper grabOrderMapper;

    /**
     * 查询grab
     * 
     * @param id grab主键
     * @return grab
     */
    @Override
    public GrabOrder selectGrabOrderById(String id)
    {
        return grabOrderMapper.selectGrabOrderById(id);
    }

    /**
     * 查询grab列表
     * 
     * @param grabOrder grab
     * @return grab
     */
    @Override
    public List<GrabOrder> selectGrabOrderList(GrabOrder grabOrder)
    {
        return grabOrderMapper.selectGrabOrderList(grabOrder);
    }

    /**
     * 新增grab
     * 
     * @param grabOrder grab
     * @return 结果
     */
    @Override
    public int insertGrabOrder(GrabOrder grabOrder)
    {
        grabOrder.setCreateTime(DateUtils.getNowDate());
        return grabOrderMapper.insertGrabOrder(grabOrder);
    }

    /**
     * 修改grab
     * 
     * @param grabOrder grab
     * @return 结果
     */
    @Override
    public int updateGrabOrder(GrabOrder grabOrder)
    {
        return grabOrderMapper.updateGrabOrder(grabOrder);
    }

    /**
     * 批量删除grab
     * 
     * @param ids 需要删除的grab主键
     * @return 结果
     */
    @Override
    public int deleteGrabOrderByIds(String[] ids)
    {
        return grabOrderMapper.deleteGrabOrderByIds(ids);
    }

    /**
     * 删除grab信息
     * 
     * @param id grab主键
     * @return 结果
     */
    @Override
    public int deleteGrabOrderById(String id)
    {
        return grabOrderMapper.deleteGrabOrderById(id);
    }

    @Override
    public int updateGrabOrderStatus(GrabOrder grabOrder) {
        return grabOrderMapper.updateGrabOrderStatus(grabOrder);
    }

    @Override
    public List<GrabOrderVo> selectMarketList(Map<String, Object> param) {
        List<GrabOrder> list = grabOrderMapper.selectMarketList(param);
        List<GrabOrderVo> res = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(item->{
                GrabOrderVo vo = new GrabOrderVo();
                BeanUtils.copyBeanProp(vo,item);
                Date createTime = vo.getCreateTime();
                //todo 本地测试暂定为10分钟取消跑分订单
                Date addMinute = DateUtils.addMinute(createTime, 10);
                vo.setCountTime((addMinute.getTime()-System.currentTimeMillis())/1000);
                res.add(vo);
            });
        }
        return res;
    }

    @Override
    public List<OrderVo> queryMyOrder(Map<String, Object> param) {
        List<OrderVo> res = new ArrayList<>();
        List<GrabOrder> list = grabOrderMapper.queryMyOrder(param);
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(item->{

            });
        }
        return null;
    }
}

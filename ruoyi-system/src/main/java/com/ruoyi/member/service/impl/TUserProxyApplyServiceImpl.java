package com.ruoyi.member.service.impl;

import java.util.List;

import com.ruoyi.common.bussiness.constants.enums.TopicEnum;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.UserInfoApplyEnums;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.member.util.RocketMqService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.member.mapper.TUserProxyApplyMapper;
import com.ruoyi.member.domain.TUserProxyApply;
import com.ruoyi.member.service.ITUserProxyApplyService;

import javax.annotation.Resource;

/**
 * 代理申请Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-06-26
 */
@Service
public class TUserProxyApplyServiceImpl implements ITUserProxyApplyService 
{
    @Autowired
    private TUserProxyApplyMapper tUserProxyApplyMapper;
    @Resource
    private RocketMqService rocketMqService;
    @Resource
    private RedisCache redisCache;

    @Override
    public long countByUid(Long userId) {

        return tUserProxyApplyMapper.countByUid(userId);
    }

    /**
     * 查询代理申请
     * 
     * @param id 代理申请主键
     * @return 代理申请
     */
    @Override
    public TUserProxyApply selectTUserProxyApplyById(Long id)
    {
        return tUserProxyApplyMapper.selectTUserProxyApplyById(id);
    }

    /**
     * 查询代理申请列表
     * 
     * @param tUserProxyApply 代理申请
     * @return 代理申请
     */
    @Override
    public List<TUserProxyApply> selectTUserProxyApplyList(TUserProxyApply tUserProxyApply)
    {
        return tUserProxyApplyMapper.selectTUserProxyApplyList(tUserProxyApply);
    }

    /**
     * 新增代理申请
     * 
     * @param tUserProxyApply 代理申请
     * @return 结果
     */
    @Override
    public int insertTUserProxyApply(TUserProxyApply tUserProxyApply)
    {
        int i = tUserProxyApplyMapper.insertTUserProxyApply(tUserProxyApply);
        if (i==1){
            long incr = redisCache.incr(UserInfoApplyEnums.PROXY_APPLY.getRedisKey(), 1);
            rocketMqService.applyToAdmin(UserInfoApplyEnums.PROXY_APPLY,incr);
        }
        return i;
    }

    /**
     * 修改代理申请
     * 
     * @param tUserProxyApply 代理申请
     * @return 结果
     */
    @Override
    public int updateTUserProxyApply(TUserProxyApply tUserProxyApply)
    {
        tUserProxyApply.setUpdateTime(DateUtils.getNowDate());
        tUserProxyApply.setUpdateAdmin(SecurityUtils.getUsername());
        Short status = tUserProxyApply.getStatus();
        String code="message.mq.refuse";
        if (Constants.APPLY_PASS.equals(status)){
            code="message.mq.pass";
        }
        int i = tUserProxyApplyMapper.updateTUserProxyApply(tUserProxyApply);
        if (i==1){
            long decr = redisCache.decr(UserInfoApplyEnums.PROXY_APPLY.getRedisKey(), 1);
            rocketMqService.applyToAdmin(UserInfoApplyEnums.PROXY_APPLY,decr);
            rocketMqService.applyComplete(code,tUserProxyApply.getUid(),"message.type.proxy", TopicEnum.PROXY_COMP);
        }
        return i;
    }

    /**
     * 批量删除代理申请
     * 
     * @param ids 需要删除的代理申请主键
     * @return 结果
     */
    @Override
    public int deleteTUserProxyApplyByIds(Long[] ids)
    {
        return tUserProxyApplyMapper.deleteTUserProxyApplyByIds(ids);
    }

    /**
     * 删除代理申请信息
     * 
     * @param id 代理申请主键
     * @return 结果
     */
    @Override
    public int deleteTUserProxyApplyById(Long id)
    {
        return tUserProxyApplyMapper.deleteTUserProxyApplyById(id);
    }
}

package com.ruoyi.settings.service.impl;

import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.settings.domain.SysBrokerage;
import com.ruoyi.settings.mapper.SysBrokerageMapper;
import com.ruoyi.settings.service.ISysBrokerageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 佣金设置Service业务层处理
 * 
 * @author nn
 * @date 2022-06-19
 */
@Service
public class SysBrokerageServiceImpl implements ISysBrokerageService
{
    @Autowired
    private SysBrokerageMapper sysBrokerageMapper;

    @Resource
    private RedisCache redisCache;

    @Override
    public SysBrokerage selectSysBrokerageByFeeType(String feeType) {
        String cacheKey = getCacheKey(feeType);
        if(redisCache.hasKey(cacheKey)){
            return redisCache.getCacheObject(cacheKey);
        }else{
            SysBrokerage sysBrokerage = new SysBrokerage();
            sysBrokerage.setFeeType(feeType);
            return sysBrokerageMapper.checkFeeTypeBrokerageUnique(sysBrokerage);
        }
    }

    /**
     * 设置cache key
     *
     * @param feeType 佣金类型
     * @return 缓存键key
     */
    private String getCacheKey(String feeType)
    {
        return CacheKeyConstants.SYS_BROKERAGE_KEY + feeType;
    }

    /**
     * 查询佣金设置
     * 
     * @param id 佣金设置主键
     * @return 佣金设置
     */
    @Override
    public SysBrokerage selectSysBrokerageById(Long id)
    {
        return sysBrokerageMapper.selectSysBrokerageById(id);
    }

    /**
     * 查询佣金设置列表
     * 
     * @param sysBrokerage 佣金设置
     * @return 佣金设置
     */
    @Override
    public List<SysBrokerage> selectSysBrokerageList(SysBrokerage sysBrokerage)
    {
        return sysBrokerageMapper.selectSysBrokerageList(sysBrokerage);
    }

    /**
     * 新增佣金设置
     * 
     * @param sysBrokerage 佣金设置
     * @return 结果
     */
    @Override
    public int insertSysBrokerage(SysBrokerage sysBrokerage)
    {
        int row = sysBrokerageMapper.insertSysBrokerage(sysBrokerage);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(sysBrokerage.getFeeType()), sysBrokerage.getRate(),3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 修改佣金设置
     * 
     * @param sysBrokerage 佣金设置
     * @return 结果
     */
    @Override
    public int updateSysBrokerage(SysBrokerage sysBrokerage)
    {
        redisCache.deleteObject(getCacheKey(sysBrokerage.getFeeType()));
        int row = sysBrokerageMapper.updateSysBrokerage(sysBrokerage);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(sysBrokerage.getFeeType()), sysBrokerage.getRate(),3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 批量删除佣金设置
     * 
     * @param ids 需要删除的佣金设置主键
     * @return 结果
     */
    @Override
    public int deleteSysBrokerageByIds(Long[] ids)
    {
        for (Long id : ids) {
            SysBrokerage sysBrokerage = sysBrokerageMapper.selectSysBrokerageById(id);
            redisCache.deleteObject(getCacheKey(sysBrokerage.getFeeType()));
        }
        return sysBrokerageMapper.deleteSysBrokerageByIds(ids);
    }

    /**
     * 删除佣金设置信息
     * 
     * @param id 佣金设置主键
     * @return 结果
     */
    @Override
    public int deleteSysBrokerageById(Long id)
    {
        SysBrokerage sysBrokerage = sysBrokerageMapper.selectSysBrokerageById(id);
        redisCache.deleteObject(getCacheKey(sysBrokerage.getFeeType()));
        return sysBrokerageMapper.deleteSysBrokerageById(id);
    }

    /**
     * 校验该佣金类型是否唯一
     * @param sysBrokerage 检测的对象
     * @return
     */
    @Override
    public String checkFeeTypeBrokerageUnique(SysBrokerage sysBrokerage) {
        Long id = StringUtils.isNull(sysBrokerage.getId()) ? -1L : sysBrokerage.getId();
        SysBrokerage unique = sysBrokerageMapper.checkFeeTypeBrokerageUnique(sysBrokerage);
        if (StringUtils.isNotNull(unique) && unique.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;

    }
}

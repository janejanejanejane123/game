package com.ruoyi.settings.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.settings.mapper.SysAgencyBackwaterMapper;
import com.ruoyi.settings.domain.SysAgencyBackwater;
import com.ruoyi.settings.service.ISysAgencyBackwaterService;

import javax.annotation.Resource;

/**
 * 代理返水设置Service业务层处理
 * 
 * @author nn
 * @date 2022-06-26
 */
@Service
public class SysAgencyBackwaterServiceImpl implements ISysAgencyBackwaterService 
{
    @Autowired
    private SysAgencyBackwaterMapper sysAgencyBackwaterMapper;

    @Resource
    private RedisCache redisCache;

    /**
     * 设置cache key
     *
     * @param userId 代理Id
     * @param feeType 手续费类型
     * @return 缓存键key
     */
    private String getCacheKey(Long userId,String feeType)
    {
        return CacheKeyConstants.SYS_AGENCY_BACKWATER_KEY + userId + ":" + feeType;
    }

    /**
     * 查询代理返水设置
     * 
     * @param id 代理返水设置主键
     * @return 代理返水设置
     */
    @Override
    public SysAgencyBackwater selectSysAgencyBackwaterById(Long id)
    {
        return sysAgencyBackwaterMapper.selectSysAgencyBackwaterById(id);
    }

    /**
     * 查询代理返水设置列表
     * 
     * @param sysAgencyBackwater 代理返水设置
     * @return 代理返水设置
     */
    @Override
    public List<SysAgencyBackwater> selectSysAgencyBackwaterList(SysAgencyBackwater sysAgencyBackwater)
    {
        return sysAgencyBackwaterMapper.selectSysAgencyBackwaterList(sysAgencyBackwater);
    }

    /**
     * 新增代理返水设置
     * 
     * @param sysAgencyBackwater 代理返水设置
     * @return 结果
     */
    @Override
    public int insertSysAgencyBackwater(SysAgencyBackwater sysAgencyBackwater)
    {
        int row = sysAgencyBackwaterMapper.insertSysAgencyBackwater(sysAgencyBackwater);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(sysAgencyBackwater.getUserId(),sysAgencyBackwater.getFeeType()), sysAgencyBackwater.getRate(),3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 修改代理返水设置
     * 
     * @param sysAgencyBackwater 代理返水设置
     * @return 结果
     */
    @Override
    public int updateSysAgencyBackwater(SysAgencyBackwater sysAgencyBackwater)
    {
        //先把修改前的缓存删除.
        redisCache.deleteObject(getCacheKey(sysAgencyBackwater.getUserId(),sysAgencyBackwater.getFeeType()));
        int row = sysAgencyBackwaterMapper.updateSysAgencyBackwater(sysAgencyBackwater);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(sysAgencyBackwater.getUserId(),sysAgencyBackwater.getFeeType()), sysAgencyBackwater.getRate(),3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 批量删除代理返水设置
     * 
     * @param ids 需要删除的代理返水设置主键
     * @return 结果
     */
    @Override
    public int deleteSysAgencyBackwaterByIds(Long[] ids)
    {
        for (Long id : ids) {
            SysAgencyBackwater sysAgencyBackwater = selectSysAgencyBackwaterById(id);
            redisCache.deleteObject(getCacheKey(sysAgencyBackwater.getUserId(),sysAgencyBackwater.getFeeType()));
        }
        return sysAgencyBackwaterMapper.deleteSysAgencyBackwaterByIds(ids);
    }

    /**
     * 删除代理返水设置信息
     * 
     * @param id 代理返水设置主键
     * @return 结果
     */
    @Override
    public int deleteSysAgencyBackwaterById(Long id)
    {
        SysAgencyBackwater sysAgencyBackwater = selectSysAgencyBackwaterById(id);
        redisCache.deleteObject(getCacheKey(sysAgencyBackwater.getUserId(),sysAgencyBackwater.getFeeType()));
        return sysAgencyBackwaterMapper.deleteSysAgencyBackwaterById(id);
    }

    /**
     * 校验该代理该类型是否唯一
     * @param sysAgencyBackwater 检测的对象
     * @return
     */
    @Override
    public String checkFeeTypeToAgencyUnique(SysAgencyBackwater sysAgencyBackwater) {
        Long id = StringUtils.isNull(sysAgencyBackwater.getId()) ? -1L : sysAgencyBackwater.getId();
        SysAgencyBackwater unique = sysAgencyBackwaterMapper.checkFeeTypeToAgencyUnique(sysAgencyBackwater);
        if (StringUtils.isNotNull(unique) && unique.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 根据返水类型获取代理的手续费.
     *
     * @param userId 代理Id
     * @param feeType 返水类型
     * @return 结果
     */
    @Override
    public BigDecimal getAgencyBackwaterByFeeType(Long userId, String feeType) {
        BigDecimal rate = redisCache.getCacheObject(getCacheKey(userId,feeType));
        if(rate == null){
            SysAgencyBackwater query = new SysAgencyBackwater();
            query.setUserId(userId);
            query.setFeeType(feeType);
            SysAgencyBackwater sysAgencyBackwater  = sysAgencyBackwaterMapper.getAgencyBackwater(query);
            if(sysAgencyBackwater != null){
                rate = sysAgencyBackwater.getRate();
                redisCache.setCacheObject(getCacheKey(userId,feeType), rate,3600 * 24, TimeUnit.SECONDS);
            }
        }
        return rate;
    }
}

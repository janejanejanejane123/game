package com.ruoyi.settings.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.settings.mapper.SysBackwaterMapper;
import com.ruoyi.settings.domain.SysBackwater;
import com.ruoyi.settings.service.ISysBackwaterService;

import javax.annotation.Resource;

/**
 * 返水设置Service业务层处理
 * 
 * @author nn
 * @date 2022-06-24
 */
@Service
public class SysBackwaterServiceImpl implements ISysBackwaterService 
{
    @Autowired
    private SysBackwaterMapper sysBackwaterMapper;

    @Resource
    private RedisCache redisCache;

    /**
     * 设置cache key
     *
     * @param feeType 返水类型
     * @return 缓存键key
     */
    private String getCacheKey(String feeType)
    {
        return CacheKeyConstants.SYS_BACKWATER_KEY + feeType;
    }

    /**
     * 查询返水设置
     * 
     * @param id 返水设置主键
     * @return 返水设置
     */
    @Override
    public SysBackwater selectSysBackwaterById(Long id)
    {
        return sysBackwaterMapper.selectSysBackwaterById(id);
    }

    /**
     * 查询返水设置列表
     * 
     * @param sysBackwater 返水设置
     * @return 返水设置
     */
    @Override
    public List<SysBackwater> selectSysBackwaterList(SysBackwater sysBackwater)
    {
        return sysBackwaterMapper.selectSysBackwaterList(sysBackwater);
    }

    /**
     * 新增返水设置
     * 
     * @param sysBackwater 返水设置
     * @return 结果
     */
    @Override
    public int insertSysBackwater(SysBackwater sysBackwater)
    {
        int row = sysBackwaterMapper.insertSysBackwater(sysBackwater);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(sysBackwater.getFeeType()), sysBackwater.getRate(),3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 修改返水设置
     * 
     * @param sysBackwater 返水设置
     * @return 结果
     */
    @Override
    public int updateSysBackwater(SysBackwater sysBackwater)
    {
        redisCache.deleteObject(getCacheKey(sysBackwater.getFeeType()));
        int row = sysBackwaterMapper.updateSysBackwater(sysBackwater);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(sysBackwater.getFeeType()), sysBackwater.getRate(),3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 批量删除返水设置
     * 
     * @param ids 需要删除的返水设置主键
     * @return 结果
     */
    @Override
    public int deleteSysBackwaterByIds(Long[] ids)
    {
        for (Long id : ids) {
            SysBackwater sysBackwater = sysBackwaterMapper.selectSysBackwaterById(id);
            redisCache.deleteObject(getCacheKey(sysBackwater.getFeeType()));
        }
        return sysBackwaterMapper.deleteSysBackwaterByIds(ids);
    }

    /**
     * 删除返水设置信息
     * 
     * @param id 返水设置主键
     * @return 结果
     */
    @Override
    public int deleteSysBackwaterById(Long id)
    {
        SysBackwater sysBackwater = sysBackwaterMapper.selectSysBackwaterById(id);
        redisCache.deleteObject(getCacheKey(sysBackwater.getFeeType()));
        return sysBackwaterMapper.deleteSysBackwaterById(id);
    }

    /**
     * 校验该佣金类型是否唯一
     * @param sysBackwater 检测的对象
     * @return
     */
    @Override
    public String checkFeeTypeBackwaterUnique(SysBackwater sysBackwater) {
        Long id = StringUtils.isNull(sysBackwater.getId()) ? -1L : sysBackwater.getId();
        SysBackwater unique = sysBackwaterMapper.checkFeeTypeBackwaterUnique(sysBackwater);
        if (StringUtils.isNotNull(unique) && unique.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;

    }
}

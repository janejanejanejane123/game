package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.file.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysPayConfigMapper;
import com.ruoyi.system.domain.SysPayConfig;
import com.ruoyi.system.service.ISysPayConfigService;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 支付配置Service业务层处理
 * 
 * @author nn
 * @date 2022-09-06
 */
@Service
public class SysPayConfigServiceImpl implements ISysPayConfigService 
{
    @Autowired
    private SysPayConfigMapper sysPayConfigMapper;

    @Resource
    private MinioUtil minioUtil;

    @Resource
    private RedisCache redisCache;

    /**
     * 设置cache key
     *
     * @param userId 商户ID
     * @return 缓存键key
     */
    private String getCacheKey(Long userId) {
        return CacheKeyConstants.SYS_PAY_CONFIG_KEY + userId;
    }

    /**
     * 查询支付配置
     * 
     * @param id 支付配置主键
     * @return 支付配置
     */
    @Override
    public SysPayConfig selectSysPayConfigById(Long id) {
        return sysPayConfigMapper.selectSysPayConfigById(id);
    }

    /**
     * 查询支付配置列表
     * 
     * @param sysPayConfig 支付配置
     * @return 支付配置
     */
    @Override
    public List<SysPayConfig> selectSysPayConfigList(SysPayConfig sysPayConfig) {
        return sysPayConfigMapper.selectSysPayConfigList(sysPayConfig);
    }

    /**
     * 新增支付配置
     * 
     * @param sysPayConfig 支付配置
     * @return 结果
     */
    @Override
    public int insertSysPayConfig(SysPayConfig sysPayConfig) {
        int row = sysPayConfigMapper.insertSysPayConfig(sysPayConfig);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(sysPayConfig.getUserId()),sysPayConfig,3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 修改支付配置
     * 
     * @param sysPayConfig 支付配置
     * @return 结果
     */
    @Override
    public int updateSysPayConfig(SysPayConfig sysPayConfig) {
        //先把修改前的缓存删除.
        redisCache.deleteObject(getCacheKey(sysPayConfig.getUserId()));
        int row = sysPayConfigMapper.updateSysPayConfig(sysPayConfig);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(sysPayConfig.getUserId()),sysPayConfig,3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 批量删除支付配置
     * 
     * @param ids 需要删除的支付配置主键
     * @return 结果
     */
    @Override
    public int deleteSysPayConfigByIds(Long[] ids)
    {
        return sysPayConfigMapper.deleteSysPayConfigByIds(ids);
    }

    /**
     * 删除支付配置信息
     * 
     * @param id 支付配置主键
     * @return 结果
     */
    @Override
    public int deleteSysPayConfigById(Long id)
    {
        return sysPayConfigMapper.deleteSysPayConfigById(id);
    }

    /**
     * 查询支付配置
     *
     * @param userId 商户ID
     * @return 支付配置
     */
    @Override
    public SysPayConfig selectSysPayConfigByUserId(Long userId) {
        SysPayConfig sysPayConfig = redisCache.getCacheObject(getCacheKey(userId));
        if(sysPayConfig == null){
            sysPayConfig = sysPayConfigMapper.selectSysPayConfigByUserId(userId);
            if(sysPayConfig != null){
                redisCache.setCacheObject(getCacheKey(sysPayConfig.getUserId()),sysPayConfig,3600 * 24, TimeUnit.SECONDS);
            }
        }
        return sysPayConfig;
    }

    /**
     * 上传Logo.
     * @param file
     * @return
     */
    @Override
    public String uploadLogo(MultipartFile file) {
        return  minioUtil.upload(file, false);
    }
}

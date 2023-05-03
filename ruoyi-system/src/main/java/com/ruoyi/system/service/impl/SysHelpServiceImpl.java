package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.CacheUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.settings.domain.SysIpBlacklist;
import com.ruoyi.settings.domain.SysServiceCharge;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysHelpMapper;
import com.ruoyi.system.domain.SysHelp;
import com.ruoyi.system.service.ISysHelpService;

import javax.annotation.Resource;

/**
 * 会员帮助Service业务层处理
 * 
 * @author nn
 * @date 2022-03-13
 */
@Service
public class SysHelpServiceImpl implements ISysHelpService 
{
    @Resource
    private SysHelpMapper sysHelpMapper;

    @Resource
    private RedisCache redisCache;

    /**
     * 设置cache key
     *
     * @param helpType 帮助类型
     * @return 缓存键key
     */
    private String getCacheKey(String helpType)
    {
        return CacheKeyConstants.SYS_HELP_KEY + helpType;
    }

    /**
     * 查询会员帮助
     * 
     * @param helpId 会员帮助主键
     * @return 会员帮助
     */
    @Override
    public SysHelp selectSysHelpByHelpId(Long helpId)
    {
        return sysHelpMapper.selectSysHelpByHelpId(helpId);
    }

    /**
     * 查询会员帮助列表
     * 
     * @param sysHelp 会员帮助
     * @return 会员帮助
     */
    @Override
    public List<SysHelp> selectSysHelpList(SysHelp sysHelp)
    {
        return sysHelpMapper.selectSysHelpList(sysHelp);
    }

    /**
     * 新增会员帮助
     * 
     * @param sysHelp 会员帮助
     * @return 结果
     */
    @Override
    public int insertSysHelp(SysHelp sysHelp)
    {
        int row = sysHelpMapper.insertSysHelp(sysHelp);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(sysHelp.getHelpType()),sysHelp.getHelpContent(),3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 修改会员帮助
     * 
     * @param sysHelp 会员帮助
     * @return 结果
     */
    @Override
    public int updateSysHelp(SysHelp sysHelp)
    {
        //先把修改前的缓存删除.
        SysHelp help = sysHelpMapper.selectSysHelpByHelpId(sysHelp.getHelpId());
        redisCache.deleteObject(getCacheKey(help.getHelpType()));
        int row = sysHelpMapper.updateSysHelp(sysHelp);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(sysHelp.getHelpType()),sysHelp.getHelpContent(),3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 批量删除会员帮助
     * 
     * @param helpIds 需要删除的会员帮助主键
     * @return 结果
     */
    @Override
    public int deleteSysHelpByHelpIds(Long[] helpIds)
    {
        for (Long helpId : helpIds) {
            SysHelp sysHelp = selectSysHelpByHelpId(helpId);
            redisCache.deleteObject(getCacheKey(sysHelp.getHelpType()));
        }
        return sysHelpMapper.deleteSysHelpByHelpIds(helpIds);
    }

    /**
     * 删除会员帮助信息
     * 
     * @param helpId 会员帮助主键
     * @return 结果
     */
    @Override
    public int deleteSysHelpByHelpId(Long helpId)
    {
        SysHelp sysHelp = selectSysHelpByHelpId(helpId);
        redisCache.deleteObject(getCacheKey(sysHelp.getHelpType()));
        return sysHelpMapper.deleteSysHelpByHelpId(helpId);
    }

    /**
     * 根据帮助类型查询帮助
     * @param helpType 会员帮助类型
     * @return
     */
    @Override
    public SysHelp selectSysHelpByHelpType(String helpType) {
        return sysHelpMapper.selectSysHelpByHelpType(helpType);
    }

    /**
     * 根据类型查询参数配置信息
     *
     * @param helpType 参数key
     * @return 参数键值
     */
    @Override
    public String selectHelpContentByHelpType(String helpType)
    {
        String helpContent =redisCache.getCacheObject(getCacheKey(helpType));
        if (StringUtils.isNotEmpty(helpContent))
        {
            return helpContent;
        }

        SysHelp sysHelp = sysHelpMapper.selectSysHelpByHelpType(helpType);
        if (StringUtils.isNotNull(sysHelp))
        {
            redisCache.setCacheObject(getCacheKey(sysHelp.getHelpType()),sysHelp.getHelpContent(),3600 * 24, TimeUnit.SECONDS);
            return sysHelp.getHelpContent();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 校验帮助是否唯一
     *
     * @param sysHelp
     * @return 结果
     */
    @Override
    public String checkHelpTypeUnique(SysHelp sysHelp)
    {
        Long helpId = StringUtils.isNull(sysHelp.getHelpId()) ? -1L : sysHelp.getHelpId();
        SysHelp help = sysHelpMapper.checkHelpTypeUnique(sysHelp.getHelpType());
        if (StringUtils.isNotNull(help) && help.getHelpId().longValue() != helpId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }
}

package com.ruoyi.member.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.annotation.Overtime;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisLock;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.ruoyi.member.mapper.TUserConfigMapper;
import com.ruoyi.member.domain.TUserConfig;
import com.ruoyi.member.service.ITUserConfigService;

import javax.annotation.Resource;

/**
 * 用户配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-04-08
 */
@Service
public class TUserConfigServiceImpl implements ITUserConfigService 
{
    @Autowired
    private TUserConfigMapper tUserConfigMapper;
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询用户配置
     * 
     * @param id 用户配置主键
     * @return 用户配置
     */
    @Override
    public TUserConfig selectTUserConfigById(Long id)
    {
        return tUserConfigMapper.selectTUserConfigById(id);
    }

    /**
     * 查询用户配置列表
     * 
     * @param tUserConfig 用户配置
     * @return 用户配置
     */
    @Override
    public List<TUserConfig> selectTUserConfigList(TUserConfig tUserConfig)
    {
        return tUserConfigMapper.selectTUserConfigList(tUserConfig);
    }

    /**
     * 新增用户配置
     * 
     * @param tUserConfig 用户配置
     * @return 结果
     */
    @Override
    @CacheEvict(key = "'t_user_config:registeredConfig'",value = "redisCache4Spring")
    public int insertTUserConfig(TUserConfig tUserConfig)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        return RedisLock.lockInOneSecondOperate("t_userConfig:insert:"+userId,()->{
            tUserConfig.setType((short)1);
            tUserConfig.setId(snowflakeIdUtils.nextId());
            tUserConfig.setAddTime(new Date());
            tUserConfig.setAddUser(loginUser.getUsername());
            return tUserConfigMapper.insertTUserConfig(tUserConfig);
        });
    }

    /**
     * 修改用户配置
     * 
     * @param tUserConfig 用户配置
     * @return 结果
     */
    @Override
    @CacheEvict(key = "'t_user_config:registeredConfig'",value = "redisCache4Spring")
    public int updateTUserConfig(TUserConfig tUserConfig)
    {
        return tUserConfigMapper.updateTUserConfig(tUserConfig);
    }

    /**
     * 批量删除用户配置
     * 
     * @param ids 需要删除的用户配置主键
     * @return 结果
     */
    @Override
    public int deleteTUserConfigByIds(Long[] ids)
    {
        return tUserConfigMapper.deleteTUserConfigByIds(ids);
    }

    /**
     * 删除用户配置信息
     * 
     * @param id 用户配置主键
     * @return 结果
     */
    @Override
    public int deleteTUserConfigById(Long id)
    {
        return tUserConfigMapper.deleteTUserConfigById(id);
    }

    /**
     * 查询用户注册配置
     * @return
     */
    @Override
    @Cacheable(key = "'t_user_config:registeredConfig'",value = "redisCache4Spring")
    @Overtime(1800)
    public AjaxResult queryRegisteredConfig() {
        List<Map<String,Object>> result= tUserConfigMapper.selectRegisteredConfig();
        return AjaxResult.success(result);
    }
}

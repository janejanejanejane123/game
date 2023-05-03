package com.ruoyi.member.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.member.domain.TUserConfig;

/**
 * 用户配置Mapper接口
 * 
 * @author ruoyi
 * @date 2022-04-08
 */
public interface TUserConfigMapper 
{
    /**
     * 查询用户配置
     * 
     * @param id 用户配置主键
     * @return 用户配置
     */
    public TUserConfig selectTUserConfigById(Long id);

    /**
     * 查询用户配置列表
     * 
     * @param tUserConfig 用户配置
     * @return 用户配置集合
     */
    public List<TUserConfig> selectTUserConfigList(TUserConfig tUserConfig);

    /**
     * 新增用户配置
     * 
     * @param tUserConfig 用户配置
     * @return 结果
     */
    public int insertTUserConfig(TUserConfig tUserConfig);

    /**
     * 修改用户配置
     * 
     * @param tUserConfig 用户配置
     * @return 结果
     */
    public int updateTUserConfig(TUserConfig tUserConfig);

    /**
     * 删除用户配置
     * 
     * @param id 用户配置主键
     * @return 结果
     */
    public int deleteTUserConfigById(Long id);

    /**
     * 批量删除用户配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTUserConfigByIds(Long[] ids);

    List<Map<String, Object>> selectRegisteredConfig();
}

package com.ruoyi.member.mapper;

import com.ruoyi.member.domain.TEmailConfig;

import java.util.List;


/**
 * 邮箱配置Mapper接口
 * 
 * @author ruoyi
 * @date 2022-03-29
 */
public interface TEmailConfigMapper 
{
    /**
     * 查询邮箱配置
     * 
     * @param id 邮箱配置主键
     * @return 邮箱配置
     */
    public TEmailConfig selectTEmailConfigById(Long id);

    /**
     * 查询邮箱配置列表
     * 
     * @param tEmailConfig 邮箱配置
     * @return 邮箱配置集合
     */
    public List<TEmailConfig> selectTEmailConfigList(TEmailConfig tEmailConfig);

    /**
     * 新增邮箱配置
     * 
     * @param tEmailConfig 邮箱配置
     * @return 结果
     */
    public int insertTEmailConfig(TEmailConfig tEmailConfig);

    /**
     * 修改邮箱配置
     * 
     * @param tEmailConfig 邮箱配置
     * @return 结果
     */
    public int updateTEmailConfig(TEmailConfig tEmailConfig);

    /**
     * 删除邮箱配置
     * 
     * @param id 邮箱配置主键
     * @return 结果
     */
    public int deleteTEmailConfigById(Long id);

    /**
     * 批量删除邮箱配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTEmailConfigByIds(Long[] ids);

    List<TEmailConfig> findEmail(TEmailConfig config);
}

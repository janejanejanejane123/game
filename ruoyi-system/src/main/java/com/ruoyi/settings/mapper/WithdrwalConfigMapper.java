package com.ruoyi.settings.mapper;

import com.ruoyi.settings.domain.WithdrwalConfig;

import java.util.List;


/**
 * 提现配置Mapper接口
 *
 * @author ruoyi
 * @date 2022-09-13
 */
public interface WithdrwalConfigMapper
{
    /**
     * 查询提现配置
     *
     * @param id 提现配置主键
     * @return 提现配置
     */
    public WithdrwalConfig selectWithdrwalConfigById(Integer id);

    /**
     * 查询提现配置列表
     *
     * @param withdrwalConfig 提现配置
     * @return 提现配置集合
     */
    public List<WithdrwalConfig> selectWithdrwalConfigList(WithdrwalConfig withdrwalConfig);

    /**
     * 新增提现配置
     *
     * @param withdrwalConfig 提现配置
     * @return 结果
     */
    public int insertWithdrwalConfig(WithdrwalConfig withdrwalConfig);

    /**
     * 修改提现配置
     *
     * @param withdrwalConfig 提现配置
     * @return 结果
     */
    public int updateWithdrwalConfig(WithdrwalConfig withdrwalConfig);

    /**
     * 删除提现配置
     *
     * @param id 提现配置主键
     * @return 结果
     */
    public int deleteWithdrwalConfigById(Integer id);

    /**
     * 批量删除提现配置
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWithdrwalConfigByIds(Integer[] ids);
}

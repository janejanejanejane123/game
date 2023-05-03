package com.ruoyi.settings.service;

import java.util.List;

import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.settings.domain.WithdrwalConfig;

/**
 * 提现配置Service接口
 *
 * @author ruoyi
 * @date 2022-09-13
 */
public interface IWithdrwalConfigService {
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
     * 批量删除提现配置
     *
     * @param ids 需要删除的提现配置主键集合
     * @return 结果
     */
    public int deleteWithdrwalConfigByIds(Integer[] ids);

    /**
     * 删除提现配置信息
     *
     * @param id 提现配置主键
     * @return 结果
     */
    public int deleteWithdrwalConfigById(Integer id);

    public boolean verUserisRecharge(String merNo, TUser tUser);
}

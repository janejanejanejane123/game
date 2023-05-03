package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysPayConfig;

/**
 * 支付配置Mapper接口
 * 
 * @author nn
 * @date 2022-09-06
 */
public interface SysPayConfigMapper 
{
    /**
     * 查询支付配置
     * 
     * @param id 支付配置主键
     * @return 支付配置
     */
    public SysPayConfig selectSysPayConfigById(Long id);

    /**
     * 查询支付配置列表
     * 
     * @param sysPayConfig 支付配置
     * @return 支付配置集合
     */
    public List<SysPayConfig> selectSysPayConfigList(SysPayConfig sysPayConfig);

    /**
     * 新增支付配置
     * 
     * @param sysPayConfig 支付配置
     * @return 结果
     */
    public int insertSysPayConfig(SysPayConfig sysPayConfig);

    /**
     * 修改支付配置
     * 
     * @param sysPayConfig 支付配置
     * @return 结果
     */
    public int updateSysPayConfig(SysPayConfig sysPayConfig);

    /**
     * 删除支付配置
     * 
     * @param id 支付配置主键
     * @return 结果
     */
    public int deleteSysPayConfigById(Long id);

    /**
     * 批量删除支付配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysPayConfigByIds(Long[] ids);

    /**
     * 根据商户Id查询
     * @param userId
     * @return
     */
    public SysPayConfig selectSysPayConfigByUserId(Long userId);
}

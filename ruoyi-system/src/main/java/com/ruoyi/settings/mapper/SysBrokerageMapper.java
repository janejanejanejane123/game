package com.ruoyi.settings.mapper;

import java.util.List;

import com.ruoyi.settings.domain.SysBrokerage;

/**
 * 佣金设置Mapper接口
 * 
 * @author nn
 * @date 2022-06-19
 */
public interface SysBrokerageMapper 
{
    /**
     * 查询佣金设置
     * 
     * @param id 佣金设置主键
     * @return 佣金设置
     */
    public SysBrokerage selectSysBrokerageById(Long id);

    /**
     * 查询佣金设置列表
     * 
     * @param sysBrokerage 佣金设置
     * @return 佣金设置集合
     */
    public List<SysBrokerage> selectSysBrokerageList(SysBrokerage sysBrokerage);

    /**
     * 新增佣金设置
     * 
     * @param sysBrokerage 佣金设置
     * @return 结果
     */
    public int insertSysBrokerage(SysBrokerage sysBrokerage);

    /**
     * 修改佣金设置
     * 
     * @param sysBrokerage 佣金设置
     * @return 结果
     */
    public int updateSysBrokerage(SysBrokerage sysBrokerage);

    /**
     * 删除佣金设置
     * 
     * @param id 佣金设置主键
     * @return 结果
     */
    public int deleteSysBrokerageById(Long id);

    /**
     * 批量删除佣金设置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysBrokerageByIds(Long[] ids);

    /**
     * 校验该佣金类型是否唯一
     *
     * @param sysBrokerage 检测的对象
     * @return 结果
     */
    public SysBrokerage checkFeeTypeBrokerageUnique(SysBrokerage sysBrokerage);
}

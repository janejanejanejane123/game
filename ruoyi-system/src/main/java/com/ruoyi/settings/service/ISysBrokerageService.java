package com.ruoyi.settings.service;


import com.ruoyi.settings.domain.SysBrokerage;

import java.util.List;

/**
 * 佣金设置Service接口
 * 
 * @author nn
 * @date 2022-06-19
 */
public interface ISysBrokerageService 
{
    /**
     * 查询佣金设置
     * 
     * @param id 佣金设置主键
     * @return 佣金设置
     */
    public SysBrokerage selectSysBrokerageById(Long id);

    public SysBrokerage selectSysBrokerageByFeeType(String feeType);

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
     * 批量删除佣金设置
     * 
     * @param ids 需要删除的佣金设置主键集合
     * @return 结果
     */
    public int deleteSysBrokerageByIds(Long[] ids);

    /**
     * 删除佣金设置信息
     * 
     * @param id 佣金设置主键
     * @return 结果
     */
    public int deleteSysBrokerageById(Long id);

    /**
     * 校验该佣金类型是否唯一
     *
     * @param sysBrokerage 检测的对象
     * @return 结果
     */
    public String checkFeeTypeBrokerageUnique(SysBrokerage sysBrokerage);
}

package com.ruoyi.settings.mapper;

import com.ruoyi.settings.domain.SysIpBlacklist;
import com.ruoyi.settings.domain.SysServiceCharge;

import java.util.List;

/**
 * 手续费设置Mapper接口
 * 
 * @author nn
 * @date 2022-03-26
 */
public interface SysServiceChargeMapper 
{
    /**
     * 查询手续费设置
     * 
     * @param id 手续费设置主键
     * @return 手续费设置
     */
    public SysServiceCharge selectSysServiceChargeById(Long id);

    /**
     * 查询手续费设置列表
     * 
     * @param sysServiceCharge 手续费设置
     * @return 手续费设置集合
     */
    public List<SysServiceCharge> selectSysServiceChargeList(SysServiceCharge sysServiceCharge);

    /**
     * 新增手续费设置
     * 
     * @param sysServiceCharge 手续费设置
     * @return 结果
     */
    public int insertSysServiceCharge(SysServiceCharge sysServiceCharge);

    /**
     * 修改手续费设置
     * 
     * @param sysServiceCharge 手续费设置
     * @return 结果
     */
    public int updateSysServiceCharge(SysServiceCharge sysServiceCharge);

    /**
     * 删除手续费设置
     * 
     * @param id 手续费设置主键
     * @return 结果
     */
    public int deleteSysServiceChargeById(Long id);

    /**
     * 批量删除手续费设置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysServiceChargeByIds(Long[] ids);

    /**
     * 校验该商户该类型是否唯一
     *
     * @param sysServiceCharge 检测的对象
     * @return 结果
     */
    public SysServiceCharge checkFeeTypeToMerchantUnique(SysServiceCharge sysServiceCharge);

    /**
     * 查询手续费设置
     *
     * @param sysServiceCharge 查询对象.
     * @return 手续费设置
     */
    public SysServiceCharge getSysServiceCharge(SysServiceCharge sysServiceCharge);
}

package com.ruoyi.settings.service;

import com.ruoyi.settings.domain.SysServiceCharge;

import java.math.BigDecimal;
import java.util.List;

/**
 * 手续费设置Service接口
 *
 * @author nn
 * @date 2022-03-26
 */
public interface ISysServiceChargeService {
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
     * 批量删除手续费设置
     *
     * @param ids 需要删除的手续费设置主键集合
     * @return 结果
     */
    public int deleteSysServiceChargeByIds(Long[] ids);

    /**
     * 删除手续费设置信息
     *
     * @param id 手续费设置主键
     * @return 结果
     */
    public int deleteSysServiceChargeById(Long id);

    /**
     * 校验该商户该类型是否唯一
     *
     * @param sysServiceCharge 检测的对象
     * @return 结果
     */
    public String checkFeeTypeToMerchantUnique(SysServiceCharge sysServiceCharge);

    /**
     * 设置手续费
     *
     * @param sysServiceCharge 手续费对象
     * @return 结果
     */
    public int feeSetting(SysServiceCharge sysServiceCharge);

    /**
     * 一键设置手续费(一种手续费类型)
     *
     * @param sysServiceCharge 手续费对象
     * @return 结果
     */
    public int aKeySetRate(SysServiceCharge sysServiceCharge);

    /**
     * 根据手续费类型获取商户的手续费.
     *
     * @param userId 商户Id
     * @param feeType 手续费类型
     * @return 结果
     */
    public BigDecimal getUserIdRateByFeeType(Long userId, String feeType);
}

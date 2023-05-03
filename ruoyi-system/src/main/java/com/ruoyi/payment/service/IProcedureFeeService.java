package com.ruoyi.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ruoyi.payment.domain.ProcedureFee;

/**
 * 代付商户手续费设置Service接口
 * 
 * @author nn
 * @date 2022-08-27
 */
public interface IProcedureFeeService 
{
    /**
     * 查询代付商户手续费设置
     * 
     * @param id 代付商户手续费设置主键
     * @return 代付商户手续费设置
     */
    public ProcedureFee selectProcedureFeeById(Long id);

    /**
     * 查询代付商户手续费设置列表
     * 
     * @param procedureFee 代付商户手续费设置
     * @return 代付商户手续费设置集合
     */
    public List<ProcedureFee> selectProcedureFeeList(ProcedureFee procedureFee);

    /**
     * 新增代付商户手续费设置
     * 
     * @param procedureFee 代付商户手续费设置
     * @return 结果
     */
    public int insertProcedureFee(ProcedureFee procedureFee);

    /**
     * 修改代付商户手续费设置
     * 
     * @param procedureFee 代付商户手续费设置
     * @return 结果
     */
    public int updateProcedureFee(ProcedureFee procedureFee);

    /**
     * 批量删除代付商户手续费设置
     * 
     * @param ids 需要删除的代付商户手续费设置主键集合
     * @return 结果
     */
    public int deleteProcedureFeeByIds(Long[] ids);

    /**
     * 删除代付商户手续费设置信息
     * 
     * @param id 代付商户手续费设置主键
     * @return 结果
     */
    public int deleteProcedureFeeById(Long id);

    /**
     * 校验该代付商户手续费是否唯一
     *
     * @param procedureFee 检测的对象
     * @return 结果
     */
    public String checkFeeUnique(ProcedureFee procedureFee);

    /**
     * 获取商户的手续费.
     *
     * @param userId 商户Id
     * @return 结果
     */
    public BigDecimal getUserRate(Long userId);

    /**
     * 获取上级应该返水的费率
     * @param userId
     * @return
     */
    public Map<String,Object> getWaterBackInfo(Long userId);

    /**
     * 获取商户手续费
     * @param userId
     * @return
     */
    public ProcedureFee selectProcedureFeeByUid(Long userId);

    /**
     * 设置手续费
     *
     * @param procedureFee 手续费对象
     * @return 结果
     */
    public int feeSetting(ProcedureFee procedureFee);

    /**
     * 获取当前商户的手续费.
     * @param userId
     * @return
     */
    public BigDecimal getMyRate(Long userId);
}

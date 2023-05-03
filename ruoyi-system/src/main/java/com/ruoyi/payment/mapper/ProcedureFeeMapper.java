package com.ruoyi.payment.mapper;

import java.util.List;
import com.ruoyi.payment.domain.ProcedureFee;
import com.ruoyi.settings.domain.SysServiceCharge;

/**
 * 代付商户手续费设置Mapper接口
 * 
 * @author nn
 * @date 2022-08-27
 */
public interface ProcedureFeeMapper 
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
     * 删除代付商户手续费设置
     * 
     * @param id 代付商户手续费设置主键
     * @return 结果
     */
    public int deleteProcedureFeeById(Long id);

    /**
     * 批量删除代付商户手续费设置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProcedureFeeByIds(Long[] ids);

    /**
     * 校验该商户该类型是否唯一
     *
     * @param procedureFee 检测的对象
     * @return 结果
     */
    public ProcedureFee checkFeeUnique(ProcedureFee procedureFee);

    /**
     * 查询手续费设置
     *
     * @param procedureFee 查询对象.
     * @return 手续费设置
     */
    public ProcedureFee getProcedureFee(ProcedureFee procedureFee);
}

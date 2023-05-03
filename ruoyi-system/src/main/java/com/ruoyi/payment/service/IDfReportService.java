package com.ruoyi.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ruoyi.payment.domain.DfReport;

/**
 * 代付报表Service接口
 * 
 * @author ry
 * @date 2022-08-30
 */
public interface IDfReportService 
{
    /**
     * 查询代付报表
     * 
     * @param id 代付报表主键
     * @return 代付报表
     */
    public DfReport selectDfReportById(Long id);

    /**
     * 查询代付报表列表
     * 
     * @param dfReport 代付报表
     * @return 代付报表集合
     */
    public List<DfReport> selectDfReportList(DfReport dfReport);

    /**
     * 新增代付报表
     * 
     * @param dfReport 代付报表
     * @return 结果
     */
    public int insertDfReport(DfReport dfReport);

    /**
     * 修改代付报表
     * 
     * @param dfReport 代付报表
     * @return 结果
     */
    public int updateDfReport(DfReport dfReport);

    /**
     * 批量删除代付报表
     * 
     * @param ids 需要删除的代付报表主键集合
     * @return 结果
     */
    public int deleteDfReportByIds(Long[] ids);

    /**
     * 删除代付报表信息
     * 
     * @param id 代付报表主键
     * @return 结果
     */
    public int deleteDfReportById(Long id);

    Map<String, BigDecimal> selectSum(DfReport dfReport);
}

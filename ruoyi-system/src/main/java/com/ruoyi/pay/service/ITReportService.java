package com.ruoyi.pay.service;

import com.ruoyi.pay.domain.TReport;
import com.ruoyi.pay.domain.vo.TReportVo;

import java.util.List;
import java.util.Map;

/**
 * 报Service接口
 * 
 * @author ruoyi
 * @date 2022-05-31
 */
public interface ITReportService 
{
    /**
     * 查询报
     * 
     * @param id 报主键
     * @return 报
     */
    public TReport selectTReportById(Long id);

    /**
     * 查询报列表
     * 
     * @param tReport 报
     * @return 报集合
     */
    public List<TReport> selectTReportList(TReportVo tReport);

    /**
     * 新增报
     * 
     * @param tReport 报
     * @return 结果
     */
    public int insertTReport(TReport tReport);

    /**
     * 修改报
     * 
     * @param tReport 报
     * @return 结果
     */
    public int updateTReport(TReport tReport);

    /**
     * 批量删除报
     * 
     * @param ids 需要删除的报主键集合
     * @return 结果
     */
    public int deleteTReportByIds(Long[] ids);

    /**
     * 删除报信息
     * 
     * @param id 报主键
     * @return 结果
     */
    public int deleteTReportById(Long id);

    List<TReport> selectReportSum(TReportVo tReport);

    int save(TReport tReport);
}

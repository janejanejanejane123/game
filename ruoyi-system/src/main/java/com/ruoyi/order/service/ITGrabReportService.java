package com.ruoyi.order.service;

import java.util.List;
import com.ruoyi.order.domain.TGrabReport;

/**
 * reportService接口
 * 
 * @author ry
 * @date 2022-08-01
 */
public interface ITGrabReportService 
{
    /**
     * 查询report
     * 
     * @param id report主键
     * @return report
     */
    public TGrabReport selectTGrabReportById(Long id);

    /**
     * 查询report列表
     * 
     * @param tGrabReport report
     * @return report集合
     */
    public List<TGrabReport> selectTGrabReportList(TGrabReport tGrabReport);

    /**
     * 新增report
     * 
     * @param tGrabReport report
     * @return 结果
     */
    public int insertTGrabReport(TGrabReport tGrabReport);

    /**
     * 修改report
     * 
     * @param tGrabReport report
     * @return 结果
     */
    public int updateTGrabReport(TGrabReport tGrabReport);

    /**
     * 批量删除report
     * 
     * @param ids 需要删除的report主键集合
     * @return 结果
     */
    public int deleteTGrabReportByIds(Long[] ids);

    /**
     * 删除report信息
     * 
     * @param id report主键
     * @return 结果
     */
    public int deleteTGrabReportById(Long id);
}

package com.ruoyi.order.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.ruoyi.order.mapper.TGrabReportMapper;
import com.ruoyi.order.domain.TGrabReport;
import com.ruoyi.order.service.ITGrabReportService;

import javax.annotation.Resource;

/**
 * reportService业务层处理
 * 
 * @author ry
 * @date 2022-08-01
 */
@Service
public class TGrabReportServiceImpl implements ITGrabReportService 
{
    @Resource
    private TGrabReportMapper tGrabReportMapper;

    /**
     * 查询report
     * 
     * @param id report主键
     * @return report
     */
    @Override
    public TGrabReport selectTGrabReportById(Long id)
    {
        return tGrabReportMapper.selectTGrabReportById(id);
    }

    /**
     * 查询report列表
     * 
     * @param tGrabReport report
     * @return report
     */
    @Override
    public List<TGrabReport> selectTGrabReportList(TGrabReport tGrabReport)
    {
        return tGrabReportMapper.selectTGrabReportList(tGrabReport);
    }

    /**
     * 新增report
     * 
     * @param tGrabReport report
     * @return 结果
     */
    @Override
    public int insertTGrabReport(TGrabReport tGrabReport)
    {
        return tGrabReportMapper.insertTGrabReport(tGrabReport);
    }

    /**
     * 修改report
     * 
     * @param tGrabReport report
     * @return 结果
     */
    @Override
    public int updateTGrabReport(TGrabReport tGrabReport)
    {
        return tGrabReportMapper.updateTGrabReport(tGrabReport);
    }

    /**
     * 批量删除report
     * 
     * @param ids 需要删除的report主键
     * @return 结果
     */
    @Override
    public int deleteTGrabReportByIds(Long[] ids)
    {
        return tGrabReportMapper.deleteTGrabReportByIds(ids);
    }

    /**
     * 删除report信息
     * 
     * @param id report主键
     * @return 结果
     */
    @Override
    public int deleteTGrabReportById(Long id)
    {
        return tGrabReportMapper.deleteTGrabReportById(id);
    }
}

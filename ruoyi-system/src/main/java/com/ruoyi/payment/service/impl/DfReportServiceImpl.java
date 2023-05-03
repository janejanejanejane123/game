package com.ruoyi.payment.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.payment.mapper.DfReportMapper;
import com.ruoyi.payment.domain.DfReport;
import com.ruoyi.payment.service.IDfReportService;

/**
 * 代付报表Service业务层处理
 * 
 * @author ry
 * @date 2022-08-30
 */
@Service
public class DfReportServiceImpl implements IDfReportService 
{
    @Autowired
    private DfReportMapper dfReportMapper;

    /**
     * 查询代付报表
     * 
     * @param id 代付报表主键
     * @return 代付报表
     */
    @Override
    public DfReport selectDfReportById(Long id)
    {
        return dfReportMapper.selectDfReportById(id);
    }

    /**
     * 查询代付报表列表
     * 
     * @param dfReport 代付报表
     * @return 代付报表
     */
    @Override
    public List<DfReport> selectDfReportList(DfReport dfReport)
    {
        return dfReportMapper.selectDfReportList(dfReport);
    }

    /**
     * 新增代付报表
     * 
     * @param dfReport 代付报表
     * @return 结果
     */
    @Override
    public int insertDfReport(DfReport dfReport)
    {
        return dfReportMapper.insertDfReport(dfReport);
    }

    /**
     * 修改代付报表
     * 
     * @param dfReport 代付报表
     * @return 结果
     */
    @Override
    public int updateDfReport(DfReport dfReport)
    {
        return dfReportMapper.updateDfReport(dfReport);
    }

    /**
     * 批量删除代付报表
     * 
     * @param ids 需要删除的代付报表主键
     * @return 结果
     */
    @Override
    public int deleteDfReportByIds(Long[] ids)
    {
        return dfReportMapper.deleteDfReportByIds(ids);
    }

    /**
     * 删除代付报表信息
     * 
     * @param id 代付报表主键
     * @return 结果
     */
    @Override
    public int deleteDfReportById(Long id)
    {
        return dfReportMapper.deleteDfReportById(id);
    }

    @Override
    public Map<String, BigDecimal> selectSum(DfReport dfReport) {
        return dfReportMapper.selectSum(dfReport);
    }
}

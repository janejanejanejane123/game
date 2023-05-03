package com.ruoyi.pay.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.pay.domain.vo.TReportVo;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pay.mapper.TReportMapper;
import com.ruoyi.pay.domain.TReport;
import com.ruoyi.pay.service.ITReportService;

/**
 * 报表Service业务层处理
 *
 * @author ruoyi
 * @date 2022-05-31
 */
@Service
public class TReportServiceImpl implements ITReportService {
    @Autowired
    private TReportMapper tReportMapper;
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询报表
     *
     * @param id 报表主键
     * @return 报表
     */
    @Override
    public TReport selectTReportById(Long id) {
        return tReportMapper.selectTReportById(id);
    }

    /**
     * 查询报表列表
     *
     * @param tReport 报表
     * @return 报表
     */
    @Override
    public List<TReport> selectTReportList(TReportVo tReport) {
        return tReportMapper.selectTReportList(tReport);
    }

    /**
     * 新增报表
     *
     * @param tReport 报表
     * @return 结果
     */
    @Override
    public int insertTReport(TReport tReport) {
        return tReportMapper.insertTReport(tReport);
    }

    /**
     * 修改报表
     *
     * @param tReport 报表
     * @return 结果
     */
    @Override
    public int updateTReport(TReport tReport) {
        return tReportMapper.updateTReport(tReport);
    }

    /**
     * 批量删除报表
     *
     * @param ids 需要删除的报表主键
     * @return 结果
     */
    @Override
    public int deleteTReportByIds(Long[] ids) {
        return tReportMapper.deleteTReportByIds(ids);
    }

    /**
     * 删除报表信息
     *
     * @param id 报表主键
     * @return 结果
     */
    @Override
    public int deleteTReportById(Long id) {
        return tReportMapper.deleteTReportById(id);
    }

    @Override
    public List<TReport> selectReportSum(TReportVo tReport) {
        return tReportMapper.selectReportSum(tReport);
    }

    @Override
    public int save(TReport tReport) {

        TReportVo query = new TReportVo();
        query.setPeriod(DateUtils.parseDate( DateUtils.getDate()));
        query.setKey(tReport.getKey());
        query.setMerNo(tReport.getMerNo());
        List<TReport> tReports = tReportMapper.selectTReportList( query);
        tReport.setTimes(1);
        if (tReports.size() > 0) {
            tReport.setId(tReports.get(0).getId());
            tReportMapper.updateTReport(tReport);
        } else {
            tReport.setId(snowflakeIdUtils.nextId());
            tReport.setPeriod(new Date());
            tReportMapper.insertTReport(tReport);
        }
        return 1;
    }
}

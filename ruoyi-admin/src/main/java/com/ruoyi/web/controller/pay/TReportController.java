package com.ruoyi.web.controller.pay;

import java.util.*;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.pay.domain.vo.TReportVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pay.domain.TReport;
import com.ruoyi.pay.service.ITReportService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报表Controller
 *
 * @author ruoyi
 * @date 2022-05-31
 */
@RestController
@RequestMapping("/pay/report")
public class TReportController extends BaseController {
    @Autowired
    private ITReportService tReportService;

    /**
     * 查询报表列表
     */
    @PreAuthorize("@ss.hasPermi('pay:report:list')")
    @GetMapping("/list")
    public TableDataInfo list(TReportVo tReport) {
        startPage();
        if (isMerchant()) {
            tReport.setMerNo(getUsername());
        }
        if (!StringUtils.isNull(tReport.getEndDate())) {
            tReport.setEndDate(DateUtils.addDay(tReport.getEndDate(), 1));
        } else {
            tReport.setStartDate(DateUtils.getDateToDayTime());
        }
        List<TReport> list = tReportService.selectTReportList(tReport);
        return getDataTable(list);
    }

    /**
     * 查询报表列表
     */
    @PreAuthorize("@ss.hasPermi('pay:report:list')")
    @GetMapping("/listReport")
    public AjaxResult list2(TReportVo tReport) {
        if (isMerchant()) {
            tReport.setMerNo(getUsername());
        }
        if (tReport.getStartDate() == null) {
            tReport.setPeriod(DateUtils.parseDate(DateUtils.getDate()));
        }
        List<TReport> list = tReportService.selectTReportList(tReport);
        Map map = new HashMap();
        list.forEach(item -> {
            map.put(item.getKey(), item);
        });
        List<TReport> maps = tReportService.selectReportSum(tReport);
        maps.forEach(item -> {
            map.put(item.getKey() + "Sum", item);
        });
        return AjaxResult.success().put("data", map);
    }

    /**
     * 导出报表列表
     */
    @PreAuthorize("@ss.hasPermi('pay:report:export')")
    @Log(title = "报表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TReportVo tReport) {
        List<TReport> list = tReportService.selectTReportList(tReport);
        ExcelUtil<TReport> util = new ExcelUtil<TReport>(TReport.class);
        util.exportExcel(response, list, "报表数据");
    }

    /**
     * 获取报表详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:report:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(tReportService.selectTReportById(id));
    }

    /**
     * 新增报表
     */
    @PreAuthorize("@ss.hasPermi('pay:report:add')")
    @Log(title = "报表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TReport tReport) {
        return toAjax(tReportService.insertTReport(tReport));
    }

    /**
     * 修改报表
     */
    @PreAuthorize("@ss.hasPermi('pay:report:edit')")
    @Log(title = "报表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TReport tReport) {
        return toAjax(tReportService.updateTReport(tReport));
    }

    /**
     * 删除报表
     */
    @PreAuthorize("@ss.hasPermi('pay:report:remove')")
    @Log(title = "报表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(tReportService.deleteTReportByIds(ids));
    }
}

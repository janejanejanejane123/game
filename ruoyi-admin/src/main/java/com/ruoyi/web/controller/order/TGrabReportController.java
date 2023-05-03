package com.ruoyi.web.controller.order;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.order.domain.TGrabReport;
import com.ruoyi.order.service.ITGrabReportService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * reportController
 * 
 * @author ry
 * @date 2022-08-01
 */
@RestController
@RequestMapping("/order/report")
public class TGrabReportController extends BaseController
{
    @Autowired
    private ITGrabReportService tGrabReportService;

    /**
     * 查询report列表
     */
    @PreAuthorize("@ss.hasPermi('order:report:list')")
    @GetMapping("/list")
    public TableDataInfo list(TGrabReport tGrabReport)
    {
        startPage();
        List<TGrabReport> list = tGrabReportService.selectTGrabReportList(tGrabReport);
        return getDataTable(list);
    }

    /**
     * 导出report列表
     */
    @PreAuthorize("@ss.hasPermi('order:report:export')")
    @Log(title = "report", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TGrabReport tGrabReport)
    {
        List<TGrabReport> list = tGrabReportService.selectTGrabReportList(tGrabReport);
        ExcelUtil<TGrabReport> util = new ExcelUtil<TGrabReport>(TGrabReport.class);
        util.exportExcel(response, list, "report数据");
    }

    /**
     * 获取report详细信息
     */
    @PreAuthorize("@ss.hasPermi('order:report:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tGrabReportService.selectTGrabReportById(id));
    }

    /**
     * 新增report
     */
    @PreAuthorize("@ss.hasPermi('order:report:add')")
    @Log(title = "report", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TGrabReport tGrabReport)
    {
        return toAjax(tGrabReportService.insertTGrabReport(tGrabReport));
    }

    /**
     * 修改report
     */
    @PreAuthorize("@ss.hasPermi('order:report:edit')")
    @Log(title = "report", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TGrabReport tGrabReport)
    {
        return toAjax(tGrabReportService.updateTGrabReport(tGrabReport));
    }

    /**
     * 删除report
     */
    @PreAuthorize("@ss.hasPermi('order:report:remove')")
    @Log(title = "report", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tGrabReportService.deleteTGrabReportByIds(ids));
    }
}

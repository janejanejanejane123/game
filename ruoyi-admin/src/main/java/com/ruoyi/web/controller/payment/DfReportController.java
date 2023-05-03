package com.ruoyi.web.controller.payment;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.payment.domain.DfReport;
import com.ruoyi.payment.domain.DfReportVo;
import com.ruoyi.payment.service.IDfReportService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 代付报表Controller
 * 
 * @author ry
 * @date 2022-08-30
 */
@RestController
@RequestMapping("/payment/report")
public class DfReportController extends BaseController
{
    @Resource
    private IDfReportService dfReportService;

    @Resource
    private ISysUserService sysUserService;

    /**
     * 查询代付报表列表
     */
    @PreAuthorize("@ss.hasPermi('payment:report:list')")
    @GetMapping("/list")
    public TableDataInfo list(DfReportVo dfReport)
    {
        List<Map<String,Object>> lowers = sysUserService.selectLowMember(SecurityUtils.getUserId());
        List<DfReport> list = new ArrayList<>();
        Map<String, BigDecimal> map = null;
        if(CollectionUtils.isNotEmpty(lowers)){
            List<Long> uids = lowers.stream().map(item -> Long.parseLong(item.get("id").toString())).collect(Collectors.toList());
            dfReport.setUids(uids);
            startPage();
            list = dfReportService.selectDfReportList(dfReport);
            map = dfReportService.selectSum(dfReport);
        }
        TableDataInfo dataTable = getDataTable(list);
        dataTable.put("moneySum",map == null ? 0 : map.get("money"));
        dataTable.put("waterSum",map == null ? 0 : map.get("water"));
        return dataTable;
    }

    /**
     * 导出代付报表列表
     */
    @PreAuthorize("@ss.hasPermi('payment:report:list')")
    @Log(title = "代付报表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DfReportVo dfReport)
    {
        List<Map<String,Object>> lowers = sysUserService.selectLowMember(SecurityUtils.getUserId());
        List<DfReport> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(lowers)){
            List<Long> uids = lowers.stream().map(item -> Long.parseLong(item.get("id").toString())).collect(Collectors.toList());
            dfReport.setUids(uids);
            list = dfReportService.selectDfReportList(dfReport);
        }
        ExcelUtil<DfReport> util = new ExcelUtil<DfReport>(DfReport.class);
        util.exportExcel(response, list, "代付报表数据");
    }

    /**
     * 获取代付报表下级详情
     */
    @PreAuthorize("@ss.hasPermi('payment:report:list')")
    @GetMapping(value = "/lowMember")
    public AjaxResult getLowMember()
    {
        Long userId = SecurityUtils.getUserId();
        List<Map<String,Object>> list = sysUserService.selectLowMember(userId);
        return AjaxResult.success(list);
    }

    /**
     * 获取代付报表详细信息
     */
//    @PreAuthorize("@ss.hasPermi('payment:report:query')")
//    @GetMapping(value = "/{id}")
//    public AjaxResult getInfo(@PathVariable("id") Long id)
//    {
//        return AjaxResult.success(dfReportService.selectDfReportById(id));
//    }

    /**
     * 新增代付报表
     */
//    @PreAuthorize("@ss.hasPermi('payment:report:add')")
//    @Log(title = "代付报表", businessType = BusinessType.INSERT)
//    @PostMapping
//    public AjaxResult add(@RequestBody DfReport dfReport)
//    {
//        return toAjax(dfReportService.insertDfReport(dfReport));
//    }

    /**
     * 修改代付报表
     */
//    @PreAuthorize("@ss.hasPermi('payment:report:edit')")
//    @Log(title = "代付报表", businessType = BusinessType.UPDATE)
//    @PutMapping
//    public AjaxResult edit(@RequestBody DfReport dfReport)
//    {
//        return toAjax(dfReportService.updateDfReport(dfReport));
//    }

    /**
     * 删除代付报表
     */
//    @PreAuthorize("@ss.hasPermi('payment:report:remove')")
//    @Log(title = "代付报表", businessType = BusinessType.DELETE)
//	@DeleteMapping("/{ids}")
//    public AjaxResult remove(@PathVariable Long[] ids)
//    {
//        return toAjax(dfReportService.deleteDfReportByIds(ids));
//    }
}

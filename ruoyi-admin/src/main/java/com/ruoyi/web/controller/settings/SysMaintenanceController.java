package com.ruoyi.web.controller.settings;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.entity.SysUser;
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
import com.ruoyi.settings.domain.SysMaintenance;
import com.ruoyi.settings.service.ISysMaintenanceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 系统维护Controller
 * 
 * @author nn
 * @date 2022-04-12
 */
@RestController
@RequestMapping("/system/maintenance")
public class SysMaintenanceController extends BaseController
{
    @Autowired
    private ISysMaintenanceService sysMaintenanceService;

    /**
     * 查询系统维护列表
     */
    @PreAuthorize("@ss.hasPermi('system:maintenance:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysMaintenance sysMaintenance)
    {
        startPage();
        List<SysMaintenance> list = sysMaintenanceService.selectSysMaintenanceList(sysMaintenance);
        return getDataTable(list);
    }

    /**
     * 导出系统维护列表
     */
    @PreAuthorize("@ss.hasPermi('system:maintenance:export')")
    @Log(title = "系统维护", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysMaintenance sysMaintenance)
    {
        List<SysMaintenance> list = sysMaintenanceService.selectSysMaintenanceList(sysMaintenance);
        ExcelUtil<SysMaintenance> util = new ExcelUtil<SysMaintenance>(SysMaintenance.class);
        util.exportExcel(response, list, "系统维护数据");
    }

    /**
     * 获取系统维护详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:maintenance:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(sysMaintenanceService.selectSysMaintenanceById(id));
    }

    /**
     * 新增系统维护
     */
    @PreAuthorize("@ss.hasPermi('system:maintenance:add')")
    @Log(title = "系统维护", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysMaintenance sysMaintenance)
    {
        return toAjax(sysMaintenanceService.insertSysMaintenance(sysMaintenance));
    }

    /**
     * 修改系统维护
     */
    @PreAuthorize("@ss.hasPermi('system:maintenance:edit')")
    @Log(title = "系统维护", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysMaintenance sysMaintenance)
    {
        return toAjax(sysMaintenanceService.updateSysMaintenance(sysMaintenance));
    }

    /**
     * 删除系统维护
     */
    @PreAuthorize("@ss.hasPermi('system:maintenance:remove')")
    @Log(title = "系统维护", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysMaintenanceService.deleteSysMaintenanceByIds(ids));
    }

    /**
     * 获取维护信息
     */
    @PreAuthorize("@ss.hasPermi('system:maintenance:list')")
    @GetMapping("/getMaintenance")
    public AjaxResult getMaintenance()
    {
        return AjaxResult.success(sysMaintenanceService.getMaintenance());
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:maintenance:edit')")
    @Log(title = "系统维护", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysMaintenance sysMaintenance)
    {
        return toAjax(sysMaintenanceService.updateStatus(sysMaintenance));
    }
}

package com.ruoyi.web.controller.settings;

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
import com.ruoyi.settings.domain.DomainManage;
import com.ruoyi.settings.service.IDomainManageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 域名管理Controller
 *
 * @author ruoyi
 * @date 2022-08-07
 */
@RestController
@RequestMapping("/settings/doainmanage")
public class DomainManageController extends BaseController
{
    @Autowired
    private IDomainManageService domainManageService;

    /**
     * 查询域名管理列表
     */
    @PreAuthorize("@ss.hasPermi('settings:doainmanage:list')")
    @GetMapping("/list")
    public TableDataInfo list(DomainManage domainManage)
    {
        startPage();
        List<DomainManage> list = domainManageService.selectDomainManageList(domainManage);
        return getDataTable(list);
    }

    /**
     * 导出域名管理列表
     */
    @PreAuthorize("@ss.hasPermi('settings:doainmanage:export')")
    @Log(title = "域名管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DomainManage domainManage)
    {
        List<DomainManage> list = domainManageService.selectDomainManageList(domainManage);
        ExcelUtil<DomainManage> util = new ExcelUtil<DomainManage>(DomainManage.class);
        util.exportExcel(response, list, "域名管理数据");
    }

    /**
     * 获取域名管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('settings:doainmanage:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(domainManageService.selectDomainManageById(id));
    }

    /**
     * 新增域名管理
     */
    @PreAuthorize("@ss.hasPermi('settings:doainmanage:add')")
    @Log(title = "域名管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DomainManage domainManage)
    {
        return toAjax(domainManageService.insertDomainManage(domainManage));
    }

    /**
     * 修改域名管理
     */
    @PreAuthorize("@ss.hasPermi('settings:doainmanage:edit')")
    @Log(title = "域名管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DomainManage domainManage)
    {
        return toAjax(domainManageService.updateDomainManage(domainManage));
    }

    /**
     * 删除域名管理
     */
    @PreAuthorize("@ss.hasPermi('settings:doainmanage:remove')")
    @Log(title = "域名管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(domainManageService.deleteDomainManageByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('settings:doainmanage:edit')")
    @Log(title = "域名更新状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody DomainManage domainManage){
        return toAjax(domainManageService.updateDomainManage(domainManage));
    }
}

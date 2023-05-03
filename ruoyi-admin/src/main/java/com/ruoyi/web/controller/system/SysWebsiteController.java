package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysWebsite;
import com.ruoyi.system.service.ISysWebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 网站管理Controller
 * 
 * @author nn
 * @date 2022-07-21
 */
@RestController
@RequestMapping("/system/website")
public class SysWebsiteController extends BaseController
{
    @Autowired
    private ISysWebsiteService sysWebsiteService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询网站管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:website:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysWebsite sysWebsite)
    {
        startPage();
        List<SysWebsite> list = sysWebsiteService.selectSysWebsiteList(sysWebsite);
        return getDataTable(list);
    }

    /**
     * 导出网站管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:website:export')")
    @Log(title = "网站管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysWebsite sysWebsite)
    {
        List<SysWebsite> list = sysWebsiteService.selectSysWebsiteList(sysWebsite);
        ExcelUtil<SysWebsite> util = new ExcelUtil<SysWebsite>(SysWebsite.class);
        util.exportExcel(response, list, "网站管理数据");
    }

    /**
     * 获取网站管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:website:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(sysWebsiteService.selectSysWebsiteById(id));
    }

    /**
     * 新增网站管理
     */
    @PreAuthorize("@ss.hasPermi('system:website:add')")
    @Log(title = "网站管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysWebsite sysWebsite)
    {
        if (UserConstants.NOT_UNIQUE.equals(sysWebsiteService.checkWebsiteUnique(sysWebsite)))
        {
            return AjaxResult.error("新增失败，网站编码或名称已存在已存在");
        }
        sysWebsite.setId(snowflakeIdUtils.nextId());
        return toAjax(sysWebsiteService.insertSysWebsite(sysWebsite));
    }

    /**
     * 修改网站管理
     */
    @PreAuthorize("@ss.hasPermi('system:website:edit')")
    @Log(title = "网站管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysWebsite sysWebsite)
    {
        if (UserConstants.NOT_UNIQUE.equals(sysWebsiteService.checkWebsiteUnique(sysWebsite)))
        {
            return AjaxResult.error("修改失败，网站编码或名称已存在已存在");
        }
        return toAjax(sysWebsiteService.updateSysWebsite(sysWebsite));
    }

    /**
     * 删除网站管理
     */
    @PreAuthorize("@ss.hasPermi('system:website:remove')")
    @Log(title = "网站管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysWebsiteService.deleteSysWebsiteByIds(ids));
    }

    /**
     * 上传Logo
     * @param file
     * @return
     */
    @PostMapping("/uploadLogo")
    @PreAuthorize("@ss.hasPermi('system:website:add')")
    public AjaxResult uploadLogo(@RequestParam("file") MultipartFile file)
    {
        String path = sysWebsiteService.uploadLogo(file);
        return AjaxResult.success().put("siteLogo",path);
    }

    /**
     * 状态修改.
     */
    @PreAuthorize("@ss.hasPermi('system:website:edit')")
    @Log(title = "网站管理-修改状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysWebsite sysWebsite)
    {
        return toAjax(sysWebsiteService.changeStatus(sysWebsite));
    }

//    /**
//     * 获取网站信息(允许匿名访问).
//     */
//    @GetMapping("/getSysWebsiteFTP")
//    public AjaxResult getSysWebsiteFTP(SysWebsite sysWebsite)
//    {
//        String siteCode = DomainFilter.getSiteCode();
//        sysWebsite.setSiteCode(siteCode);
//        SysWebsite info = sysWebsiteService.getSysWebsite(sysWebsite);
//        return AjaxResult.success(info);
//    }
//
//    /**
//     * 获取网站信息.
//     */
//    @GetMapping("/getSysWebsite")
//    public AjaxResult getSysWebsite(SysWebsite sysWebsite)
//    {
//        String siteCode = DomainFilter.getSiteCode();
//        sysWebsite.setSiteCode(siteCode);
//        SysWebsite info = sysWebsiteService.getSysWebsite(sysWebsite);
//        return AjaxResult.success(info);
//    }
}

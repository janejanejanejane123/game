package com.ruoyi.web.controller.settings;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
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
import com.ruoyi.settings.domain.SysIpBlacklist;
import com.ruoyi.settings.service.ISysIpBlacklistService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * ip后台登录白名单Controller
 * 
 * @author nn
 * @date 2022-03-25
 */
@RestController
@RequestMapping("/settings/blacklist")
public class SysIpBlacklistController extends BaseController
{
    @Autowired
    private ISysIpBlacklistService sysIpBlacklistService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询ip后台登录白名单列表
     */
    @PreAuthorize("@ss.hasPermi('settings:blacklist:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysIpBlacklist sysIpBlacklist)
    {
        startPage();
        List<SysIpBlacklist> list = sysIpBlacklistService.selectSysIpBlacklistList(sysIpBlacklist);
        return getDataTable(list);
    }

    /**
     * 导出ip后台登录白名单列表
     */
    @PreAuthorize("@ss.hasPermi('settings:blacklist:export')")
    @Log(title = "ip后台登录白名单-导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysIpBlacklist sysIpBlacklist)
    {
        List<SysIpBlacklist> list = sysIpBlacklistService.selectSysIpBlacklistList(sysIpBlacklist);
        ExcelUtil<SysIpBlacklist> util = new ExcelUtil<SysIpBlacklist>(SysIpBlacklist.class);
        util.exportExcel(response, list, "ip后台登录白名单数据");
    }

    /**
     * 获取ip后台登录白名单详细信息
     */
    @PreAuthorize("@ss.hasPermi('settings:blacklist:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(sysIpBlacklistService.selectSysIpBlacklistById(id));
    }

    /**
     * 新增ip后台登录白名单
     */
    @PreAuthorize("@ss.hasPermi('settings:blacklist:add')")
    @Log(title = "ip后台登录白名单-新增", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysIpBlacklist sysIpBlacklist)
    {
        if (UserConstants.NOT_UNIQUE.equals(sysIpBlacklistService.checkIpUnique(sysIpBlacklist)))
        {
            return AjaxResult.error("新增'" + sysIpBlacklist.getUserName() + "'的IP:'" + sysIpBlacklist.getIp() + "'失败，ip已存在");
        }
        sysIpBlacklist.setId(snowflakeIdUtils.nextId());
        sysIpBlacklist.setCreateBy(getUsername());
        sysIpBlacklist.setCreateTime(DateUtils.getNowDate());
        return toAjax(sysIpBlacklistService.insertSysIpBlacklist(sysIpBlacklist));
    }

    /**
     * 修改ip后台登录白名单
     */
    @PreAuthorize("@ss.hasPermi('settings:blacklist:edit')")
    @Log(title = "ip后台登录白名单-修改", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysIpBlacklist sysIpBlacklist)
    {
        if (UserConstants.NOT_UNIQUE.equals(sysIpBlacklistService.checkIpUnique(sysIpBlacklist)))
        {
            return AjaxResult.error("修改'" + sysIpBlacklist.getUserName() + "'的IP:'" + sysIpBlacklist.getIp() + "'失败，ip已存在");
        }

        sysIpBlacklist.setUpdateBy(getUsername());
        sysIpBlacklist.setUpdateTime(DateUtils.getNowDate());
        return toAjax(sysIpBlacklistService.updateSysIpBlacklist(sysIpBlacklist));
    }

    /**
     * 删除ip后台登录白名单
     */
    @PreAuthorize("@ss.hasPermi('settings:blacklist:remove')")
    @Log(title = "ip后台登录白名单-删除", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysIpBlacklistService.deleteSysIpBlacklistByIds(ids));
    }
}

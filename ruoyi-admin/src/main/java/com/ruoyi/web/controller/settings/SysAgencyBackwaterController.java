package com.ruoyi.web.controller.settings;

import java.util.Date;
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
import com.ruoyi.settings.domain.SysAgencyBackwater;
import com.ruoyi.settings.service.ISysAgencyBackwaterService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 代理返水设置Controller
 * 
 * @author nn
 * @date 2022-06-26
 */
@RestController
@RequestMapping("/agency/backwater")
public class SysAgencyBackwaterController extends BaseController
{
    @Autowired
    private ISysAgencyBackwaterService sysAgencyBackwaterService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询代理返水设置列表
     */
    @PreAuthorize("@ss.hasPermi('agency:backwater:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysAgencyBackwater sysAgencyBackwater)
    {
        startPage();
        List<SysAgencyBackwater> list = sysAgencyBackwaterService.selectSysAgencyBackwaterList(sysAgencyBackwater);
        return getDataTable(list);
    }

    /**
     * 导出代理返水设置列表
     */
    @PreAuthorize("@ss.hasPermi('agency:backwater:export')")
    @Log(title = "代理返水设置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysAgencyBackwater sysAgencyBackwater)
    {
        List<SysAgencyBackwater> list = sysAgencyBackwaterService.selectSysAgencyBackwaterList(sysAgencyBackwater);
        ExcelUtil<SysAgencyBackwater> util = new ExcelUtil<SysAgencyBackwater>(SysAgencyBackwater.class);
        util.exportExcel(response, list, "代理返水设置数据");
    }

    /**
     * 获取代理返水设置详细信息
     */
    @PreAuthorize("@ss.hasPermi('agency:backwater:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(sysAgencyBackwaterService.selectSysAgencyBackwaterById(id));
    }

    /**
     * 新增代理返水设置
     */
    @PreAuthorize("@ss.hasPermi('agency:backwater:add')")
    @Log(title = "新增代理返水设置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysAgencyBackwater sysAgencyBackwater)
    {
        if (UserConstants.NOT_UNIQUE.equals(sysAgencyBackwaterService.checkFeeTypeToAgencyUnique(sysAgencyBackwater)))
        {
            return AjaxResult.error("新增失败，该代理该类型返水已存在");
        }
        sysAgencyBackwater.setId(snowflakeIdUtils.nextId());
        sysAgencyBackwater.setCreateBy(getUsername());
        sysAgencyBackwater.setCreateTime(DateUtils.getNowDate());
        return toAjax(sysAgencyBackwaterService.insertSysAgencyBackwater(sysAgencyBackwater));
    }

    /**
     * 修改代理返水设置
     */
    @PreAuthorize("@ss.hasPermi('agency:backwater:edit')")
    @Log(title = "代理返水设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysAgencyBackwater sysAgencyBackwater)
    {
        if (UserConstants.NOT_UNIQUE.equals(sysAgencyBackwaterService.checkFeeTypeToAgencyUnique(sysAgencyBackwater)))
        {
            return AjaxResult.error("修改失败，该代理该类型返水已存在");
        }
        sysAgencyBackwater.setUpdateBy(getUsername());
        sysAgencyBackwater.setUpdateTime(DateUtils.getNowDate());
        return toAjax(sysAgencyBackwaterService.updateSysAgencyBackwater(sysAgencyBackwater));
    }

    /**
     * 删除代理返水设置
     */
    @PreAuthorize("@ss.hasPermi('agency:backwater:remove')")
    @Log(title = "代理返水设置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysAgencyBackwaterService.deleteSysAgencyBackwaterByIds(ids));
    }
}

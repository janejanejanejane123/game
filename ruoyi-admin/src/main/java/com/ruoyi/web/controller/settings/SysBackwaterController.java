package com.ruoyi.web.controller.settings;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.constant.UserConstants;
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
import com.ruoyi.settings.domain.SysBackwater;
import com.ruoyi.settings.service.ISysBackwaterService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 返水设置Controller
 * 
 * @author nn
 * @date 2022-06-24
 */
@RestController
@RequestMapping("/system/backwater")
public class SysBackwaterController extends BaseController
{
    @Autowired
    private ISysBackwaterService sysBackwaterService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询返水设置列表
     */
    @PreAuthorize("@ss.hasPermi('system:backwater:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysBackwater sysBackwater)
    {
        startPage();
        List<SysBackwater> list = sysBackwaterService.selectSysBackwaterList(sysBackwater);
        return getDataTable(list);
    }

    /**
     * 导出返水设置列表
     */
    @PreAuthorize("@ss.hasPermi('system:backwater:export')")
    @Log(title = "导出返水设置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysBackwater sysBackwater)
    {
        List<SysBackwater> list = sysBackwaterService.selectSysBackwaterList(sysBackwater);
        ExcelUtil<SysBackwater> util = new ExcelUtil<SysBackwater>(SysBackwater.class);
        util.exportExcel(response, list, "返水设置数据");
    }

    /**
     * 获取返水设置详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:backwater:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(sysBackwaterService.selectSysBackwaterById(id));
    }

    /**
     * 新增返水设置
     */
    @PreAuthorize("@ss.hasPermi('system:backwater:add')")
    @Log(title = "新增返水设置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysBackwater sysBackwater)
    {
        if (UserConstants.NOT_UNIQUE.equals(sysBackwaterService.checkFeeTypeBackwaterUnique(sysBackwater)))
        {
            return AjaxResult.error("新增失败，该返水类型费率已存在");
        }
        sysBackwater.setId(snowflakeIdUtils.nextId());
        sysBackwater.setCreateBy(getUsername());
        sysBackwater.setCreateTime(new Date());
        return toAjax(sysBackwaterService.insertSysBackwater(sysBackwater));
    }

    /**
     * 修改返水设置
     */
    @PreAuthorize("@ss.hasPermi('system:backwater:edit')")
    @Log(title = "修改返水设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysBackwater sysBackwater)
    {
        if (UserConstants.NOT_UNIQUE.equals(sysBackwaterService.checkFeeTypeBackwaterUnique(sysBackwater)))
        {
            return AjaxResult.error("修改失败，该返水类型费率已存在");
        }
        sysBackwater.setUpdateBy(getUsername());
        sysBackwater.setUpdateTime(new Date());
        return toAjax(sysBackwaterService.updateSysBackwater(sysBackwater));
    }

    /**
     * 删除返水设置
     */
    @PreAuthorize("@ss.hasPermi('system:backwater:remove')")
    @Log(title = "删除返水设置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysBackwaterService.deleteSysBackwaterByIds(ids));
    }
}

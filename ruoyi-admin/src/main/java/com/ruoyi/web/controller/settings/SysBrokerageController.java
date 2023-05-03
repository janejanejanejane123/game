package com.ruoyi.web.controller.settings;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.settings.domain.SysBrokerage;
import com.ruoyi.settings.service.ISysBrokerageService;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 佣金设置Controller
 * 
 * @author nn
 * @date 2022-06-19
 */
@RestController
@RequestMapping("/system/brokerage")
public class SysBrokerageController extends BaseController
{
    @Autowired
    private ISysBrokerageService sysBrokerageService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询佣金设置列表
     */
    @PreAuthorize("@ss.hasPermi('system:brokerage:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysBrokerage sysBrokerage)
    {
        startPage();
        List<SysBrokerage> list = sysBrokerageService.selectSysBrokerageList(sysBrokerage);
        return getDataTable(list);
    }

    /**
     * 导出佣金设置列表
     */
    @PreAuthorize("@ss.hasPermi('system:brokerage:export')")
    @Log(title = "导出佣金设置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysBrokerage sysBrokerage)
    {
        List<SysBrokerage> list = sysBrokerageService.selectSysBrokerageList(sysBrokerage);
        ExcelUtil<SysBrokerage> util = new ExcelUtil<SysBrokerage>(SysBrokerage.class);
        util.exportExcel(response, list, "佣金设置数据");
    }

    /**
     * 获取佣金设置详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:brokerage:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(sysBrokerageService.selectSysBrokerageById(id));
    }

    /**
     * 新增佣金设置
     */
    @PreAuthorize("@ss.hasPermi('system:brokerage:add')")
    @Log(title = "新增佣金设置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysBrokerage sysBrokerage)
    {
        if (UserConstants.NOT_UNIQUE.equals(sysBrokerageService.checkFeeTypeBrokerageUnique(sysBrokerage)))
        {
            return AjaxResult.error("新增失败，该佣金类型费率已存在");
        }
        sysBrokerage.setId(snowflakeIdUtils.nextId());
        sysBrokerage.setCreateBy(getUsername());
        sysBrokerage.setCreateTime(new Date());
        return toAjax(sysBrokerageService.insertSysBrokerage(sysBrokerage));
    }

    /**
     * 修改佣金设置
     */
    @PreAuthorize("@ss.hasPermi('system:brokerage:edit')")
    @Log(title = "修改佣金设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysBrokerage sysBrokerage)
    {
        if (UserConstants.NOT_UNIQUE.equals(sysBrokerageService.checkFeeTypeBrokerageUnique(sysBrokerage)))
        {
            return AjaxResult.error("修改失败，该佣金类型费率已存在");
        }
        sysBrokerage.setUpdateBy(getUsername());
        sysBrokerage.setUpdateTime(new Date());
        return toAjax(sysBrokerageService.updateSysBrokerage(sysBrokerage));
    }

    /**
     * 删除佣金设置
     */
    @PreAuthorize("@ss.hasPermi('system:brokerage:remove')")
    @Log(title = "删除佣金设置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysBrokerageService.deleteSysBrokerageByIds(ids));
    }
}

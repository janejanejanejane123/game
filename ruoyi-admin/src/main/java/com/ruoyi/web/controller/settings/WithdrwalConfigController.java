package com.ruoyi.settings.controller;

import java.util.Date;
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
import com.ruoyi.settings.domain.WithdrwalConfig;
import com.ruoyi.settings.service.IWithdrwalConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 提现配置Controller
 *
 * @author ruoyi
 * @date 2022-09-13
 */
@RestController
@RequestMapping("/settings/withdrwalConfig")
public class WithdrwalConfigController extends BaseController {
    @Autowired
    private IWithdrwalConfigService withdrwalConfigService;

    /**
     * 查询提现配置列表
     */
    @PreAuthorize("@ss.hasPermi('settings:withdrwalConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(WithdrwalConfig withdrwalConfig) {
        startPage();
        if (isMerchant()) {
            withdrwalConfig.setMerNo(getUsername());
        }
        List<WithdrwalConfig> list = withdrwalConfigService.selectWithdrwalConfigList(withdrwalConfig);
        return getDataTable(list);
    }
/*
    *//**
     * 导出提现配置列表
     *//*
    @PreAuthorize("@ss.hasPermi('settings:withdrwalConfig:export')")
    @Log(title = "提现配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WithdrwalConfig withdrwalConfig) {
        List<WithdrwalConfig> list = withdrwalConfigService.selectWithdrwalConfigList(withdrwalConfig);
        ExcelUtil<WithdrwalConfig> util = new ExcelUtil<WithdrwalConfig>(WithdrwalConfig.class);
        util.exportExcel(response, list, "提现配置数据");
    }*/

    /**
     * 获取提现配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('settings:withdrwalConfig:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id) {
        return AjaxResult.success(withdrwalConfigService.selectWithdrwalConfigById(id));
    }

    /**
     * 新增提现配置
     */
    @PreAuthorize("@ss.hasPermi('settings:withdrwalConfig:add')")
    @Log(title = "提现配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WithdrwalConfig withdrwalConfig) {
        withdrwalConfig.setCreateBy(getUsername());
        if (isMerchant()) {
            withdrwalConfig.setMerNo(getUsername());
        }
        return toAjax(withdrwalConfigService.insertWithdrwalConfig(withdrwalConfig));
    }

    /**
     * 修改提现配置
     */
    @PreAuthorize("@ss.hasPermi('settings:withdrwalConfig:edit')")
    @Log(title = "提现配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WithdrwalConfig withdrwalConfig) {
        withdrwalConfig.setUpdateBy(getUsername());
        return toAjax(withdrwalConfigService.updateWithdrwalConfig(withdrwalConfig));
    }

    /**
     * 删除提现配置
     */
    @PreAuthorize("@ss.hasPermi('settings:withdrwalConfig:remove')")
    @Log(title = "提现配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids) {
        return toAjax(withdrwalConfigService.deleteWithdrwalConfigByIds(ids));
    }
}

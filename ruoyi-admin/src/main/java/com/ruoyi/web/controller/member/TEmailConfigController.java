package com.ruoyi.web.controller.member;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.member.domain.TEmailConfig;
import com.ruoyi.member.service.ITEmailConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 邮箱配置Controller
 * 
 * @author ruoyi
 * @date 2022-03-29
 */
@RestController
@RequestMapping("/member/email/config")
public class TEmailConfigController extends BaseController
{
    @Autowired
    private ITEmailConfigService tEmailConfigService;

    /**
     * 查询邮箱配置列表
     */
    @PreAuthorize("@ss.hasPermi('email:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(TEmailConfig tEmailConfig)
    {
        startPage();
        List<TEmailConfig> list = tEmailConfigService.selectTEmailConfigList(tEmailConfig);
        return getDataTable(list);
    }

    /**
     * 导出邮箱配置列表
     */
    @PreAuthorize("@ss.hasPermi('email:config:export')")
    @Log(title = "邮箱配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TEmailConfig tEmailConfig)
    {
        List<TEmailConfig> list = tEmailConfigService.selectTEmailConfigList(tEmailConfig);
        ExcelUtil<TEmailConfig> util = new ExcelUtil<TEmailConfig>(TEmailConfig.class);
        util.exportExcel(response, list, "邮箱配置数据");
    }

    /**
     * 获取邮箱配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:config:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tEmailConfigService.selectTEmailConfigById(id));
    }

    /**
     * 新增邮箱配置
     */
    @PreAuthorize("@ss.hasPermi('email:config:add')")
    @Log(title = "邮箱配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TEmailConfig tEmailConfig)
    {
        return toAjax(tEmailConfigService.insertTEmailConfig(tEmailConfig));
    }

    /**
     * 修改邮箱配置
     */
    @PreAuthorize("@ss.hasPermi('email:config:edit')")
    @Log(title = "邮箱配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TEmailConfig tEmailConfig)
    {
        return toAjax(tEmailConfigService.updateTEmailConfig(tEmailConfig));
    }

    /**
     * 删除邮箱配置
     */
    @PreAuthorize("@ss.hasPermi('email:config:remove')")
    @Log(title = "邮箱配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tEmailConfigService.deleteTEmailConfigByIds(ids));
    }
}

package com.ruoyi.web.controller.member;

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
import com.ruoyi.member.domain.TUserConfig;
import com.ruoyi.member.service.ITUserConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户配置Controller
 * 
 * @author ruoyi
 * @date 2022-04-08
 */
@RestController
@RequestMapping("/member/config")
public class TUserConfigController extends BaseController
{
    @Autowired
    private ITUserConfigService tUserConfigService;

    /**
     * 查询用户配置列表
     */
    @PreAuthorize("@ss.hasPermi('member:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(TUserConfig tUserConfig)
    {
        startPage();
        List<TUserConfig> list = tUserConfigService.selectTUserConfigList(tUserConfig);
        return getDataTable(list);
    }

    /**
     * 导出用户配置列表
     */
    @PreAuthorize("@ss.hasPermi('member:config:export')")
    @Log(title = "用户配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TUserConfig tUserConfig)
    {
        List<TUserConfig> list = tUserConfigService.selectTUserConfigList(tUserConfig);
        ExcelUtil<TUserConfig> util = new ExcelUtil<TUserConfig>(TUserConfig.class);
        util.exportExcel(response, list, "用户配置数据");
    }

    /**
     * 获取用户配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('member:config:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tUserConfigService.selectTUserConfigById(id));
    }

    /**
     * 新增用户配置
     */
    @PreAuthorize("@ss.hasPermi('member:config:add')")
    @Log(title = "用户配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TUserConfig tUserConfig)
    {
        return toAjax(tUserConfigService.insertTUserConfig(tUserConfig));
    }

    /**
     * 修改用户配置
     */
    @PreAuthorize("@ss.hasPermi('member:config:edit')")
    @Log(title = "用户配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TUserConfig tUserConfig)
    {
        return toAjax(tUserConfigService.updateTUserConfig(tUserConfig));
    }

    /**
     * 删除用户配置
     */
    @PreAuthorize("@ss.hasPermi('member:config:remove')")
    @Log(title = "用户配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tUserConfigService.deleteTUserConfigByIds(ids));
    }
}

package com.ruoyi.controller;

import java.util.List;

import com.ruoyi.game.domain.ApiDetail;
import com.ruoyi.game.service.IApiDetailService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 【请填写功能名称】Controller
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
@Controller
@RequestMapping("/com/ruoyi/system/detail")
public class ApiDetailController extends BaseController
{
    private String prefix = "com/ruoyi/system/detail";

    @Autowired
    private IApiDetailService apiDetailService;

    @RequiresPermissions("system:detail:view")
    @GetMapping()
    public String detail()
    {
        return prefix + "/detail";
    }

    /**
     * 新增【请填写功能名称】
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存【请填写功能名称】
     */
    @RequiresPermissions("system:detail:add")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ApiDetail apiDetail)
    {
        return toAjax(apiDetailService.save(apiDetail));
    }

    /**
     * 修改【请填写功能名称】
     */
    @RequiresPermissions("system:detail:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        ApiDetail apiDetail = apiDetailService.getById(id);
        mmap.put("apiDetail", apiDetail);
        return prefix + "/edit";
    }

    /**
     * 修改保存【请填写功能名称】
     */
    @RequiresPermissions("system:detail:edit")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ApiDetail apiDetail)
    {
        return toAjax(apiDetailService.updateById(apiDetail));
    }

    /**
     * 删除【请填写功能名称】
     */
    @RequiresPermissions("system:detail:remove")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(List<String> ids)
    {
        return toAjax(apiDetailService.removeByIds(ids));
    }
}

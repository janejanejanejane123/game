package com.ruoyi.controller;

import java.util.List;

import com.ruoyi.game.domain.GameResult;
import com.ruoyi.game.service.IGameResultService;
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
@RequestMapping("/com/ruoyi/system/result")
public class GameResultController extends BaseController
{
    private String prefix = "com/ruoyi/system/result";

    @Autowired
    private IGameResultService gameResultService;

    @RequiresPermissions("system:result:view")
    @GetMapping()
    public String result()
    {
        return prefix + "/result";
    }
   /**
     * 新增保存【请填写功能名称】
     */
    @RequiresPermissions("system:result:add")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(GameResult gameResult)
    {
        return toAjax(gameResultService.save(gameResult));
    }

    /**
     * 修改【请填写功能名称】
     */
    @RequiresPermissions("system:result:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        GameResult gameResult = gameResultService.getById(id);
        mmap.put("gameResult", gameResult);
        return prefix + "/edit";
    }

    /**
     * 修改保存【请填写功能名称】
     */
    @RequiresPermissions("system:result:edit")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(GameResult gameResult)
    {
        return toAjax(gameResultService.updateById(gameResult));
    }

    /**
     * 删除【请填写功能名称】
     */
    @RequiresPermissions("system:result:remove")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(List<String> ids)
    {
        return toAjax(gameResultService.removeByIds(ids));
    }
}

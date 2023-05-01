package com.ruoyi.controller;

import java.util.List;

import com.ruoyi.game.domain.GameConfig;
import com.ruoyi.game.service.IGameConfigService;
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
@RequestMapping("/com/ruoyi/system/config")
public class GameConfigController extends BaseController {
    private String prefix = "com/ruoyi/system/config";

    @Autowired
    private IGameConfigService gameConfigService;

    @RequiresPermissions("system:config:view")
    @GetMapping()
    public String config() {
        return prefix + "/config";
    }

    /**
     * 新增保存【请填写功能名称】
     */
    @RequiresPermissions("system:config:add")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(GameConfig gameConfig) {
        return toAjax(gameConfigService.save(gameConfig));
    }

    /**
     * 修改【请填写功能名称】
     */
    @RequiresPermissions("system:config:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        GameConfig gameConfig = gameConfigService.getById(id);
        mmap.put("gameConfig", gameConfig);
        return prefix + "/edit";
    }

    /**
     * 修改保存【请填写功能名称】
     */
    @RequiresPermissions("system:config:edit")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(GameConfig gameConfig) {
        return toAjax(gameConfigService.updateById(gameConfig));
    }

    /**
     * 删除【请填写功能名称】
     */
    @RequiresPermissions("system:config:remove")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(List<String> ids) {
        return toAjax(gameConfigService.removeByIds(ids));
    }
}

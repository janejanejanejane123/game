package com.ruoyi.controller;

import com.ruoyi.game.domain.GameBet;
import com.ruoyi.game.service.IGameBetService;
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
@RequestMapping("/com/ruoyi/system/bet")
public class GameBetController extends BaseController {
    private String prefix = "com/ruoyi/system/bet";

    @Autowired
    private IGameBetService gameBetService;

    @RequiresPermissions("system:bet:view")
    @GetMapping()
    public String bet() {
        return prefix + "/bet";
    }

    /**
     * 新增【请填写功能名称】
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存【请填写功能名称】
     */
    @RequiresPermissions("system:bet:add")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(GameBet gameBet) {
        return toAjax(gameBetService.save(gameBet));
    }

    /**
     * 修改【请填写功能名称】
     */
    @RequiresPermissions("system:bet:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        GameBet gameBet = gameBetService.getById(id);
        mmap.put("gameBet", gameBet);
        return prefix + "/edit";
    }

    /**
     * 修改保存【请填写功能名称】
     */
    @RequiresPermissions("system:bet:edit")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(GameBet gameBet) {
        return toAjax(gameBetService.updateById(gameBet));
    }

    /**
     * 删除【请填写功能名称】
     */
    @RequiresPermissions("system:bet:remove")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(gameBetService.removeById(ids));
    }
}

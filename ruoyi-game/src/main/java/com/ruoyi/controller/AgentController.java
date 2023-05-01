package com.ruoyi.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.game.domain.Agent;
import com.ruoyi.game.service.IAgentService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 【请填写功能名称】Controller
 *
 * @author ruoyi
 * @date 2023-04-28
 */
@Controller
@RequestMapping("/com/ruoyi/system/agent")
public class AgentController extends BaseController {
    private String prefix = "com/ruoyi/system/agent";

    @Autowired
    private IAgentService agentService;

    @RequiresPermissions("system:agent:view")
    @GetMapping()
    public String agent() {
        return prefix + "/agent";
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
    @RequiresPermissions("system:agent:add")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Agent agent) {
        return toAjax(agentService.save(agent));
    }

    /**
     * 修改【请填写功能名称】
     */
    @RequiresPermissions("system:agent:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        Agent agent = agentService.getById(id);
        mmap.put("agent", agent);
        return prefix + "/edit";
    }

    /**
     * 修改保存【请填写功能名称】
     */
    @RequiresPermissions("system:agent:edit")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Agent agent) {
        return toAjax(agentService.updateById(agent));
    }

    /**
     * 删除【请填写功能名称】
     */
    @RequiresPermissions("system:agent:remove")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(List<String> ids) {
        return toAjax(agentService.removeByIds(ids));
    }
}

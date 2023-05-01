package com.ruoyi.controller;

import java.util.List;

import com.ruoyi.game.domain.UserLocalAuth;
import com.ruoyi.game.service.IUserLocalAuthService;
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
 * 本地用户Controller
 *
 * @author ruoyi
 * @date 2023-04-28
 */
@Controller
@RequestMapping("/com/ruoyi/system/userLocalAuth")
public class UserLocalAuthController extends BaseController {
    private String prefix = "com/ruoyi/system/auth";

    @Autowired
    private IUserLocalAuthService userLocalAuthService;

    @RequiresPermissions("system:auth:view")
    @GetMapping()
    public String auth() {
        return prefix + "/auth";
    }

    /**
     * 新增本地用户
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存本地用户
     */
    @RequiresPermissions("system:auth:add")
    @Log(title = "本地用户", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UserLocalAuth userLocalAuth) {
        return toAjax(userLocalAuthService.save(userLocalAuth));
    }

    /**
     * 修改本地用户
     */
    @RequiresPermissions("system:auth:edit")
    @GetMapping("/edit/{authId}")
    public String edit(@PathVariable("authId") Long authId, ModelMap mmap) {
        UserLocalAuth userLocalAuth = userLocalAuthService.getById(authId);
        mmap.put("userLocalAuth", userLocalAuth);
        return prefix + "/edit";
    }

    /**
     * 修改保存本地用户
     */
    @RequiresPermissions("system:auth:edit")
    @Log(title = "本地用户", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UserLocalAuth userLocalAuth) {
        return toAjax(userLocalAuthService.updateById(userLocalAuth));
    }

    /**
     * 删除本地用户
     */
    @RequiresPermissions("system:auth:remove")
    @Log(title = "本地用户", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(List<String> ids) {
        return toAjax(userLocalAuthService.removeByIds(ids));
    }
}

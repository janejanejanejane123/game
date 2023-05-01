package com.ruoyi.controller;

import java.util.List;

import com.ruoyi.game.domain.UserThirdAuth;
import com.ruoyi.game.service.IUserThirdAuthService;
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
 * 第三方用户Controller
 *
 * @author ruoyi
 * @date 2023-04-28
 */
@Controller
@RequestMapping("/com/ruoyi/game/userThirdAuth")
public class UserThirdAuthController extends BaseController {
    private String prefix = "com/ruoyi/system/auth";

    @Autowired
    private IUserThirdAuthService userThirdAuthService;

    @RequiresPermissions("system:auth:view")
    @GetMapping()
    public String auth() {
        return prefix + "/auth";
    }

    /**
     * 新增第三方用户
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存第三方用户
     */
    @RequiresPermissions("system:auth:add")
    @Log(title = "第三方用户", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UserThirdAuth userThirdAuth) {
        return toAjax(userThirdAuthService.save(userThirdAuth));
    }

    /**
     * 修改第三方用户
     */
    @RequiresPermissions("system:auth:edit")
    @GetMapping("/edit/{authId}")
    public String edit(@PathVariable("authId") Long authId, ModelMap mmap) {
        UserThirdAuth userThirdAuth = userThirdAuthService.getById(authId);
        mmap.put("userThirdAuth", userThirdAuth);
        return prefix + "/edit";
    }

    /**
     * 修改保存第三方用户
     */
    @RequiresPermissions("system:auth:edit")
    @Log(title = "第三方用户", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UserThirdAuth userThirdAuth) {
        return toAjax(userThirdAuthService.updateById(userThirdAuth));
    }

    /**
     * 删除第三方用户
     */
    @RequiresPermissions("system:auth:remove")
    @Log(title = "第三方用户", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(List<String> ids) {
        return toAjax(userThirdAuthService.removeByIds(ids));
    }
}

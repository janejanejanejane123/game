package com.ruoyi.controller;

import java.util.List;

import com.ruoyi.game.domain.UserAuthRel;
import com.ruoyi.game.service.IUserAuthRelService;
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
 * 用户验证关联Controller
 *
 * @author ruoyi
 * @date 2023-04-28
 */
@Controller
@RequestMapping("/com/ruoyi/system/rel")
public class UserAuthRelController extends BaseController {
    private String prefix = "com/ruoyi/system/rel";

    @Autowired
    private IUserAuthRelService userAuthRelService;

    @RequiresPermissions("system:rel:view")
    @GetMapping()
    public String rel() {
        return prefix + "/rel";
    }

    /**
     * 新增用户验证关联
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存用户验证关联
     */
    @RequiresPermissions("system:rel:add")
    @Log(title = "用户验证关联", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UserAuthRel userAuthRel) {
        return toAjax(userAuthRelService.save(userAuthRel));
    }

    /**
     * 修改用户验证关联
     */
    @RequiresPermissions("system:rel:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        UserAuthRel userAuthRel = userAuthRelService.getById(id);
        mmap.put("userAuthRel", userAuthRel);
        return prefix + "/edit";
    }

    /**
     * 修改保存用户验证关联
     */
    @RequiresPermissions("system:rel:edit")
    @Log(title = "用户验证关联", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UserAuthRel userAuthRel) {
        return toAjax(userAuthRelService.updateById(userAuthRel));
    }

    /**
     * 删除用户验证关联
     */
    @RequiresPermissions("system:rel:remove")
    @Log(title = "用户验证关联", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(List<String> ids) {
        return toAjax(userAuthRelService.removeByIds(ids));
    }
}

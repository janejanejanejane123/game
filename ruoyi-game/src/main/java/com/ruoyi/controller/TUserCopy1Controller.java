package com.ruoyi.controller;

import java.util.List;

import com.ruoyi.game.domain.TUserCopy1;
import com.ruoyi.game.service.ITUserCopy1Service;
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
 * 用户Controller
 *
 * @author ruoyi
 * @date 2023-04-28
 */
@Controller
@RequestMapping("/com/ruoyi/system/copy1")
public class TUserCopy1Controller extends BaseController {
    private String prefix = "com/ruoyi/system/copy1";

    @Autowired
    private ITUserCopy1Service tUserCopy1Service;

    @RequiresPermissions("system:copy1:view")
    @GetMapping()
    public String copy1() {
        return prefix + "/copy1";
    }

    /**
     * 新增用户
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存用户
     */
    @RequiresPermissions("system:copy1:add")
    @Log(title = "用户", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(TUserCopy1 tUserCopy1) {
        return toAjax(tUserCopy1Service.save(tUserCopy1));
    }

    /**
     * 修改用户
     */
    @RequiresPermissions("system:copy1:edit")
    @GetMapping("/edit/{uid}")
    public String edit(@PathVariable("uid") Long uid, ModelMap mmap) {
        TUserCopy1 tUserCopy1 = tUserCopy1Service.getById(uid);
        mmap.put("tUserCopy1", tUserCopy1);
        return prefix + "/edit";
    }

    /**
     * 修改保存用户
     */
    @RequiresPermissions("system:copy1:edit")
    @Log(title = "用户", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(TUserCopy1 tUserCopy1) {
        return toAjax(tUserCopy1Service.updateById(tUserCopy1));
    }

    /**
     * 删除用户
     */
    @RequiresPermissions("system:copy1:remove")
    @Log(title = "用户", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(List<String> ids) {
        return toAjax(tUserCopy1Service.removeByIds(ids));
    }
}

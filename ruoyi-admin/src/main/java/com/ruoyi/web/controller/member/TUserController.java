package com.ruoyi.web.controller.member;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Decrypt;
import com.ruoyi.common.annotation.DecryptLong;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.core.domain.model.member.TUserVo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.member.service.ITUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户Controller
 *
 * @author ruoyi
 * @date 2022-03-29
 */
@RestController
@RequestMapping("/member/user")
public class TUserController extends BaseController
{
    @Autowired
    private ITUserService tUserService;

    /**
     * 查询用户列表
     */
    @PreAuthorize("@ss.hasPermi('member:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(TUserVo tUser)
    {
        startPage();
        if (isMerchant()){
            tUser.setMerchantId(getUserId());
        }
        List<TUser> tUsers = tUserService.selectTUserList(tUser);
        return getDataTable(tUsers);
    }

    /**
     * 导出用户列表
     */
    @PreAuthorize("@ss.hasPermi('member:user:export')")
    @Log(title = "用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TUserVo tUser)
    {
        if (isMerchant()){
            tUser.setMerchantId(getUserId());
        }
        List<TUser> list = tUserService.selectTUserList(tUser);
        ExcelUtil<TUser> util = new ExcelUtil<TUser>(TUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    /**
     * 获取用户详细信息
     */
    @PreAuthorize("@ss.hasPermi('member:user:query')")
    @GetMapping(value = "/{uid}")
    public AjaxResult getInfo(@PathVariable("uid") Long uid)
    {
        return AjaxResult.success(tUserService.selectTUserByUid(uid));
    }

//    /**
//     * 新增用户
//     */
//    @PreAuthorize("@ss.hasPermi('member:user:add')")
//    @Log(title = "用户", businessType = BusinessType.INSERT)
//    @PostMapping
//    @DecryptLong
//    public AjaxResult add(@RequestBody TUser tUser)
//    {
//        tUser.setCreateBy(getUsername());
//        tUser.setCreateTime(new Date());
//        return toAjax(tUserService.insertTUser(tUser));
//    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('member:user:edit')")
    @Log(title = "用户", businessType = BusinessType.UPDATE)
    @PutMapping
    @DecryptLong
    public AjaxResult edit(@RequestBody TUser tUser)
    {
        if (isMerchant()){
            tUser.setMerchantId(getUserId());
        }
        tUser.setUpdateBy(getUsername());
        tUser.setUpdateTime(new Date());
        return toAjax(tUserService.updateTUser(tUser));
    }

    /**
     * 根据用户名称模糊查询查询
     */
    @GetMapping("/selectUserByUserName")
    @PreAuthorize("@ss.hasPermi('member:user:list')")
    public AjaxResult selectUserByUserName(TUserVo tUser)
    {
        if (isMerchant()){
            tUser.setMerchantId(getUserId());
        }
        List<TUser> list = tUserService.selectTUserList(tUser);
        return AjaxResult.success(list);
    }

}

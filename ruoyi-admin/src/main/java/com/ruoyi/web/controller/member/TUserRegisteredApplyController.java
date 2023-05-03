package com.ruoyi.web.controller.member;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.member.domain.TUserRegisteredApply;
import com.ruoyi.member.service.ITUserRegisteredApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户注册申请Controller
 * 
 * @author ruoyi
 * @date 2022-03-29
 */
@RestController
@RequestMapping("/member/registered/apply")
public class TUserRegisteredApplyController extends BaseController
{
    @Autowired
    private ITUserRegisteredApplyService tUserRegisteredApplyService;

    /**
     * 查询用户注册申请列表
     */
    @PreAuthorize("@ss.hasPermi('member:registered:list')")
    @GetMapping("/list")
    public TableDataInfo list(TUserRegisteredApply tUserRegisteredApply)
    {
        startPage();
        List<TUserRegisteredApply> list = tUserRegisteredApplyService.selectTUserRegisteredApplyList(tUserRegisteredApply);
        return getDataTable(list);
    }

    /**
     * 导出用户注册申请列表
     */
    @PreAuthorize("@ss.hasPermi('member:registered:export')")
    @Log(title = "用户注册申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TUserRegisteredApply tUserRegisteredApply)
    {
        List<TUserRegisteredApply> list = tUserRegisteredApplyService.selectTUserRegisteredApplyList(tUserRegisteredApply);
        ExcelUtil<TUserRegisteredApply> util = new ExcelUtil<TUserRegisteredApply>(TUserRegisteredApply.class);
        util.exportExcel(response, list, "用户注册申请数据");
    }

    /**
     * 获取用户注册申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('member:registered:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tUserRegisteredApplyService.selectTUserRegisteredApplyById(id));
    }

//    /**
//     * 新增用户注册申请
//     */
//    @PreAuthorize("@ss.hasPermi('member:registered:add')")
//    @Log(title = "用户注册申请", businessType = BusinessType.INSERT)
//    @PostMapping
//    public AjaxResult add(@RequestBody TUserRegisteredApply tUserRegisteredApply)
//    {
//        return toAjax(tUserRegisteredApplyService.insertTUserRegisteredApply(tUserRegisteredApply));
//    }

    /**
     * 修改用户注册申请
     */
    @PreAuthorize("@ss.hasPermi('member:registered:edit')")
    @Log(title = "用户注册申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TUserRegisteredApply tUserRegisteredApply)
    {
        return toAjax(tUserRegisteredApplyService.updateTUserRegisteredApply(tUserRegisteredApply));
    }

//    /**
//     * 删除用户注册申请
//     */
//    @PreAuthorize("@ss.hasPermi('member:registered:remove')")
//    @Log(title = "用户注册申请", businessType = BusinessType.DELETE)
//	@DeleteMapping("/{ids}")
//    public AjaxResult remove(@PathVariable Long[] ids)
//    {
//        return toAjax(tUserRegisteredApplyService.deleteTUserRegisteredApplyByIds(ids));
//    }
}

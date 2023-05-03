package com.ruoyi.web.controller.member;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.member.domain.TUserInfoApply;
import com.ruoyi.member.service.ITUserInfoApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户资料申请与审核Controller
 * 
 * @author ruoyi
 * @date 2022-03-28
 */
@RestController
@RequestMapping("/member/info/apply")
public class TUserInfoApplyController extends BaseController
{
    @Autowired
    private ITUserInfoApplyService tUserInfoApplyService;

    /**
     * 查询用户资料申请与审核列表
     */
    @PreAuthorize("@ss.hasPermi('member:apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(TUserInfoApply tUserInfoApply)
    {
        startPage();
        List<TUserInfoApply> list = tUserInfoApplyService.selectTUserInfoApplyList(tUserInfoApply);
        return getDataTable(list);
    }

    /**
     * 导出用户资料申请与审核列表
     */
    @PreAuthorize("@ss.hasPermi('member:apply:export')")
    @Log(title = "用户资料申请与审核", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TUserInfoApply tUserInfoApply)
    {
        List<TUserInfoApply> list = tUserInfoApplyService.selectTUserInfoApplyList(tUserInfoApply);
        ExcelUtil<TUserInfoApply> util = new ExcelUtil<TUserInfoApply>(TUserInfoApply.class);
        util.exportExcel(response, list, "用户资料申请与审核数据");
    }

    /**
     * 获取用户资料申请与审核详细信息
     */
    @PreAuthorize("@ss.hasPermi('member:apply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tUserInfoApplyService.selectTUserInfoApplyById(id));
    }

//    /**
//     * 新增用户资料申请与审核
//     */
//    @PreAuthorize("@ss.hasPermi('member:apply:add')")
//    @Log(title = "用户资料申请与审核", businessType = BusinessType.INSERT)
//    @PostMapping
//    public AjaxResult add(@RequestBody TUserInfoApply tUserInfoApply)
//    {
//        return toAjax(tUserInfoApplyService.insertTUserInfoApply(tUserInfoApply));
//    }

    /**
     * 修改用户资料申请与审核
     */
    @PreAuthorize("@ss.hasPermi('member:apply:edit')")
    @Log(title = "用户资料申请与审核", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TUserInfoApply tUserInfoApply)
    {
        return toAjax(tUserInfoApplyService.updateTUserInfoApply(tUserInfoApply));
    }

//    /**
//     * 删除用户资料申请与审核
//     */
//    @PreAuthorize("@ss.hasPermi('member:apply:remove')")
//    @Log(title = "用户资料申请与审核", businessType = BusinessType.DELETE)
//	@DeleteMapping("/{ids}")
//    public AjaxResult remove(@PathVariable Long[] ids)
//    {
//        return toAjax(tUserInfoApplyService.deleteTUserInfoApplyByIds(ids));
//    }
}

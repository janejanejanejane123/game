package com.ruoyi.web.controller.member;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.member.domain.TUserProxyApply;
import com.ruoyi.member.service.ITUserProxyApplyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 代理申请Controller
 * 
 * @author ruoyi
 * @date 2022-06-26
 */
@RestController
@RequestMapping("/member/proxy")
public class TUserProxyApplyController extends BaseController
{
    @Autowired
    private ITUserProxyApplyService tUserProxyApplyService;

    /**
     * 查询代理申请列表
     */
    @PreAuthorize("@ss.hasPermi('member:proxy:list')")
    @GetMapping("/list")
    public TableDataInfo list(TUserProxyApply tUserProxyApply)
    {
        startPage();
        List<TUserProxyApply> list = tUserProxyApplyService.selectTUserProxyApplyList(tUserProxyApply);
        return getDataTable(list);
    }

//    /**
//     * 导出代理申请列表
//     */
//    @PreAuthorize("@ss.hasPermi('member:apply:export')")
//    @Log(title = "代理申请", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, TUserProxyApply tUserProxyApply)
//    {
//        List<TUserProxyApply> list = tUserProxyApplyService.selectTUserProxyApplyList(tUserProxyApply);
//        ExcelUtil<TUserProxyApply> util = new ExcelUtil<TUserProxyApply>(TUserProxyApply.class);
//        util.exportExcel(response, list, "代理申请数据");
//    }

    /**
     * 获取代理申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('member:proxy:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tUserProxyApplyService.selectTUserProxyApplyById(id));
    }

//    /**
//     * 新增代理申请
//     */
//    @PreAuthorize("@ss.hasPermi('member:apply:add')")
//    @Log(title = "代理申请", businessType = BusinessType.INSERT)
//    @PostMapping
//    public AjaxResult add(@RequestBody TUserProxyApply tUserProxyApply)
//    {
//        return toAjax(tUserProxyApplyService.insertTUserProxyApply(tUserProxyApply));
//    }

    /**
     * 修改代理申请
     */
    @PreAuthorize("@ss.hasPermi('member:proxy:edit')")
    @Log(title = "代理申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TUserProxyApply tUserProxyApply)
    {
        return toAjax(tUserProxyApplyService.updateTUserProxyApply(tUserProxyApply));
    }

//    /**
//     * 删除代理申请
//     */
//    @PreAuthorize("@ss.hasPermi('member:apply:remove')")
//    @Log(title = "代理申请", businessType = BusinessType.DELETE)
//	@DeleteMapping("/{ids}")
//    public AjaxResult remove(@PathVariable Long[] ids)
//    {
//        return toAjax(tUserProxyApplyService.deleteTUserProxyApplyByIds(ids));
//    }
}

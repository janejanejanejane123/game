package com.ruoyi.web.controller.member;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.member.service.IUserMailBoxService;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * UserMailBoxController
 * 
 * @author ry
 * @date 2022-06-06
 */
@RestController
@RequestMapping("/system/member/box")
public class UserMailBoxController extends BaseController
{
    @Resource
    private IUserMailBoxService userMailBoxService;

    /**
     * 查询站内信列表
     */
    @PreAuthorize("@ss.hasPermi('member:mailbox:list')")
    @GetMapping("/list")
    public TableDataInfo list(UserMailBox userMailBox)
    {
        startPage();
        List<UserMailBox> list = userMailBoxService.selectUserMailBoxList(userMailBox);
        return getDataTable(list);
    }

    /**
     * 导出站内信列表
     */
    @PreAuthorize("@ss.hasPermi('member:mailbox:export')")
    @Log(title = "member", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserMailBox userMailBox)
    {
        List<UserMailBox> list = userMailBoxService.selectUserMailBoxList(userMailBox);
        ExcelUtil<UserMailBox> util = new ExcelUtil<UserMailBox>(UserMailBox.class);
        util.exportExcel(response, list, "member数据");
    }

    /**
     * 获取站内信详细信息
     */
    @PreAuthorize("@ss.hasPermi('member:mailbox:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(userMailBoxService.selectUserMailBoxById(id));
    }

    /**
     * 新增站内信
     */
    @PreAuthorize("@ss.hasPermi('member:mailbox:add')")
    @Log(title = "member", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserMailBox userMailBox)
    {
        return toAjax(userMailBoxService.insertUserMailBox(userMailBox));
    }

    /**
     * 修改站内信
     */
    @PreAuthorize("@ss.hasPermi('member:mailbox:edit')")
    @Log(title = "member", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserMailBox userMailBox)
    {
        return toAjax(userMailBoxService.updateUserMailBox(userMailBox));
    }

    /**
     * 删除站内信
     */
    @PreAuthorize("@ss.hasPermi('member:mailbox:remove')")
    @Log(title = "member", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(userMailBoxService.deleteUserMailBoxByIds(ids));
    }
}

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
import com.ruoyi.member.domain.TUserNicknameBlackList;
import com.ruoyi.member.service.ITUserNicknameBlackListService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户昵称黑名单Controller
 * 
 * @author ruoyi
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/member/list")
public class TUserNicknameBlackListController extends BaseController
{
    @Autowired
    private ITUserNicknameBlackListService tUserNicknameBlackListService;

    /**
     * 查询用户昵称黑名单列表
     */
    @PreAuthorize("@ss.hasPermi('member:nicknameBlack:list')")
    @GetMapping("/list")
    public TableDataInfo list(TUserNicknameBlackList tUserNicknameBlackList)
    {
        startPage();
        List<TUserNicknameBlackList> list = tUserNicknameBlackListService.selectTUserNicknameBlackListList(tUserNicknameBlackList);
        return getDataTable(list);
    }

    /**
     * 导出用户昵称黑名单列表
     */
    @PreAuthorize("@ss.hasPermi('member:nicknameBlack:export')")
    @Log(title = "用户昵称黑名单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TUserNicknameBlackList tUserNicknameBlackList)
    {
        List<TUserNicknameBlackList> list = tUserNicknameBlackListService.selectTUserNicknameBlackListList(tUserNicknameBlackList);
        ExcelUtil<TUserNicknameBlackList> util = new ExcelUtil<TUserNicknameBlackList>(TUserNicknameBlackList.class);
        util.exportExcel(response, list, "用户昵称黑名单数据");
    }

    /**
     * 获取用户昵称黑名单详细信息
     */
    @PreAuthorize("@ss.hasPermi('member:nicknameBlack:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tUserNicknameBlackListService.selectTUserNicknameBlackListById(id));
    }

    /**
     * 新增用户昵称黑名单
     */
    @PreAuthorize("@ss.hasPermi('member:nicknameBlack:add')")
    @Log(title = "用户昵称黑名单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TUserNicknameBlackList tUserNicknameBlackList)
    {
        return toAjax(tUserNicknameBlackListService.insertTUserNicknameBlackList(tUserNicknameBlackList));
    }

    /**
     * 修改用户昵称黑名单
     */
    @PreAuthorize("@ss.hasPermi('member:nicknameBlack:edit')")
    @Log(title = "用户昵称黑名单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TUserNicknameBlackList tUserNicknameBlackList)
    {
        return toAjax(tUserNicknameBlackListService.updateTUserNicknameBlackList(tUserNicknameBlackList));
    }

    /**
     * 删除用户昵称黑名单
     */
    @PreAuthorize("@ss.hasPermi('member:list:remove')")
    @Log(title = "用户昵称黑名单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tUserNicknameBlackListService.deleteTUserNicknameBlackListById(ids[0]));
    }
}

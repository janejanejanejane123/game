package com.ruoyi.web.controller.member;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.member.domain.TUserPhoto;
import com.ruoyi.member.service.ITUserPhotoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户头像Controller
 * 
 * @author ruoyi
 * @date 2022-05-22
 */
@RestController
@RequestMapping("/member/photo")
public class TUserPhotoController extends BaseController
{
    @Autowired
    private ITUserPhotoService tUserPhotoService;

    /**
     * 查询用户头像列表
     */
    @PreAuthorize("@ss.hasPermi('member:photo:list')")
    @GetMapping("/list")
    public TableDataInfo list(TUserPhoto tUserPhoto)
    {
        startPage();
        List<TUserPhoto> list = tUserPhotoService.selectTUserPhotoList(tUserPhoto);
        return getDataTable(list);
    }

//    /**
//     * 导出用户头像列表
//     */
//    @PreAuthorize("@ss.hasPermi('member:photo:export')")
//    @Log(title = "用户头像", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, TUserPhoto tUserPhoto)
//    {
//        List<TUserPhoto> list = tUserPhotoService.selectTUserPhotoList(tUserPhoto);
//        ExcelUtil<TUserPhoto> util = new ExcelUtil<TUserPhoto>(TUserPhoto.class);
//        util.exportExcel(response, list, "用户头像数据");
//    }

//    /**
//     * 获取用户头像详细信息
//     */
//    @PreAuthorize("@ss.hasPermi('member:photo:query')")
//    @GetMapping(value = "/{id}")
//    public AjaxResult getInfo(@PathVariable("id") Long id)
//    {
//        return AjaxResult.success(tUserPhotoService.selectTUserPhotoById(id));
//    }


    @PostMapping("/upload")
    @PreAuthorize("@ss.hasPermi('member:photo:add')")
    public AjaxResult upload(@RequestParam("file") MultipartFile file)
    {
        String path = tUserPhotoService.upload(file);
        return AjaxResult.success().put("src",path);
    }

    /**
     * 新增用户头像
     */
    @PreAuthorize("@ss.hasPermi('member:photo:add')")
    @Log(title = "用户头像", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TUserPhoto tUserPhoto)
    {
        return toAjax(tUserPhotoService.insertTUserPhoto(tUserPhoto));
    }

    /**
     * 修改用户头像
     */
    @PreAuthorize("@ss.hasPermi('member:photo:edit')")
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TUserPhoto tUserPhoto)
    {
        return toAjax(tUserPhotoService.updateTUserPhoto(tUserPhoto));
    }

    /**
     * 删除用户头像
     */
    @PreAuthorize("@ss.hasPermi('member:photo:remove')")
    @Log(title = "用户头像", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tUserPhotoService.deleteTUserPhotoByIds(ids));
    }
}

package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysWebsite;
import com.ruoyi.system.service.ISysWebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 网站管理Controller
 * 
 * @author nn
 * @date 2022-07-21
 */
@RestController
@RequestMapping("/web/system/website")
public class SysWebsiteController extends BaseController
{
    @Autowired
    private ISysWebsiteService sysWebsiteService;

    /**
     * 获取网站信息(允许匿名访问).
     */
    @GetMapping("/getSysWebsiteFTP")
    public AjaxResult getSysWebsiteFTP(SysWebsite sysWebsite)
    {
        SysWebsite info = sysWebsiteService.getSysWebsite(sysWebsite);
        return AjaxResult.success(info);
    }

    /**
     * 获取网站信息.
     */
    @GetMapping("/getSysWebsite")
    public AjaxResult getSysWebsite(SysWebsite sysWebsite)
    {
        SysWebsite info = sysWebsiteService.getSysWebsite(sysWebsite);
        return AjaxResult.success(info);
    }
}

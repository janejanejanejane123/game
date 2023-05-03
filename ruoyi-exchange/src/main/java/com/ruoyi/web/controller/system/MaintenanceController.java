package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.settings.service.ISysMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统维护Controller
 * 
 * @author nn
 * @date 2022-04-12
 */
@RestController
@RequestMapping("/web/system/maintenance")
public class MaintenanceController extends BaseController
{
    @Autowired
    private ISysMaintenanceService sysMaintenanceService;

    /**
     * 获取维护信息
     */
    @GetMapping("/getMaintenance")
    public AjaxResult getMaintenance()
    {
        return AjaxResult.success(sysMaintenanceService.getMaintenance());
    }

}

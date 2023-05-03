package com.ruoyi.quartz.task;

import com.ruoyi.settings.service.ISysMaintenanceService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 系统维护定时任务调度
 * 
 * @author nn
 */
@Component("maintainTask")
public class MaintainTask {

    @Resource
    private ISysMaintenanceService iSysMaintenanceService;

    public void systemMaintenance() {
        iSysMaintenanceService.getMaintenanceTask();
    }

}

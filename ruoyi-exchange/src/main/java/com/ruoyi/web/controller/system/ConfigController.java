package com.ruoyi.web.controller.system;

import com.ruoyi.common.constant.ConfigKeyConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 参数配置 信息操作处理
 * 
 * @author nn
 */
@RestController
@RequestMapping("/web/system/config")
public class ConfigController extends BaseController
{
    @Autowired
    private ISysConfigService configService;

    /**
     * 根据参数key获取参数Value
     */
    @GetMapping(value = "/getConfigValueByConfigKey")
    public AjaxResult getConfigValueByConfigKey(@PathVariable String configKey)
    {
        String configValue = configService.selectConfigByKey(configKey);
        return AjaxResult.success(configValue);
    }

    /**
     * 获取在线客服.
     */
    @GetMapping(value = "/getCustomerService")
    public AjaxResult getCustomerService()
    {
        String configValue = configService.selectConfigByKey(ConfigKeyConstants.SYS_CUSTOMER_SERVICE);
        return AjaxResult.success(configValue);
    }

}

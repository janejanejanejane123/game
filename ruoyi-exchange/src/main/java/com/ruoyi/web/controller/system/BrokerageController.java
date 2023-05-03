package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.settings.domain.SysBrokerage;
import com.ruoyi.settings.service.ISysBrokerageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 佣金设置Controller
 * 
 * @author nn
 * @date 2022-06-19
 */
@RestController
@RequestMapping("/web/system/brokerage")
public class BrokerageController extends BaseController
{
    @Autowired
    private ISysBrokerageService sysBrokerageService;

    /**
     * 获取佣金列表.
     */
    @GetMapping(value = "/getBrokerageList")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult getBrokerageList() {
        List<SysBrokerage> list = sysBrokerageService.selectSysBrokerageList(new SysBrokerage());
        return AjaxResult.success(list);
    }

}

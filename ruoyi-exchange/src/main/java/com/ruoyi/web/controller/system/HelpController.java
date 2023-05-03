package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysHelp;
import com.ruoyi.system.service.ISysHelpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 会员帮助Controller
 * 
 * @author nn
 * @date 2022-03-20
 */
@RestController
@RequestMapping("/web/system/help")
public class HelpController extends BaseController
{
    @Autowired
    private ISysHelpService sysHelpService;

    /**
     * 根据帮助类型获取帮助内容
     */
    @GetMapping(value = "/getHelpContentByHelpType")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult getHelpContentByHelpType(@PathVariable("helpType") String helpType) {

        String  helpContent = sysHelpService.selectHelpContentByHelpType(helpType);
        return AjaxResult.success(helpContent);
    }

    /**
     * 获取帮助列表.
     */
    @GetMapping(value = "/getHelpList")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult getHelpList() {
        SysHelp sysHelp = new SysHelp();
        sysHelp.setStatus("0");
        List<SysHelp> list = sysHelpService.selectSysHelpList(sysHelp);
        return AjaxResult.success(list);
    }

}

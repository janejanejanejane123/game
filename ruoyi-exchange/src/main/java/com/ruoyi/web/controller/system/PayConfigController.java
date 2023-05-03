package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.system.domain.SysPayConfig;
import com.ruoyi.system.service.IMemberService;
import com.ruoyi.system.service.ISysPayConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 支付配置Controller
 * 
 * @author nn
 * @date 2022-09-06
 */
@RestController
@RequestMapping("/web/system/payConfig")
public class PayConfigController extends BaseController {
    @Autowired
    private ISysPayConfigService sysPayConfigService;

    @Autowired
    private IMemberService memberService;

    /**
     * 获取商户支付配置.
     */
    @GetMapping("/getSysPayConfig")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult getSysPayConfig() {
        TUser user = memberService.selectMemberByUid(getUserId());
        SysPayConfig sysPayConfig = sysPayConfigService.selectSysPayConfigByUserId(user.getMerchantId());
        //如果商户还未配置，不返回null，返回一个空对象(属性无值).
        if(sysPayConfig == null){
            return AjaxResult.success(new SysPayConfig());
        }
        return AjaxResult.success(sysPayConfig);
    }

}

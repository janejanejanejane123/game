package com.ruoyi.web.controller.tool;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.member.service.ITUserService;
import com.ruoyi.system.service.IMemberService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * swagger 用户测试方法
 * 
 * @author ruoyi
 */
@Api("用户信息管理")
@RestController
@RequestMapping("/test/user")
public class TestController extends BaseController
{
    @Resource
    private ITUserService itUserService;
    @Resource
    private IMemberService memberService;

    @GetMapping("/test")
    public AjaxResult test(){
        TUser tUser = memberService.selectMemberByUid(1015339351663665152L);
        return AjaxResult.success().put("merchantId",tUser.getMerchantId());
    }

}

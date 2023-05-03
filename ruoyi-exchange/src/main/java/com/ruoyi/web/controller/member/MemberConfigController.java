package com.ruoyi.web.controller.member;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.member.service.ITUserConfigService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/8,14:46
 * @return:
 **/
@RestController
@RequestMapping("/config")
public class MemberConfigController {


    @Resource
    private ITUserConfigService userConfigService;


    @GetMapping("/registerConfig")
    public AjaxResult registerConfig(){
        return userConfigService.queryRegisteredConfig();
    }
}

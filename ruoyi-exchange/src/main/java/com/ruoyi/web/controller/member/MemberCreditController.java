package com.ruoyi.web.controller.member;

import com.ruoyi.common.annotation.RedisLockOperate;
import com.ruoyi.common.annotation.ReferType;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.vo.EncStringParamsVo;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.member.service.ITBankService;
import com.ruoyi.member.service.ITUserCreditService;
import com.ruoyi.member.vo.CreditVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/28,12:10
 * @return:
 **/

@RestController
@RequestMapping("/credit")
public class MemberCreditController {

    @Resource
    private ITUserCreditService userCreditService;
    @Resource
    private ITBankService bankService;
    @Resource
    private TokenService tokenService;


    @GetMapping("/bank")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult bank(){
        return bankService.list();
    }

    @PostMapping("/add")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult addCredit(@RequestBody @ReferType(CreditVo.class)EncStringParamsVo vo){
        tokenService.checkCaptchaStatus();
        return userCreditService.addCredit(vo.getBody());
    }



    @PostMapping("/apply")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult applyCredit(@RequestBody @ReferType(CreditVo.class)EncStringParamsVo vo) {
        tokenService. checkCaptchaStatus();
        return userCreditService.applyCredit(vo.getBody());
    }


    @PostMapping("/delete")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult deleteCredit(@RequestBody @ReferType(CreditVo.class) EncStringParamsVo vo){
        return userCreditService.deleteCredit(vo.getBody());
    }


    @GetMapping("/list")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult list(){
        return userCreditService.list();
    }

}

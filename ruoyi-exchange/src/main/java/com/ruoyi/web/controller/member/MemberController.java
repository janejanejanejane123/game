package com.ruoyi.web.controller.member;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.annotation.ReferType;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.redis.RedisLock;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.http.CookiesUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.vo.EncStringParamsVo;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.member.domain.TUserDataConfig;
import com.ruoyi.member.service.ITUserDataConfigService;
import com.ruoyi.member.service.ITUserPhotoService;
import com.ruoyi.member.service.ITUserService;
import com.ruoyi.member.vo.*;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/15,15:19
 * @return:
 **/


@RestController
@RequestMapping
public class MemberController extends BaseController {

    @Resource
    private ITUserService userService;
    @Resource
    private ITUserDataConfigService configService;
    @Resource
    private TokenService tokenService;

    @Resource
    private HttpServletRequest request;



    @GetMapping("/verifiedConfig")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult verifiedConfig(){
        return userService.verifiedConfig();
    }
    /**
     * 实名认证
     */

    @PostMapping("/verified")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult verified(@RequestBody JSONObject object){
        Long cardId = object.getLong("cardId");
        Long videoId = object.getLong("videoId");
        String realName = object.getString("realName");
        String ek= CookiesUtils.getCookie(request,"emp-id");
        return userService.verified(realName,cardId,videoId,ek);
    }



    @PostMapping("/registerChild")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult registerChild(@RequestBody @ReferType(RegisteredChildVo.class)EncStringParamsVo vo){
        tokenService.checkCaptchaStatus();
        return userService.registeredChildMember(vo.getBody());
    }


    @PostMapping("/register")
    @RepeatSubmit
    public AjaxResult registered(@RequestBody @ReferType(RegisteredInfo.class) EncStringParamsVo vo){
        tokenService.checkCaptchaStatus();
        LoginMember loginMember = userService.registeredMember(vo.getBody());
        //登录
        String token = tokenService.createToken(loginMember);

        tokenService.setUidTokenListMapping(loginMember);

        return AjaxResult.success().put(Constants.TOKEN,token);
    }


    @PostMapping("/login")
    public void login(@RequestBody @ReferType(LoginInfo.class)  EncStringParamsVo vo){
        tokenService.checkCaptchaStatus();
        String ipAddr = IpUtils.getIpAddr(request);
        RedisLock.lockInOneSecondOperate("playUserLogin:"+ipAddr , ()->{
            userService.memberLogin( vo.getBody());
            return null;
        });
    }

    @GetMapping("/getInfo")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult getInfo(){
       return userService.getInfo();
    }


//    @GetMapping("/getChildList")
//    @PreAuthorize("@ss.playerOnly()")
//    public TableDataInfo getChildList(){
//        startPage();
//        List<Map<String,Object>> result=userService.getChildList();
//        return getDataTable(result);
//    }

    @GetMapping("/modifyPhoto")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult modifyPhoto(Long id){
        userService.modifyPhoto(id);
        return AjaxResult.success();
    }


    @GetMapping("/allPhoto")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult allPhoto(){
        return  AjaxResult.success(userService.getAvailablePhotos());
    }


    @PostMapping("/modifyMemberPassword")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult modifyMemberPassword(@RequestBody @ReferType(ModifyPasswordVo.class)  EncStringParamsVo vo){
        return userService.modifyMemberPassword(vo.getBody());
    }

    @PostMapping("/modifyNickName")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult modifyNickName(@RequestBody @ReferType  EncStringParamsVo vo){
        LoginMember user = (LoginMember)SecurityUtils.getLoginUser();
        JSONObject object=vo.getBody();
        String nickName = object.getString("nickName");
        userService.modifyNickName(nickName,user.getUserId());
        return AjaxResult.success();
    }



    @PostMapping("/modifyMemberPayPassword")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult modifyMemberPayPassword(@RequestBody @ReferType(ModifyPayPasswordVo.class)  EncStringParamsVo vo){
        return userService.modifyMemberPayPassword(vo.getBody());
    }


    @PostMapping("/checkUsername")
    public AjaxResult checkUsername(@RequestBody @ReferType(CheckNameVo.class)
                                     EncStringParamsVo vo){
        CheckNameVo body =  vo.getBody();
        return userService.checkUsername(body.getUsername());
    }

    @PostMapping("/emailVerify")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult emailVerify(@RequestBody @ReferType EncStringParamsVo vo){
        return userService.emailVerify(vo.getBody());
    }


    @PostMapping("/bindEmail")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult bindEmail(@RequestBody @ReferType(EmailVo.class) EncStringParamsVo vo){
        tokenService.checkCaptchaStatus();

        userService.bindEmail(vo.getBody());

        tokenService.setLoginUser(SecurityUtils.getLoginUser());

        return AjaxResult.success();

    }

    @PostMapping("/resetPassword")
    public AjaxResult resetPassword(@RequestBody @ReferType EncStringParamsVo vo){
        JSONObject object=vo.getBody();
        return userService.resetPassword(object);
    }

    @PostMapping("/confirmReset")
    public AjaxResult confirmReset(@RequestBody @ReferType EncStringParamsVo vo){
        JSONObject object=vo.getBody();
        return userService.confirmReset(object);
    }

    @GetMapping("/multiFunctionalEmailVerify")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult multiFunctionalEmailVerify(short type){
        return userService.multiFunctionalEmailVerify(type);
    }

    @GetMapping("/applyProxy")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult applyProxy(){
        return userService.applyProxy();
    }




    @GetMapping("/user/conf/update")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult confUpdate(UserDataConfVo confVo){
        return configService.update(confVo);
    }
}

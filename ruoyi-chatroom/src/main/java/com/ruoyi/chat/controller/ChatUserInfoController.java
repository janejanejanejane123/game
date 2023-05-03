package com.ruoyi.chat.controller;

import com.ruoyi.chatroom.db.domain.ChatUserInfo;
import com.ruoyi.chatroom.db.vo.ChatUserInfoVo;
import com.ruoyi.chatroom.service.ChatUserInfoService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.chat.GeneralUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户信息 Controller
 * @author nn
 * @date 2020/08/21
 */
@RequestMapping("/userInfo")
@RestController
public class ChatUserInfoController extends BaseController {

    @Resource
    private ChatUserInfoService chatUserInfoService;

    /**
     * 查询我的信息
     * @return
     */
    @GetMapping("/myChatUserInfo")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult myChatUserInfo() {
        ChatUserInfo chatUserInfo = chatUserInfoService.myChatUserInfo(GeneralUtils.getCurrentLoginUser());
        return AjaxResult.success(chatUserInfo);
    }

    /**
     * 根据userIdentifier查询信息
     * @param userIdentifier  好友用户标识
     * @return
     */
    @GetMapping("/getChatUserInfoVoByUserIdentifier")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult getChatUserInfoVoByUserIdentifier(String userIdentifier) {
        Assert.isBlank(userIdentifier, "请输入正确的好友标识!");

        ChatUserInfoVo chatUserInfoVo = chatUserInfoService.getChatUserInfoVoByUserIdentifier(userIdentifier,getUserId());
        if(chatUserInfoVo == null){
            throw new ServiceException("请输入正确的好友标识!");
        }
        return AjaxResult.success(chatUserInfoVo);
    }


    /**
     * ：客服根据会员账号，或者昵称查询信息
     * @param parameter 查询参数（会员账号，昵称）
     * @return
     */

    @GetMapping("/getChatUserInfoByCustomerService")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult getChatUserInfoByCustomerService(String parameter) {
        Assert.isBlank(parameter, "查询条件不能为空!");
        ChatUserInfoVo chatUserInfoVo = chatUserInfoService.getChatUserInfoByCustomerService(parameter,getUserId());
        return AjaxResult.success(chatUserInfoVo);
    }
}

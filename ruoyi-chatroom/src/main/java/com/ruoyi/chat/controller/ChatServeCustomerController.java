package com.ruoyi.chat.controller;

import com.ruoyi.chatroom.domain.vo.ChatServeCustomerVo;
import com.ruoyi.chatroom.service.IChatServeCustomerService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @description: 客服列表 Controller
 * @author: nn
 * @create: 2020-07-11 14:22
 **/
@RestController
@RequestMapping("/customer/service")
public class ChatServeCustomerController extends BaseController {

    @Resource
    private IChatServeCustomerService chatServeCustomerService;

    /**
     * 客服列表(用户不用登录)
     */
    @GetMapping("/customerServiceList")
    public AjaxResult selectChatServeCustomerVoList(@RequestParam Map<String, Object> params) {
        List<ChatServeCustomerVo> customerServiceVoList = chatServeCustomerService.customerServiceVOList();
        return AjaxResult.success(customerServiceVoList);
    }

    /**
     * 是否是客服(用户不用登录)
     * @param  userName 用户名称
     */
    @GetMapping("/isServeCustomerByUserName")
    public AjaxResult isServeCustomerByUserName(String userName) {
        Assert.isBlank(userName, "账号不能空!");
        boolean result = chatServeCustomerService.isServeCustomerByUserName(userName);
        return AjaxResult.success(result);
    }
}

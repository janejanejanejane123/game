package com.ruoyi.chat.controller;

import com.ruoyi.chatroom.service.ChatSessionSetTopService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.Assert;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 会话置顶信息 Controller
 * @author nn
 * @date 2022/07/20
 */
@RequestMapping("/sessionSetTop")
@RestController
public class ChatSessionSetTopController extends BaseController {

    @Resource
    private ChatSessionSetTopService chatSessionSetTopService;


    /**
     * 1.获取我的所有会话置顶.
     * @return
     */
    @GetMapping("/mySessionSetTopList")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult mySessionSetTopList() {
        List<String> conversationIdList = chatSessionSetTopService.mySessionSetTopList(getUserId());
        return AjaxResult.success(conversationIdList);
    }

    /**
     * 2.置顶
     * @param conversationId   会话Id
     * @return
     */
    @PostMapping("/setTop")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult setTop(String conversationId) {
        Assert.isBlank(conversationId, "置顶的会话不能为空!");
        chatSessionSetTopService.setTop(conversationId, getLoginUser());
        return new AjaxResult();
    }

    /**
     * 3.取消置顶.
     * @param conversationId 会话Id
     * @return
     */
    @PostMapping("/cancelTop")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult cancelTop(String conversationId) {
        Assert.isBlank(conversationId, "取消置顶的会话不能为空!");
        chatSessionSetTopService.cancelTop(conversationId,getUserId());
        return new AjaxResult();
    }

}

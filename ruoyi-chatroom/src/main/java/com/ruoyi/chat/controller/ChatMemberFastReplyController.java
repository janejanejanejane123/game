package com.ruoyi.chat.controller;

import com.ruoyi.chatroom.db.vo.ChatMemberFastReplyVo;
import com.ruoyi.chatroom.service.ChatMemberFastReplyService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Assert;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 会员快捷回复 Controller
 * @author nn
 * @date 2022/07/20
 */
@RequestMapping("/member/fastReply")
@RestController
public class ChatMemberFastReplyController extends BaseController {

    @Resource
    private ChatMemberFastReplyService chatMemberFastReplyService;

    /**
     * 获取我的所有快捷回复.
     * @return
     */
    @GetMapping("/queryChatMemberFastReplyVoList")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult queryChatMemberFastReplyVoList() {
        List<ChatMemberFastReplyVo> chatMemberFastReplyVoList = chatMemberFastReplyService.queryChatMemberFastReplyVoListByUserId(getUserId());
        return AjaxResult.success(chatMemberFastReplyVoList);
    }

    /**
     * 保存会员快捷回复.
     * @param replyContent  快捷回复内容
     * @return
     */
    @PostMapping("/saveChatMemberFastReply")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult saveChatMemberFastReply(String replyContent) throws Exception{
        Assert.isBlank(replyContent, "快捷回复内容不能为空!");
        if(replyContent.length() > 300){
            throw new ServiceException("快捷回复最多300个字符!");
        }
        chatMemberFastReplyService.saveChatMemberFastReply(getLoginUser(),replyContent);
        return new AjaxResult();
    }

    /**
     * 修改会员快捷回复.
     * @param id Id
     * @param replyContent  回复内容
     * @return
     */
    @PostMapping("/updateChatMemberFastReply")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult updateChatMemberFastReply(Long id, String replyContent) {
        Assert.isBlank(replyContent, "回复内容不能为空!");

        if(id == null || id.longValue() == 0L ){
            throw new ServiceException("请选择需要修改的回复!");
        }
        if(replyContent.length() > 300){
            throw new ServiceException("快捷回复最多300个字符!");
        }
        chatMemberFastReplyService.updateChatMemberFastReply(id,replyContent,getUserId());
        return new AjaxResult();
    }

    /**
     * 删除会员快捷回复.
     * @param id Id
     * @return
     */
    @PostMapping("/deleteChatMemberFastReply")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult deleteChatMemberFastReply(Long id) {

        if(id == null || id.longValue() == 0L ){
            throw new ServiceException("请选择需要删除的回复!");
        }

        chatMemberFastReplyService.deleteChatMemberFastReply(id,getUserId());
        return new AjaxResult();
    }
}

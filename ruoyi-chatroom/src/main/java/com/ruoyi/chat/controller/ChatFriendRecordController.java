package com.ruoyi.chat.controller;

import com.ruoyi.chatroom.db.domain.ChatUserInfo;
import com.ruoyi.chatroom.domain.ChatSettings;
import com.ruoyi.chatroom.service.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.enums.ChatRoomConfigEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.clazz.ClassUtil;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.concurrent.ExecutionException;

/**
 * @description: 私聊消息记录
 * @author: nn
 * @create: 2022-08-01 12:26
 **/
@RestController
@RequestMapping("/chatFriend/record")
@Api(value = "聊天室", description = "聊天室私聊消息记录接口")
public class ChatFriendRecordController extends BaseController {

    @Resource
    private ChatFriendRecordService chatFriendRecordService;

    @Resource
    private IChatSettingsService chatSettingsService;

    @Resource
    private ChatUserInfoService chatUserInfoService;

    @Resource
    private IChatServeCustomerService chatServeCustomerService;

    @Resource
    private ChatFriendsService chatFriendsService;

    /**
     * 举报消息.
     * @param messageId
     * @param loginMember
     * @param remarks
     * @param reportType
     * @return
     */
    @PostMapping("/reportMessage")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult reportMessage(@NotNull Long messageId, LoginMember loginMember, String remarks, String reportType) {

        if (messageId == null || messageId.longValue() <= 0L) {
            throw new ServiceException("请选择要举报的记录!");
        }
        chatFriendRecordService.reportMessage( messageId, loginMember, remarks, reportType);
        return AjaxResult.success("举报成功!");
    }

    /**
     * 撤回消息.
     * @param friendIdentifier
     * @param messageId
     * @param loginMember
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @PostMapping("/retractMessage")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult retractMessage(@NotBlank String friendIdentifier, @NotNull Long messageId, LoginMember loginMember ) throws ExecutionException, InterruptedException {

        //是否允许私聊消息撤回
        ChatSettings chatSettings = chatSettingsService.getSettingByKey(ChatRoomConfigEnum.PRIVATE_MESSAGE_RECALL.getKey());
        Boolean configValue = ClassUtil.formatObject(chatSettings.getConfigValue(), Boolean.class);
        if (!configValue) {
            throw new ServiceException("私聊消息撤回已关闭!");
        }
        boolean isServeCustomer = chatServeCustomerService.isServeCustomer(friendIdentifier, null);
        ChatUserInfo friendChatUserInfo = chatUserInfoService.getChatUserInfoByUserIdentifier(friendIdentifier);
        if (!isServeCustomer && (friendChatUserInfo == null || !chatFriendsService.hasMyFriends( loginMember.getUserId(), friendChatUserInfo.getUserId()))) {
            throw new ServiceException("他(她)已经不是你的好友");
        }

        chatFriendRecordService.retractMessage(friendChatUserInfo, messageId, loginMember);

        return AjaxResult.success();
    }


}

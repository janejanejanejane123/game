package com.ruoyi.chat.controller;

import com.ruoyi.chatroom.service.ChatRoomRecordService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.ReportSourceEnum;
import com.ruoyi.common.enums.UserTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * @description: 大厅消息记录
 * @author: nn
 * @create: 2020-06- 12:26
 **/
@RestController
@RequestMapping("/chatRoom/record")
@Api(value = "聊天室", description = "聊天室大厅消息记录接口")
public class ChatRoomRecordController extends BaseController {

    @Resource
    private ChatRoomRecordService chatRoomRecordService;





    /**
     * @param messageId 消息ID
     * @return void
     * @Description: 举报消息
     */
    @PostMapping("/reportMessage")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult reportMessage(@NotNull Long messageId, LoginMember loginMember, String remarks, String reportType) {
        if (messageId == null || messageId.longValue() <= 0L) {
            throw new ServiceException("请选择要举报的记录!");
        }
        chatRoomRecordService.reportMessage( messageId,remarks, reportType,loginMember, ReportSourceEnum.CHATROOM.getReportSource());

        return AjaxResult.success("举报成功!");
    }

    /**
     * 撤回消息 - 前端撤回消息
     * @param messageId  消息Id
     * @return
     */
    @PostMapping("/retractMessage")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult retractMessage(@NotNull Long messageId){
        String loginIp = IpUtils.getIpAddr(ServletUtils.getRequest());
        LoginUser loginUser =  getLoginUser();
        chatRoomRecordService.retractMessage(messageId,(byte)0,loginIp,loginUser, UserTypeEnum.PLAYER);
        return new AjaxResult();

    }

}

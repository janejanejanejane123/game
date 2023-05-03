package com.ruoyi.chat.controller;

import com.ruoyi.chatroom.service.ChatFriendsService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Assert;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 好友 Controller
 * @author nn
 * @date 2022/07/31
 */
@RequestMapping("/friends")
@RestController
public class ChatFriendsController extends BaseController {

    @Resource
    private ChatFriendsService chatFriendsService;

    /**
     * 我的好友列表.
     * @param params
     * @return
     */
    @GetMapping("/myFriendList")
    @PreAuthorize("@ss.playerOnly()")
    public TableDataInfo myFriendList(@RequestParam Map<String, Object> params) {
        TableDataInfo tableDataInfo = chatFriendsService.myFriendList(params,getUserId());
        return tableDataInfo;
    }

    /**
     * 我的好友总记录数.
     * @return
     */
    @GetMapping("/myFriendTotalCount")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult myFriendTotalCount() {
        int totalCount = chatFriendsService.myFriendTotalCount(getUserId());
        return AjaxResult.success(totalCount);
    }

    /**
     * 设置好友消息声音.
     * @param userIdentifier   好友用户标识
     * @param muteOrSound  静音mute  响声sound
     * @return
     */
    @PostMapping("/setMuteOrSound")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult setMuteOrSound(String userIdentifier,String muteOrSound) {
        Assert.isBlank(userIdentifier, "好友不能为空!");
        Assert.isBlank(muteOrSound, "您是想静音？还是响声？");

        long count = chatFriendsService.setMuteOrSound(userIdentifier,muteOrSound,getUserId());
        if(count > 0){
            return AjaxResult.success("设置成功!");
        }
        throw new ServiceException("设置失败！");
    }

    /**
     * 修改好友备注.
     * @param userIdentifier  好友用户标识
     * @param remark  好友备注
     * @return
     */
    @PostMapping("/updateFriendRemark")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult updateFriendRemark(String userIdentifier,String remark) {
        Assert.isBlank(userIdentifier, "好友不能为空!");
        Assert.isBlank(remark, "好友备注不能为空!");
        if(remark.length() > 16){
            throw new ServiceException("好友备注过长,请重新输入好友备注!");
        }
        long count = chatFriendsService.updateFriendRemark(userIdentifier,remark,getUserId());
        if(count > 0){
            return AjaxResult.success("修改成功!");
        }
        throw new ServiceException("修改失败！");
    }

    /**
     * 删除好友.
     * @param userIdentifier 用户标识
     * @return
     */
    @PostMapping("/deleteFriend")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult deleteFriend(String userIdentifier) {
        Assert.isBlank(userIdentifier, "好友不能为空!");

        long count = chatFriendsService.deleteFriend(userIdentifier,getUserId());
        if(count > 0){
            return AjaxResult.success("删除成功!");
        }
        throw new ServiceException("删除失败！");
    }

    /**
     * 对离线服务获取确认.
     * @param ids  离线消息Id数组
     * @return
     */
    @PostMapping("/ackToFriend")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult ackToFriend(@RequestBody Long[] ids)throws InterruptedException {
        if(ids == null || ids.length == 0 ){
            throw new ServiceException("没有要确认的离线消息!");
        }
        chatFriendsService.ackToFriend(ids,getUserId());
        return new AjaxResult();
    }
}

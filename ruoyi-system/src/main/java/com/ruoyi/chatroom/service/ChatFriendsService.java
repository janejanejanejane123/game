package com.ruoyi.chatroom.service;

import com.ruoyi.chatroom.db.domain.ChatFriends;
import com.ruoyi.chatroom.db.vo.ChatFriendsVo;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * @description: 好友申请记录 业务类
 * @author: nn
 * @create: 2022-07-31 11:42
 **/
public interface ChatFriendsService {

    /**
     * 我的好友列表(分页).
     * @param params
     * @param myUserId  我的userId
     * @return
     */
    TableDataInfo myFriendList(Map<String, Object> params, Long myUserId);

    /**
     * 我的好友总记录数.
     * @param myUserId  我的userId
     * @return
     */
    int myFriendTotalCount(Long myUserId);

    /**
     * 设置好友消息声音.
     * @param userIdentifier   好友用户标识
     * @param muteOrSound  静音mute  响声sound
     * @param myUserId  我的userId
     * @return
     */
    Long setMuteOrSound(String userIdentifier, String muteOrSound, Long myUserId);

    /**
     * 修改好友备注.
     * @param userIdentifier  好友用户标识
     * @param remark  好友备注
     * @param myUserId 我的userId
     * @return
     */
    Long updateFriendRemark(String userIdentifier, String remark, Long myUserId);

    /**
     * 删除好友.
     * @param userIdentifier  好友用户标识
     * @param myUserId 我的userId
     * @return
     */
    Long deleteFriend(String userIdentifier, Long myUserId);


    /**
     *  对离线服务获取确认.
     * @param ids  离线消息Id数组
     * @return
     */
    AjaxResult ackToFriend(Long[] ids, Long userId) throws InterruptedException;

    /**
     * @Description:
     * @param myUserId 用户ID
     * @param friendUserId 朋友ID
     * @return boolean
     */
    boolean hasMyFriends(Long myUserId, Long friendUserId);

    /**
     * @Description: 会员上线下线消息回调
     * @param userId 用户ID
     * @param online  1:在线 0:离线
     * @return boolean
     */
    AjaxResult onOfflinesCallback(Long userId, byte online);

    /**
     * 查询我的好友记录集合.
     * @param
     * @return
     */
    List<ChatFriends> queryMyFriendsList(Long userId);

}

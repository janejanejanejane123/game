package com.ruoyi.chatroom.service;

import com.ruoyi.chatroom.db.domain.ChatUserInfo;
import com.ruoyi.chatroom.db.vo.ChatUserInfoVo;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.vo.MemberMessageVo;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/18,15:49
 * @return:
 **/

public interface ChatUserInfoService {

    /**
     * 根据userId查询信息.
     * @param
     * @return ChatUserInfo
     */
    ChatUserInfo getChatUserInfoByUserId(Long userId);

    /**
     * 查询我信息.
     * @param loginMember  当前登录用户信息
     * @return
     */
    ChatUserInfo myChatUserInfo(LoginMember loginMember);

    /**
     *  查询.
     * @param userName  用户名称.
     * @param nikeName  用户昵称.
     * @return  ChatUserInfo
     */
    ChatUserInfo getChatUserInfo(String userName, String nikeName);

    /**
     *  查询.
     * @param userIds  userIds.
     * @return  List<ChatUserInfo>
     */
    List<ChatUserInfo> queryChatUserInfoListByUserIds(List<Long> userIds);

    /**
     * 根据userIdentifier查询信息.
     * @param userIdentifier  我的用户标识
     * @param myUserId 我的userId
     * @return ChatUserInfoVo
     */
    ChatUserInfoVo getChatUserInfoVoByUserIdentifier(String userIdentifier,Long myUserId);


    /**
     * 根据用户标识查询信息.
     * @param
     * @return ChatUserInfo
     */
    ChatUserInfo getChatUserInfoByUserIdentifier(String userIdentifier);

    /**
     *  修改会员相关的数据.
     * @param memberMessageVo
     * @return
     */
    Long  updateChatUserInfo(MemberMessageVo memberMessageVo, String userIdentifier);

    /**
     * 新增会员相关的数据.
     * @param memberMessageVo
     * @return
     */
    ChatUserInfo  addChatUserInfo(MemberMessageVo memberMessageVo);

    /**
     * @Description: 根据用户Id获取用户信息
     * @param userId
     * @return
     */
    Future<ChatUserInfo> getChatUserInFuturefoByUserId(Long userId);

    /**
     * 修改用户版本信息
     * @param chatUserInfo
     * @return
     */
    Long updateChatUserInfoVersion(ChatUserInfo chatUserInfo);

    /**
     *  删除用户信息.
     *
     * @return
     */
    AjaxResult deleteChatUserInfo(byte accountType);

    /**
     * 客服根据会员账号，或者昵称，或者好友ID查询信息
     * @param parameter  查询参数（会员账号，昵称，好友ID）
     * @param myUserId 当前userId
     * @return
     * @author nn
     * @date 2019/11/3 14:37
     */
    ChatUserInfoVo getChatUserInfoByCustomerService(String parameter, Long myUserId);

}

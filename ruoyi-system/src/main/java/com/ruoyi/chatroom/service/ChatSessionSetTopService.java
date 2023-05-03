package com.ruoyi.chatroom.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;

import java.util.List;

/**
 * 会话置顶业务类
 * @author nn
 * @date 2022/07/19
 */
public interface ChatSessionSetTopService {

    /**
     * 1.获取我的所有会话置顶.
     * @param myUserId  我的userId
     * @return
     */
    List<String> mySessionSetTopList(Long myUserId);


    /**
     * 2：置顶.
     * @param conversationId 会话ID
     * @param loginUser  当前登录用户信息
     * @return
     * @author nn
     * @date 2019/11/3 14:37
     */
    AjaxResult setTop(String conversationId, LoginUser loginUser);


    /**
     * 3：取消置顶.
     * @param conversationId 会话ID
     * @param myUserId  我的userId
     * @return
     * @author nn
     * @date 2019/11/3 14:37
     */
    AjaxResult cancelTop(String conversationId, Long myUserId);


}

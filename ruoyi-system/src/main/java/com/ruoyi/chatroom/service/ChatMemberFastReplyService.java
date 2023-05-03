package com.ruoyi.chatroom.service;

import com.ruoyi.chatroom.db.vo.ChatMemberFastReplyVo;
import com.ruoyi.common.core.domain.model.LoginUser;

import java.util.List;

/**
 * @description: 会员快捷回复 业务类
 * @author: nn
 * @create: 2022-07-19 11:42
 **/
public interface ChatMemberFastReplyService {

    /**
     *  查询我的快捷回复数据.
     * @param userId  userId.
     * @return  List<ChatMemberFastReply>
     */
    List<ChatMemberFastReplyVo> queryChatMemberFastReplyVoListByUserId(Long userId);

    /**
     * 保存会员快捷回复 回复内容
     * @param loginUser
     * @param replyContent
     * @return
     */
    Long saveChatMemberFastReply(LoginUser loginUser, String replyContent) throws Exception;

    /**
     * 修改会员快捷回复.
     * @param id  Id
     * @param replyContent  回复内容
     * @param myUserId  我的userId
     * @return
     */
    Long updateChatMemberFastReply(Long id,String replyContent,Long myUserId);

    /**
     * 删除会员快捷回复.
     * @param id  Id
     * @param myUserId  我的userId
     * @return
     */
    Long deleteChatMemberFastReply(Long id,Long myUserId);


}

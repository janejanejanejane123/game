package com.ruoyi.chatroom.service;

import com.ruoyi.chatroom.db.domain.ChatRoomRecord;
import com.ruoyi.chatroom.db.vo.ChatRoomRecordVo;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserTypeEnum;

import java.util.List;

/**
 * @description: 大厅聊天记录业务类
 * @author: nn
 * @create: 2022-07-19 11:42
 **/
public interface ChatRoomRecordService {

    /**
     * 获取消息列表.
     * @param messageId
     * @param pageSize
     * @param loginUser
     * @return
     */
    List<ChatRoomRecordVo> getChatRoomRecordVoList(Long messageId, int pageSize, LoginUser loginUser);

    /**
     *  举报消息.
     * @param messageId 消息Id
     * @param remarks 备注
     * @param reportType 举报类型
     * @return
     */
    void reportMessage(Long messageId, String remarks, String reportType, LoginMember loginMember,byte reportSource);

    /**
     * @Description: 撤回消息
     * @param messageId 消息ID
     * @param isManager 是否管理员撤回，0:否,1:是
     * @param loginUser 撤回用户
     * @return
     */
    void retractMessage(Long messageId, byte isManager, String loginIp, LoginUser loginUser, UserTypeEnum userTypeEnum);


    /**
     *  定时删除大厅消息.
     * @param day 天数
     * @return
     */
    AjaxResult deleteOnTimeChatRoomRecord(int day);

    /**
     * 保存消息
     * @param replaceMsg
     */
    void saveChatRoomRecord(Object replaceMsg);



    void saveChatRoomRecord(ChatRoomRecord chatRoomRecord);
}

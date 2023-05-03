package com.ruoyi.chatroom.service;

import java.util.List;
import com.ruoyi.chatroom.domain.ChatSpeakBlacklist;

/**
 * 聊天室发言白名单Service接口
 * 
 * @author nn
 * @date 2022-08-28
 */
public interface IChatSpeakBlacklistService 
{
    /**
     * 查询聊天室发言白名单
     * 
     * @param id 聊天室发言白名单主键
     * @return 聊天室发言白名单
     */
    public ChatSpeakBlacklist selectChatSpeakBlacklistById(Long id);

    /**
     * 查询聊天室发言白名单列表
     * 
     * @param chatSpeakBlacklist 聊天室发言白名单
     * @return 聊天室发言白名单集合
     */
    public List<ChatSpeakBlacklist> selectChatSpeakBlacklistList(ChatSpeakBlacklist chatSpeakBlacklist);

    /**
     * 新增聊天室发言白名单
     * 
     * @param chatSpeakBlacklist 聊天室发言白名单
     * @return 结果
     */
    public int insertChatSpeakBlacklist(ChatSpeakBlacklist chatSpeakBlacklist);

    /**
     * 修改聊天室发言白名单
     * 
     * @param chatSpeakBlacklist 聊天室发言白名单
     * @return 结果
     */
    public int updateChatSpeakBlacklist(ChatSpeakBlacklist chatSpeakBlacklist);

    /**
     * 批量删除聊天室发言白名单
     * 
     * @param ids 需要删除的聊天室发言白名单主键集合
     * @return 结果
     */
    public int deleteChatSpeakBlacklistByIds(Long[] ids);

    /**
     * 删除聊天室发言白名单信息
     * 
     * @param id 聊天室发言白名单主键
     * @return 结果
     */
    public int deleteChatSpeakBlacklistById(Long id);

    /**
     * 修改状态
     *
     * @param chatSpeakBlacklist 信息
     * @return 结果
     */
    public int changeStatus(ChatSpeakBlacklist chatSpeakBlacklist);

    /**
     * 校验是否唯一
     *
     * @param chatSpeakBlacklist 检测的对象
     * @return 结果
     */
    public String checkSpeakBlacklistUnique(ChatSpeakBlacklist chatSpeakBlacklist);

    /**
     * @Description: 是否在发言白名单中
     * @param userId 用户ID
     * @return {@link boolean}
     */
    boolean isSpeakBlacklist(Long userId);
}

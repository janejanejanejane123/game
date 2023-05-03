package com.ruoyi.chatroom.mapper;

import java.util.List;
import com.ruoyi.chatroom.domain.ChatSpeakBlacklist;

/**
 * 聊天室发言白名单Mapper接口
 * 
 * @author nn
 * @date 2022-08-28
 */
public interface ChatSpeakBlacklistMapper 
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
     * 删除聊天室发言白名单
     * 
     * @param id 聊天室发言白名单主键
     * @return 结果
     */
    public int deleteChatSpeakBlacklistById(Long id);

    /**
     * 批量删除聊天室发言白名单
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteChatSpeakBlacklistByIds(Long[] ids);

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
    public ChatSpeakBlacklist checkSpeakBlacklistUnique(ChatSpeakBlacklist chatSpeakBlacklist);

    /**
     * 根据用户ID查询聊天室发言白名单
     *
     * @param userId 聊天室发言白名单主键
     * @return 聊天室发言白名单
     */
    public ChatSpeakBlacklist getSpeakBlacklistByUserId(Long userId);
}

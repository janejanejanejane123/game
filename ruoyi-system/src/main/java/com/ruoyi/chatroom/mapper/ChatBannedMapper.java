package com.ruoyi.chatroom.mapper;

import java.util.List;
import com.ruoyi.chatroom.domain.ChatBanned;
import com.ruoyi.chatroom.domain.ChatServeCustomer;

/**
 * 禁言管理Mapper接口
 * 
 * @author ruoyi
 * @date 2022-07-10
 */
public interface ChatBannedMapper
{
    /**
     * 查询禁言管理
     * 
     * @param id 禁言管理主键
     * @return 禁言管理
     */
    public ChatBanned selectChatBannedById(Long id);

    /**
     * 查询禁言管理列表
     * 
     * @param chatBanned 禁言管理
     * @return 禁言管理集合
     */
    public List<ChatBanned> selectChatBannedList(ChatBanned chatBanned);

    /**
     * 新增禁言管理
     * 
     * @param chatBanned 禁言管理
     * @return 结果
     */
    public int insertChatBanned(ChatBanned chatBanned);

    /**
     * 修改禁言管理
     * 
     * @param chatBanned 禁言管理
     * @return 结果
     */
    public int updateChatBanned(ChatBanned chatBanned);

    /**
     * 删除禁言管理
     * 
     * @param id 禁言管理主键
     * @return 结果
     */
    public int deleteChatBannedById(Long id);

    /**
     * 批量删除禁言管理
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteChatBannedByIds(Long[] ids);

    /**
     * 校验禁言是否唯一
     *
     * @param chatBanned 检测的对象
     * @return 结果
     */
    public ChatBanned checkChatBannedUnique(ChatBanned chatBanned);
}

package com.ruoyi.chatroom.mapper;

import java.util.List;
import com.ruoyi.chatroom.domain.ChatEmoji;

/**
 * 表情包Mapper接口
 * 
 * @author nn
 * @date 2022-07-13
 */
public interface ChatEmojiMapper 
{
    /**
     * 查询表情包
     * 
     * @param emojiId 表情包主键
     * @return 表情包
     */
    public ChatEmoji selectChatEmojiByEmojiId(Long emojiId);

    /**
     * 查询表情包列表
     * 
     * @param chatEmoji 表情包
     * @return 表情包集合
     */
    public List<ChatEmoji> selectChatEmojiList(ChatEmoji chatEmoji);

    /**
     * 新增表情包
     * 
     * @param chatEmoji 表情包
     * @return 结果
     */
    public int insertChatEmoji(ChatEmoji chatEmoji);

    /**
     * 修改表情包
     * 
     * @param chatEmoji 表情包
     * @return 结果
     */
    public int updateChatEmoji(ChatEmoji chatEmoji);

    /**
     * 删除表情包
     * 
     * @param emojiId 表情包主键
     * @return 结果
     */
    public int deleteChatEmojiByEmojiId(Long emojiId);

    /**
     * 批量删除表情包
     * 
     * @param emojiIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteChatEmojiByEmojiIds(Long[] emojiIds);
}

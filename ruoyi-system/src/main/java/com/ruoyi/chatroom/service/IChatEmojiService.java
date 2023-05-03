package com.ruoyi.chatroom.service;

import java.util.List;
import com.ruoyi.chatroom.domain.ChatEmoji;
import org.springframework.web.multipart.MultipartFile;

/**
 * 表情包Service接口
 * 
 * @author nn
 * @date 2022-07-13
 */
public interface IChatEmojiService 
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
     * 批量删除表情包
     * 
     * @param emojiIds 需要删除的表情包主键集合
     * @return 结果
     */
    public int deleteChatEmojiByEmojiIds(Long[] emojiIds);

    /**
     * 删除表情包信息
     * 
     * @param emojiId 表情包主键
     * @return 结果
     */
    public int deleteChatEmojiByEmojiId(Long emojiId);

    /**
     * 上传表情包.
     * @param file
     * @return
     */
    String uploadEmoji(MultipartFile file);

    /**
     * 前端查询表情包列表.
     * @param chatEmoji
     * @return
     */
    public List<ChatEmoji> getChatEmojiList(ChatEmoji chatEmoji);
}

package com.ruoyi.chatroom.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ruoyi.chatroom.domain.ChatInterestLanguage;
import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.file.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.ruoyi.chatroom.mapper.ChatEmojiMapper;
import com.ruoyi.chatroom.domain.ChatEmoji;
import com.ruoyi.chatroom.service.IChatEmojiService;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 表情包Service业务层处理
 * 
 * @author nn
 * @date 2022-07-13
 */
@Service
public class ChatEmojiServiceImpl implements IChatEmojiService 
{
    @Autowired
    private ChatEmojiMapper chatEmojiMapper;

    @Resource
    private MinioUtil minioUtil;

    @Resource
    private RedisCache redisCache;

    /**
     * 设置cache key
     *
     * @return 缓存键key
     */
    private String getCacheKey(String emojiType)
    {
        return CacheKeyConstants.T_CHAT_EMOJI_KEY + emojiType;
    }

    /**
     * 查询表情包
     * 
     * @param emojiId 表情包主键
     * @return 表情包
     */
    @Override
    @Cacheable(key = "'t_chat_emoji:'+#emojiId",cacheNames = "redisCache4Spring")
    public ChatEmoji selectChatEmojiByEmojiId(Long emojiId)
    {
        return chatEmojiMapper.selectChatEmojiByEmojiId(emojiId);
    }

    /**
     * 查询表情包列表
     * 
     * @param chatEmoji 表情包
     * @return 表情包
     */
    @Override
    public List<ChatEmoji> selectChatEmojiList(ChatEmoji chatEmoji)
    {
        return chatEmojiMapper.selectChatEmojiList(chatEmoji);
    }

    /**
     * 新增表情包
     * 
     * @param chatEmoji 表情包
     * @return 结果
     */
    @Override
    public int insertChatEmoji(ChatEmoji chatEmoji)
    {
        redisCache.deleteObject(getCacheKey(chatEmoji.getEmojiType()));
        return chatEmojiMapper.insertChatEmoji(chatEmoji);
    }

    /**
     * 修改表情包
     * 
     * @param chatEmoji 表情包
     * @return 结果
     */
    @Override
    public int updateChatEmoji(ChatEmoji chatEmoji)
    {
        redisCache.deleteObject(getCacheKey(chatEmoji.getEmojiType()));
        return chatEmojiMapper.updateChatEmoji(chatEmoji);
    }

    /**
     * 批量删除表情包
     * 
     * @param emojiIds 需要删除的表情包主键
     * @return 结果
     */
    @Override
    public int deleteChatEmojiByEmojiIds(Long[] emojiIds)
    {
        for(Long id : emojiIds){
            ChatEmoji chatEmoji = selectChatEmojiByEmojiId(id);
            redisCache.deleteObject(getCacheKey(chatEmoji.getEmojiType()));
        }
        return chatEmojiMapper.deleteChatEmojiByEmojiIds(emojiIds);
    }

    /**
     * 删除表情包信息
     * 
     * @param emojiId 表情包主键
     * @return 结果
     */
    @Override
    public int deleteChatEmojiByEmojiId(Long emojiId)
    {
        ChatEmoji chatEmoji = selectChatEmojiByEmojiId(emojiId);
        redisCache.deleteObject(getCacheKey(chatEmoji.getEmojiType()));
        return chatEmojiMapper.deleteChatEmojiByEmojiId(emojiId);
    }

    @Override
    public String uploadEmoji(MultipartFile file) {
        return  minioUtil.upload(file, false);
    }

    /**
     * 前端查询表情包列表.
     *
     * @param chatEmoji 表情包
     * @return 表情包列表
     */
    @Override
    public List<ChatEmoji> getChatEmojiList(ChatEmoji chatEmoji)
    {
        List<ChatEmoji> list = redisCache.getCacheObject(getCacheKey(chatEmoji.getEmojiType()));
        if(list == null || list.size() == 0){
            chatEmoji.setStatus("0");
            list = chatEmojiMapper.selectChatEmojiList(chatEmoji);
            if(list != null){
                redisCache.setCacheObject(getCacheKey(chatEmoji.getEmojiType()), list,3600 * 24, TimeUnit.SECONDS);
            }
        }
        return list;
    }
}

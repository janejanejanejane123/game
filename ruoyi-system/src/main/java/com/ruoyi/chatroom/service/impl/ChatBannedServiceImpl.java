package com.ruoyi.chatroom.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.chatroom.mapper.ChatBannedMapper;
import com.ruoyi.chatroom.domain.ChatBanned;
import com.ruoyi.chatroom.service.IChatBannedService;

import javax.annotation.Resource;

/**
 * 禁言管理Service业务层处理
 * 
 * @author nn
 * @date 2022-07-10
 */
@Service
public class ChatBannedServiceImpl implements IChatBannedService
{
    @Autowired
    private ChatBannedMapper chatBannedMapper;

    @Resource
    private RedisCache redisCache;

    /**
     * 设置cache key
     *
     * @return 缓存键key
     */
    private String getCacheKey(Long userId)
    {
        return CacheKeyConstants.T_CHAT_BANNED_KEY + userId;
    }

    /**
     * 查询禁言管理
     * 
     * @param id 禁言管理主键
     * @return 禁言管理
     */
    @Override
    public ChatBanned selectChatBannedById(Long id)
    {
        return chatBannedMapper.selectChatBannedById(id);
    }

    /**
     * 查询禁言管理列表
     * 
     * @param chatBanned 禁言管理
     * @return 禁言管理
     */
    @Override
    public List<ChatBanned> selectChatBannedList(ChatBanned chatBanned)
    {
        return chatBannedMapper.selectChatBannedList(chatBanned);
    }

    /**
     * 新增禁言管理
     * 
     * @param chatBanned 禁言管理
     * @return 结果
     */
    @Override
    public int insertChatBanned(ChatBanned chatBanned)
    {
        int row = chatBannedMapper.insertChatBanned(chatBanned);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(chatBanned.getUserId()), chatBanned.getUserId(),3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 修改禁言管理
     * 
     * @param chatBanned 禁言管理
     * @return 结果
     */
    @Override
    public int updateChatBanned(ChatBanned chatBanned)
    {
        //先把修改前的缓存删除.
        redisCache.deleteObject(getCacheKey(chatBanned.getUserId()));
        int row = chatBannedMapper.updateChatBanned(chatBanned);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(chatBanned.getUserId()), chatBanned.getUserId(),3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 批量删除禁言管理
     * 
     * @param ids 需要删除的禁言管理主键
     * @return 结果
     */
    @Override
    public int deleteChatBannedByIds(Long[] ids)
    {
        for (Long id : ids) {
            ChatBanned chatBanned = selectChatBannedById(id);
            redisCache.deleteObject(getCacheKey(chatBanned.getUserId()));
        }
        return chatBannedMapper.deleteChatBannedByIds(ids);
    }

    /**
     * 删除禁言管理信息
     * 
     * @param id 禁言管理主键
     * @return 结果
     */
    @Override
    public int deleteChatBannedById(Long id)
    {
        ChatBanned chatBanned = selectChatBannedById(id);
        redisCache.deleteObject(getCacheKey(chatBanned.getUserId()));
        return chatBannedMapper.deleteChatBannedById(id);
    }

    /**
     * 校验禁言是否唯一
     * @param chatBanned 检测的对象
     * @return
     */
    @Override
    public String checkChatBannedUnique(ChatBanned chatBanned) {
        Long id = StringUtils.isNull(chatBanned.getId()) ? -1L : chatBanned.getId();
        ChatBanned unique = chatBannedMapper.checkChatBannedUnique(chatBanned);
        if (StringUtils.isNotNull(unique) && unique.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }
}

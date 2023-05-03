package com.ruoyi.chatroom.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.chatroom.mapper.ChatSpeakBlacklistMapper;
import com.ruoyi.chatroom.domain.ChatSpeakBlacklist;
import com.ruoyi.chatroom.service.IChatSpeakBlacklistService;

import javax.annotation.Resource;

/**
 * 聊天室发言白名单Service业务层处理
 * 
 * @author nn
 * @date 2022-08-28
 */
@Service
public class ChatSpeakBlacklistServiceImpl implements IChatSpeakBlacklistService 
{
    @Autowired
    private ChatSpeakBlacklistMapper chatSpeakBlacklistMapper;

    @Resource
    private RedisCache redisCache;

    /**
     * 设置cache key
     *
     * @return 缓存键key
     */
    private String getCacheKey(Long userId)
    {
        return CacheKeyConstants.T_CHAT_SPEAK_BLACKLIST_KEY + userId;
    }

    /**
     * 查询聊天室发言白名单
     * 
     * @param id 聊天室发言白名单主键
     * @return 聊天室发言白名单
     */
    @Override
    public ChatSpeakBlacklist selectChatSpeakBlacklistById(Long id)
    {
        return chatSpeakBlacklistMapper.selectChatSpeakBlacklistById(id);
    }

    /**
     * 查询聊天室发言白名单列表
     * 
     * @param chatSpeakBlacklist 聊天室发言白名单
     * @return 聊天室发言白名单
     */
    @Override
    public List<ChatSpeakBlacklist> selectChatSpeakBlacklistList(ChatSpeakBlacklist chatSpeakBlacklist)
    {
        return chatSpeakBlacklistMapper.selectChatSpeakBlacklistList(chatSpeakBlacklist);
    }

    /**
     * 新增聊天室发言白名单
     * 
     * @param chatSpeakBlacklist 聊天室发言白名单
     * @return 结果
     */
    @Override
    public int insertChatSpeakBlacklist(ChatSpeakBlacklist chatSpeakBlacklist)
    {
        int row = chatSpeakBlacklistMapper.insertChatSpeakBlacklist(chatSpeakBlacklist);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(chatSpeakBlacklist.getUserId()), chatSpeakBlacklist,3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 修改聊天室发言白名单
     * 
     * @param chatSpeakBlacklist 聊天室发言白名单
     * @return 结果
     */
    @Override
    public int updateChatSpeakBlacklist(ChatSpeakBlacklist chatSpeakBlacklist)
    {
        //先把修改前的缓存删除.
        redisCache.deleteObject(getCacheKey(chatSpeakBlacklist.getUserId()));
        int row = chatSpeakBlacklistMapper.updateChatSpeakBlacklist(chatSpeakBlacklist);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(chatSpeakBlacklist.getUserId()), chatSpeakBlacklist,3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 批量删除聊天室发言白名单
     * 
     * @param ids 需要删除的聊天室发言白名单主键
     * @return 结果
     */
    @Override
    public int deleteChatSpeakBlacklistByIds(Long[] ids) {
        for (Long id : ids) {
            ChatSpeakBlacklist chatSpeakBlacklist = selectChatSpeakBlacklistById(id);
            redisCache.deleteObject(getCacheKey(chatSpeakBlacklist.getUserId()));
        }
        return chatSpeakBlacklistMapper.deleteChatSpeakBlacklistByIds(ids);
    }

    /**
     * 删除聊天室发言白名单信息
     * 
     * @param id 聊天室发言白名单主键
     * @return 结果
     */
    @Override
    public int deleteChatSpeakBlacklistById(Long id) {
        ChatSpeakBlacklist chatSpeakBlacklist = selectChatSpeakBlacklistById(id);
        redisCache.deleteObject(getCacheKey(chatSpeakBlacklist.getUserId()));
        return chatSpeakBlacklistMapper.deleteChatSpeakBlacklistById(id);
    }

    /**
     * 修改状态.
     *
     * @param chatSpeakBlacklist 信息
     * @return 结果
     */
    @Override
    public int changeStatus(ChatSpeakBlacklist chatSpeakBlacklist) {
        redisCache.deleteObject(getCacheKey(chatSpeakBlacklist.getUserId()));
        int row = chatSpeakBlacklistMapper.changeStatus(chatSpeakBlacklist);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(chatSpeakBlacklist.getUserId()), chatSpeakBlacklist,3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 校验是否唯一
     * @param chatSpeakBlacklist 检测的对象
     * @return
     */
    @Override
    public String checkSpeakBlacklistUnique(ChatSpeakBlacklist chatSpeakBlacklist) {
        Long id = StringUtils.isNull(chatSpeakBlacklist.getId()) ? -1L : chatSpeakBlacklist.getId();
        ChatSpeakBlacklist unique = chatSpeakBlacklistMapper.checkSpeakBlacklistUnique(chatSpeakBlacklist);
        if (StringUtils.isNotNull(unique) && unique.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 是否是发言白名单中.
     * @param userId 用户ID
     * @return
     */
    @Override
    public boolean isSpeakBlacklist(Long userId) {
        boolean isSpeakBlacklist = false;
        ChatSpeakBlacklist chatSpeakBlacklist = redisCache.getCacheObject(getCacheKey(userId));
        if(chatSpeakBlacklist != null){
            if("0".equals(chatSpeakBlacklist.getStatus())){
                isSpeakBlacklist = true;
            }
        }else {
            chatSpeakBlacklist = chatSpeakBlacklistMapper.getSpeakBlacklistByUserId(userId);
            if("0".equals(chatSpeakBlacklist.getStatus())){
                isSpeakBlacklist = true;
            }
        }

        return isSpeakBlacklist;
    }
}

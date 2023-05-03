package com.ruoyi.chatroom.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.chatroom.mapper.ChatInterestLanguageMapper;
import com.ruoyi.chatroom.domain.ChatInterestLanguage;
import com.ruoyi.chatroom.service.IChatInterestLanguageService;

import javax.annotation.Resource;

/**
 * 趣语Service业务层处理
 * 
 * @author nn
 * @date 2022-07-11
 */
@Service
public class ChatInterestLanguageServiceImpl implements IChatInterestLanguageService 
{
    @Autowired
    private ChatInterestLanguageMapper chatInterestLanguageMapper;

    @Resource
    private RedisCache redisCache;

    /**
     * 设置cache key
     *
     * @return 缓存键key
     */
    private String getCacheKey(String interestType)
    {
        return CacheKeyConstants.T_CHAT_INTEREST_LANGUAGE_KEY + interestType;
    }

    /**
     * 查询趣语
     * 
     * @param interestId 趣语主键
     * @return 趣语
     */
    @Override
    public ChatInterestLanguage selectChatInterestLanguageByInterestId(Long interestId)
    {
        return chatInterestLanguageMapper.selectChatInterestLanguageByInterestId(interestId);
    }

    /**
     * 查询趣语列表
     * 
     * @param chatInterestLanguage 趣语
     * @return 趣语
     */
    @Override
    public List<ChatInterestLanguage> selectChatInterestLanguageList(ChatInterestLanguage chatInterestLanguage)
    {
        return chatInterestLanguageMapper.selectChatInterestLanguageList(chatInterestLanguage);
    }

    /**
     * 新增趣语
     * 
     * @param chatInterestLanguage 趣语
     * @return 结果
     */
    @Override
    public int insertChatInterestLanguage(ChatInterestLanguage chatInterestLanguage)
    {
        redisCache.deleteObject(getCacheKey(chatInterestLanguage.getInterestType()));
        chatInterestLanguage.setCreateTime(DateUtils.getNowDate());
        return chatInterestLanguageMapper.insertChatInterestLanguage(chatInterestLanguage);
    }

    /**
     * 修改趣语
     * 
     * @param chatInterestLanguage 趣语
     * @return 结果
     */
    @Override
    public int updateChatInterestLanguage(ChatInterestLanguage chatInterestLanguage)
    {
        redisCache.deleteObject(getCacheKey(chatInterestLanguage.getInterestType()));
        return chatInterestLanguageMapper.updateChatInterestLanguage(chatInterestLanguage);
    }

    /**
     * 批量删除趣语
     * 
     * @param interestIds 需要删除的趣语主键
     * @return 结果
     */
    @Override
    public int deleteChatInterestLanguageByInterestIds(Long[] interestIds)
    {
        for (Long id : interestIds) {
            ChatInterestLanguage chatInterestLanguage = selectChatInterestLanguageByInterestId(id);
            redisCache.deleteObject(getCacheKey(chatInterestLanguage.getInterestType()));
        }
        return chatInterestLanguageMapper.deleteChatInterestLanguageByInterestIds(interestIds);
    }

    /**
     * 删除趣语信息
     * 
     * @param interestId 趣语主键
     * @return 结果
     */
    @Override
    public int deleteChatInterestLanguageByInterestId(Long interestId)
    {
        ChatInterestLanguage chatInterestLanguage = selectChatInterestLanguageByInterestId(interestId);
        redisCache.deleteObject(getCacheKey(chatInterestLanguage.getInterestType()));
        return chatInterestLanguageMapper.deleteChatInterestLanguageByInterestId(interestId);
    }

    /**
     * 校验禁言是否唯一
     * @param chatInterestLanguage 检测的对象
     * @return
     */
    @Override
    public String checkInterestLanguageUnique(ChatInterestLanguage chatInterestLanguage) {
        Long id = StringUtils.isNull(chatInterestLanguage.getInterestId()) ? -1L : chatInterestLanguage.getInterestId();
        ChatInterestLanguage unique = chatInterestLanguageMapper.checkInterestLanguageUnique(chatInterestLanguage);
        if (StringUtils.isNotNull(unique) && unique.getInterestId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 前端查询趣语列表
     *
     * @param chatInterestLanguage 趣语
     * @return 趣语列表
     */
    @Override
    public List<ChatInterestLanguage> getChatInterestLanguageList(ChatInterestLanguage chatInterestLanguage)
    {
        List<ChatInterestLanguage> list = redisCache.getCacheObject(getCacheKey(chatInterestLanguage.getInterestType()));
        if(list == null || list.size() == 0){
            chatInterestLanguage.setStatus("0");
            list = chatInterestLanguageMapper.selectChatInterestLanguageList(chatInterestLanguage);
            if(list != null){
                redisCache.setCacheObject(getCacheKey(chatInterestLanguage.getInterestType()), list,3600 * 24, TimeUnit.SECONDS);
            }
        }
        return list;
    }
}

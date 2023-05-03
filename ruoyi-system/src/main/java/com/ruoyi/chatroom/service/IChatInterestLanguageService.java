package com.ruoyi.chatroom.service;

import java.util.List;

import com.ruoyi.chatroom.domain.ChatInterestLanguage;

/**
 * 趣语Service接口
 * 
 * @author nn
 * @date 2022-07-11
 */
public interface IChatInterestLanguageService 
{
    /**
     * 查询趣语
     * 
     * @param interestId 趣语主键
     * @return 趣语
     */
    public ChatInterestLanguage selectChatInterestLanguageByInterestId(Long interestId);

    /**
     * 查询趣语列表
     * 
     * @param chatInterestLanguage 趣语
     * @return 趣语集合
     */
    public List<ChatInterestLanguage> selectChatInterestLanguageList(ChatInterestLanguage chatInterestLanguage);

    /**
     * 新增趣语
     * 
     * @param chatInterestLanguage 趣语
     * @return 结果
     */
    public int insertChatInterestLanguage(ChatInterestLanguage chatInterestLanguage);

    /**
     * 修改趣语
     * 
     * @param chatInterestLanguage 趣语
     * @return 结果
     */
    public int updateChatInterestLanguage(ChatInterestLanguage chatInterestLanguage);

    /**
     * 批量删除趣语
     * 
     * @param interestIds 需要删除的趣语主键集合
     * @return 结果
     */
    public int deleteChatInterestLanguageByInterestIds(Long[] interestIds);

    /**
     * 删除趣语信息
     * 
     * @param interestId 趣语主键
     * @return 结果
     */
    public int deleteChatInterestLanguageByInterestId(Long interestId);

    /**
     * 校验趣语是否唯一
     *
     * @param chatInterestLanguage 检测的对象
     * @return 结果
     */
    public String checkInterestLanguageUnique(ChatInterestLanguage chatInterestLanguage);

    /**
     * 前端查询趣语列表
     *
     * @param chatInterestLanguage 趣语
     * @return 趣语列表
     */
    public List<ChatInterestLanguage> getChatInterestLanguageList(ChatInterestLanguage chatInterestLanguage);
}

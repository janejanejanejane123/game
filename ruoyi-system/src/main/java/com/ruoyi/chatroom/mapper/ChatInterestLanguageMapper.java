package com.ruoyi.chatroom.mapper;

import java.util.List;

import com.ruoyi.chatroom.domain.ChatInterestLanguage;

/**
 * 趣语Mapper接口
 * 
 * @author nn
 * @date 2022-07-11
 */
public interface ChatInterestLanguageMapper 
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
     * 删除趣语
     * 
     * @param interestId 趣语主键
     * @return 结果
     */
    public int deleteChatInterestLanguageByInterestId(Long interestId);

    /**
     * 批量删除趣语
     * 
     * @param interestIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteChatInterestLanguageByInterestIds(Long[] interestIds);

    /**
     * 校验趣语是否唯一
     *
     * @param chatInterestLanguage 检测的对象
     * @return 结果
     */
    public ChatInterestLanguage checkInterestLanguageUnique(ChatInterestLanguage chatInterestLanguage);
}

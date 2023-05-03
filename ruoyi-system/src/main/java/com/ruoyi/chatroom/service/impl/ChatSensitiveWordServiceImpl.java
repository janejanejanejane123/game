package com.ruoyi.chatroom.service.impl;

import java.util.List;

import com.ruoyi.chatroom.domain.ChatServeCustomer;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.chatroom.mapper.ChatSensitiveWordMapper;
import com.ruoyi.chatroom.domain.ChatSensitiveWord;
import com.ruoyi.chatroom.service.IChatSensitiveWordService;

/**
 * 敏感词库Service业务层处理
 * 
 * @author nn
 * @date 2022-07-11
 */
@Service
public class ChatSensitiveWordServiceImpl implements IChatSensitiveWordService 
{
    @Autowired
    private ChatSensitiveWordMapper chatSensitiveWordMapper;

    /**
     * 查询敏感词库
     * 
     * @param id 敏感词库主键
     * @return 敏感词库
     */
    @Override
    public ChatSensitiveWord selectChatSensitiveWordById(Long id)
    {
        return chatSensitiveWordMapper.selectChatSensitiveWordById(id);
    }

    /**
     * 查询敏感词库列表
     * 
     * @param chatSensitiveWord 敏感词库
     * @return 敏感词库
     */
    @Override
    public List<ChatSensitiveWord> selectChatSensitiveWordList(ChatSensitiveWord chatSensitiveWord)
    {
        return chatSensitiveWordMapper.selectChatSensitiveWordList(chatSensitiveWord);
    }

    @Override
    public int batchInsertRecords(List<ChatSensitiveWord> sensitiveWords) {
        chatSensitiveWordMapper.batchInsertChatSensitiveWord(sensitiveWords);
        return 0;
    }

    /**
     * 新增敏感词库
     * 
     * @param chatSensitiveWord 敏感词库
     * @return 结果
     */
    @Override
    public int insertChatSensitiveWord(ChatSensitiveWord chatSensitiveWord)
    {
        chatSensitiveWord.setCreateTime(DateUtils.getNowDate());
        return chatSensitiveWordMapper.insertChatSensitiveWord(chatSensitiveWord);
    }

    /**
     * 修改敏感词库
     * 
     * @param chatSensitiveWord 敏感词库
     * @return 结果
     */
    @Override
    public int updateChatSensitiveWord(ChatSensitiveWord chatSensitiveWord)
    {
        return chatSensitiveWordMapper.updateChatSensitiveWord(chatSensitiveWord);
    }

    /**
     * 批量删除敏感词库
     * 
     * @param ids 需要删除的敏感词库主键
     * @return 结果
     */
    @Override
    public int deleteChatSensitiveWordByIds(Long[] ids)
    {
        return chatSensitiveWordMapper.deleteChatSensitiveWordByIds(ids);
    }

    /**
     * 删除敏感词库信息
     * 
     * @param id 敏感词库主键
     * @return 结果
     */
    @Override
    public int deleteChatSensitiveWordById(Long id)
    {
        return chatSensitiveWordMapper.deleteChatSensitiveWordById(id);
    }

    /**
     * 校验敏感词是否唯一
     * @param chatSensitiveWord 检测的对象
     * @return
     */
    @Override
    public String checkSensitiveWordUnique(ChatSensitiveWord chatSensitiveWord) {
        Long id = StringUtils.isNull(chatSensitiveWord.getId()) ? -1L : chatSensitiveWord.getId();
        ChatSensitiveWord unique = chatSensitiveWordMapper.checkSensitiveWordUnique(chatSensitiveWord);
        if (StringUtils.isNotNull(unique) && unique.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int count(ChatSensitiveWord chatSensitiveWord) {
        return chatSensitiveWordMapper.count(chatSensitiveWord);
    }
}

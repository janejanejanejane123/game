package com.ruoyi.chatroom.service;

import java.util.List;
import com.ruoyi.chatroom.domain.ChatSensitiveWord;
import com.ruoyi.chatroom.domain.ChatServeCustomer;

/**
 * 敏感词库Service接口
 * 
 * @author nn
 * @date 2022-07-11
 */
public interface IChatSensitiveWordService 
{
    /**
     * 查询敏感词库
     * 
     * @param id 敏感词库主键
     * @return 敏感词库
     */
    public ChatSensitiveWord selectChatSensitiveWordById(Long id);

    /**
     * 查询敏感词库列表
     * 
     * @param chatSensitiveWord 敏感词库
     * @return 敏感词库集合
     */
    public List<ChatSensitiveWord> selectChatSensitiveWordList(ChatSensitiveWord chatSensitiveWord);



    public int batchInsertRecords(List<ChatSensitiveWord> sensitiveWords);
    /**
     * 新增敏感词库
     * 
     * @param chatSensitiveWord 敏感词库
     * @return 结果
     */
    public int insertChatSensitiveWord(ChatSensitiveWord chatSensitiveWord);

    /**
     * 修改敏感词库
     * 
     * @param chatSensitiveWord 敏感词库
     * @return 结果
     */
    public int updateChatSensitiveWord(ChatSensitiveWord chatSensitiveWord);

    /**
     * 批量删除敏感词库
     * 
     * @param ids 需要删除的敏感词库主键集合
     * @return 结果
     */
    public int deleteChatSensitiveWordByIds(Long[] ids);

    /**
     * 删除敏感词库信息
     * 
     * @param id 敏感词库主键
     * @return 结果
     */
    public int deleteChatSensitiveWordById(Long id);

    /**
     * 校验敏感词是否唯一
     *
     * @param chatSensitiveWord 检测的对象
     * @return 结果
     */
    public String checkSensitiveWordUnique(ChatSensitiveWord chatSensitiveWord);

    int count(ChatSensitiveWord chatSensitiveWord);

}

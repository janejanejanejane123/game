package com.ruoyi.chatroom.mapper;

import java.util.List;
import com.ruoyi.chatroom.domain.ChatSensitiveWord;
import com.ruoyi.chatroom.domain.ChatServeCustomer;
import org.apache.ibatis.annotations.Param;

/**
 * 敏感词库Mapper接口
 * 
 * @author nn
 * @date 2022-07-11
 */
public interface ChatSensitiveWordMapper 
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

    /**
     * 新增敏感词库
     * 
     * @param chatSensitiveWord 敏感词库
     * @return 结果
     */
    public int insertChatSensitiveWord(ChatSensitiveWord chatSensitiveWord);


    /**
     * 批量插入
     * @param chatSensitiveWords
     * @return
     */
    public void batchInsertChatSensitiveWord(@Param("list") List<ChatSensitiveWord> chatSensitiveWords);

    /**
     * 修改敏感词库
     * 
     * @param chatSensitiveWord 敏感词库
     * @return 结果
     */
    public int updateChatSensitiveWord(ChatSensitiveWord chatSensitiveWord);

    /**
     * 删除敏感词库
     * 
     * @param id 敏感词库主键
     * @return 结果
     */
    public int deleteChatSensitiveWordById(Long id);

    /**
     * 批量删除敏感词库
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteChatSensitiveWordByIds(Long[] ids);

    /**
     * 校验客服是否唯一
     *
     * @param chatSensitiveWord 检测的对象
     * @return 结果
     */
    public ChatSensitiveWord checkSensitiveWordUnique(ChatSensitiveWord chatSensitiveWord);

    int count(ChatSensitiveWord chatSensitiveWord);

}

package com.ruoyi.chatroom.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.chatroom.domain.ChatSettings;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 聊天室配置Service接口
 * 
 * @author nn
 * @date 2022-07-16
 */
public interface IChatSettingsService 
{
    /**
     * 查询聊天室配置
     * 
     * @param id 聊天室配置主键
     * @return 聊天室配置
     */
    public ChatSettings selectChatSettingsById(Long id);

    /**
     * 查询聊天室配置列表
     * 
     * @param chatSettings 聊天室配置
     * @return 聊天室配置集合
     */
    public List<ChatSettings> selectChatSettingsList(ChatSettings chatSettings);

    /**
     * 新增聊天室配置
     * 
     * @param chatSettings 聊天室配置
     * @return 结果
     */
    public int insertChatSettings(ChatSettings chatSettings);

    /**
     * 修改聊天室配置
     * 
     * @param chatSettings 聊天室配置
     * @return 结果
     */
    public int updateChatSettings(ChatSettings chatSettings);

    /**
     * 批量删除聊天室配置
     * 
     * @param ids 需要删除的聊天室配置主键集合
     * @return 结果
     */
    public int deleteChatSettingsByIds(Long[] ids);

    /**
     * 删除聊天室配置信息
     * 
     * @param id 聊天室配置主键
     * @return 结果
     */
    public int deleteChatSettingsById(Long id);

    AjaxResult query(Map<String, Object> params);

    /**
     * 保存聊天室配置
     *
     * @param chatSettings 聊天室配置
     * @return 结果
     */
    public AjaxResult saveSettings(ChatSettings chatSettings);

    /**
     * 根据key查询配置
     * @param key
     * @return
     */
    public ChatSettings getSettingByKey(String key);
}

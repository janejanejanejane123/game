package com.ruoyi.chatroom.service.impl;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.constant.TopicConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.ChatRoomConfigEnum;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.member.domain.UserMailBox;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import com.ruoyi.chatroom.mapper.ChatSettingsMapper;
import com.ruoyi.chatroom.domain.ChatSettings;
import com.ruoyi.chatroom.service.IChatSettingsService;

import javax.annotation.Resource;

/**
 * 聊天室配置Service业务层处理
 * 
 * @author nn
 * @date 2022-07-16
 */
@Service
public class ChatSettingsServiceImpl implements IChatSettingsService {

    private static Logger logger = LoggerFactory.getLogger(ChatSettingsServiceImpl.class);

    @Autowired
    private ChatSettingsMapper chatSettingsMapper;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    @Resource
    private RedisCache redisCache;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 设置cache key
     *
     * @return 缓存键key
     */
    private String getCacheKey(String key)
    {
        return CacheKeyConstants.T_CHAT_SETTINGS_KEY + key;
    }

    /**
     * 查询聊天室配置
     * 
     * @param id 聊天室配置主键
     * @return 聊天室配置
     */
    @Override
    public ChatSettings selectChatSettingsById(Long id)
    {
        return chatSettingsMapper.selectChatSettingsById(id);
    }

    /**
     * 查询聊天室配置列表
     * 
     * @param chatSettings 聊天室配置
     * @return 聊天室配置
     */
    @Override
    public List<ChatSettings> selectChatSettingsList(ChatSettings chatSettings)
    {
        return chatSettingsMapper.selectChatSettingsList(chatSettings);
    }

    /**
     * 新增聊天室配置
     * 
     * @param chatSettings 聊天室配置
     * @return 结果
     */
    @Override
    public int insertChatSettings(ChatSettings chatSettings)
    {
        chatSettings.setCreateTime(DateUtils.getNowDate());
        return chatSettingsMapper.insertChatSettings(chatSettings);
    }

    /**
     * 修改聊天室配置
     * 
     * @param chatSettings 聊天室配置
     * @return 结果
     */
    @Override
    public int updateChatSettings(ChatSettings chatSettings)
    {
        redisCache.deleteObject(getCacheKey(chatSettings.getConfigKey()));
        chatSettings.setUpdateTime(DateUtils.getNowDate());
        return chatSettingsMapper.updateChatSettings(chatSettings);
    }

    /**
     * 批量删除聊天室配置
     * 
     * @param ids 需要删除的聊天室配置主键
     * @return 结果
     */
    @Override
    public int deleteChatSettingsByIds(Long[] ids)
    {
        return chatSettingsMapper.deleteChatSettingsByIds(ids);
    }

    /**
     * 删除聊天室配置信息
     * 
     * @param id 聊天室配置主键
     * @return 结果
     */
    @Override
    public int deleteChatSettingsById(Long id)
    {
        ChatSettings chatSettings = chatSettingsMapper.selectChatSettingsById(id);
        redisCache.deleteObject(getCacheKey(chatSettings.getConfigKey()));
        return chatSettingsMapper.deleteChatSettingsById(id);
    }

    /**
     * 后台查询列表.
     * @param params
     * @return
     */
    @Override
    public AjaxResult query(Map<String, Object> params) {
        List<ChatSettings> list = chatSettingsMapper.selectChatSettingsList(new ChatSettings());
        ChatSettings chatSetting = null;

        List<String> keys = new ArrayList();
        for (ChatSettings setting : list) {
            keys.add(setting.getConfigKey());
        }

        ChatRoomConfigEnum[] values = ChatRoomConfigEnum.values();

        //数据库没有，从枚举获取默认.
        for (ChatRoomConfigEnum value : values) {
            if (!keys.contains(value.getKey())) {
                chatSetting = new ChatSettings();
                chatSetting.setConfigKey(value.getKey());
                chatSetting.setConfigValue(value.getValue());
                chatSetting.setConfigType(value.getType());
                chatSetting.setConfigComment(value.getComment());
                list.add(chatSetting);
            }
        }

        Map infos = new HashMap(3);
        Map jsons = new HashMap<>(16);
        Map buttons = new HashMap<>(16);
        Map inputs = new HashMap<>(16);
        for (int i = 0; i < list.size(); i++) {
            if ("json".equals(list.get(i).getConfigType())) {
                if (StringUtils.isNotBlank(list.get(i).getConfigValue())) {
                    JSONObject jsonObject = JSON.parseObject(list.get(i).getConfigValue());
                    list.get(i).setJsonObj(jsonObject);
                }
                jsons.put(list.get(i).getConfigKey(), list.get(i));
            } else if ("button".equals(list.get(i).getConfigType())) {
                if ("false".equals(list.get(i).getConfigValue())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("switch", false);
                    list.get(i).setJsonObj(jsonObject);
                }
                if ("true".equals(list.get(i).getConfigValue())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("switch", true);
                    list.get(i).setJsonObj(jsonObject);
                }
                buttons.put(list.get(i).getConfigKey(), list.get(i));
            } else if ("input".equals(list.get(i).getConfigType()) || "select".equals(list.get(i).getConfigType())) {
                inputs.put(list.get(i).getConfigKey(), list.get(i));
            }
        }
        infos.put("jsons", jsons);
        infos.put("buttons", buttons);
        infos.put("inputs", inputs);
        return AjaxResult.success(infos);
    }

    /**
     * 后台保存.
     * @param chatSettings 聊天室配置
     * @return
     */
    @Override
    public AjaxResult saveSettings(ChatSettings chatSettings) {
        if ("json".equals(chatSettings.getConfigType())) {
            chatSettings.setConfigValue(chatSettings.getJsonObj().toString());
        }
        if ("button".equals(chatSettings.getConfigType())) {
            JSONObject jsonObj = chatSettings.getJsonObj();
            Object aSwitch = jsonObj.get("switch");
            if (aSwitch != null) {
                if ((Boolean) aSwitch) {
                    chatSettings.setConfigValue("true");
                } else {
                    chatSettings.setConfigValue("false");
                }
            }
        }

        if (null == chatSettings.getId()) {
            //新增
            chatSettings.setId(snowflakeIdUtils.nextId());
            chatSettings.setCreateBy(SecurityUtils.getUsername());
            chatSettings.setCreateTime(new Date());
            chatSettings.setConfigName(chatSettings.getConfigComment());
            chatSettings.setRemark("新增");
            chatSettingsMapper.insertChatSettings(chatSettings);
        } else {
            redisCache.deleteObject(getCacheKey(chatSettings.getConfigKey()));
            //修改
            chatSettings.setUpdateBy(SecurityUtils.getUsername());
            chatSettings.setUpdateTime(new Date());
            chatSettings.setRemark("修改");
            chatSettingsMapper.updateChatSettings(chatSettings);
        }
        //聊天室开关.
        if(ChatRoomConfigEnum.CHAT_SWITCH.getKey().equals(chatSettings.getConfigKey())){
            //发送消息给用户.
            UserMailBox mailBox = new UserMailBox();
            mailBox.setContent(chatSettings.getConfigValue());
            mailBox.setUserIds(null);
            mailBox.setUserType(0);
            mailBox.setTopic(TopicConstants.CHAT_SWITCH);
            MessageBuilder builder = MessageBuilder.withPayload(mailBox);
            SendResult result = rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder.build());
            if (!SendStatus.SEND_OK.equals(result.getSendStatus())) {
                logger.error("聊天室开关-消息发送失败!");
            }
        }
        //聊天室大厅开关.
        if(ChatRoomConfigEnum.CHAT_HALL_SWITCH.getKey().equals(chatSettings.getConfigKey())){
            //发送消息给用户.
            UserMailBox mailBox = new UserMailBox();
            mailBox.setContent(chatSettings.getConfigValue());
            mailBox.setUserIds(null);
            mailBox.setUserType(0);
            mailBox.setTopic(TopicConstants.CHAT_HALL_SWITCH);
            MessageBuilder builder = MessageBuilder.withPayload(mailBox);
            SendResult result = rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder.build());
            if (!SendStatus.SEND_OK.equals(result.getSendStatus())) {
                logger.error("聊天室大厅开关-消息发送失败!");
            }
        }
        redisCache.setCacheObject(getCacheKey(chatSettings.getConfigKey()),chatSettings,3600 * 24, TimeUnit.SECONDS);
        return AjaxResult.success("保存成功");
    }

    /**
     * 根据key查询配置
     * @param key
     * @return
     */
    @Override
    public ChatSettings getSettingByKey(String key) {
        ChatSettings chatSettings = redisCache.getCacheObject(getCacheKey(key));
        if(chatSettings == null){
            chatSettings = chatSettingsMapper.getSettingByKey(key);
            if (chatSettings == null) {
                ChatRoomConfigEnum configKeyEnum = ChatRoomConfigEnum.valueOf(key);
                chatSettings = new ChatSettings();
                chatSettings.setConfigKey(configKeyEnum.getKey());
                chatSettings.setConfigType(configKeyEnum.getType());
                chatSettings.setConfigValue(configKeyEnum.getValue());
                chatSettings.setConfigComment(configKeyEnum.getComment());
                chatSettings.setConfigName(configKeyEnum.getComment());
            }
            redisCache.setCacheObject(getCacheKey(chatSettings.getConfigKey()),chatSettings,3600 * 24, TimeUnit.SECONDS);
        }
        return chatSettings;
    }
}

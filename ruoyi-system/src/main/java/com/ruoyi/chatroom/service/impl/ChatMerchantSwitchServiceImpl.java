package com.ruoyi.chatroom.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.chatroom.mapper.ChatMerchantSwitchMapper;
import com.ruoyi.chatroom.domain.ChatMerchantSwitch;
import com.ruoyi.chatroom.service.IChatMerchantSwitchService;

import javax.annotation.Resource;

/**
 * 商户开关Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-12-18
 */
@Service
public class ChatMerchantSwitchServiceImpl implements IChatMerchantSwitchService 
{
    @Autowired
    private ChatMerchantSwitchMapper chatMerchantSwitchMapper;

    @Resource
    private RedisCache redisCache;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 设置cache key
     *
     * @return 缓存键key
     */
    private String getCacheKey(Long userId)
    {
        return CacheKeyConstants.T_CHAT_MERCHANT_SWITCH + userId;
    }

    /**
     * 查询商户开关
     * 
     * @param id 商户开关主键
     * @return 商户开关
     */
    @Override
    public ChatMerchantSwitch selectChatMerchantSwitchById(Long id)
    {
        return chatMerchantSwitchMapper.selectChatMerchantSwitchById(id);
    }

    /**
     * 查询商户开关列表
     * 
     * @param chatMerchantSwitch 商户开关
     * @return 商户开关
     */
    @Override
    public List<ChatMerchantSwitch> selectChatMerchantSwitchList(ChatMerchantSwitch chatMerchantSwitch)
    {
        return chatMerchantSwitchMapper.selectChatMerchantSwitchList(chatMerchantSwitch);
    }

    /**
     * 新增商户开关
     * 
     * @param chatMerchantSwitch 商户开关
     * @return 结果
     */
    @Override
    public int insertChatMerchantSwitch(ChatMerchantSwitch chatMerchantSwitch)
    {
        int row = chatMerchantSwitchMapper.insertChatMerchantSwitch(chatMerchantSwitch);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(chatMerchantSwitch.getUserId()), chatMerchantSwitch,3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 修改商户开关
     * 
     * @param chatMerchantSwitch 商户开关
     * @return 结果
     */
    @Override
    public int updateChatMerchantSwitch(ChatMerchantSwitch chatMerchantSwitch)
    {
        //先把修改前的缓存删除.
        redisCache.deleteObject(getCacheKey(chatMerchantSwitch.getUserId()));
        int row = chatMerchantSwitchMapper.updateChatMerchantSwitch(chatMerchantSwitch);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(chatMerchantSwitch.getUserId()), chatMerchantSwitch,3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 批量删除商户开关
     * 
     * @param ids 需要删除的商户开关主键
     * @return 结果
     */
    @Override
    public int deleteChatMerchantSwitchByIds(Long[] ids)
    {
        for (Long id : ids) {
            ChatMerchantSwitch chatMerchantSwitch = selectChatMerchantSwitchById(id);
            redisCache.deleteObject(getCacheKey(chatMerchantSwitch.getUserId()));
        }
        return chatMerchantSwitchMapper.deleteChatMerchantSwitchByIds(ids);
    }

    /**
     * 删除商户开关信息
     * 
     * @param id 商户开关主键
     * @return 结果
     */
    @Override
    public int deleteChatMerchantSwitchById(Long id)
    {
        ChatMerchantSwitch chatMerchantSwitch = selectChatMerchantSwitchById(id);
        redisCache.deleteObject(getCacheKey(chatMerchantSwitch.getUserId()));
        return chatMerchantSwitchMapper.deleteChatMerchantSwitchById(id);
    }

    /**
     * 修改状态.
     *
     * @param chatMerchantSwitch 信息
     * @return 结果
     */
    @Override
    public int changeStatus(ChatMerchantSwitch chatMerchantSwitch) {
        redisCache.deleteObject(getCacheKey(chatMerchantSwitch.getUserId()));
        int row = chatMerchantSwitchMapper.changeStatus(chatMerchantSwitch);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(chatMerchantSwitch.getUserId()), chatMerchantSwitch,3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 校验是否唯一
     * @param chatMerchantSwitch 检测的对象
     * @return
     */
    @Override
    public String checkChatMerchantSwitchUnique(ChatMerchantSwitch chatMerchantSwitch) {
        Long id = StringUtils.isNull(chatMerchantSwitch.getId()) ? -1L : chatMerchantSwitch.getId();
        ChatMerchantSwitch unique = chatMerchantSwitchMapper.checkChatMerchantSwitchUnique(chatMerchantSwitch);
        if (StringUtils.isNotNull(unique) && unique.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 商户开关
     * @param userId 用户ID
     * @return
     */
    @Override
    public boolean isChatMerchantSwitch(Long userId) {
        boolean isChatMerchantSwitch = false;
        ChatMerchantSwitch chatMerchantSwitch = redisCache.getCacheObject(getCacheKey(userId));
        if(chatMerchantSwitch != null){
            if("0".equals(chatMerchantSwitch.getStatus())){
                isChatMerchantSwitch = true;
            }
        }else {
            chatMerchantSwitch = chatMerchantSwitchMapper.getChatMerchantSwitchByUserId(userId);
            if(chatMerchantSwitch != null){
                if("0".equals(chatMerchantSwitch.getStatus())){
                    isChatMerchantSwitch = true;
                }
            }else {
                //缓存中没有,数据库中没有，说明还未给该商户添加开关，那么就默认加一个关闭的开关.
                SysUser sysUser = sysUserService.selectUserById(userId);
                if(sysUser != null){
                    chatMerchantSwitch = new ChatMerchantSwitch();
                    chatMerchantSwitch.setId(snowflakeIdUtils.nextId());
                    chatMerchantSwitch.setStatus("1");  //关闭
                    chatMerchantSwitch.setUserId(sysUser.getUserId());
                    chatMerchantSwitch.setUserName(sysUser.getUserName());
                    chatMerchantSwitch.setCreateBy("系统创建");
                    chatMerchantSwitch.setCreateTime(new Date());
                    chatMerchantSwitch.setRemark("系统创建的默认开关");
                    chatMerchantSwitchMapper.insertChatMerchantSwitch(chatMerchantSwitch);
                }
            }
        }

        return isChatMerchantSwitch;
    }
}

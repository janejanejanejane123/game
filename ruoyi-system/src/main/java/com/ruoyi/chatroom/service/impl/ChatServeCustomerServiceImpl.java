package com.ruoyi.chatroom.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.ruoyi.chatroom.db.domain.ChatUserInfo;
import com.ruoyi.chatroom.domain.ChatServeCustomer;
import com.ruoyi.chatroom.domain.vo.ChatServeCustomerVo;
import com.ruoyi.chatroom.mapper.ChatServeCustomerMapper;
import com.ruoyi.chatroom.service.ChatUserInfoService;
import com.ruoyi.chatroom.service.IChatServeCustomerService;
import com.ruoyi.common.annotation.Overtime;
import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SpringContextUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;
import io.netty.channel.Channel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 客服Service业务层处理
 * 
 * @author nn
 * @date 2022-07-09
 */
@Service
public class ChatServeCustomerServiceImpl implements IChatServeCustomerService
{
    @Autowired
    private ChatServeCustomerMapper chatServeCustomerMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private ChatUserInfoService chatUserInfoService;

    @Resource
    private ChatroomFacade chatroomFacade;

    /**
     * 设置cache key
     *
     * @return 缓存键key
     */
    private String getCacheKey()
    {
        return CacheKeyConstants.T_CHAT_SERVE_CUSTOMER_KEY;
    }

    /**
     * 查询客服
     * 
     * @param id 客服主键
     * @return 客服
     */
    @Override
    public ChatServeCustomer selectChatServeCustomerById(Long id)
    {
        return chatServeCustomerMapper.selectChatServeCustomerById(id);
    }

    /**
     * 查询客服列表
     * 
     * @param chatServeCustomer 客服
     * @return 客服
     */
    @Override
    public List<ChatServeCustomer> selectChatServeCustomerList(ChatServeCustomer chatServeCustomer)
    {
        return chatServeCustomerMapper.selectChatServeCustomerList(chatServeCustomer);
    }

    /**
     * 新增客服
     * 
     * @param chatServeCustomer 客服
     * @return 结果
     */
    @Override
    @Caching(evict={@CacheEvict(key = "'t_chat_serve_customer:getChatServeCustomerVoList'",value = "redisCache4Spring")})
    public int insertChatServeCustomer(ChatServeCustomer chatServeCustomer)
    {
        redisCache.deleteObject(getCacheKey());
        chatServeCustomer.setCreateTime(DateUtils.getNowDate());
        return chatServeCustomerMapper.insertChatServeCustomer(chatServeCustomer);
    }

    /**
     * 修改客服
     * 
     * @param chatServeCustomer 客服
     * @return 结果
     */
    @Override
    @Caching(evict={@CacheEvict(key = "'t_chat_serve_customer:getChatServeCustomerVoList'",value = "redisCache4Spring")})
    public int updateChatServeCustomer(ChatServeCustomer chatServeCustomer)
    {
        redisCache.deleteObject(getCacheKey());
        chatServeCustomer.setUpdateTime(DateUtils.getNowDate());
        return chatServeCustomerMapper.updateChatServeCustomer(chatServeCustomer);
    }

    /**
     * 批量删除客服
     * 
     * @param ids 需要删除的客服主键
     * @return 结果
     */
    @Override
    @Caching(evict={@CacheEvict(key = "'t_chat_serve_customer:getChatServeCustomerVoList'",value = "redisCache4Spring")})
    public int deleteChatServeCustomerByIds(Long[] ids)
    {
        redisCache.deleteObject(getCacheKey());
        return chatServeCustomerMapper.deleteChatServeCustomerByIds(ids);
    }

    /**
     * 删除客服信息
     * 
     * @param id 客服主键
     * @return 结果
     */
    @Override
    @Caching(evict={@CacheEvict(key = "'t_chat_serve_customer:getChatServeCustomerVoList'",value = "redisCache4Spring")})
    public int deleteChatServeCustomerById(Long id)
    {
        redisCache.deleteObject(getCacheKey());
        return chatServeCustomerMapper.deleteChatServeCustomerById(id);
    }

    /**
     * 修改客服状态.
     *
     * @param chatServeCustomer 客服信息
     * @return 结果
     */
    @Override
    @Caching(evict={
            @CacheEvict(key = "'t_chat_serve_customer:getChatServeCustomerVoList'",value = "redisCache4Spring"),
            @CacheEvict(key = "'t_chat_serve_customer:getChatServeCustomerVoList'",value = "redisCache4Spring")
    })
    public int changeStatus(ChatServeCustomer chatServeCustomer) {
        redisCache.deleteObject(getCacheKey());
        return chatServeCustomerMapper.changeStatus(chatServeCustomer);
    }

    /**
     * 校验客服是否唯一
     * @param chatServeCustomer 检测的对象
     * @return
     */
    @Override
    public String checkServeCustomerUnique(ChatServeCustomer chatServeCustomer) {
        Long id = StringUtils.isNull(chatServeCustomer.getId()) ? -1L : chatServeCustomer.getId();
        ChatServeCustomer unique = chatServeCustomerMapper.checkServeCustomerUnique(chatServeCustomer);
        if (StringUtils.isNotNull(unique) && unique.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 前端查询客服列表
     *
     * @param chatServeCustomer 客服
     * @return 客服
     */
    @Override
    public List<ChatServeCustomer> getChatServeCustomerList(ChatServeCustomer chatServeCustomer)
    {
        List<ChatServeCustomer> list = redisCache.getCacheObject(getCacheKey());
        if(list == null || list.size() == 0){
            chatServeCustomer.setStatus("0");
            list = chatServeCustomerMapper.selectChatServeCustomerList(chatServeCustomer);
            if(list != null){
                redisCache.setCacheObject(getCacheKey(), list,3600 * 24, TimeUnit.SECONDS);
            }
        }
        return list;
    }

    /**
     * 前端查询客服列表
     *
     * @return 客服
     */
    @Override
    @Cacheable(key = "'t_chat_serve_customer:getChatServeCustomerVoList'",value = "redisCache4Spring")
    public List<ChatServeCustomerVo> getChatServeCustomerVoList()
    {
        List<ChatServeCustomer> serveCustomerList = chatServeCustomerMapper.selectChatServeCustomer();
        List<ChatServeCustomerVo> serveCustomerVoList = new ArrayList<>();
        for(ChatServeCustomer chatServeCustomer : serveCustomerList){
            //赋值对象
            ChatServeCustomerVo serveCustomerVo  = new ChatServeCustomerVo();
            CglibBeanCopierUtils.copyProperties(serveCustomerVo,chatServeCustomer);
            String userIdentifier = chatroomFacade.getUserIdentifier(chatServeCustomer.getUserId(),chatServeCustomer.getUserName());
            serveCustomerVo.setUserIdentifier(userIdentifier);
            serveCustomerVoList.add(serveCustomerVo);
        }
        return serveCustomerVoList;
    }

    /**
     * 是否是客服.
     * @param userIdentifier 用户标示
     * @param userId 用户ID
     * @return
     */
    @Override
    public boolean isServeCustomer(String userIdentifier,Long userId) {
        boolean isServeCustomer = false;

        List<ChatServeCustomerVo>  serveCustomerVoList = SpringContextUtil.getBean(this.getClass()).getChatServeCustomerVoList();
        if(CollectionUtils.isNotEmpty(serveCustomerVoList)){
            for(ChatServeCustomerVo serveCustomerVo : serveCustomerVoList){
                if(StringUtils.equals(userIdentifier,serveCustomerVo.getUserIdentifier()) || (userId != null && serveCustomerVo.getUserId().longValue() == userId.longValue())){
                    isServeCustomer = true;
                    break;
                }
            }
        }
        return isServeCustomer;
    }

    @Override
    public boolean isServeCustomerByUserName(String userName) {
        boolean isServeCustomer = false;
        List<ChatServeCustomerVo>  serveCustomerVoList = SpringContextUtil.getBean(this.getClass()).getChatServeCustomerVoList();
        if(CollectionUtils.isNotEmpty(serveCustomerVoList)){
            for(ChatServeCustomerVo serveCustomerVo : serveCustomerVoList){
                if(org.apache.commons.lang3.StringUtils.equals(userName,serveCustomerVo.getUserName())){
                    isServeCustomer = true;
                    break;
                }
            }
        }
        return isServeCustomer;
    }

    /**
     * 查询所有启用客服的UserId.
     * @return
     */
    @Override
    @Overtime(value = 3600)
    @Cacheable(key = "'t_serve_customer:serveCustomerUserIdList' ", value = "redisCache4Spring", sync = true)
    public List<Long> serveCustomerUserIdList() {
        List<Long> serveCustomerUserIdList = chatServeCustomerMapper.serveCustomerUserIdList();
        return serveCustomerUserIdList;
    }

    /**
     * 查询所有启用的客服(前端).
     * @return
     */
    @Override
    @Overtime(value = 3600)
    @Cacheable(key = "'t_serve_customer:customerServiceVoList' ", value = "redisCache4Spring", sync = true)
    public List<ChatServeCustomerVo> customerServiceVoList() {
        List<ChatServeCustomer> serveCustomerList = chatServeCustomerMapper.selectChatServeCustomer();
        List<ChatServeCustomerVo> customerServiceVoList = new ArrayList<>();

        List<Long> userIds = serveCustomerUserIdList();
        List<ChatUserInfo> chatUserInfoList = chatUserInfoService.queryChatUserInfoListByUserIds(userIds);
        for(ChatServeCustomer serveCustomer : serveCustomerList){
            //赋值对象
            ChatServeCustomerVo customerServiceVo = new ChatServeCustomerVo();
            CglibBeanCopierUtils.copyProperties(customerServiceVo,serveCustomer);
            for (ChatUserInfo chatUserInfo : chatUserInfoList) {
                if (serveCustomer.getUserId().equals(chatUserInfo.getUserId())) {
                    customerServiceVo.setSmallHead(chatUserInfo.getSmallHead());
                    customerServiceVo.setLargeHead(chatUserInfo.getLargeHead());
                    customerServiceVo.setUserIdentifier(chatUserInfo.getUserIdentifier());
                    customerServiceVo.setNickName(chatUserInfo.getNikeName());
                    break;
                }
            }
            customerServiceVoList.add(customerServiceVo);
        }
        return customerServiceVoList;
    }

    @Override
    public List<ChatServeCustomerVo> customerServiceVOList() {
        List<ChatServeCustomerVo> customerServiceVoList = SpringContextUtil.getBean(this.getClass()).customerServiceVoList();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(customerServiceVoList)){
            ConcurrentMap<String, List<Channel>> map = null;
            for(ChatServeCustomerVo customerServiceVo : customerServiceVoList) {
                //返回的时候把userId去掉.
                customerServiceVo.setUserId(null);
            }
        }
        return customerServiceVoList;
    }
}

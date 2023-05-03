package com.ruoyi.chatroom.mapper;

import java.util.List;
import com.ruoyi.chatroom.domain.ChatMerchantSwitch;

/**
 * 商户开关Mapper接口
 * 
 * @author ruoyi
 * @date 2022-12-18
 */
public interface ChatMerchantSwitchMapper 
{
    /**
     * 查询商户开关
     * 
     * @param id 商户开关主键
     * @return 商户开关
     */
    public ChatMerchantSwitch selectChatMerchantSwitchById(Long id);

    /**
     * 查询商户开关列表
     * 
     * @param chatMerchantSwitch 商户开关
     * @return 商户开关集合
     */
    public List<ChatMerchantSwitch> selectChatMerchantSwitchList(ChatMerchantSwitch chatMerchantSwitch);

    /**
     * 新增商户开关
     * 
     * @param chatMerchantSwitch 商户开关
     * @return 结果
     */
    public int insertChatMerchantSwitch(ChatMerchantSwitch chatMerchantSwitch);

    /**
     * 修改商户开关
     * 
     * @param chatMerchantSwitch 商户开关
     * @return 结果
     */
    public int updateChatMerchantSwitch(ChatMerchantSwitch chatMerchantSwitch);

    /**
     * 删除商户开关
     * 
     * @param id 商户开关主键
     * @return 结果
     */
    public int deleteChatMerchantSwitchById(Long id);

    /**
     * 批量删除商户开关
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteChatMerchantSwitchByIds(Long[] ids);

    /**
     * 修改状态
     *
     * @param chatMerchantSwitch 信息
     * @return 结果
     */
    public int changeStatus(ChatMerchantSwitch chatMerchantSwitch);

    /**
     * 校验是否唯一
     *
     * @param chatMerchantSwitch 检测的对象
     * @return 结果
     */
    public ChatMerchantSwitch checkChatMerchantSwitchUnique(ChatMerchantSwitch chatMerchantSwitch);

    /**
     * 根据用户ID查询聊天室商户开关
     *
     * @param userId 商户ID
     * @return 商户开关
     */
    public ChatMerchantSwitch getChatMerchantSwitchByUserId(Long userId);
}

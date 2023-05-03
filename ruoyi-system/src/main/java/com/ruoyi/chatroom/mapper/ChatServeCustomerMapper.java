package com.ruoyi.chatroom.mapper;

import com.ruoyi.chatroom.domain.ChatServeCustomer;
import com.ruoyi.common.core.domain.entity.SysUser;

import java.util.List;

/**
 * 客服Mapper接口
 * 
 * @author ruoyi
 * @date 2022-07-09
 */
public interface ChatServeCustomerMapper
{
    /**
     * 查询客服
     * 
     * @param id 客服主键
     * @return 客服
     */
    public ChatServeCustomer selectChatServeCustomerById(Long id);

    /**
     * 查询客服列表
     * 
     * @param chatServeCustomer 客服
     * @return 客服集合
     */
    public List<ChatServeCustomer> selectChatServeCustomerList(ChatServeCustomer chatServeCustomer);

    /**
     * 新增客服
     * 
     * @param chatServeCustomer 客服
     * @return 结果
     */
    public int insertChatServeCustomer(ChatServeCustomer chatServeCustomer);

    /**
     * 修改客服
     * 
     * @param chatServeCustomer 客服
     * @return 结果
     */
    public int updateChatServeCustomer(ChatServeCustomer chatServeCustomer);

    /**
     * 删除客服
     * 
     * @param id 客服主键
     * @return 结果
     */
    public int deleteChatServeCustomerById(Long id);

    /**
     * 批量删除客服
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteChatServeCustomerByIds(Long[] ids);

    /**
     * 修改客服状态
     *
     * @param chatServeCustomer 客服信息
     * @return 结果
     */
    public int changeStatus(ChatServeCustomer chatServeCustomer);

    /**
     * 校验客服是否唯一
     *
     * @param chatServeCustomer 检测的对象
     * @return 结果
     */
    public ChatServeCustomer checkServeCustomerUnique(ChatServeCustomer chatServeCustomer);

    /**
     * 查询所有启用客服的UserId.
     * @return
     */
    public List<Long> serveCustomerUserIdList();

    /**
     * 查询所有启用的客服.
     * @return
     */
    List<ChatServeCustomer> selectChatServeCustomer();
}

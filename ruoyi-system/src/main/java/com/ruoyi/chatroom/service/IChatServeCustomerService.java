package com.ruoyi.chatroom.service;

import com.ruoyi.chatroom.domain.ChatServeCustomer;
import com.ruoyi.chatroom.domain.vo.ChatServeCustomerVo;
import com.ruoyi.settings.domain.SysAgencyBackwater;

import java.util.List;

/**
 * 客服Service接口
 * 
 * @author ruoyi
 * @date 2022-07-09
 */
public interface IChatServeCustomerService
{
    /**
     * 查询客服列
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
     * 批量删除客服
     * 
     * @param ids 需要删除的客服主键集合
     * @return 结果
     */
    public int deleteChatServeCustomerByIds(Long[] ids);

    /**
     * 删除客服信息
     * 
     * @param id 客服列主键
     * @return 结果
     */
    public int deleteChatServeCustomerById(Long id);

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
    public String checkServeCustomerUnique(ChatServeCustomer chatServeCustomer);

    /**
     * 前端获取客服列表.
     * @param chatServeCustomer
     * @return
     */
    public List<ChatServeCustomer> getChatServeCustomerList(ChatServeCustomer chatServeCustomer);

    /**
     * 前端获取客服列表.
     * @return
     */
    public List<ChatServeCustomerVo> getChatServeCustomerVoList();

    /**
     * @Description: 是否是客服-根据
     * @param userIdentifier 用户标示
     * @param userId 用户ID
     * @return {@link boolean}
     */
    boolean isServeCustomer(String userIdentifier,Long userId);

    /**
     * @Description: 是否是客服
     * @param userName 用户名称
     * @return {@link boolean}
     */
    boolean isServeCustomerByUserName(String userName);

    /**
     * 查询所有启用的客服Id
     * @return
     */
    List<Long> serveCustomerUserIdList();

    /**
     * 查询所有启用的客服
     * @return
     */
    List<ChatServeCustomerVo> customerServiceVoList();

    /**
     * @Description: 查询所有启用的客服
     * @author nn
     * @date 2022/07/24 18:40
     */
    List<ChatServeCustomerVo> customerServiceVOList();
}

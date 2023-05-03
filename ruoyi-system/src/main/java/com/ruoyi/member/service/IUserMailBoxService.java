package com.ruoyi.member.service;

import com.ruoyi.member.domain.UserMailBox;

import java.util.List;

/**
 * memberService接口
 * 
 * @author ry
 * @date 2022-06-06
 */
public interface IUserMailBoxService 
{
    /**
     * 查询member
     * 
     * @param id member主键
     * @return member
     */
    public UserMailBox selectUserMailBoxById(Long id);

    /**
     * 查询member列表
     * 
     * @param userMailBox member
     * @return member集合
     */
    public List<UserMailBox> selectUserMailBoxList(UserMailBox userMailBox);

    /**
     * 新增member
     * 
     * @param userMailBox member
     * @return 结果
     */
    public int insertUserMailBox(UserMailBox userMailBox);

    /**
     * 修改member
     * 
     * @param userMailBox member
     * @return 结果
     */
    public int updateUserMailBox(UserMailBox userMailBox);

    /**
     * 批量删除member
     * 
     * @param ids 需要删除的member主键集合
     * @return 结果
     */
    public int deleteUserMailBoxByIds(Long[] ids);

    /**
     * 删除member信息
     * 
     * @param id member主键
     * @return 结果
     */
    public int deleteUserMailBoxById(Long id);

    public List<UserMailBox> selectMyMailBoxList(Long uid);

    int changeReadStatus(Long id);
}

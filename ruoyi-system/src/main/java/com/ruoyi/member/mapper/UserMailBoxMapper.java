package com.ruoyi.member.mapper;

import com.ruoyi.member.domain.UserMailBox;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * memberMapper接口
 * 
 * @author ry
 * @date 2022-06-06
 */
public interface UserMailBoxMapper 
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

    public List<UserMailBox> selectMyMailBoxList(Long uid);

    /**
     * 新增站内信
     * 
     * @param userMailBox member
     * @return 结果
     */
    public int insertUserMailBox(UserMailBox userMailBox);

    /**
     * 修改站内信
     * 
     * @param list
     * @return 结果
     */
    public int insertBatchUserMailBox(@Param("list")List<UserMailBox> list);

    public int updateUserMailBox(UserMailBox userMailBox);

    /**
     * 删除站内信
     * 
     * @param id member主键
     * @return 结果
     */
    public int deleteUserMailBoxById(Long id);

    /**
     * 批量删除站内信
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserMailBoxByIds(Long[] ids);

    int changeReadStatus(Long id);
}

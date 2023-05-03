package com.ruoyi.member.service;

import java.util.List;

import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.member.domain.TUserNicknameBlackList;

/**
 * 用户昵称黑名单Service接口
 * 
 * @author ruoyi
 * @date 2022-08-09
 */
public interface ITUserNicknameBlackListService 
{
    /**
     * 查询用户昵称黑名单
     * 
     * @param id 用户昵称黑名单主键
     * @return 用户昵称黑名单
     */
    public TUserNicknameBlackList selectTUserNicknameBlackListById(Long id);

    /**
     * 查询用户昵称黑名单列表
     * 
     * @param tUserNicknameBlackList 用户昵称黑名单
     * @return 用户昵称黑名单集合
     */
    public List<TUserNicknameBlackList> selectTUserNicknameBlackListList(TUserNicknameBlackList tUserNicknameBlackList);

    /**
     * 新增用户昵称黑名单
     * 
     * @param tUserNicknameBlackList 用户昵称黑名单
     * @return 结果
     */
    public int insertTUserNicknameBlackList(TUserNicknameBlackList tUserNicknameBlackList);

    /**
     * 修改用户昵称黑名单
     * 
     * @param tUserNicknameBlackList 用户昵称黑名单
     * @return 结果
     */
    public int updateTUserNicknameBlackList(TUserNicknameBlackList tUserNicknameBlackList);

    /**
     * 批量删除用户昵称黑名单
     * 
     * @param ids 需要删除的用户昵称黑名单主键集合
     * @return 结果
     */
    public int deleteTUserNicknameBlackListByIds(Long[] ids);

    /**
     * 删除用户昵称黑名单信息
     * 
     * @param id 用户昵称黑名单主键
     * @return 结果
     */
    public int deleteTUserNicknameBlackListById(Long id);



    boolean checkBlackList(LoginMember member);
}

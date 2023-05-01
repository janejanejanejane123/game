package com.ruoyi.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.game.domain.TUser;

import java.util.List;

/**
 * 用户Mapper接口
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
public interface TUserMapper extends BaseMapper<TUser>
{
    /**
     * 查询用户
     * 
     * @param uid 用户主键
     * @return 用户
     */
    public TUser selectTUserByUid(Long uid);

    /**
     * 查询用户列表
     * 
     * @param tUser 用户
     * @return 用户集合
     */
    public List<TUser> selectTUserList(TUser tUser);

    /**
     * 新增用户
     * 
     * @param tUser 用户
     * @return 结果
     */
    public int insertTUser(TUser tUser);

    /**
     * 修改用户
     * 
     * @param tUser 用户
     * @return 结果
     */
    public int updateTUser(TUser tUser);

    /**
     * 删除用户
     * 
     * @param uid 用户主键
     * @return 结果
     */
    public int deleteTUserByUid(Long uid);

    /**
     * 批量删除用户
     * 
     * @param uids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTUserByUids(String[] uids);
}

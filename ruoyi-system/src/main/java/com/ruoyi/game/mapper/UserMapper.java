package com.ruoyi.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.game.domain.User;


import java.util.List;

/**
 * 用户Mapper接口
 *
 * @author ruoyi
 * @date 2023-04-28
 */
public interface UserMapper extends BaseMapper<User> {
    /**
     * 查询用户
     *
     * @param userId 用户主键
     * @return 用户
     */
    public User selectUserByUserId(Long userId);

    /**
     * 查询用户列表
     *
     * @param user 用户
     * @return 用户集合
     */
    public List<User> selectUserList(User user);

    /**
     * 新增用户
     *
     * @param user 用户
     * @return 结果
     */
    public int insertUser(User user);

    /**
     * 修改用户
     *
     * @param user 用户
     * @return 结果
     */
    public int updateUser(User user);

    /**
     * 删除用户
     *
     * @param userId 用户主键
     * @return 结果
     */
    public int deleteUserByUserId(Long userId);

    /**
     * 批量删除用户
     *
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserByUserIds(String[] userIds);
}

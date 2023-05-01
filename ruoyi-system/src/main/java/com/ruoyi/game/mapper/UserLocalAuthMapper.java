package com.ruoyi.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.game.domain.UserLocalAuth;

import java.util.List;

/**
 * 本地用户Mapper接口
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
public interface UserLocalAuthMapper  extends BaseMapper<UserLocalAuth>
{
    /**
     * 查询本地用户
     * 
     * @param authId 本地用户主键
     * @return 本地用户
     */
    public UserLocalAuth selectUserLocalAuthByAuthId(Long authId);

    /**
     * 查询本地用户列表
     * 
     * @param userLocalAuth 本地用户
     * @return 本地用户集合
     */
    public List<UserLocalAuth> selectUserLocalAuthList(UserLocalAuth userLocalAuth);

    /**
     * 新增本地用户
     * 
     * @param userLocalAuth 本地用户
     * @return 结果
     */
    public int insertUserLocalAuth(UserLocalAuth userLocalAuth);

    /**
     * 修改本地用户
     * 
     * @param userLocalAuth 本地用户
     * @return 结果
     */
    public int updateUserLocalAuth(UserLocalAuth userLocalAuth);

    /**
     * 删除本地用户
     * 
     * @param authId 本地用户主键
     * @return 结果
     */
    public int deleteUserLocalAuthByAuthId(Long authId);

    /**
     * 批量删除本地用户
     * 
     * @param authIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserLocalAuthByAuthIds(String[] authIds);
}

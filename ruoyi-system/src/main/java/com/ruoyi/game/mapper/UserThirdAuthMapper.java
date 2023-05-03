package com.ruoyi.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.game.domain.UserThirdAuth;

import java.util.List;

/**
 * 第三方用户Mapper接口
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
public interface UserThirdAuthMapper  extends BaseMapper<UserThirdAuth>
{
    /**
     * 查询第三方用户
     * 
     * @param authId 第三方用户主键
     * @return 第三方用户
     */
    public UserThirdAuth selectUserThirdAuthByAuthId(Long authId);

    /**
     * 查询第三方用户列表
     * 
     * @param userThirdAuth 第三方用户
     * @return 第三方用户集合
     */
    public List<UserThirdAuth> selectUserThirdAuthList(UserThirdAuth userThirdAuth);

    /**
     * 新增第三方用户
     * 
     * @param userThirdAuth 第三方用户
     * @return 结果
     */
    public int insertUserThirdAuth(UserThirdAuth userThirdAuth);

    /**
     * 修改第三方用户
     * 
     * @param userThirdAuth 第三方用户
     * @return 结果
     */
    public int updateUserThirdAuth(UserThirdAuth userThirdAuth);

    /**
     * 删除第三方用户
     * 
     * @param authId 第三方用户主键
     * @return 结果
     */
    public int deleteUserThirdAuthByAuthId(Long authId);

    /**
     * 批量删除第三方用户
     * 
     * @param authIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserThirdAuthByAuthIds(String[] authIds);
}

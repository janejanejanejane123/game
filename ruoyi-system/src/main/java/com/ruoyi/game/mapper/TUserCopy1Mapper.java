package com.ruoyi.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.game.domain.TUserCopy1;

import java.util.List;

/**
 * 用户Mapper接口
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
public interface TUserCopy1Mapper  extends BaseMapper<TUserCopy1>
{
    /**
     * 查询用户
     * 
     * @param uid 用户主键
     * @return 用户
     */
    public TUserCopy1 selectTUserCopy1ByUid(Long uid);

    /**
     * 查询用户列表
     * 
     * @param tUserCopy1 用户
     * @return 用户集合
     */
    public List<TUserCopy1> selectTUserCopy1List(TUserCopy1 tUserCopy1);

    /**
     * 新增用户
     * 
     * @param tUserCopy1 用户
     * @return 结果
     */
    public int insertTUserCopy1(TUserCopy1 tUserCopy1);

    /**
     * 修改用户
     * 
     * @param tUserCopy1 用户
     * @return 结果
     */
    public int updateTUserCopy1(TUserCopy1 tUserCopy1);

    /**
     * 删除用户
     * 
     * @param uid 用户主键
     * @return 结果
     */
    public int deleteTUserCopy1ByUid(Long uid);

    /**
     * 批量删除用户
     * 
     * @param uids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTUserCopy1ByUids(String[] uids);
}

package com.ruoyi.member.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.domain.model.member.TUser;
import org.apache.ibatis.annotations.Param;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2022-03-15
 */
public interface TUserMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public TUser selectTUserByUid(Long uid);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param tUser 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<TUser> selectTUserList(TUser tUser);

    /**
     * 新增【请填写功能名称】
     * 
     * @param tUser 【请填写功能名称】
     * @return 结果
     */
    public int insertTUser(TUser tUser);

    /**
     * 修改【请填写功能名称】
     * 
     * @param tUser 【请填写功能名称】
     * @return 结果
     */
    public int updateTUser(TUser tUser);

    /**
     * 删除【请填写功能名称】
     * 
     * @param uid 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteTUserByUid(Long uid);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param uids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTUserByUids(Long[] uids);


    /***
     *
     * @param tUser
     * @return
     */
    long count(TUser tUser);

    TUser selectApiUserByUsername(String username);

    TUser selectUserByUsername(String username);

    List<Map<String,Object>> selectChildByPid(@Param("pid") Long id);

    Long findMax();
}

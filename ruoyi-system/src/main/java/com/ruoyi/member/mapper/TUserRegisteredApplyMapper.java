package com.ruoyi.member.mapper;

import com.ruoyi.member.domain.TUserRegisteredApply;

import java.util.List;

/**
 * 用户注册申请Mapper接口
 * 
 * @author ruoyi
 * @date 2022-03-29
 */
public interface TUserRegisteredApplyMapper 
{
    /**
     * 查询用户注册申请
     * 
     * @param id 用户注册申请主键
     * @return 用户注册申请
     */
    public TUserRegisteredApply selectTUserRegisteredApplyById(Long id);

    /**
     * 查询用户注册申请列表
     * 
     * @param tUserRegisteredApply 用户注册申请
     * @return 用户注册申请集合
     */
    public List<TUserRegisteredApply> selectTUserRegisteredApplyList(TUserRegisteredApply tUserRegisteredApply);

    /**
     * 新增用户注册申请
     * 
     * @param tUserRegisteredApply 用户注册申请
     * @return 结果
     */
    public int insertTUserRegisteredApply(TUserRegisteredApply tUserRegisteredApply);

    /**
     * 修改用户注册申请
     * 
     * @param tUserRegisteredApply 用户注册申请
     * @return 结果
     */
    public int updateTUserRegisteredApply(TUserRegisteredApply tUserRegisteredApply);

    /**
     * 删除用户注册申请
     * 
     * @param id 用户注册申请主键
     * @return 结果
     */
    public int deleteTUserRegisteredApplyById(Long id);

    /**
     * 批量删除用户注册申请
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTUserRegisteredApplyByIds(Long[] ids);

    /**
     *
     * @param userId
     * @return
     */
    long countByUid(Long userId);
}

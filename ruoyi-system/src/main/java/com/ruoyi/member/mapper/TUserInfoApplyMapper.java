package com.ruoyi.member.mapper;

import java.util.List;

import com.ruoyi.member.domain.TUserInfoApply;

/**
 * 用户资料申请与审核Mapper接口
 * 
 * @author ruoyi
 * @date 2022-03-28
 */
public interface TUserInfoApplyMapper 
{
    /**
     * 查询用户资料申请与审核
     * 
     * @param id 用户资料申请与审核主键
     * @return 用户资料申请与审核
     */
    public TUserInfoApply selectTUserInfoApplyById(Long id);

    /**
     * 查询用户资料申请与审核列表
     * 
     * @param tUserInfoApply 用户资料申请与审核
     * @return 用户资料申请与审核集合
     */
    public List<TUserInfoApply> selectTUserInfoApplyList(TUserInfoApply tUserInfoApply);

    /**
     * 新增用户资料申请与审核
     * 
     * @param tUserInfoApply 用户资料申请与审核
     * @return 结果
     */
    public int insertTUserInfoApply(TUserInfoApply tUserInfoApply);

    /**
     * 修改用户资料申请与审核
     * 
     * @param tUserInfoApply 用户资料申请与审核
     * @return 结果
     */
    public int updateTUserInfoApply(TUserInfoApply tUserInfoApply);

    /**
     * 删除用户资料申请与审核
     * 
     * @param id 用户资料申请与审核主键
     * @return 结果
     */
    public int deleteTUserInfoApplyById(Long id);

    /**
     * 批量删除用户资料申请与审核
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTUserInfoApplyByIds(Long[] ids);


    long countByCause(TUserInfoApply tUserInfoApply);
}

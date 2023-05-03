package com.ruoyi.member.service;

import java.util.List;

import com.ruoyi.common.enums.UserInfoApplyEnums;
import com.ruoyi.member.domain.TUserInfoApply;

/**
 * 用户资料申请与审核Service接口
 * 
 * @author ruoyi
 * @date 2022-03-28
 */
public interface ITUserInfoApplyService 
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
     * 批量删除用户资料申请与审核
     * 
     * @param ids 需要删除的用户资料申请与审核主键集合
     * @return 结果
     */
    public int deleteTUserInfoApplyByIds(Long[] ids);

    /**
     * 删除用户资料申请与审核信息
     * 
     * @param id 用户资料申请与审核主键
     * @return 结果
     */
    public int deleteTUserInfoApplyById(Long id);


    /**
     * 申请审核;
     * @param content 审核内容
     * @param infoApplyEnums 审核类型;
     *
     *
     */

    void apply2Check(String content, UserInfoApplyEnums infoApplyEnums);

}

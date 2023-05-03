package com.ruoyi.member.service;

import java.util.List;
import com.ruoyi.member.domain.TUserProxyApply;

/**
 * 代理申请Service接口
 * 
 * @author ruoyi
 * @date 2022-06-26
 */
public interface ITUserProxyApplyService 
{

    long countByUid(Long userId);
    /**
     * 查询代理申请
     * 
     * @param id 代理申请主键
     * @return 代理申请
     */
    public TUserProxyApply selectTUserProxyApplyById(Long id);

    /**
     * 查询代理申请列表
     * 
     * @param tUserProxyApply 代理申请
     * @return 代理申请集合
     */
    public List<TUserProxyApply> selectTUserProxyApplyList(TUserProxyApply tUserProxyApply);

    /**
     * 新增代理申请
     * 
     * @param tUserProxyApply 代理申请
     * @return 结果
     */
    public int insertTUserProxyApply(TUserProxyApply tUserProxyApply);

    /**
     * 修改代理申请
     * 
     * @param tUserProxyApply 代理申请
     * @return 结果
     */
    public int updateTUserProxyApply(TUserProxyApply tUserProxyApply);

    /**
     * 批量删除代理申请
     * 
     * @param ids 需要删除的代理申请主键集合
     * @return 结果
     */
    public int deleteTUserProxyApplyByIds(Long[] ids);

    /**
     * 删除代理申请信息
     * 
     * @param id 代理申请主键
     * @return 结果
     */
    public int deleteTUserProxyApplyById(Long id);


}

package com.ruoyi.member.service;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.EmailEnums;
import com.ruoyi.member.domain.TEmailConfig;


/**
 * 邮箱配置Service接口
 * 
 * @author ruoyi
 * @date 2022-03-29
 */
public interface ITEmailConfigService 
{
    /**
     * 查询邮箱配置
     * 
     * @param id 邮箱配置主键
     * @return 邮箱配置
     */
    public TEmailConfig selectTEmailConfigById(Long id);

    /**
     * 查询邮箱配置列表
     * 
     * @param tEmailConfig 邮箱配置
     * @return 邮箱配置集合
     */
    public List<TEmailConfig> selectTEmailConfigList(TEmailConfig tEmailConfig);

    /**
     * 新增邮箱配置
     * 
     * @param tEmailConfig 邮箱配置
     * @return 结果
     */
    public int insertTEmailConfig(TEmailConfig tEmailConfig);

    /**
     * 修改邮箱配置
     * 
     * @param tEmailConfig 邮箱配置
     * @return 结果
     */
    public int updateTEmailConfig(TEmailConfig tEmailConfig);

    /**
     * 批量删除邮箱配置
     * 
     * @param ids 需要删除的邮箱配置主键集合
     * @return 结果
     */
    public int deleteTEmailConfigByIds(Long[] ids);

    /**
     * 删除邮箱配置信息
     * 
     * @param id 邮箱配置主键
     * @return 结果
     */
    public int deleteTEmailConfigById(Long id);




    AjaxResult sendMessage(EmailEnums email, String toEmail, String message, Long uid);
}

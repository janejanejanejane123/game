package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysPayConfig;
import org.springframework.web.multipart.MultipartFile;

/**
 * 支付配置Service接口
 * 
 * @author nn
 * @date 2022-09-06
 */
public interface ISysPayConfigService 
{
    /**
     * 查询支付配置
     * 
     * @param id 支付配置主键
     * @return 支付配置
     */
    public SysPayConfig selectSysPayConfigById(Long id);

    /**
     * 查询支付配置列表
     * 
     * @param sysPayConfig 支付配置
     * @return 支付配置集合
     */
    public List<SysPayConfig> selectSysPayConfigList(SysPayConfig sysPayConfig);

    /**
     * 新增支付配置
     * 
     * @param sysPayConfig 支付配置
     * @return 结果
     */
    public int insertSysPayConfig(SysPayConfig sysPayConfig);

    /**
     * 修改支付配置
     * 
     * @param sysPayConfig 支付配置
     * @return 结果
     */
    public int updateSysPayConfig(SysPayConfig sysPayConfig);

    /**
     * 批量删除支付配置
     * 
     * @param ids 需要删除的支付配置主键集合
     * @return 结果
     */
    public int deleteSysPayConfigByIds(Long[] ids);

    /**
     * 删除支付配置信息
     * 
     * @param id 支付配置主键
     * @return 结果
     */
    public int deleteSysPayConfigById(Long id);

    /**
     * 查询支付配置
     *
     * @param userId 商户ID
     * @return 支付配置
     */
    public SysPayConfig selectSysPayConfigByUserId(Long userId);

    /**
     * 上传Logo.
     * @param file
     * @return
     */
    String uploadLogo(MultipartFile file);
}

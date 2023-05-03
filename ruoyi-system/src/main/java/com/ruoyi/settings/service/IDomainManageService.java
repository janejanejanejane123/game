package com.ruoyi.settings.service;

import com.ruoyi.settings.domain.DomainManage;

import java.util.List;


/**
 * 域名管理Service接口
 *
 * @author ruoyi
 * @date 2022-08-06
 */
public interface IDomainManageService
{
    /**
     * 查询域名管理
     *
     * @param id 域名管理主键
     * @return 域名管理
     */
    public DomainManage selectDomainManageById(Long id);

    /**
     * 查询域名管理列表
     *
     * @param domainManage 域名管理
     * @return 域名管理集合
     */
    public List<DomainManage> selectDomainManageList(DomainManage domainManage);



    public DomainManage selectDomainByHost(String host,Short type);


    public List<DomainManage> selectDomainManage(DomainManage domainManage);
    /**
     * 新增域名管理
     *
     * @param domainManage 域名管理
     * @return 结果
     */
    public int insertDomainManage(DomainManage domainManage);

    /**
     * 修改域名管理
     *
     * @param domainManage 域名管理
     * @return 结果
     */
    public int updateDomainManage(DomainManage domainManage);

    /**
     * 批量删除域名管理
     *
     * @param ids 需要删除的域名管理主键集合
     * @return 结果
     */
    public int deleteDomainManageByIds(Long[] ids);

    /**
     * 删除域名管理信息
     *
     * @param id 域名管理主键
     * @return 结果
     */
    public int deleteDomainManageById(Long id);
}

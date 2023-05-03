package com.ruoyi.settings.mapper;

import java.util.List;
import com.ruoyi.settings.domain.DomainManage;

/**
 * 域名管理Mapper接口
 *
 * @author ruoyi
 * @date 2022-08-06
 */
public interface DomainManageMapper
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
     * 删除域名管理
     *
     * @param id 域名管理主键
     * @return 结果
     */
    public int deleteDomainManageById(Long id);

    /**
     * 批量删除域名管理
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDomainManageByIds(Long[] ids);
}

package com.ruoyi.system.mapper;

import java.util.List;

import com.ruoyi.system.domain.SysWebsite;
import org.apache.ibatis.annotations.Param;

/**
 * 网站管理Mapper接口
 * 
 * @author nn
 * @date 2022-07-21
 */
public interface SysWebsiteMapper 
{
    /**
     * 查询网站管理
     * 
     * @param id 网站管理主键
     * @return 网站管理
     */
    public SysWebsite selectSysWebsiteById(Long id);

    /**
     * 查询网站管理列表
     * 
     * @param sysWebsite 网站管理
     * @return 网站管理集合
     */
    public List<SysWebsite> selectSysWebsiteList(SysWebsite sysWebsite);

    /**
     * 新增网站管理
     * 
     * @param sysWebsite 网站管理
     * @return 结果
     */
    public int insertSysWebsite(SysWebsite sysWebsite);

    /**
     * 修改网站管理
     * 
     * @param sysWebsite 网站管理
     * @return 结果
     */
    public int updateSysWebsite(SysWebsite sysWebsite);

    /**
     * 删除网站管理
     * 
     * @param id 网站管理主键
     * @return 结果
     */
    public int deleteSysWebsiteById(Long id);

    /**
     * 批量删除网站管理
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysWebsiteByIds(Long[] ids);

    /**
     * 校验网站是否唯一
     *
     * @param sysWebsite 检测的对象
     * @return 结果
     */
    public SysWebsite checkWebsiteUnique(SysWebsite sysWebsite);

    /**
     * 修改网站状态
     *
     * @param sysWebsite 网站信息
     * @return 结果
     */
    public int changeStatus(SysWebsite sysWebsite);

    /**
     * 获取网站信息.
     * @param sysWebsite
     * @return
     */
    public SysWebsite getSysWebsite(SysWebsite sysWebsite);

}

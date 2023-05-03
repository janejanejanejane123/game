package com.ruoyi.system.service;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysWebsite;
import org.springframework.web.multipart.MultipartFile;

/**
 * 网站管理Service接口
 * 
 * @author nn
 * @date 2022-07-21
 */
public interface ISysWebsiteService 
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
     * 批量删除网站管理
     * 
     * @param ids 需要删除的网站管理主键集合
     * @return 结果
     */
    public int deleteSysWebsiteByIds(Long[] ids);

    /**
     * 删除网站管理信息
     * 
     * @param id 网站管理主键
     * @return 结果
     */
    public int deleteSysWebsiteById(Long id);

    /**
     * 上传Logo.
     * @param file
     * @return
     */
    String uploadLogo(MultipartFile file);

    /**
     * 校验网站是否唯一
     *
     * @param sysWebsite 检测的对象
     * @return 结果
     */
    public String checkWebsiteUnique(SysWebsite sysWebsite);

    /**
     * 修改网站状态
     *
     * @param sysWebsite 网站信息
     * @return 结果
     */
    public int changeStatus(SysWebsite sysWebsite);

    /**
     * 获取网站信息
     *
     * @param sysWebsite 网站信息
     * @return 结果
     */
    public SysWebsite getSysWebsite(SysWebsite sysWebsite);
}

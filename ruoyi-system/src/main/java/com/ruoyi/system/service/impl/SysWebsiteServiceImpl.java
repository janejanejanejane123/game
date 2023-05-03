package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.Overtime;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.MinioUtil;
import com.ruoyi.common.utils.ip.old.OldIpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysWebsiteMapper;
import com.ruoyi.system.domain.SysWebsite;
import com.ruoyi.system.service.ISysWebsiteService;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 网站管理Service业务层处理
 * 
 * @author nn
 * @date 2022-07-21
 */
@Service
public class SysWebsiteServiceImpl implements ISysWebsiteService 
{
    @Autowired
    private SysWebsiteMapper sysWebsiteMapper;

    @Resource
    private MinioUtil minioUtil;

    /**
     * 查询网站管理
     * 
     * @param id 网站管理主键
     * @return 网站管理
     */
    @Override
    public SysWebsite selectSysWebsiteById(Long id)
    {
        return sysWebsiteMapper.selectSysWebsiteById(id);
    }

    /**
     * 查询网站管理列表
     * 
     * @param sysWebsite 网站管理
     * @return 网站管理
     */
    @Override
    public List<SysWebsite> selectSysWebsiteList(SysWebsite sysWebsite)
    {
        return sysWebsiteMapper.selectSysWebsiteList(sysWebsite);
    }

    /**
     * 新增网站管理
     * 
     * @param sysWebsite 网站管理
     * @return 结果
     */
    @Override
    public int insertSysWebsite(SysWebsite sysWebsite)
    {
        return sysWebsiteMapper.insertSysWebsite(sysWebsite);
    }

    /**
     * 修改网站管理
     * 
     * @param sysWebsite 网站管理
     * @return 结果
     */
    @Override
    @CacheEvict(key = "'sys_website:getSysWebsite:'+ #sysWebsite.siteCode",value = "redisCache4Spring")
    public int updateSysWebsite(SysWebsite sysWebsite)
    {
        return sysWebsiteMapper.updateSysWebsite(sysWebsite);
    }

    /**
     * 批量删除网站管理
     * 
     * @param ids 需要删除的网站管理主键
     * @return 结果
     */
    @Override
    public int deleteSysWebsiteByIds(Long[] ids)
    {
        return sysWebsiteMapper.deleteSysWebsiteByIds(ids);
    }

    /**
     * 删除网站管理信息
     * 
     * @param id 网站管理主键
     * @return 结果
     */
    @Override
    public int deleteSysWebsiteById(Long id)
    {
        return sysWebsiteMapper.deleteSysWebsiteById(id);
    }

    @Override
    public String uploadLogo(MultipartFile file) {
        return  minioUtil.upload(file, false);
    }

    /**
     * 校验SiteCode是否唯一
     * @param sysWebsite 检测的对象
     * @return
     */
    @Override
    public String checkWebsiteUnique(SysWebsite sysWebsite) {
        Long id = StringUtils.isNull(sysWebsite.getId()) ? -1L : sysWebsite.getId();
        SysWebsite unique = sysWebsiteMapper.checkWebsiteUnique(sysWebsite);
        if (StringUtils.isNotNull(unique) && unique.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 修改网站状态.
     *
     * @param sysWebsite 网站信息
     * @return 结果
     */
    @Override
    @CacheEvict(key = "'sys_website:getSysWebsite:'+#sysWebsite.siteCode",value = "redisCache4Spring")
    public int changeStatus(SysWebsite sysWebsite) {
        return sysWebsiteMapper.changeStatus(sysWebsite);
    }

    @Cacheable(key = "'sys_website:getSysWebsite:'+#sysWebsite.siteCode",value = "redisCache4Spring")
    @Overtime( 3600 * 24 )
    @Override
    public SysWebsite getSysWebsite(SysWebsite sysWebsite) {
        return sysWebsiteMapper.getSysWebsite(sysWebsite);
    }
}

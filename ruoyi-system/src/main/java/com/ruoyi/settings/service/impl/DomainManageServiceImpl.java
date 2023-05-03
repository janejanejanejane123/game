package com.ruoyi.settings.service.impl;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.ruoyi.settings.mapper.DomainManageMapper;
import com.ruoyi.settings.domain.DomainManage;
import com.ruoyi.settings.service.IDomainManageService;

/**
 * 域名管理Service业务层处理
 *
 * @author ruoyi
 * @date 2022-08-06
 */
@Service
public class DomainManageServiceImpl implements IDomainManageService {
    @Autowired
    private DomainManageMapper domainManageMapper;
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;
    @Autowired
    private RedisCache redisCache;

    /**
     * 查询域名管理
     *
     * @param id 域名管理主键
     * @return 域名管理
     */
    @Override
    public DomainManage selectDomainManageById(Long id) {
        return domainManageMapper.selectDomainManageById(id);
    }

    /**
     * 查询域名管理列表
     *
     * @param domainManage 域名管理
     * @return 域名管理
     */
    @Override
    public List<DomainManage> selectDomainManageList(DomainManage domainManage) {
        return  domainManageMapper.selectDomainManageList(domainManage);
    }

    @Override
    @Cacheable(key = "'domain:selectDomainByHost:'+#type+':'+#host",value = "redisCache4Spring")
    public DomainManage selectDomainByHost(String host,Short type) {
        DomainManage domainManage = new DomainManage();
        domainManage.setType(type);
        domainManage.setDomain(host);
        List<DomainManage> domainManages = domainManageMapper.selectDomainManageList(domainManage);
        if (domainManages.size()>0){
            return domainManages.get(0);
        }else {
            return null;
        }
    }

    @Override
    public List<DomainManage> selectDomainManage(DomainManage domainManage) {
        Assert.test(domainManage.getType()==null||domainManage.getType()==0,"system.error");
        return domainManageMapper.selectDomainManageList(domainManage);
    }



    private void updateDomain(short type,DomainManage domainManage){
        String key="domain:selectDomainByHost:"+type+":"+domainManage.getDomain();
        redisCache.setCacheObject(key,domainManage);
    }

    /**
     * 新增域名管理
     *
     * @param domainManage 域名管理
     * @return 结果
     */
    @Override
    public int insertDomainManage(DomainManage domainManage) {

        domainManage.setId(snowflakeIdUtils.nextId());
        domainManage.setCreateTime(DateUtils.getNowDate());
        int i = domainManageMapper.insertDomainManage(domainManage);
        if (i==1){
            redisCache.deleteObject("domainManage");
            updateDomain(domainManage.getType(),domainManage);
        }
        return i;
    }

    /**
     * 修改域名管理
     *
     * @param domainManage 域名管理
     * @return 结果
     */
    @Override
    public int updateDomainManage(DomainManage domainManage) {
        domainManage.setUpdateTime(DateUtils.getNowDate());
        int i = domainManageMapper.updateDomainManage(domainManage);
        if (i==1){
            redisCache.deleteObject("domainManage");
            updateDomain(domainManage.getType(),domainManage);
        }
        return i;
    }

    /**
     * 批量删除域名管理
     *
     * @param ids 需要删除的域名管理主键
     * @return 结果
     */
    @Override
    public int deleteDomainManageByIds(Long[] ids) {
        int i = domainManageMapper.deleteDomainManageByIds(ids);
        if (i>=1){
            redisCache.deleteObject("domainManage");
        }
        redisCache.deleteObject("domainManage");
        return 1;
    }



    /**
     * 删除域名管理信息
     *
     * @param id 域名管理主键
     * @return 结果
     */
    @Override
    public int deleteDomainManageById(Long id) {
        int i = domainManageMapper.deleteDomainManageById(id);
        if (i==1){
            redisCache.deleteObject("domainManage");

        };
        return i;
    }
}

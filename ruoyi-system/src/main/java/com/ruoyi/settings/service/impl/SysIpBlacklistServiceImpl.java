package com.ruoyi.settings.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.CacheUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysHelp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.ruoyi.settings.mapper.SysIpBlacklistMapper;
import com.ruoyi.settings.domain.SysIpBlacklist;
import com.ruoyi.settings.service.ISysIpBlacklistService;

import javax.annotation.Resource;

/**
 * ip后台登录白名单Service业务层处理
 * 
 * @author nn
 * @date 2022-03-25
 */
@Service
public class SysIpBlacklistServiceImpl implements ISysIpBlacklistService 
{
    @Autowired
    private SysIpBlacklistMapper sysIpBlacklistMapper;

    @Resource
    private RedisCache redisCache;

    /**
     * 设置cache key
     *
     * @param userName ip
     * @return 缓存键key
     */
    private String getCacheKey(String userName)
    {
        return CacheKeyConstants.SYS_IP_BLACKLIST_KEY + userName;
    }

    /**
     * 查询ip后台登录白名单
     * 
     * @param id ip后台登录白名单主键
     * @return ip后台登录白名单
     */
    @Override
    public SysIpBlacklist selectSysIpBlacklistById(Long id)
    {
        return sysIpBlacklistMapper.selectSysIpBlacklistById(id);
    }

    /**
     * 查询ip后台登录白名单列表
     * 
     * @param sysIpBlacklist ip后台登录白名单
     * @return ip后台登录白名单
     */
    @Override
    public List<SysIpBlacklist> selectSysIpBlacklistList(SysIpBlacklist sysIpBlacklist)
    {
        return sysIpBlacklistMapper.selectSysIpBlacklistList(sysIpBlacklist);
    }

    /**
     * 新增ip后台登录白名单
     * 
     * @param sysIpBlacklist ip后台登录白名单
     * @return 结果
     */
    @Override
    public int insertSysIpBlacklist(SysIpBlacklist sysIpBlacklist)
    {
        redisCache.deleteObject(getCacheKey(sysIpBlacklist.getUserName()));
        return sysIpBlacklistMapper.insertSysIpBlacklist(sysIpBlacklist);
    }

    /**
     * 修改ip后台登录白名单
     * 
     * @param sysIpBlacklist ip后台登录白名单
     * @return 结果
     */
    @Override
    public int updateSysIpBlacklist(SysIpBlacklist sysIpBlacklist)
    {
        //先把修改前的缓存删除.
        redisCache.deleteObject(getCacheKey(sysIpBlacklist.getUserName()));
        return sysIpBlacklistMapper.updateSysIpBlacklist(sysIpBlacklist);
    }

    /**
     * 批量删除ip后台登录白名单
     * 
     * @param ids 需要删除的ip后台登录白名单主键
     * @return 结果
     */
    @Override
    public int deleteSysIpBlacklistByIds(Long[] ids)
    {
        for (Long id : ids) {
            SysIpBlacklist sysIpBlacklist = selectSysIpBlacklistById(id);
            redisCache.deleteObject(getCacheKey(sysIpBlacklist.getUserName()));
        }
        return sysIpBlacklistMapper.deleteSysIpBlacklistByIds(ids);
    }

    /**
     * 删除ip后台登录白名单信息
     * 
     * @param id ip后台登录白名单主键
     * @return 结果
     */
    @Override
    public int deleteSysIpBlacklistById(Long id)
    {
        SysIpBlacklist sysIpBlacklist = selectSysIpBlacklistById(id);
        redisCache.deleteObject(getCacheKey(sysIpBlacklist.getUserName()));
        return sysIpBlacklistMapper.deleteSysIpBlacklistById(id);
    }

    /**
     * 根据ip查询
     *
     * @param ip 地址
     * @return ip后台登录白名单
     */
    @Override
    public SysIpBlacklist selectSysIpBlacklistByIp(String ip){
        return sysIpBlacklistMapper.selectSysIpBlacklistByIp(ip);
    }

    /**
     * 根据ip查询
     *
     * @param sysIpBlacklist 地址
     * @return ip后台登录白名单
     */
    @Override
    public List<SysIpBlacklist> querySysIpBlacklist(SysIpBlacklist sysIpBlacklist){
        List<SysIpBlacklist> sysIpBlacklistList = sysIpBlacklistMapper.querySysIpBlacklist(sysIpBlacklist);
        return sysIpBlacklistList;
    }

    /**
     * 校验ip是否唯一
     *
     * @param sysIpBlacklist
     * @return 结果
     */
    @Override
    public String checkIpUnique(SysIpBlacklist sysIpBlacklist)
    {
        Long id = StringUtils.isNull(sysIpBlacklist.getId()) ? -1L : sysIpBlacklist.getId();
        SysIpBlacklist ip = sysIpBlacklistMapper.checkIpUnique(sysIpBlacklist);
        if (StringUtils.isNotNull(ip) && ip.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 验证ip是否在白名单中.
     * @param userName 用户名
     * @param loginIp 地址
     * @return ip后台登录白名单
     */
    @Override
    public boolean verifyIpBlacklist(String userName,String loginIp){
        boolean flag = false;
        List<String> myIpList =  redisCache.getCacheObject(getCacheKey(userName));
        if(myIpList != null && myIpList.size() > 0){
            for(String ip : myIpList){
                if(ip.equals(loginIp)){
                    flag = true;
                }
            }
        }else {
            SysIpBlacklist sysIpBlacklist = new SysIpBlacklist();
            sysIpBlacklist.setUserName(userName);
            List<SysIpBlacklist>  ipBlacklistList = querySysIpBlacklist(sysIpBlacklist);
            if(ipBlacklistList != null && ipBlacklistList.size() > 0){
                myIpList = new ArrayList<>();
                for(SysIpBlacklist ipBlack : ipBlacklistList){
                    myIpList.add(ipBlack.getIp());
                    if(ipBlack.getIp().equals(loginIp)){
                        flag = true;
                    }
                }
                redisCache.setCacheObject(getCacheKey(userName), myIpList,3600 * 24, TimeUnit.SECONDS);
            }
        }

        return flag;
    }
}

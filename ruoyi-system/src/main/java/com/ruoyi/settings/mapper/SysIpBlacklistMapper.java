package com.ruoyi.settings.mapper;

import java.util.List;
import com.ruoyi.settings.domain.SysIpBlacklist;

/**
 * ip后台登录白名单Mapper接口
 * 
 * @author nn
 * @date 2022-03-25
 */
public interface SysIpBlacklistMapper 
{
    /**
     * 查询ip后台登录白名单
     * 
     * @param id ip后台登录白名单主键
     * @return ip后台登录白名单
     */
    public SysIpBlacklist selectSysIpBlacklistById(Long id);

    /**
     * 查询ip后台登录白名单列表
     * 
     * @param sysIpBlacklist ip后台登录白名单
     * @return ip后台登录白名单集合
     */
    public List<SysIpBlacklist> selectSysIpBlacklistList(SysIpBlacklist sysIpBlacklist);

    /**
     * 新增ip后台登录白名单
     * 
     * @param sysIpBlacklist ip后台登录白名单
     * @return 结果
     */
    public int insertSysIpBlacklist(SysIpBlacklist sysIpBlacklist);

    /**
     * 修改ip后台登录白名单
     * 
     * @param sysIpBlacklist ip后台登录白名单
     * @return 结果
     */
    public int updateSysIpBlacklist(SysIpBlacklist sysIpBlacklist);

    /**
     * 删除ip后台登录白名单
     * 
     * @param id ip后台登录白名单主键
     * @return 结果
     */
    public int deleteSysIpBlacklistById(Long id);

    /**
     * 批量删除ip后台登录白名单
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysIpBlacklistByIds(Long[] ids);

    /**
     * 根据ip查询
     *
     * @param ip 地址
     * @return ip后台登录白名单
     */
    public SysIpBlacklist selectSysIpBlacklistByIp(String ip);

    /**
     * 查询用户登陆IP
     * @param sysIpBlacklist
     * @return
     */
    public List<SysIpBlacklist> querySysIpBlacklist(SysIpBlacklist sysIpBlacklist);

    /**
     * 校验ip是否唯一
     *
     * @param sysIpBlacklist 检测的ip
     * @return 结果
     */
    public SysIpBlacklist checkIpUnique(SysIpBlacklist sysIpBlacklist);
}

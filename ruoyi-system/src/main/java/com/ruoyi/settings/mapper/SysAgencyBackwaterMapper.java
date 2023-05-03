package com.ruoyi.settings.mapper;

import java.util.List;
import com.ruoyi.settings.domain.SysAgencyBackwater;
import com.ruoyi.settings.domain.SysServiceCharge;

/**
 * 代理返水设置Mapper接口
 * 
 * @author nn
 * @date 2022-06-26
 */
public interface SysAgencyBackwaterMapper 
{
    /**
     * 查询代理返水设置
     * 
     * @param id 代理返水设置主键
     * @return 代理返水设置
     */
    public SysAgencyBackwater selectSysAgencyBackwaterById(Long id);

    /**
     * 查询代理返水设置列表
     * 
     * @param sysAgencyBackwater 代理返水设置
     * @return 代理返水设置集合
     */
    public List<SysAgencyBackwater> selectSysAgencyBackwaterList(SysAgencyBackwater sysAgencyBackwater);

    /**
     * 新增代理返水设置
     * 
     * @param sysAgencyBackwater 代理返水设置
     * @return 结果
     */
    public int insertSysAgencyBackwater(SysAgencyBackwater sysAgencyBackwater);

    /**
     * 修改代理返水设置
     * 
     * @param sysAgencyBackwater 代理返水设置
     * @return 结果
     */
    public int updateSysAgencyBackwater(SysAgencyBackwater sysAgencyBackwater);

    /**
     * 删除代理返水设置
     * 
     * @param id 代理返水设置主键
     * @return 结果
     */
    public int deleteSysAgencyBackwaterById(Long id);

    /**
     * 批量删除代理返水设置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysAgencyBackwaterByIds(Long[] ids);

    /**
     * 校验该代理该类型是否唯一
     *
     * @param sysAgencyBackwater 检测的对象
     * @return 结果
     */
    public SysAgencyBackwater checkFeeTypeToAgencyUnique(SysAgencyBackwater sysAgencyBackwater);

    /**
     * 查询手续费设置
     *
     * @param sysAgencyBackwater 查询对象.
     * @return 手续费设置
     */
    public SysAgencyBackwater getAgencyBackwater(SysAgencyBackwater sysAgencyBackwater);
}

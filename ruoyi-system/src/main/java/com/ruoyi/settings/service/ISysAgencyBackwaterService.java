package com.ruoyi.settings.service;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.settings.domain.SysAgencyBackwater;

/**
 * 代理返水设置Service接口
 * 
 * @author nn
 * @date 2022-06-26
 */
public interface ISysAgencyBackwaterService 
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
     * 批量删除代理返水设置
     * 
     * @param ids 需要删除的代理返水设置主键集合
     * @return 结果
     */
    public int deleteSysAgencyBackwaterByIds(Long[] ids);

    /**
     * 删除代理返水设置信息
     * 
     * @param id 代理返水设置主键
     * @return 结果
     */
    public int deleteSysAgencyBackwaterById(Long id);

    /**
     * 校验该代理该类型是否唯一
     *
     * @param sysAgencyBackwater 检测的对象
     * @return 结果
     */
    public String checkFeeTypeToAgencyUnique(SysAgencyBackwater sysAgencyBackwater);

    /**
     * 根据返水类型获取代理的手续费.
     *
     * @param userId 代理Id
     * @param feeType 返水类型
     * @return 结果
     */
    public BigDecimal getAgencyBackwaterByFeeType(Long userId, String feeType);
}

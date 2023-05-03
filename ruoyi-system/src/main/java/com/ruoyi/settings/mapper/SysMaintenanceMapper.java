package com.ruoyi.settings.mapper;

import java.util.List;
import com.ruoyi.settings.domain.SysMaintenance;

/**
 * 系统维护Mapper接口
 * 
 * @author nn
 * @date 2022-04-12
 */
public interface SysMaintenanceMapper 
{
    /**
     * 查询系统维护
     * 
     * @param id 系统维护主键
     * @return 系统维护
     */
    public SysMaintenance selectSysMaintenanceById(Long id);

    /**
     * 查询系统维护列表
     * 
     * @param sysMaintenance 系统维护
     * @return 系统维护集合
     */
    public List<SysMaintenance> selectSysMaintenanceList(SysMaintenance sysMaintenance);

    /**
     * 新增系统维护
     * 
     * @param sysMaintenance 系统维护
     * @return 结果
     */
    public int insertSysMaintenance(SysMaintenance sysMaintenance);

    /**
     * 修改系统维护
     * 
     * @param sysMaintenance 系统维护
     * @return 结果
     */
    public int updateSysMaintenance(SysMaintenance sysMaintenance);

    /**
     * 删除系统维护
     * 
     * @param id 系统维护主键
     * @return 结果
     */
    public int deleteSysMaintenanceById(Long id);

    /**
     * 批量删除系统维护
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysMaintenanceByIds(Long[] ids);
}

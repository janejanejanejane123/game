package com.ruoyi.settings.mapper;

import java.util.List;
import com.ruoyi.settings.domain.SysBackwater;

/**
 * 返水设置Mapper接口
 * 
 * @author nn
 * @date 2022-06-24
 */
public interface SysBackwaterMapper 
{
    /**
     * 查询返水设置
     * 
     * @param id 返水设置主键
     * @return 返水设置
     */
    public SysBackwater selectSysBackwaterById(Long id);

    /**
     * 查询返水设置列表
     * 
     * @param sysBackwater 返水设置
     * @return 返水设置集合
     */
    public List<SysBackwater> selectSysBackwaterList(SysBackwater sysBackwater);

    /**
     * 新增返水设置
     * 
     * @param sysBackwater 返水设置
     * @return 结果
     */
    public int insertSysBackwater(SysBackwater sysBackwater);

    /**
     * 修改返水设置
     * 
     * @param sysBackwater 返水设置
     * @return 结果
     */
    public int updateSysBackwater(SysBackwater sysBackwater);

    /**
     * 删除返水设置
     * 
     * @param id 返水设置主键
     * @return 结果
     */
    public int deleteSysBackwaterById(Long id);

    /**
     * 批量删除返水设置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysBackwaterByIds(Long[] ids);

    /**
     * 校验该返水类型是否唯一
     *
     * @param sysBackwater 检测的对象
     * @return 结果
     */
    public SysBackwater checkFeeTypeBackwaterUnique(SysBackwater sysBackwater);
}

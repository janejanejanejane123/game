package com.ruoyi.settings.service;

import java.util.List;
import com.ruoyi.settings.domain.SysBackwater;
import com.ruoyi.settings.domain.SysBrokerage;

/**
 * 返水设置Service接口
 * 
 * @author ruoyi
 * @date 2022-06-24
 */
public interface ISysBackwaterService 
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
     * 批量删除返水设置
     * 
     * @param ids 需要删除的返水设置主键集合
     * @return 结果
     */
    public int deleteSysBackwaterByIds(Long[] ids);

    /**
     * 删除返水设置信息
     * 
     * @param id 返水设置主键
     * @return 结果
     */
    public int deleteSysBackwaterById(Long id);

    /**
     * 校验该返水类型是否唯一
     *
     * @param sysBackwater 检测的对象
     * @return 结果
     */
    public String checkFeeTypeBackwaterUnique(SysBackwater sysBackwater);
}

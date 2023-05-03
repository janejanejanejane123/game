package com.ruoyi.system.service;

import java.util.List;

import com.ruoyi.settings.domain.SysIpBlacklist;
import com.ruoyi.system.domain.SysHelp;

/**
 * 会员帮助Service接口
 * 
 * @author nn
 * @date 2022-03-13
 */
public interface ISysHelpService 
{
    /**
     * 查询会员帮助
     * 
     * @param helpId 会员帮助主键
     * @return 会员帮助
     */
    public SysHelp selectSysHelpByHelpId(Long helpId);

    /**
     * 查询会员帮助列表
     * 
     * @param sysHelp 会员帮助
     * @return 会员帮助集合
     */
    public List<SysHelp> selectSysHelpList(SysHelp sysHelp);

    /**
     * 新增会员帮助
     * 
     * @param sysHelp 会员帮助
     * @return 结果
     */
    public int insertSysHelp(SysHelp sysHelp);

    /**
     * 修改会员帮助
     * 
     * @param sysHelp 会员帮助
     * @return 结果
     */
    public int updateSysHelp(SysHelp sysHelp);

    /**
     * 批量删除会员帮助
     * 
     * @param helpIds 需要删除的会员帮助主键集合
     * @return 结果
     */
    public int deleteSysHelpByHelpIds(Long[] helpIds);

    /**
     * 删除会员帮助信息
     * 
     * @param helpId 会员帮助主键
     * @return 结果
     */
    public int deleteSysHelpByHelpId(Long helpId);

    /**
     * 根据帮助类型查询会员帮助
     *
     * @param helpType 会员帮助类型
     * @return 会员帮助
     */
    public SysHelp selectSysHelpByHelpType(String helpType);

    /**
     * 根据帮助类型查询会员帮助
     *
     * @param helpType 会员帮助类型
     * @return 会员帮助
     */
    public String selectHelpContentByHelpType(String helpType);

    /**
     * 校验ip是否唯一
     *
     * @param sysHelp 会员帮助
     * @return 结果
     */
    public String checkHelpTypeUnique(SysHelp sysHelp);
}

package com.ruoyi.system.mapper;

import java.util.List;

import com.ruoyi.settings.domain.SysIpBlacklist;
import com.ruoyi.system.domain.SysHelp;

/**
 * 会员帮助Mapper接口
 * 
 * @author ruoyi
 * @date 2022-03-13
 */
public interface SysHelpMapper 
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
     * 删除会员帮助
     * 
     * @param helpId 会员帮助主键
     * @return 结果
     */
    public int deleteSysHelpByHelpId(Long helpId);

    /**
     * 批量删除会员帮助
     * 
     * @param helpIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysHelpByHelpIds(Long[] helpIds);

    /**
     * 根据帮助类型查询会员帮助
     *
     * @param helpType 会员帮助类型
     * @return 会员帮助
     */
    public SysHelp selectSysHelpByHelpType(String helpType);

    /**
     * 校验帮助是否唯一
     *
     * @param helpType 检测的帮助
     * @return 结果
     */
    public SysHelp checkHelpTypeUnique(String helpType);
}

package com.ruoyi.member.mapper;

import java.util.List;
import com.ruoyi.member.domain.TUserDataConfig;

/**
 * 用户自我配置Mapper接口
 * 
 * @author ruoyi
 * @date 2022-06-24
 */
public interface TUserDataConfigMapper 
{
    /**
     * 查询用户自我配置
     * 
     * @param uid 用户自我配置主键
     * @return 用户自我配置
     */
    public TUserDataConfig selectTUserDataConfigByUid(Long uid);

    /**
     * 查询用户自我配置列表
     * 
     * @param tUserDataConfig 用户自我配置
     * @return 用户自我配置集合
     */
    public List<TUserDataConfig> selectTUserDataConfigList(TUserDataConfig tUserDataConfig);

    /**
     * 新增用户自我配置
     * 
     * @param tUserDataConfig 用户自我配置
     * @return 结果
     */
    public int insertTUserDataConfig(TUserDataConfig tUserDataConfig);

    /**
     * 修改用户自我配置
     * 
     * @param tUserDataConfig 用户自我配置
     * @return 结果
     */
    public int updateTUserDataConfig(TUserDataConfig tUserDataConfig);

    /**
     * 删除用户自我配置
     * 
     * @param uid 用户自我配置主键
     * @return 结果
     */
    public int deleteTUserDataConfigByUid(Long uid);

    /**
     * 批量删除用户自我配置
     * 
     * @param uids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTUserDataConfigByUids(Long[] uids);
}

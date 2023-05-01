package com.ruoyi.game.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.game.domain.Account;

import java.util.List;


/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
public interface AccountMapper  extends BaseMapper<Account>
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public Account selectAccountById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param account 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<Account> selectAccountList(Account account);

    /**
     * 新增【请填写功能名称】
     * 
     * @param account 【请填写功能名称】
     * @return 结果
     */
    public int insertAccount(Account account);

    /**
     * 修改【请填写功能名称】
     * 
     * @param account 【请填写功能名称】
     * @return 结果
     */
    public int updateAccount(Account account);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteAccountById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAccountByIds(String[] ids);
}

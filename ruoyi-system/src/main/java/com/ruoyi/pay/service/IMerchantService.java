package com.ruoyi.pay.service;

import java.util.List;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.pay.domain.Merchant;

/**
 * 商户Service接口
 *
 * @author
 * @date 2022-03-07
 */
public interface IMerchantService
{
    /**
     * 查询商户
     *
     * @param id 商户主键
     * @return 商户
     */
    public Merchant selectMerchantById(Long id);

    /**
     * 查询商户列表
     *
     * @param merchant 商户
     * @return 商户集合
     */
    public List<Merchant> selectMerchantList(Merchant merchant);

    /**
     * 新增商户
     *
     * @param merchant 商户
     * @return 结果
     */
    public int insertMerchant(Merchant merchant);

    /**
     * 修改商户
     *
     * @param merchant 商户
     * @return 结果
     */
    public int updateMerchant(Merchant merchant);

    public int updateMerchantStauts(SysUser sysUser);

    /**
     * 批量删除商户
     *
     * @param ids 需要删除的商户主键集合
     * @return 结果
     */
    public int deleteMerchantByIds(Long[] ids);

    /**
     * 删除商户信息
     *
     * @param id 商户主键
     * @return 结果
     */
    public int deleteMerchantById(Long id);

    Merchant selectMerchantByNo(String merNo);
}

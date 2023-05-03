package com.ruoyi.pay.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.pay.domain.Merchant;

/**
 * 商户Mapper接口
 *
 * @author
 * @date 2022-03-07
 */
public interface MerchantMapper {
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

    public int updateMerchantByUid(Merchant merchant);


    /**
     * 删除商户
     *
     * @param id 商户主键
     * @return 结果
     */
    public int deleteMerchantById(Long id);

    /**
     * 批量删除商户
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMerchantByIds(Long[] ids);

    Merchant selectMerchantByMerNo(String merNo);

}

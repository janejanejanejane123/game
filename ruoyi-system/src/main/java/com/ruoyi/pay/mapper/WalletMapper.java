package com.ruoyi.pay.mapper;

import java.util.List;

import com.ruoyi.pay.domain.Wallet;

/**
 * 钱包Mapper接口
 *
 * @author 
 * @date 2022-03-08
 */
public interface WalletMapper {
    /**
     * 查询钱包
     *
     * @param id 钱包主键
     * @return 钱包
     */
    public Wallet selectWalletById(Long id);

    /**
     * 查询钱包列表
     *
     * @param wallet 钱包
     * @return 钱包集合
     */
    public List<Wallet> selectWalletList(Wallet wallet);

    /**
     * 新增钱包
     *
     * @param wallet 钱包
     * @return 结果
     */
    public int insertWallet(Wallet wallet);

    /**
     * 修改钱包
     *
     * @param wallet 钱包
     * @return 结果
     */
    public int updateWallet(Wallet wallet);

    /**
     * 删除钱包
     *
     * @param id 钱包主键
     * @return 结果
     */
    public int deleteWalletById(Long id);

    /**
     * 批量删除钱包
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWalletByIds(Long[] ids);

    int modifyWalletByUserId(Wallet wallet);

    Wallet selectWalletByaddress(String address);

    Wallet selectWalletByUid(Long uid);

    Wallet selectWalletSum(Wallet wallet);

    String selectMerchantByUid(Long uid);
}

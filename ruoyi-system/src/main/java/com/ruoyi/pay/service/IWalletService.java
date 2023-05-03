package com.ruoyi.pay.service;

import java.util.List;

import com.ruoyi.common.enums.WalletEnum;
import com.ruoyi.pay.domain.Wallet;
import com.ruoyi.pay.domain.vo.WalletVo;

import javax.validation.constraints.NotNull;

/**
 * 钱包Service接口
 *
 * @author ruoyi
 * @date 2022-03-08
 */
public interface IWalletService {
    /**
     * 查询钱包
     *
     * @param id 钱包主键
     * @return 钱包
     */
    Wallet selectWalletById(Long id);

    Wallet selectWlletByaddress(String address);

    /**
     * 查询钱包列表
     *
     * @param wallet 钱包
     * @return 钱包集合
     */
    List<Wallet> selectWalletList(Wallet wallet);

    /**
     * 新增钱包
     *
     * @param wallet 钱包
     * @return 结果
     */
    int insertWallet(Wallet wallet);

    int updateWalletByUid(WalletVo walletVo );

    Wallet selectWalletByUid(Long id);

    String selectMerchantByUid(Long uid);

    Wallet selectWalletSum(Wallet wallet);

    Wallet selectByName(String name);
}

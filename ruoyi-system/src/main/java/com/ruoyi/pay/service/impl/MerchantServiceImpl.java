package com.ruoyi.pay.service.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.CreateSecrteKey;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.uuid.Seq;
import com.ruoyi.pay.domain.Wallet;
import com.ruoyi.pay.service.IOrderSeqService;
import com.ruoyi.pay.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pay.mapper.MerchantMapper;
import com.ruoyi.pay.domain.Merchant;
import com.ruoyi.pay.service.IMerchantService;

import javax.annotation.Resource;

/**
 * 商户Service业务层处理
 *
 * @author
 * @date 2022-03-07
 */
@Service
public class MerchantServiceImpl implements IMerchantService {
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private IWalletService iWalletService;
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;
    @Resource
    private RedisCache redisCache;
    @Resource
    private IOrderSeqService orderSeqService;

    private final String MERCHANGE_KEY = "MERCHANT:select:";

    /**
     * 查询商户
     *
     * @param id 商户主键
     * @return 商户
     */
    @Override
    public Merchant selectMerchantById(Long id) {
        return merchantMapper.selectMerchantById(id);
    }

    /**
     * 查询商户列表
     *
     * @param merchant 商户
     * @return 商户
     */
    @Override
    public List<Merchant> selectMerchantList(Merchant merchant) {
        return merchantMapper.selectMerchantList(merchant);
    }

    /**
     * 新增商户
     *
     * @param merchant 商户
     * @return 结果
     */
    @Override
    public int insertMerchant(Merchant merchant) {
        merchant.setCreateTime(new Date());
        merchant.setUpdateTime(new Date());
        merchant.setId(System.currentTimeMillis());
        Map<String, String> rsaMp = CreateSecrteKey.getRsa();
        merchant.setPriKey(rsaMp.get("privateKey"));
        merchant.setPubKey(rsaMp.get("publicKey"));
        merchant.setCreateTime(DateUtils.getNowDate());
        merchant.setState(0);
        merchant.setSeqNo(orderSeqService.getSeqNo());

        Wallet wallet = new Wallet();
        wallet.setUid(merchant.getUid());
        wallet.setName(merchant.getMerNo());
        wallet.setType(1L);
        iWalletService.insertWallet(wallet);
        return merchantMapper.insertMerchant(merchant);
    }

    /**
     * 修改商户
     *
     * @param merchant 商户
     * @return 结果
     */
    @Override
    public int updateMerchant(Merchant merchant) {
        clearConfigCache();
        merchant.setUpdateTime(DateUtils.getNowDate());
        return merchantMapper.updateMerchant(merchant);
    }

    @Override
    public int updateMerchantStauts(SysUser sysUser) {
        clearConfigCache();
        Merchant merchant = new Merchant();
        merchant.setUid(sysUser.getUserId());
        merchant.setState(Integer.parseInt(sysUser.getStatus()));
        merchant.setUpdateName(sysUser.getUpdateBy());
        merchant.setUpdateTime(new Date());
        return merchantMapper.updateMerchantByUid(merchant);
    }

    /**
     * 批量删除商户
     *
     * @param ids 需要删除的商户主键
     * @return 结果
     */
    @Override
    public int deleteMerchantByIds(Long[] ids) {
        return merchantMapper.deleteMerchantByIds(ids);
    }

    /**
     * 删除商户信息
     *
     * @param id 商户主键
     * @return 结果
     */
    @Override
    public int deleteMerchantById(Long id) {
        return merchantMapper.deleteMerchantById(id);
    }

    @Override
    public Merchant selectMerchantByNo(String merNo) {
        Object cacheObject = redisCache.getCacheObject(MERCHANGE_KEY + merNo);
        if (cacheObject != null) {
            return (Merchant) cacheObject;
        } else {
            Merchant merchant = merchantMapper.selectMerchantByMerNo(merNo);
            redisCache.setCacheObject(MERCHANGE_KEY + merNo, merchant, 3600 * 2, TimeUnit.SECONDS);
            return merchant;
        }

    }

    public void clearConfigCache() {
        Collection<String> keys = redisCache.keys(MERCHANGE_KEY + "*");
        redisCache.deleteObject(keys);
    }
}

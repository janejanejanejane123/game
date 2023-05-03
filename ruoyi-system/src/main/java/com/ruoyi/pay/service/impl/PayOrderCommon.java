package com.ruoyi.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.enums.PayResponEnums;
import com.ruoyi.common.enums.TreasureTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.RSAUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.pay.domain.Merchant;
import com.ruoyi.pay.domain.TReport;
import com.ruoyi.pay.domain.Wallet;
import com.ruoyi.pay.domain.vo.PayOrderVo;
import com.ruoyi.pay.domain.vo.WalletVo;
import com.ruoyi.pay.service.IMerchantService;
import com.ruoyi.pay.service.ITReportService;
import com.ruoyi.pay.service.IWalletService;
import com.ruoyi.pay.service.IWithdrawalRecordService;
import com.ruoyi.settings.service.ISysServiceChargeService;
import com.ruoyi.system.service.IMemberService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class PayOrderCommon {
    private Logger logger = LoggerFactory.getLogger(PayOrderCommon.class);
    @Resource
    private IMemberService iMemberService;
    @Resource
    private IMerchantService iMerchantService;
    @Resource
    private IWalletService iWalletService;
    @Resource
    private ISysServiceChargeService iSysServiceChargeService;
    @Resource
    private ITReportService itReportService;


    protected boolean verifySign(PayOrderVo orderVo, String pubKey) {
        JSONObject o = JSONObject.parseObject(JSONObject.toJSONString(orderVo));
        logger.info("验签参数:" + o.toString());
        boolean isSign;
        try {
            Map signMap = o;
            isSign = RSAUtil.verify(signMap, pubKey, "sign");
        } catch (Exception e) {
            logger.error("签名异常{0}", JSON.toJSONString(orderVo), e);
            throw new ServiceException(PayResponEnums.SIGN_ERR.getMsg(), PayResponEnums.SIGN_ERR.getCode());
        }
        if (!isSign) {
            throw new ServiceException(PayResponEnums.SIGN_ERR.getMsg(), PayResponEnums.SIGN_ERR.getCode());
        }
        return true;
    }

    /**
     * IP加白
     *
     * @param ip
     * @param merchant
     */
    protected void checkIPWhite(String ip, Merchant merchant) {
        if (StringUtils.isNotBlank(merchant.getIp())) {
            List<String> ips = Arrays.asList(merchant.getIp().split(","));
            if (!ips.contains(ip)) {
                logger.error("IP未加白{}", ip);
                throw new ServiceException(PayResponEnums.IP.getMsg(), PayResponEnums.IP.getCode());
            }
        } else {
            logger.error("IP未加白{}", ip);
            throw new ServiceException(PayResponEnums.IP.getMsg(), PayResponEnums.IP.getCode());
        }
    }

    @NotNull
    protected TUser gettUser(PayOrderVo orderVo, String merSeq) {
        String apiUsername = StringUtils.apiUsername(orderVo.getAccount(), merSeq);
        TUser tUser = iMemberService.queryApiMemberByUsername(apiUsername);
        if (tUser == null) {
            //会员不存在则需注册
            throw new ServiceException("账号不存在", PayResponEnums.ACCOUNT_NO_EXT.getCode());
        } else if (tUser.getDisabled() == 0L) {
            throw new ServiceException("账号存在异常", PayResponEnums.ACCOUNT_ABNORMAL.getCode());
        }
        return tUser;
    }

    @NotNull
    protected Merchant getMerchant(String merNo) {
        Merchant merchant = iMerchantService.selectMerchantByNo(merNo);
        if (merchant == null) {
            throw new ServiceException("签名错误", 3);
        }
        if (merchant.getState() != 0) {
            throw new ServiceException("商户账号异常!");
        }
        return merchant;
    }



    protected Wallet getWalletByNameorAddress(PayOrderVo orderVo, String merSeq) {
        Wallet wallet;
        if (StringUtils.isNotBlank(orderVo.getAccount())) {
            String apiUsername = StringUtils.apiUsername(orderVo.getAccount(), merSeq);
            wallet = iWalletService.selectByName(apiUsername);
        } else {
            wallet = iWalletService.selectWlletByaddress(orderVo.getAddress());
        }
        return wallet;
    }

    /**
     * 查询手续费
     *
     * @param key    key
     * @param amount 金额
     * @param uid    uid
     * @return BigDecimal
     */
    protected BigDecimal selectFee(String key, BigDecimal amount, Long uid) {
        BigDecimal fee = iSysServiceChargeService.getUserIdRateByFeeType(uid, key);
        return amount.multiply(new BigDecimal("0.001")).multiply(fee).setScale(2, BigDecimal.ROUND_UP);
    }

    /**
     * @param orderNo
     * @param amount
     * @param name             账号
     * @param uid
     * @param treasureTypeEnum 操作枚兴趣
     * @param fee              手续费
     * @return
     */
    protected WalletVo updateBal(String orderNo, BigDecimal amount, String name, Long uid, TreasureTypeEnum treasureTypeEnum, BigDecimal fee, Long merId) {
        WalletVo walletVo = new WalletVo();
        walletVo.setRefId(orderNo);
        walletVo.setRemark(treasureTypeEnum.getMsg());
        walletVo.setAmount(amount);
        walletVo.setIp("");
        walletVo.setTreasureTypeEnum(treasureTypeEnum);
        walletVo.setUserName(name);
        walletVo.setUid(uid);
        walletVo.setFee(fee);
        walletVo.setMerId(merId);
        iWalletService.updateWalletByUid(walletVo);
        return walletVo;
    }

    /**
     * @param amount
     * @param fee
     * @param key
     * @param merNo
     */
    protected void saveReport(BigDecimal amount, BigDecimal fee, String key, String merNo) {
        TReport tReport = new TReport();
        tReport.setAmount(amount);
        tReport.setFee(fee);
        tReport.setKey(key);
        tReport.setPeriod(new Date());
        tReport.setMerNo(merNo);
        itReportService.save(tReport);
    }
}

package com.ruoyi.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.FeeTypeConstants;
import com.ruoyi.common.constant.ReportConstant;
import com.ruoyi.common.enums.PayResponEnums;
import com.ruoyi.common.enums.TreasureTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.MapSortUtil;
import com.ruoyi.common.utils.RSAUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.Seq;
import com.ruoyi.pay.domain.Merchant;
import com.ruoyi.pay.domain.Wallet;
import com.ruoyi.pay.domain.WithdrawalRecord;
import com.ruoyi.pay.domain.vo.PayNotifyVO;
import com.ruoyi.pay.domain.vo.PayOrderVo;
import com.ruoyi.pay.domain.vo.WalletVo;
import com.ruoyi.pay.service.IOrderWithdrawalService;
import com.ruoyi.pay.service.IWithdrawalRecordService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderWithdrawalServiceImpl extends PayOrderCommon implements IOrderWithdrawalService {
    private Logger logger = LoggerFactory.getLogger(OrderWithdrawalServiceImpl.class);
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private IWithdrawalRecordService iWithdrawalRecordService;

    /**
     * 下分
     *
     * @param orderVo 订单VO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void subPoints(PayOrderVo orderVo, String ip) {
        String merNo = orderVo.getMerNo();
        BigDecimal amount = new BigDecimal(orderVo.getAmount());
        Merchant merchant = getMerchant(merNo);
        String orderNo = orderVo.getOrderid();
        logger.info("验证IP");
        checkIPWhite(ip, merchant);
        logger.info("验签" + orderNo);
        verifySign(orderVo, merchant.getPubKey());
        gettUser(orderVo, merchant.getSeqNo());
        extWithdOrder(orderVo, merNo);
        Wallet wallet = getWalletByNameorAddress(orderVo, merchant.getSeqNo());
        if (wallet != null) {
            //生成单号
            String sysOrdering = Seq.generatorOrderNo(merchant.getMerNo());
            //获取手续费
            BigDecimal fee = selectFee(FeeTypeConstants.PLAYER_WITHDRAW, amount, merchant.getUid());
            logger.info("下分生成的单号:" + sysOrdering);
            //更新会员钱包
            updateBal(sysOrdering, amount, wallet.getName(), wallet.getUid(), TreasureTypeEnum.BALANCE_INTO, BigDecimal.ZERO, merchant.getUid());
            //更新商户钱包
            WalletVo walletVo = updateBal(sysOrdering, amount, merNo, merchant.getUid(), TreasureTypeEnum.TRANSFER_PAYMENT, fee, merchant.getUid());
            WithdrawalRecord record = saveWithRecord(orderVo, wallet, sysOrdering, fee, walletVo,ip);
            this.saveReport(amount, fee, ReportConstant.MEMBER_TX, merNo);
            notifyWithdraw(merchant, record);
        } else {
            throw new ServiceException(PayResponEnums.ADDRESS_ERR.getMsg(), PayResponEnums.ADDRESS_ERR.getCode());
        }
    }

    /**
     * 发送延迟消息回调
     *
     * @param merchant
     * @param amountRecord
     */
    private void notifyWithdraw(Merchant merchant, WithdrawalRecord amountRecord) {
        try {
            if (StringUtils.isNotBlank(amountRecord.getNotifyUrl())) {
                PayNotifyVO payNotifyVO = new PayNotifyVO();
                payNotifyVO.setAmount(amountRecord.getAmount().toString());
                payNotifyVO.setOrderid(amountRecord.getOrderNo());
                payNotifyVO.setStatus(amountRecord.getStatus());
                payNotifyVO.setMerNo(amountRecord.getMerNo());
                payNotifyVO.setSysOrder(amountRecord.getSysOrder());
                payNotifyVO.setPayTime(amountRecord.getCreateTime().getTime());
                payNotifyVO.setNotifyUrl(amountRecord.getNotifyUrl());
                payNotifyVO.setNote(amountRecord.getRemark());

                Map notifyMap = new <String, Object>HashMap();
                notifyMap.put("amount", amountRecord.getAmount().toString());
                notifyMap.put("merNo", amountRecord.getMerNo());
                notifyMap.put("payTime", amountRecord.getCreateTime().getTime() + "");
                notifyMap.put("orderid", amountRecord.getOrderNo());
                notifyMap.put("sysOrder", amountRecord.getSysOrder());
                notifyMap.put("status", amountRecord.getStatus());
                String stringByMap = MapSortUtil.getStringByMap(notifyMap);
                String sign = RSAUtil.signBySHA1WithRSA(stringByMap, merchant.getPriKey());
                payNotifyVO.setSign(sign);

                MessageBuilder builder = MessageBuilder.withPayload(payNotifyVO);
                rocketMQTemplate.syncSend("PAY_NOTIFY_STATUS:payNotifyStatus", builder.build(), 3000, 2);
            }
        } catch (Exception e) {
            logger.error("发送异常");
        }
    }

    protected void extWithdOrder(PayOrderVo orderVo, String merNo) {
        WithdrawalRecord record = new WithdrawalRecord();
        record.setOrderNo(orderVo.getOrderid());
        record.setMerNo(merNo);
        List records = iWithdrawalRecordService.selectWithdrawalRecordList(record);
        if (records.size() > 0) {
            throw new ServiceException(PayResponEnums.ORDER_EXT.getMsg(), PayResponEnums.ORDER_EXT.getCode());
        }
    }

    /**
     * @param orderVo    计单VO
     * @param wallet     钱包类
     * @param sysOrderno 系统订单号
     * @param fee        手续费
     * @param walletVo   钱包VO
     */
    private WithdrawalRecord saveWithRecord(PayOrderVo orderVo, Wallet wallet, String sysOrderno, BigDecimal fee, WalletVo walletVo,String ip) {
        WithdrawalRecord record = new WithdrawalRecord();
        record.setCreateTime(new Date());
        record.setAmount(walletVo.getAmount());
        record.setStatus(1);
        record.setOrderNo(orderVo.getOrderid());
        record.setMid(walletVo.getUid());
        record.setIp(walletVo.getIp());
        record.setVersion(0);
        record.setMerNo(orderVo.getMerNo());
        record.setNotifyUrl("");
        record.setUserName(wallet.getName());
        record.setFee(fee);
        record.setAddress(wallet.getAddress());
        record.setUid(wallet.getUid());
        record.setRemark(orderVo.getNote());
        record.setSysOrder(sysOrderno);
        record.setNotifyUrl(orderVo.getNotifyurl());
        record.setPayTime(new Date());
        record.setIp("");
        iWithdrawalRecordService.insertWithdrawalRecord(record);
        return record;
    }

}

package com.ruoyi.pay.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.FeeTypeConstants;
import com.ruoyi.common.constant.ReportConstant;
import com.ruoyi.common.core.auth.ApiAuthKey;
import com.ruoyi.common.core.auth.ApiAuthenticationToken;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.enums.PayResponEnums;
import com.ruoyi.common.enums.TreasureTypeEnum;
import com.ruoyi.common.enums.UserOrderConst;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.uuid.Seq;
import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.member.service.ITUserService;
import com.ruoyi.member.util.RocketMqService;
import com.ruoyi.order.domain.GrabOrder;
import com.ruoyi.order.domain.vo.OrderMessageVo;
import com.ruoyi.order.service.IGrabOrderService;
import com.ruoyi.pay.domain.*;
import com.ruoyi.pay.domain.vo.PayNotifyVO;
import com.ruoyi.pay.domain.vo.PayOrderVo;
import com.ruoyi.pay.domain.vo.WalletVo;
import com.ruoyi.pay.service.*;
import com.ruoyi.settings.domain.DomainManage;
import com.ruoyi.settings.service.IDomainManageService;
import com.ruoyi.settings.service.ISysBrokerageService;
import com.ruoyi.settings.service.ISysServiceChargeService;
import com.ruoyi.settings.service.IWithdrwalConfigService;
import com.ruoyi.system.service.IMemberService;
import com.ruoyi.system.service.impl.SysConfigServiceImpl;
import lombok.val;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImpl extends PayOrderCommon implements IOrderService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private IMerchantService iMerchantService;
    @Resource
    private IWalletService iWalletService;
    @Resource
    private IAmountRecordService iAmountRecordService;
    @Resource
    private IWithdrawalRecordService iWithdrawalRecordService;
    @Resource
    private ISysServiceChargeService iSysServiceChargeService;
    @Resource
    private SysConfigServiceImpl iSysConfigService;
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;
    @Resource
    private ITUserService itUserService;
    @Resource
    private ITReportService itReportService;
    @Resource
    private ISysBrokerageService sysBrokerageService;
    @Resource
    private IGrabOrderService grabOrderService;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private RocketMqService rocketMqService;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private IDomainManageService iDomainManageService;
    @Resource
    private IMemberService iMemberService;
    @Resource
    private IWithdrwalConfigService iWithdrwalConfigService;


    /**
     * 登录与注册
     *
     * @param orderVo 订单VO
     */
    @Override
    public AjaxResult loginandReg(PayOrderVo orderVo, String ip) {
        logger.info("登录检测商户" + orderVo.getMerNo());
        Merchant merchant = getMerchant(orderVo.getMerNo());
        checkIPWhite(ip, merchant);
        //验签
        logger.info("验签");
        boolean versing = verifySign(orderVo, merchant.getPubKey());
        Assert.isFalse(versing, PayResponEnums.SIGN_ERR.getMsg());
        boolean isReg = isRegister(orderVo, merchant);
        if (!isReg) {
            gettUser(orderVo, merchant.getSeqNo());
        }
        //登录操作
        logger.info("获取TOKEN");
        String token = getToken(orderVo.getAccount(), merchant.getUid(), merchant.getSeqNo());
        logger.info("获取TOKEN成功");
        String url = getUrl(ip, token, "temp", merchant.getMerNo());
        return getAjaxResult(orderVo, isReg, url, merchant.getSeqNo());
    }

    @Override
    public AjaxResult queryBal(PayOrderVo orderVo, String ip) {
        logger.info("查询余额检测商户" + orderVo.getMerNo());
        Merchant merchant = getMerchant(orderVo.getMerNo());
        checkIPWhite(ip, merchant);
        //验签
        logger.info("验签");
        boolean versign = verifySign(orderVo, merchant.getPubKey());
        Assert.isFalse(versign, PayResponEnums.SIGN_ERR.getMsg());
        Wallet wallet = getWalletByNameorAddress(orderVo, merchant.getSeqNo());
        if (wallet == null) {
            throw new ServiceException("账号不存在,请先注册", PayResponEnums.ACCOUNT_NO_EXT.getCode());
        }
        return AjaxResult.success().put("balance", wallet.getBalance());
    }


    private AjaxResult getAjaxResult(PayOrderVo orderVo, boolean isReg, String url, String seqNo) {
        if (isReg) {
            Wallet wallet = getWalletByNameorAddress(orderVo, seqNo);
            return AjaxResult.success().put("pay_url", url).put("address", wallet.getAddress());
        } else {
            return AjaxResult.success().put("pay_url", url);
        }
    }

    @NotNull
    private String getUrl(String ip, String token, String type, String merNo) {
        DomainManage domainManage = new DomainManage();
        domainManage.setType(new Short("1"));
        domainManage.setStatus(0);
        domainManage.setMerNo(merNo);
        List<DomainManage> domainManages = iDomainManageService.selectDomainManage(domainManage);
        Assert.isTrue(domainManages.size() == 0, "无可用的地址");
        String url;
        logger.info("登录成功");
        if (ip.equals("127.0.0.1")) {
            url = "https://" + domainManages.get(0).getDomain() + ":1689/myepay/" + type + "?token=" + token;
        } else {
            url = "https://" + domainManages.get(0).getDomain() + "/" + type + "?token=" + token;
        }
        return url;
    }


    /**
     * 请求充值
     *
     * @param orderVo 订单VO
     * @param ip      iP地址
     * @return orderVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addPoints(PayOrderVo orderVo, String ip) {
        String merNo = orderVo.getMerNo();
        BigDecimal amount = new BigDecimal(orderVo.getAmount());
        logger.info("检测商户是否存在");
        Merchant merchant = getMerchant(merNo);
        checkIPWhite(ip, merchant);
        logger.info("检测会员是否存在");
       boolean isReg = isRegister(orderVo, merchant);
        if (!isReg) {
            gettUser(orderVo, merchant.getSeqNo());
        }
        //判断订单是否存在
        if (checkOrderExt(orderVo.getOrderid(), merchant.getMerNo(), merchant.getUid())) {
            //验签
            logger.info("验签");
            if (!verifySign(orderVo, merchant.getPubKey())) {
                return AjaxResult.error(PayResponEnums.SIGN_ERR.getCode(), PayResponEnums.SIGN_ERR.getMsg());
            }
            //生成待支付订单信息
            AmountRecord amountRecord = new AmountRecord();
            amountRecord.setAmount(amount);
            amountRecord.setStatus(0);
            amountRecord.setIp(ip);
            createAmount(orderVo, merNo, amount, merchant, amountRecord);
            iAmountRecordService.insertAmountRecord(amountRecord);
            logger.info("充值订单保存数据库成功");
            logger.info("获取token");
            String token = getToken(orderVo.getAccount(), merchant.getUid(), merchant.getSeqNo());
            String url = getUrl(ip, token, "qrcodeshow", merchant.getMerNo()) + "&id=" + amountRecord.getSysOrder();
            return getAjaxResult(orderVo, false, url, merchant.getSeqNo());
        } else {
            return AjaxResult.error(PayResponEnums.ORDER_EXT.getCode(), PayResponEnums.ORDER_EXT.getMsg());
        }

    }


    /**
     * 一键上分
     *
     * @param orderVo 订单VO
     * @param ip      IP地址
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult onekeyUp(PayOrderVo orderVo, String ip) {
        String merNo = orderVo.getMerNo();
        BigDecimal amount = new BigDecimal(orderVo.getAmount());
        Merchant merchant = getMerchant(merNo);
        checkIPWhite(ip, merchant);
        verifySign(orderVo, merchant.getPubKey());
        TUser tUser = gettUser(orderVo, merchant.getSeqNo());
        boolean b = iWithdrwalConfigService.verUserisRecharge(orderVo.getMerNo(), tUser);
        if (!b) {
            throw new ServiceException(PayResponEnums.WITHDRAWAL_LIMIT.getMsg(), PayResponEnums.WITHDRAWAL_LIMIT.getCode());
        }
        //判断订单是否存在
        if (checkOrderExt(orderVo.getOrderid(), merchant.getMerNo(), merchant.getUid())) {
            //验签
            if (verifySign(orderVo, merchant.getPubKey())) {
                //生成待支付订单信息
                AmountRecord amountRecord = new AmountRecord();
                amountRecord.setAmount(amount);
                amountRecord.setStatus(1);
                createAmount(orderVo, merNo, amount, merchant, amountRecord);
                amountRecord.setPayUid(tUser.getUid());
                amountRecord.setIp(ip);
                amountRecord.setUserName(tUser.getUsername());
                amountRecord.setPayTime(new Date());
                iAmountRecordService.insertAmountRecord(amountRecord);

                updateMemberWallet(amountRecord, TreasureTypeEnum.BALANCE_OUT);
                updateMercantWallet(merchant, amountRecord, TreasureTypeEnum.TRANSFER_RECEIPT);
                this.saveReport(amount, amountRecord.getFee(), ReportConstant.MEMBER_RE, merNo);
                notify(merchant, amountRecord);
                return AjaxResult.success("充值成功").put("status", "1");
            } else {
                return AjaxResult.error(PayResponEnums.SIGN_ERR.getCode(), PayResponEnums.SIGN_ERR.getMsg());
            }
        } else {
            return AjaxResult.error(PayResponEnums.ORDER_EXT.getCode(), PayResponEnums.ORDER_EXT.getMsg());
        }
    }

    private void notify(Merchant merchant, AmountRecord amountRecord) {
        try {
            if (StringUtils.isNotBlank(amountRecord.getNotifyUrl())) {
                PayNotifyVO payNotifyVO = new PayNotifyVO();
                payNotifyVO.setAmount(amountRecord.getAmount().toString());
                payNotifyVO.setOrderid(amountRecord.getMerOrder());
                payNotifyVO.setStatus(amountRecord.getStatus());
                payNotifyVO.setMerNo(amountRecord.getMerNo());
                payNotifyVO.setSysOrder(amountRecord.getSysOrder());
                payNotifyVO.setPayTime(amountRecord.getPayTime().getTime());


                Map notifyMap = new <String, Object>HashMap();
                notifyMap.put("amount", amountRecord.getAmount().toString());
                notifyMap.put("merNo", amountRecord.getMerNo());
                notifyMap.put("payTime", amountRecord.getPayTime().getTime() + "");
                notifyMap.put("orderid", amountRecord.getMerOrder());
                notifyMap.put("sysOrder", amountRecord.getSysOrder());
                notifyMap.put("status", amountRecord.getStatus());
                String stringByMap = MapSortUtil.getStringByMap(notifyMap);
                String sign = RSAUtil.signBySHA1WithRSA(stringByMap, merchant.getPriKey());
                logger.info(sign);
                payNotifyVO.setSign(sign);
                payNotifyVO.setNotifyUrl(amountRecord.getNotifyUrl());
                payNotifyVO.setNote(amountRecord.getRemark());
                MessageBuilder builder = MessageBuilder.withPayload(payNotifyVO);
                rocketMQTemplate.syncSend("PAY_NOTIFY_STATUS:payNotifyStatus", builder.build(), 3000, 2);
            }
        } catch (Exception e) {
            logger.error("发送失败");
        }
    }


    private void updateMercantWallet(Merchant merchant, AmountRecord amountRecord, TreasureTypeEnum treasureTypeEnum) {
        WalletVo walletVo = new WalletVo();
        walletVo.setRefId(amountRecord.getSysOrder());
        walletVo.setRemark(treasureTypeEnum.getMsg());
        walletVo.setAmount(amountRecord.getAmount());
        walletVo.setIp(amountRecord.getIp());
        walletVo.setTreasureTypeEnum(treasureTypeEnum);
        walletVo.setUserName(merchant.getMerNo());
        walletVo.setUid(merchant.getUid());
        walletVo.setFee(amountRecord.getFee());
        walletVo.setMerId(merchant.getUid());
        iWalletService.updateWalletByUid(walletVo);
    }

    private void updateMemberWallet(AmountRecord amountRecord, TreasureTypeEnum treasureTypeEnum) {
        WalletVo walletVo = new WalletVo();
        walletVo.setRefId(amountRecord.getSysOrder());
        walletVo.setRemark(treasureTypeEnum.getMsg());
        walletVo.setAmount(amountRecord.getAmount());
        walletVo.setIp(amountRecord.getIp());
        walletVo.setTreasureTypeEnum(treasureTypeEnum);
        walletVo.setUserName(amountRecord.getUserName());
        walletVo.setUid(amountRecord.getPayUid());
        walletVo.setFee(BigDecimal.ZERO);
        walletVo.setMerId(amountRecord.getMid());
        iWalletService.updateWalletByUid(walletVo);
    }


    private void createAmount(PayOrderVo orderVo, String merNo, BigDecimal amount, Merchant merchant, AmountRecord amountRecord) {
        amountRecord.setMerOrder(orderVo.getOrderid());
        amountRecord.setMid(merchant.getUid());
        amountRecord.setVersion(0);
        amountRecord.setMerNo(merNo);
        amountRecord.setNotifyUrl(orderVo.getNotifyurl());
        amountRecord.setFee(selectFee(FeeTypeConstants.PLAYER_RECHARGE, amount, merchant.getUid()));
        amountRecord.setSysOrder(Seq.generatorOrderNo(merNo));
        amountRecord.setRemark(orderVo.getNote());
    }

    @Override
    public AmountRecord queryOrder(String orderNo) {
        return iAmountRecordService.selectAmountRecordByOrderNo(orderNo);
    }

    /***
     * 付款
     * @param map 付款MAP
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String payment(Map<String, Object> map) {
        /*付款成功的操作*/
        //验证密码
        String payPassword = Convert.toStr(map.get("payPassword"));
        Long uid = SecurityUtils.getLoginUser().getUserId();
        TUser tUser = itUserService.selectTUserByUid(uid);
        Assert.isTrue(tUser.getPassword().equals("0"), "请设置支付密码");
        boolean b = SecurityUtils.matchesPassword(payPassword, tUser.getPayPassword());
        Assert.isFalse(b, "密码不正确");
        String orderNo = map.get("id").toString();
        AmountRecord amountRecord = iAmountRecordService.selectAmountRecordByOrderNo(orderNo);
        Assert.isTrue(amountRecord.getStatus() != 0, "此订单不能付款");
        long time = (System.currentTimeMillis() - amountRecord.getCreateTime().getTime()) / (1000 * 60);
        Assert.isTrue(time > 20, "订单已超时不能付款");
        amountRecord.setPayUid(uid);
        amountRecord.setUserName(tUser.getUsername());
        amountRecord.setStatus(1);
        amountRecord.setPayTime(new Date());

        int i = iAmountRecordService.updateAmountRecord(amountRecord);
        Assert.isTrue(i == 0, "支付失败");
        Merchant merchant = getMerchant(amountRecord.getMerNo());
        //商户加钱
        updateMemberWallet(amountRecord, TreasureTypeEnum.BALANCE_OUT);
        updateMercantWallet(merchant, amountRecord, TreasureTypeEnum.TRANSFER_RECEIPT);

        //付款成功上分
        this.saveReport(amountRecord.getAmount(), amountRecord.getFee(), ReportConstant.MEMBER_RE, amountRecord.getMerNo());

        //回调
        if (StringUtils.isNotBlank(amountRecord.getNotifyUrl())) {
            try {
                notify(merchant, amountRecord);
            } catch (Exception e) {
                logger.error("【回调失败】{}", amountRecord.getMerOrder());
            }
        }
        return merchant.getMerNo() + ":" + amountRecord.getMerOrder();
    }


    @Override
    public AjaxResult grabPay(PayOrderVo orderVo) {
        String merNo = orderVo.getMerNo();
        BigDecimal amount = new BigDecimal(orderVo.getAmount());
        Merchant merchant = getMerchant(merNo);

        GrabOrder record = new GrabOrder();
        record.setOrderId(orderVo.getOrderid());
        record.setMerchant(orderVo.getMerNo());
        List records = grabOrderService.selectGrabOrderList(record);
        if (records.size() > 0) {
            return AjaxResult.error(PayResponEnums.ORDER_EXT.getCode(), PayResponEnums.ORDER_EXT.getMsg());
        }
        if (!verifySign(orderVo, merchant.getPubKey())) {
            return AjaxResult.error(PayResponEnums.SIGN_ERR.getCode(), PayResponEnums.SIGN_ERR.getMsg());
        }
        String orderNo = orderVo.getOrderid();
        String notifyUrl = orderVo.getNotifyurl();
        Integer payWay = Integer.parseInt(orderVo.getPayWay());
        Date now = new Date();
        String snowId = snowflakeIdUtils.nextId() + "";
        String orderId = "GRAB" + DateUtils.toString(now, "yyyyMMddHHmmssSSS") + snowId.substring(snowId.length() - 6);

        //生成待支付订单信息
        //todo 查询在线卖蛋会员的订单，直接进行下单
        GrabOrder grabOrder = new GrabOrder();
        grabOrder.setId(orderId);
        grabOrder.setMerchant(merchant.getMerNo());
        grabOrder.setOrderId(orderNo);
        grabOrder.setAmount(amount);
        grabOrder.setRechargeWay(payWay);
        grabOrder.setOrderStatus(UserOrderConst.PAY_STATUS_CREATE);
        grabOrder.setCreateTime(now);
        grabOrder.setCallBackUrl(notifyUrl);
        BigDecimal feeRate = sysBrokerageService.selectSysBrokerageByFeeType(payWay + "").getRate();
        grabOrder.setFeeRate(feeRate);
        BigDecimal fee = amount.multiply(feeRate).divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_DOWN);
        grabOrder.setFee(fee);
        grabOrder.setRemark(orderVo.getNote());
        grabOrderService.insertGrabOrder(grabOrder);

        //发送延迟消息(超时时间设为3s,否则可能会出现发送失败的情况)，3分钟对应级别7,并将状态从待支付改为等待取消
        OrderMessageVo orderMessageVo = new OrderMessageVo();
        orderMessageVo.setOrderDetailId(grabOrder.getId());
        orderMessageVo.setOrderPoolId(grabOrder.getOrderId());
        orderMessageVo.setOrderBeforeStatus(UserOrderConst.PAY_STATUS_CREATE);
        orderMessageVo.setOrderAfterStatus(UserOrderConst.PAY_STATUS_WAITOUT);
        MessageBuilder builder = MessageBuilder.withPayload(orderMessageVo);
        SendResult sendResult = rocketMQTemplate.syncSend("GRAB_CHANGE_STATUS:changeGrabOrderStatus", builder.build(), 3000, 14);
        if (SendStatus.SEND_OK != sendResult.getSendStatus()) {
            throw new ServiceException("订单购买延迟取消消息发送失败");
        }

        //发送websocket通知所有会员进行抢单
        UserMailBox userMailBox = new UserMailBox();
        userMailBox.setUserType(1);
        userMailBox.setTitle("一大波新的跑分订单来啦!!!");
        JSONObject json = new JSONObject();
        json.put("id", grabOrder.getId());
        json.put("orderId", grabOrder.getOrderId());
        json.put("payWay", grabOrder.getRechargeWay());
        json.put("amount", grabOrder.getAmount());
        userMailBox.setContent(json.toJSONString());
        userMailBox.setTopic("grab_order_coming");
        rocketMqService.sendMsgToWeb(userMailBox);

        String pay_recharge_url = iSysConfigService.selectConfigByKey("pay_mayi_recharge_url") + "?id=" + orderId;
        return AjaxResult.success("success").put("pay_url", pay_recharge_url).put("id", orderId);
    }


    private boolean checkOrderExt(String orderId, String merNo, Long merUIid) {
        val queryRecord = new AmountRecord();
        queryRecord.setMerOrder(orderId);
        queryRecord.setMerNo(merNo);
        queryRecord.setMid(merUIid);
        List records = iAmountRecordService.selectAmountRecordList(queryRecord);
        if (records.size() > 0) {
            //   return AjaxResult.error(PayResponEnums.ORDER_EXT.getCode(), PayResponEnums.ORDER_EXT.getMsg());
            throw new ServiceException(PayResponEnums.ORDER_EXT.getMsg(), PayResponEnums.ORDER_EXT.getCode());
        }
        return true;
    }

    /**
     * 注册
     *
     * @param userName 账号
     * @param realName 真实姓名
     * @param merId    商户UID
     */
    private void register(String userName, String realName, Long merId, String merSeq) {
        String reName = null;
        if (!StringUtils.isBlank(realName)) {
            try {
                reName = new String(new BASE64Decoder().decodeBuffer(realName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<ApiAuthKey, Object> map = new <ApiAuthKey, Object>HashMap(3);
        map.put(ApiAuthKey.MERCHANT_NUMBER, merSeq);
        map.put(ApiAuthKey.MERCHANT_ID, merId);
        map.put(ApiAuthKey.REAL_NAME, reName);
        itUserService.generatorApiUser(userName, map);
    }

    private String getToken(String account, Long merId, String seqNo) {
        //登录操作
        HashMap<ApiAuthKey, Object> map = new HashMap<>();
        map.put(ApiAuthKey.MERCHANT_ID, merId);
        map.put(ApiAuthKey.MERCHANT_NUMBER, seqNo);
        Authentication authenticate1 = authenticationManager.authenticate(new ApiAuthenticationToken(account, map));
        return (String) authenticate1.getPrincipal();

    }

    /**
     * @param orderVo  订单VO
     * @param merchant 商用户
     * @return boolean
     */
    private boolean isRegister(PayOrderVo orderVo, Merchant merchant) {
        String apiUsername = StringUtils.apiUsername(orderVo.getAccount(), merchant.getSeqNo());
        TUser tUser = iMemberService.queryApiMemberByUsername(apiUsername);
        if (tUser == null) {
            //会员不存在则需注册
            register(orderVo.getAccount(), orderVo.getRealName(), merchant.getUid(), merchant.getSeqNo());
            return true;
        }
        return false;
    }
}

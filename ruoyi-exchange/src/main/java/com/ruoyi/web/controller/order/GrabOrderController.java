package com.ruoyi.web.controller.order;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.core.domain.model.member.TUserCredit;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.redis.RedisScriptStrings;
import com.ruoyi.common.enums.TreasureTypeEnum;
import com.ruoyi.common.enums.UserOrderConst;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.common.utils.http.HttpUtil;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.member.service.ITUserCreditService;
import com.ruoyi.member.service.IUserMailBoxService;
import com.ruoyi.order.domain.GrabOrder;
import com.ruoyi.order.domain.vo.OrderMessageVo;
import com.ruoyi.order.service.IGrabOrderService;
import com.ruoyi.pay.domain.Merchant;
import com.ruoyi.pay.domain.Wallet;
import com.ruoyi.pay.domain.vo.WalletVo;
import com.ruoyi.pay.service.IMerchantService;
import com.ruoyi.pay.service.IWalletService;
import com.ruoyi.settings.service.ISysAgencyBackwaterService;
import com.ruoyi.settings.service.ISysServiceChargeService;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/grab")
public class GrabOrderController extends BaseController {

    final private Logger logger = LoggerFactory.getLogger(GrabOrderController.class);

    @Resource
    IGrabOrderService grabOrderService;

    @Resource
    ITUserCreditService userCreditService;

    @Resource
    RocketMQTemplate rocketMQTemplate;

    @Resource
    IWalletService walletService;

    @Resource
    ISysServiceChargeService sysServiceChargeService;

    @Resource
    ISysAgencyBackwaterService sysAgencyBackwaterService;

    @Resource
    IMerchantService merchantService;

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    SnowflakeIdUtils snowflakeIdUtils;

    @Resource
    IUserMailBoxService userMailBoxService;


    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/order/{id}")
    public AjaxResult queryOrder(@PathVariable String id){
        GrabOrder grabOrder = grabOrderService.selectGrabOrderById(id);
        AjaxResult result = AjaxResult.success(grabOrder);
        //todo 本地测试暂定为10分钟取消跑分订单
        if(UserOrderConst.PAY_STATUS_CREATE.equals(grabOrder.getOrderStatus())){
            Date createTime = grabOrder.getCreateTime();
            Date addMinute = DateUtils.addMinute(createTime, 10);
            result.put("countTime",(addMinute.getTime()-System.currentTimeMillis())/1000);
        }
        if(UserOrderConst.PAY_STATUS_CONFIRM.equals(grabOrder.getOrderStatus())){
            Date createTime = grabOrder.getGrabTime();
            Date addMinute = DateUtils.addMinute(createTime, 10);
            result.put("countTime",(addMinute.getTime()-System.currentTimeMillis())/1000);
        }
        return result;
    }


    /**
     * 查询市场跑分订单
     * @param param
     * @return
     */
    @GetMapping("/market")
    @PreAuthorize("@ss.realNameVerified()")
    public TableDataInfo queryList(@RequestParam Map<String,Object> param){
        startPage();
        return getDataTable(grabOrderService.selectMarketList(param));
    }


    /**
     * 查询我的跑分单
     * @param param
     * @return
     */
    @GetMapping("/myOrder")
    @PreAuthorize("@ss.realNameVerified()")
    public TableDataInfo queryMyOrder(@RequestParam Map<String,Object> param){
        Long userId = SecurityUtils.getUserId();
        param.put("uid",userId);
        startPage();
        return getDataTable(grabOrderService.queryMyOrder(param));
    }


    /**
     * 第三方会员取消订单
     * @param id
     * @return
     */
    @GetMapping("/cancelOrder")
    public AjaxResult cancelOrder(@RequestParam String id) {
        GrabOrder grabOrder = grabOrderService.selectGrabOrderById(id);
        if(!UserOrderConst.PAY_STATUS_CREATE.equals(grabOrder.getOrderStatus())){
            throw new ServiceException("此订单已被录入,请稍后再试!");
        }
        GrabOrder temp = new GrabOrder();
        temp.setId(id);
        temp.setRemark(UserOrderConst.PAY_STATUS_CREATE + "");
        temp.setCancelTime(new Date());
        temp.setOrderStatus(UserOrderConst.PAY_STATUS_CANCEL);
        int i = grabOrderService.updateGrabOrderStatus(temp);
        return i > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 卖家确认抢单
     * @param param
     * @return
     */
    @GetMapping("/confirm")
    @PreAuthorize("@ss.realNameVerified()")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult confirmOrder(@RequestParam Map<String,String> param) {
        String id = param.get("id");
        String cardId = param.get("cardId");
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        GrabOrder grabOrder = grabOrderService.selectGrabOrderById(id);
        if(!UserOrderConst.PAY_STATUS_CREATE.equals(grabOrder.getOrderStatus())){
            throw new ServiceException("此订单已被抢单,请选择其他订单!");
        }
        Wallet wallet = walletService.selectWalletByUid(userId);
        if(wallet.getDepositBalance().compareTo(grabOrder.getAmount()) < 0){
            throw new ServiceException("保证金不足,请缴纳足够的保证金后再操作!");
        }
        TUserCredit userCredit;
        try{
            userCredit = userCreditService.SelectById(Long.parseLong(cardId));
        }catch (Exception e){
            logger.error("跑分查询卖家收款方式异常",e.getLocalizedMessage());
            throw new ServiceException("请检查收款方式!");
        }
        Assert.test(userCredit == null || !userCredit.getUid().equals(userId),"credit.not.match");
        Assert.test(userCredit.getType() * 1 != grabOrder.getRechargeWay(),"credit.type.not.match");
        grabOrder.setOrderStatus(UserOrderConst.PAY_STATUS_CONFIRM);
        grabOrder.setRemark(UserOrderConst.PAY_STATUS_CREATE + "");
        grabOrder.setGrabTime(new Date());
        grabOrder.setUserId(userId);
        grabOrder.setUserName(loginUser.getUsername());
        grabOrder.setUserRealName(userCredit.getCreditName());
        grabOrder.setUserCardAddress(userCredit.getContent());
        grabOrder.setUserCardRemark(userCredit.getCreditBank());
        grabOrder.setSaleRemark(loginUser.getUser().getNickName());
        int i = grabOrderService.updateGrabOrderStatus(grabOrder);
        //设置消息延迟级别，我这里设置14，对应就是延时10m
        // "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h"
        //发送延迟消息(超时时间设为3s,否则可能会出现发送失败的情况)，10分钟对应级别14,并将状态从待支付改为超时取消
        if(i > 0){
            OrderMessageVo orderMessageVo = new OrderMessageVo();
            orderMessageVo.setOrderDetailId(id);
            orderMessageVo.setOrderBeforeStatus(UserOrderConst.PAY_STATUS_CONFIRM);
            orderMessageVo.setOrderAfterStatus(UserOrderConst.PAY_STATUS_TIMEOUT);
            MessageBuilder builder = MessageBuilder.withPayload(orderMessageVo);
            SendResult sendResult = rocketMQTemplate.syncSend("GRAB_CHANGE_STATUS:changeGrabOrderStatus", builder.build(), 3000, 14);
            if (SendStatus.SEND_OK != sendResult.getSendStatus()) {
                throw new ServiceException("订单确认延迟取消消息发送失败");
            }
            //扣除钱包押金
            WalletVo walletVo = new WalletVo();
            walletVo.setRefId(grabOrder.getId());
            walletVo.setRemark("会员跑分扣除押金");
            walletVo.setAmount(grabOrder.getAmount());
            walletVo.setIp(IpUtils.getCurrentReqIp());
            walletVo.setTreasureTypeEnum(TreasureTypeEnum.MY_TAKEORDERS);
            walletVo.setUserName(grabOrder.getUserName());
            walletVo.setUid(grabOrder.getUserId());
            walletService.updateWalletByUid(walletVo);
            return AjaxResult.success(grabOrder);
        }
        throw new ServiceException("抢单失败,请重试!");
    }

    /**
     * 买家付款
     */
    @GetMapping("/pay")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult payOrder(@RequestParam String primaryKey, String imgId) {
        if (StringUtils.isEmpty(imgId)) {
            return AjaxResult.error("请上传图片后再确定完成付款!");
        }
        String path = getUploadFilePath(imgId);
        GrabOrder grabOrder = grabOrderService.selectGrabOrderById(primaryKey);
        grabOrder.setOrderStatus(UserOrderConst.PAY_STATUS_PAID);
        grabOrder.setPayTime(new Date());
        grabOrder.setSuccessImg(path);
        grabOrder.setRemark(UserOrderConst.PAY_STATUS_CONFIRM + "");
        int i = grabOrderService.updateGrabOrderStatus(grabOrder);
        if(i > 0){
            //发送站内信和websocket消息
            UserMailBox mailBox = new UserMailBox();
            mailBox.setId(snowflakeIdUtils.nextId());
            mailBox.setTitle("跑分订单已到账!");
            mailBox.setContent("您有一笔跑分订单" + grabOrder.getId() + "待完成!");
            mailBox.setUserNames(grabOrder.getUserName());
            mailBox.setUserIds(grabOrder.getUserId() + "");
            mailBox.setUserType(0);
            mailBox.setCreatTime(new Date());
            mailBox.setSendTime(new Date());
            mailBox.setRemark(grabOrder.getId());
            mailBox.setState(UserOrderConst.UNREADSTATE);
            mailBox.setTopic(UserOrderConst.GRAB_TOPIC);
            userMailBoxService.insertUserMailBox(mailBox);
            MessageBuilder builder = MessageBuilder.withPayload(mailBox);
            SendResult result = rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder.build());
            if(!SendStatus.SEND_OK.equals(result.getSendStatus())){
                throw new ServiceException("消息发送失败!");
            }
            return AjaxResult.success(grabOrder);
        }
        throw new ServiceException("确认付款失败,请重试!");
    }

    /**
     * 卖家确认收款
     * 回调第三方,钱包增加返佣，上级增加返水，扣除押金冻结，商户增加余额，并扣除一定比例手续费
     */
    @GetMapping("/finish")
    @PreAuthorize("@ss.realNameVerified()")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult finishOrder(@RequestParam String primaryKey) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        GrabOrder grabOrder = grabOrderService.selectGrabOrderById(primaryKey);
        if(!loginUser.getUserId().equals(grabOrder.getUserId())){
            throw new ServiceException("请勿非法操作他人订单!");
        }
        //更改订单状态
        grabOrder.setOrderStatus(UserOrderConst.PAY_STATUS_FINISH);
        grabOrder.setFinishTime(new Date());
        grabOrder.setRemark(UserOrderConst.PAY_STATUS_PAID + "");
        grabOrderService.updateGrabOrderStatus(grabOrder);
        //钱包扣除押金冻结
        WalletVo walletVo = new WalletVo();
        walletVo.setRefId(grabOrder.getId());
        walletVo.setRemark("会员跑分完成订单");
        walletVo.setAmount(grabOrder.getAmount());
        walletVo.setIp(IpUtils.getCurrentReqIp());
        walletVo.setTreasureTypeEnum(TreasureTypeEnum.MY_SUCCESSORDERS);
        walletVo.setUserName(grabOrder.getUserName());
        walletVo.setUid(grabOrder.getUserId());
        walletService.updateWalletByUid(walletVo);
        //钱包增加返佣
        WalletVo walletV1 = new WalletVo();
        BeanUtils.copyBeanProp(walletV1,walletVo);
        walletVo.setRemark("会员跑分订单返佣");
        walletVo.setAmount(grabOrder.getFee());
        walletVo.setTreasureTypeEnum(TreasureTypeEnum.RECHARGE_RETURN);
        walletService.updateWalletByUid(walletVo);
        //上级返水
        TUser tUser = ((LoginMember) loginUser).gettUser();
        Integer rechargeWay = grabOrder.getRechargeWay();
        Long pid = tUser.getPid();
        if(pid != null){
            BigDecimal backwater = sysAgencyBackwaterService.getAgencyBackwaterByFeeType(pid, rechargeWay + "");
            if(backwater != null){
                WalletVo backWallet = new WalletVo();
                backWallet.setRefId(grabOrder.getId());
                backWallet.setRemark("会员跑分代理返水");
                backWallet.setAmount(grabOrder.getAmount().multiply(backwater).setScale(2,BigDecimal.ROUND_DOWN));
                backWallet.setIp(IpUtils.getCurrentReqIp());
                backWallet.setTreasureTypeEnum(TreasureTypeEnum.RETURN_WATER);
                backWallet.setUserName(tUser.getPidArray());
                backWallet.setUid(pid);
                walletService.updateWalletByUid(backWallet);
            }
        }
        //商户增加余额，并扣除一定比例手续费
        String merchantName = grabOrder.getMerchant();
        Merchant merchant = merchantService.selectMerchantByNo(merchantName);
        Long merchantUid = merchant.getUid();
        BigDecimal feeType = sysServiceChargeService.getUserIdRateByFeeType(merchantUid, rechargeWay + 9 + "");
        WalletVo merchantWallet = new WalletVo();
        merchantWallet.setRefId(grabOrder.getId());
        merchantWallet.setRemark("商家通过跑分充值");
        merchantWallet.setAmount(grabOrder.getAmount());
        merchantWallet.setFee(grabOrder.getAmount().multiply(feeType).divide(new BigDecimal("1000"),2, RoundingMode.DOWN));
        merchantWallet.setIp(IpUtils.getCurrentReqIp());
        merchantWallet.setTreasureTypeEnum(TreasureTypeEnum.RETURN_WATER);
        merchantWallet.setUserName(merchantName);
        merchantWallet.setUid(merchantUid);
        walletService.updateWalletByUid(merchantWallet);
        //回调第三方
        if (StringUtils.isNotBlank(grabOrder.getCallBackUrl())) {
            try {
                Map notifyMap = new HashMap();
                notifyMap.put("amount", grabOrder.getAmount().toString());
                notifyMap.put("merNo", grabOrder.getMerchant());
                notifyMap.put("payTime", grabOrder.getFinishTime().getTime() + "");
                notifyMap.put("orderid", grabOrder.getOrderId());
                notifyMap.put("sysOrder", grabOrder.getId());
                String stringByMap = MapSortUtil.getStringByMap(notifyMap);
                notifyMap.put("sign", RSAUtil.signBySHA1WithRSA(stringByMap, merchant.getPriKey()));
                String s = HttpUtil.toPostJson(JSONObject.toJSONString(notifyMap), grabOrder.getCallBackUrl());
                logger.info("回调返回的参数:", s);
            } catch (Exception e) {
                logger.error("【回调失败】{}", JSON.toJSONString(grabOrder));

            }
        }
        return AjaxResult.success();
    }

    /**
     * 卖家暂停收款
     */
    @GetMapping("/pause")
    @PreAuthorize("@ss.realNameVerified()")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult pauseOrder(@RequestParam String primaryKey, String imgId) {
        if (StringUtils.isEmpty(imgId)) {
            return AjaxResult.error("请上传图片后再进行暂停付款!");
        }
        String path = getUploadFilePath(imgId);
        GrabOrder grabOrder = grabOrderService.selectGrabOrderById(primaryKey);
        grabOrder.setOrderStatus(UserOrderConst.PAY_STATUS_PAUSE);
        grabOrder.setPauseTime(new Date());
        grabOrder.setPauseImg(path);
        grabOrder.setRemark(UserOrderConst.PAY_STATUS_PAID + "");
        int i = grabOrderService.updateGrabOrderStatus(grabOrder);
        if(i > 0){
            return AjaxResult.success(grabOrder);
        }
        throw new ServiceException("暂停付款失败,请重试!");
    }

    public String getUploadFilePath(String imgId) {
        return (String)redisTemplate.execute(RedisScriptStrings.GET_AND_DEL, Collections.singletonList(MimeTypeUtils.UPFILE_VERIFYID_KEY + imgId));
    }
}

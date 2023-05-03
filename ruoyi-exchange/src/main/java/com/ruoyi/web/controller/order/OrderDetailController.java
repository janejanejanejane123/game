package com.ruoyi.web.controller.order;

import com.ruoyi.common.annotation.RedisLockOperate;
import com.ruoyi.common.annotation.ReferType;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.constant.ReportConstant;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.core.domain.model.member.TUserCredit;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.redis.RedisScriptStrings;
import com.ruoyi.common.enums.TreasureTypeEnum;
import com.ruoyi.common.enums.UserInfoApplyEnums;
import com.ruoyi.common.enums.UserOrderConst;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.uuid.Seq;
import com.ruoyi.common.vo.EncStringParamsVo;
import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.member.service.ITUserCreditService;
import com.ruoyi.member.service.IUserMailBoxService;
import com.ruoyi.member.util.RocketMqService;
import com.ruoyi.order.domain.UserOrder;
import com.ruoyi.order.domain.UserOrderDetail;
import com.ruoyi.order.domain.vo.OrderDetailVo;
import com.ruoyi.order.domain.vo.OrderMessageVo;
import com.ruoyi.order.domain.vo.UserOrderVo;
import com.ruoyi.order.service.IUserOrderDetailService;
import com.ruoyi.order.service.IUserOrderService;
import com.ruoyi.pay.domain.TReport;
import com.ruoyi.pay.domain.Wallet;
import com.ruoyi.pay.domain.vo.WalletVo;
import com.ruoyi.pay.service.ITReportService;
import com.ruoyi.pay.service.IWalletService;
import com.ruoyi.system.service.IMemberService;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: aping
 * @Date: 2019/8/31 11:00
 * @Description
 */
@RestController
@RequestMapping("/api/detail")
public class OrderDetailController extends BaseController {

    final private Logger logger = LoggerFactory.getLogger(OrderDetailController.class);

    @Resource
    private IUserOrderDetailService userOrderDetailService;

    @Resource
    IUserOrderService userOrderService;

//    @Resource
//    ISysConfigService configService;
//
//    @Resource
//    IUserRankService userRankService;

    @Resource
    SnowflakeIdUtils snowflakeIdUtils;

    @Resource
    private RocketMqService rocketMqService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    IUserMailBoxService userMailBoxService;

    @Resource
    ITUserCreditService userCreditService;

    @Resource
    IMemberService memberService;

    @Resource
    IWalletService iWalletService;

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    ITReportService itReportService;

//    @Resource
//    ITUserDataConfigService itUserDataConfigService;

    /**
     * 出售e蛋 普通用户，需后台审核
     * @param vo
     * @return
     */
    @RedisLockOperate
    @PostMapping("/sale")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PreAuthorize("@ss.realNameVerified()")
    public AjaxResult orderSale(@RequestBody @ReferType(UserOrderVo.class) EncStringParamsVo vo) {
        UserOrderVo userOrderVo = vo.getBody();
        LoginMember loginUser = (LoginMember)SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
//        TUserDataConfig userDataConfig = itUserDataConfigService.queryByUid(userId);
        //验证邮箱验证码
//        if(Short.valueOf("1").equals(userDataConfig.getEmailForSell())){
//            String key = EmailEnums.SELLCOIN.redisKey(userId);
//            Object obj = redisTemplate.opsForValue().get(key);
//            Assert.test(obj == null || !userOrderVo.getEmailCode().equals(obj.toString()),"email.verify.not.match");
//        }
        //验证支付密码
//        if(Short.valueOf("1").equals(userDataConfig.getPayPasswordForSell())){
        TUser user = memberService.selectMemberByUid(userId);
        Assert.test(!SecurityUtils.matchesPassword(userOrderVo.getPayPassword(),user.getPayPassword()),"credit.payPassword.notMatch");
        Assert.test(user.getDisabled() == 0L,"member.not.available");
//        }
        //验证收款方式
        TUserCredit userCredit = userCreditService.SelectById(userOrderVo.getCardId());
        Assert.test(userCredit == null || !userCredit.getUid().equals(userId),"credit.not.match");
        //验证出售金额
        BigDecimal totalAmout = userOrderVo.getTotalAmout();
        Assert.test(totalAmout.compareTo(BigDecimal.ZERO) <= 0,"walletvo.amount.great0!");
        Wallet wallet = iWalletService.selectWalletByUid(userId);
        Assert.test(totalAmout.compareTo(wallet.getEffectiveBal()) > 0,"sale.amount.outofrange");
        UserOrder userOrder = new UserOrder();
        BeanUtils.copyBeanProp(userOrder,userOrderVo);
        userOrder.setId(Seq.generateId("S"));
        userOrder.setUserName(loginUser.getUsername());
        userOrder.setUserId(userId);
        TUser tUser = loginUser.gettUser();
        userOrder.setSaleRemark(tUser.getNickname());
        userOrder.setUserImage(tUser.getPhoto());
        //审核状态
        userOrder.setCreateTime(new Date());
        userOrder.setOrderStatus(UserOrderConst.ORDER_STATUS_CHECK);
        //0 银行卡，1 微信，2 支付宝，3 QQ
        userOrder.setPayWay(Short.valueOf(userCredit.getType() + ""));
        userOrder.setUserRealName(userCredit.getCreditName());
        userOrder.setUserCardAddress(userCredit.getContent());
        userOrder.setIsSplit(UserOrderConst.FREE_SALE);
        userOrder.setUserCardRemark(userCredit.getCreditBank());
        int i = userOrderService.insertUserOrder(userOrder);
        if(i > 0){
            //redis中新增待审核消息内容，并发送消息给后台管理员
            Long num = redisTemplate.opsForValue().increment(UserInfoApplyEnums.SALE_COIN_VERIFY.getRedisKey(), 1);
            rocketMqService.applyToAdmin(UserInfoApplyEnums.REAL_NAME_VERIFY,num == null ? 0 : num);
            //卖家扣除钱包余额
            WalletVo salerVo = new WalletVo();
            salerVo.setRefId(userOrder.getId());
            salerVo.setRemark("会员卖蛋");
            salerVo.setAmount(totalAmout);
            salerVo.setIp(IpUtils.getCurrentReqIp());
            salerVo.setTreasureTypeEnum(TreasureTypeEnum.WITHDRAW_1);
            salerVo.setUserName(loginUser.getUsername());
            salerVo.setUid(userId);
            salerVo.setMerId(user.getMerchantId());
            iWalletService.updateWalletByUid(salerVo);
            return AjaxResult.success("提交成功,请留意订单变化!");
        }
        throw new ServiceException("系统繁忙,请稍后再试!");
    }

    /**
     * 查询状态为待抢单的订单池
     * @return
     */
    @GetMapping("/query")
    @PreAuthorize("@ss.realNameVerified()")
    public TableDataInfo query(@RequestParam Map<String, Object> param) {
        param.put("uid", SecurityUtils.getUserId());
        int pageNum = Integer.parseInt(param.remove("pageNum").toString());
        int pageSize = Integer.parseInt(param.remove("pageSize").toString());
        List<Map<String, Object>> pool = userOrderService.selectPool(param);
        if (param.containsKey("online") && "0".equals(param.get("online").toString())) {
            pool = pool.stream().
                    filter(item -> "0".equals(item.get("online"))).collect(Collectors.toList());
        }
        TableDataInfo rspData = new TableDataInfo();
        int total = pool.size();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(pool.subList(pageSize * (pageNum - 1), pageSize * pageNum > total ? total : pageSize * pageNum));
        rspData.setCurrPage(pageNum);
        rspData.setPageSize(pageSize);
        rspData.setTotal(total);
        rspData.setTotalPage(total % pageSize == 0 ? total / pageSize : total / pageSize + 1);
        return rspData;
    }

    /**
     * 购买e蛋
     * @param param 参数
     * @return
     */
    @PostMapping("/buy")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PreAuthorize("@ss.realNameVerified()")
    public AjaxResult orderBuy(@RequestBody Map<String,String> param) {
        //查询是否有未完成订单
        LoginMember loginUser = (LoginMember)SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        TUser tUser = memberService.selectMemberByUid(userId);
        Assert.test(tUser.getDisabled() == 0L,"member.not.available");
        BigDecimal res = userOrderDetailService.queryUnPaidOrderByUid(userId);
        if (res != null && res.compareTo(BigDecimal.ZERO) > 0) {
            throw new ServiceException("你有一笔尚未完成的订单!");
        }
        TUserCredit userCredit = null;
        String cardId = param.get("cardId");
        try{
            userCredit = userCreditService.SelectById(Long.parseLong(cardId));
        }catch (Exception e){
            logger.error("购买e蛋查询买家付款id异常",e.getLocalizedMessage());
            throw new ServiceException("请检查付款方式!");
        }
        Assert.test(userCredit == null,"credit.not.match");
        Assert.test(!userCredit.getUid().equals(userId),"credit.not.match");
        String orderId = param.get("primaryId");
        UserOrder order = userOrderService.selectUserOrderById(orderId);
        if (!UserOrderConst.ORDER_STATUS_CREATE.equals(order.getOrderStatus())) {
            throw new ServiceException("此订单不可交易,请稍后再试或选择其它订单!");
        }
        Assert.test(userCredit.getType()*1 != order.getPayWay()*1,"credit.type.not.match");
        boolean isMerchant = UserOrderConst.MERCHANT_SALE.equals(order.getIsSplit());
        UserOrderDetail orderDetail = new UserOrderDetail();
        orderDetail.setPrimaryId(Seq.generateId(isMerchant ? "M":"B"));
        orderDetail.setReferId(orderId);
        orderDetail.setCreateTime(new Date());
        orderDetail.setPayStatus(UserOrderConst.PAY_STATUS_CREATE);
        orderDetail.setPayWayId(order.getPayWay());
        orderDetail.setSalerId(order.getUserId());
        orderDetail.setSalerName(order.getUserName());
        orderDetail.setSaleAmout(order.getTotalAmout());
        orderDetail.setSalerCardName(order.getUserRealName());
        orderDetail.setSalerCardAddress(order.getUserCardAddress());
        orderDetail.setSalerCardRemark(order.getUserCardRemark());
        orderDetail.setSalerRemark(order.getSaleRemark());
        orderDetail.setBuyerId(userId);
        orderDetail.setBuyerName(loginUser.getUsername());
        orderDetail.setBuyerRemark(loginUser.gettUser().getNickname());
        orderDetail.setBuyerCardName(userCredit.getCreditName());
        orderDetail.setBuyerCardAddress(userCredit.getContent());
        orderDetail.setBuyerCardRemark(userCredit.getCreditBank());
        int i = userOrderDetailService.insertUserOrderDetail(orderDetail);
        int x = userOrderService.changeOrderStatus(UserOrderConst.ORDER_STATUS_WAITING,orderId,UserOrderConst.ORDER_STATUS_CREATE);
        //插入成功,修改订单池订单状态为待支付和锁定状态,并通知卖家确认订单
        if (i > 0 && x > 0) {
            //发送延迟消息(超时时间设为3s,否则可能会出现发送失败的情况)，10分钟对应级别14,并将状态从待支付改为超时取消
            OrderMessageVo orderMessageVo = new OrderMessageVo();
            orderMessageVo.setOrderDetailId(orderDetail.getPrimaryId());
            orderMessageVo.setOrderPoolId(orderDetail.getReferId());
            orderMessageVo.setOrderBeforeStatus(UserOrderConst.PAY_STATUS_CREATE);
            orderMessageVo.setOrderAfterStatus(UserOrderConst.PAY_STATUS_TIMEOUT);
            MessageBuilder builder = MessageBuilder.withPayload(orderMessageVo);
            SendResult sendResult = rocketMQTemplate.syncSend("ECOIN_CHANGE_STATUS:changeECoinOrderStatus", builder.build(), 3000, 14);
            if (SendStatus.SEND_OK != sendResult.getSendStatus()) {
                throw new ServiceException("订单购买延迟取消消息发送失败");
            }
            //给会员发送站内信和websocket消息，代付商户不需要
            if(!isMerchant){
                UserMailBox mailBox = new UserMailBox();
                mailBox.setId(snowflakeIdUtils.nextId());
                mailBox.setTitle("出售订单待确认!");
                mailBox.setContent("您有一笔出售订单" + orderDetail.getReferId() + "待确认!");
                mailBox.setUserNames(orderDetail.getSalerName());
                mailBox.setUserIds(orderDetail.getSalerId() + "");
                mailBox.setUserType(Integer.parseInt(loginUser.getUserType()));
                mailBox.setCreatTime(new Date());
                mailBox.setSendTime(new Date());
                mailBox.setState(UserOrderConst.UNREADSTATE);
                mailBox.setTopic(UserOrderConst.TOPIC);
                mailBox.setRemark(orderDetail.getPrimaryId());
                userMailBoxService.insertUserMailBox(mailBox);
                sendMsgToWeb(mailBox);
            }
            return AjaxResult.success("订单创建成功!");
        }
        throw new ServiceException("订单创建失败!");
    }

    public void sendMsgToWeb(UserMailBox mailBox){
        MessageBuilder builder = MessageBuilder.withPayload(mailBox);
        SendResult result = rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder.build());
        if(!SendStatus.SEND_OK.equals(result.getSendStatus())){
            throw new ServiceException("消息发送失败!");
        }
    }

//    @GetMapping("/testMsg")
//    public AjaxResult testMsg(@RequestParam Map<String,String> map) {
//        JSONObject json = new JSONObject();
//        json.put("sender","system");
//        json.put("userIds",map.get("id"));
//        json.put("format","0");
//        json.put("action",map.get("action"));
//        json.put("content",map.get("msg"));
//        json.put("title","this is a test");
//        json.put("extra","this is a topic");
//        json.put("userType",map.get("type"));
//        MessageBuilder builder = MessageBuilder.withPayload(json);
//        SendResult sendResult = rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder.build());
//        return AjaxResult.success();
//    }
//
//    @GetMapping("/testDelayMsg")
//    public AjaxResult testDelayMsg() {
//        OrderMessageVo orderMessageVo = new OrderMessageVo();
//        orderMessageVo.setOrderDetailId("1");
//        orderMessageVo.setOrderPoolId("2");
//        orderMessageVo.setOrderBeforeStatus(UserOrderConst.PAY_STATUS_CREATE);
//        orderMessageVo.setOrderAfterStatus(UserOrderConst.PAY_STATUS_TIMEOUT);
//        MessageBuilder builder = MessageBuilder.withPayload(orderMessageVo);
//        SendResult sendResult = rocketMQTemplate.syncSend("ECOIN_CHANGE_STATUS:changeECoinOrderStatus", builder.build());
//        return AjaxResult.success();
//    }

    /**
     * 卖家确认
     *
     * @param primaryKey
     * @return
     */
    @GetMapping("/preConfirm")
    @PreAuthorize("@ss.realNameVerified()")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult preConfirm(@RequestParam String primaryKey) {
        UserOrderDetail orderDetail = new UserOrderDetail();
        orderDetail.setPrimaryId(primaryKey);
        orderDetail.setPayStatus(UserOrderConst.PAY_STATUS_CONFIRM);
        orderDetail.setSalerCheckTime(new Date());
        orderDetail.setPayWayId(UserOrderConst.PAY_STATUS_CREATE);
        int i = userOrderDetailService.updateStatus(orderDetail);
        orderDetail = userOrderDetailService.selectUserOrderDetailByPrimaryId(primaryKey);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(!loginUser.getUserId().equals(orderDetail.getSalerId())){
            throw new ServiceException("非法操作订单！");
        }
        //设置消息延迟级别，我这里设置3，对应就是延时10s
        // "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h"
        //发送延迟消息(超时时间设为3s,否则可能会出现发送失败的情况)，10分钟对应级别14,并将状态从待支付改为超时取消
        OrderMessageVo orderMessageVo = new OrderMessageVo();
        orderMessageVo.setOrderDetailId(orderDetail.getPrimaryId());
        orderMessageVo.setOrderPoolId(orderDetail.getReferId());
        orderMessageVo.setOrderBeforeStatus(UserOrderConst.PAY_STATUS_CONFIRM);
        orderMessageVo.setOrderAfterStatus(UserOrderConst.PAY_STATUS_TIMEOUT);
        MessageBuilder builder = MessageBuilder.withPayload(orderMessageVo);
        SendResult sendResult = rocketMQTemplate.syncSend("ECOIN_CHANGE_STATUS:changeECoinOrderStatus", builder.build(), 3000, 14);
        if (SendStatus.SEND_OK != sendResult.getSendStatus()) {
            throw new ServiceException("订单确认延迟取消消息发送失败");
        }
        if (i > 0) {
            //插入成功,通知买家付款 发送站内信和websocket消息
            String buyerName = orderDetail.getBuyerName();
            UserMailBox mailBox = new UserMailBox();
            mailBox.setId(snowflakeIdUtils.nextId());
            mailBox.setTitle("购买订单待付款!");
            mailBox.setContent("您有一笔购买订单" + orderDetail.getPrimaryId() + "待付款!");
            mailBox.setUserNames(buyerName);
            mailBox.setUserIds(orderDetail.getBuyerId() + "");
            mailBox.setUserType(Integer.parseInt(loginUser.getUserType()));
            mailBox.setCreatTime(new Date());
            mailBox.setSendTime(new Date());
            mailBox.setTopic(UserOrderConst.TOPIC);
            mailBox.setState(UserOrderConst.UNREADSTATE);
            mailBox.setRemark(orderDetail.getPrimaryId());
            userMailBoxService.insertUserMailBox(mailBox);
            sendMsgToWeb(mailBox);
            OrderDetailVo vo = new OrderDetailVo();
            BeanUtils.copyBeanProp(vo,orderDetail);
            vo.setSalerRemark(null);
            vo.setBuyerCardName(StringUtils.realNameChange(vo.getBuyerCardName()));
            vo.setBuyerCardAddress(vo.getBuyerCardAddress());
            long l = DateUtils.addMinute(orderDetail.getCreateTime(), 10).getTime() - System.currentTimeMillis();
            vo.setCountTime(l / 1000);
            vo.setOperation_1("等待买家付款");
            return AjaxResult.success(vo);
        }
        throw new ServiceException("确认订单失败!");
    }

    /**
     * 买家付款,需上传成功付款截图
     *
     * @param primaryKey
     * @return
     */
    @GetMapping("/finishPay")
    @PreAuthorize("@ss.realNameVerified()")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult finishPay(@RequestParam String primaryKey, String imgId) {
        if (StringUtils.isEmpty(imgId)) {
            return AjaxResult.error("请上传图片后再确定完成付款!");
        }
        String path = getUploadFilePath(imgId);
        UserOrderDetail orderDetail = new UserOrderDetail();
        orderDetail.setPrimaryId(primaryKey);
        orderDetail.setPayStatus(UserOrderConst.PAY_STATUS_PAID);
        orderDetail.setSuccessImg(path);
        orderDetail.setBuyerPayTime(new Date());
        orderDetail.setPayWayId(UserOrderConst.PAY_STATUS_CONFIRM);
        int i = userOrderDetailService.updateStatus(orderDetail);
        if (i > 0) {
            //插入成功,通知卖家放蛋
            orderDetail = userOrderDetailService.selectUserOrderDetailByPrimaryId(primaryKey);
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if(!loginUser.getUserId().equals(orderDetail.getBuyerId())){
                throw new ServiceException("非法操作订单！");
            }
            if(primaryKey.startsWith("EPAYB")){
                UserMailBox mailBox = new UserMailBox();
                mailBox.setId(snowflakeIdUtils.nextId());
                mailBox.setTitle("出售订单已到账!");
                mailBox.setContent("您有一笔出售订单" + orderDetail.getReferId() + "待放蛋!");
                mailBox.setUserNames(orderDetail.getSalerName());
                mailBox.setUserIds(orderDetail.getSalerId() + "");
                mailBox.setUserType(Integer.parseInt(loginUser.getUserType()));
                mailBox.setCreatTime(new Date());
                mailBox.setSendTime(new Date());
                mailBox.setRemark(orderDetail.getPrimaryId());
                mailBox.setState(UserOrderConst.UNREADSTATE);
                mailBox.setTopic(UserOrderConst.TOPIC);
                userMailBoxService.insertUserMailBox(mailBox);
                sendMsgToWeb(mailBox);
            }
            OrderDetailVo vo = new OrderDetailVo();
            BeanUtils.copyBeanProp(vo,orderDetail);
            vo.setOperation_1("等待卖家放蛋");
            vo.setSalerRemark(null);
            vo.setBuyerCardName(StringUtils.realNameChange(vo.getBuyerCardName()));
            vo.setBuyerCardAddress(vo.getBuyerCardAddress());
            vo.setSalerCardName(StringUtils.realNameChange(vo.getSalerCardName()));
            vo.setSalerCardAddress(vo.getSalerCardAddress());
            return AjaxResult.success(vo);
        }
        throw new ServiceException("确认付款失败!");
    }

    /**
     * 已完成付款的订单充值成功回调
     *
     * @param primaryKey
     * @return
     */
    @GetMapping("/sendBalance")
    @PreAuthorize("@ss.realNameVerified()")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult sendBalance(@RequestParam String primaryKey) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return sendBalanceByPulic(primaryKey, UserOrderConst.PAY_STATUS_PAID, loginUser);
    }

    /**
     * 暂停上分的订单充值成功回调
     *
     * @param primaryKey
     * @return
     */
    @GetMapping("/sendBalanceBySaler")
    @PreAuthorize("@ss.realNameVerified()")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult sendBalanceBySaler(@RequestParam String primaryKey) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return sendBalanceByPulic(primaryKey, UserOrderConst.PAY_STATUS_PAUSE, loginUser);
    }

    public AjaxResult sendBalanceByPulic(String primaryKey, short beforeStatus, LoginUser loginUser) {
        String loginName = loginUser.getUsername();
        UserOrderDetail orderDetail = new UserOrderDetail();
        orderDetail.setPrimaryId(primaryKey);
        orderDetail.setSalerConfirmTime(new Date());
        orderDetail.setOrderFinishTime(new Date());
        orderDetail.setPayStatus(UserOrderConst.PAY_STATUS_FINISH);
        orderDetail.setOperateName(loginName);
        orderDetail.setPayWayId(beforeStatus);
        int i = userOrderDetailService.updateStatus(orderDetail);
        if (i > 0) {
            //插入成功,通知卖家确认订单
            orderDetail = userOrderDetailService.selectUserOrderDetailByPrimaryId(primaryKey);
            Long salerId = orderDetail.getSalerId();
            String referId = orderDetail.getReferId();
            if(!salerId.equals(loginUser.getUserId()) && loginUser.getUserType().equals("0")){
                throw new ServiceException("非法操作订单！");
            }
            int m = userOrderService.changeOrderStatus(UserOrderConst.ORDER_STATUS_FINISH, referId,UserOrderConst.ORDER_STATUS_WAITING);
            if(m < 1){
                throw new ServiceException("非法操作订单！");
            }
            BigDecimal saleAmout = orderDetail.getSaleAmout();
            //卖家扣除钱包余额
            WalletVo salerVo = new WalletVo();
            salerVo.setRefId(referId);
            salerVo.setRemark("会员卖蛋");
            salerVo.setAmount(saleAmout);
            salerVo.setIp(IpUtils.getCurrentReqIp());
            salerVo.setTreasureTypeEnum(TreasureTypeEnum.WITHDRAW_2);
            salerVo.setUserName(orderDetail.getSalerName());
            salerVo.setUid(salerId);
            Long merchantId = memberService.selectMemberByUid(salerId).getMerchantId();
            salerVo.setMerId(merchantId);
            iWalletService.updateWalletByUid(salerVo);
            TReport tReport = new TReport();
            tReport.setAmount(saleAmout);
            tReport.setFee(BigDecimal.ZERO);
            tReport.setKey(ReportConstant.SELL_AMOUNT);
            tReport.setPeriod(new Date());
            tReport.setMerNo(iWalletService.selectMerchantByUid(merchantId));
            itReportService.save(tReport);
            //买家增加钱包余额
            WalletVo buyerVo = new WalletVo();
            BeanUtils.copyBeanProp(buyerVo,salerVo);
            buyerVo.setRemark("会员买蛋");
            buyerVo.setRefId(orderDetail.getPrimaryId());
            buyerVo.setTreasureTypeEnum(TreasureTypeEnum.RECHARGE);
            buyerVo.setUserName(orderDetail.getBuyerName());
            Long buyerId = orderDetail.getBuyerId();
            buyerVo.setUid(buyerId);
            Long buyMerId = memberService.selectMemberByUid(buyerId).getMerchantId();
            buyerVo.setMerId(buyMerId);
            iWalletService.updateWalletByUid(buyerVo);
            TReport sReport = new TReport();
            sReport.setAmount(saleAmout);
            sReport.setFee(BigDecimal.ZERO);
            sReport.setKey(ReportConstant.BUY_AMOUNT);
            sReport.setPeriod(new Date());
            sReport.setMerNo(iWalletService.selectMerchantByUid(buyMerId));
            itReportService.save(sReport);
            //根据条件判断会员星级
//            String s = configService.selectConfigByKey(ConfigKeyConstants.USER_RANK_CONDITION);
//            JSONObject json = JSONObject.parseObject(s);
//            UserRank userRank = userRankService.selectUserRankByUid(buyerId);
//            //无星级会员判断
//            if(userRank == null){
//                //更新并判断是否需要更新信用积分
//            }else{
//                //更新字段并判断是否需要更新信用积分
//                String uids = userRank.getTransferUids();
//                BigDecimal transferMoney = userRank.getTransferMoney();
//            }
            //通知买卖双方订单已完成
            UserMailBox salerBox = new UserMailBox();
            salerBox.setId(snowflakeIdUtils.nextId());
            salerBox.setTitle("出售订单已成功!");
            salerBox.setContent("您的出售订单号为" + referId + "已完成!");
            salerBox.setUserNames(orderDetail.getSalerName());
            salerBox.setUserIds(salerId + "");
            salerBox.setUserType(Integer.parseInt(loginUser.getUserType()));
            salerBox.setCreatTime(new Date());
            salerBox.setSendTime(new Date());
            salerBox.setTopic(UserOrderConst.TOPIC);
            salerBox.setState(UserOrderConst.UNREADSTATE);
            salerBox.setRemark(orderDetail.getPrimaryId());
            if(primaryKey.startsWith("EPAYB")){
                userMailBoxService.insertUserMailBox(salerBox);
                sendMsgToWeb(salerBox);
            }
            UserMailBox buyerBox = new UserMailBox();
            BeanUtils.copyBeanProp(buyerBox,salerBox);
            buyerBox.setId(snowflakeIdUtils.nextId());
            buyerBox.setTitle("购买订单已成功!");
            buyerBox.setContent("您的购买订单号为" + orderDetail.getPrimaryId() + "已完成!");
            buyerBox.setUserNames(orderDetail.getBuyerName());
            buyerBox.setUserIds(buyerId + "");
            userMailBoxService.insertUserMailBox(buyerBox);
            sendMsgToWeb(buyerBox);
            OrderDetailVo vo = new OrderDetailVo();
            BeanUtils.copyBeanProp(vo,orderDetail);
            vo.setSalerRemark(null);
            vo.setBuyerCardName(StringUtils.realNameChange(vo.getBuyerCardName()));
            vo.setBuyerCardAddress(StringUtils.realNameChange(vo.getBuyerCardAddress()));
            vo.setSalerCardName(StringUtils.realNameChange(vo.getSalerCardName()));
            vo.setSalerCardAddress(StringUtils.realNameChange(vo.getSalerCardAddress()));
            return AjaxResult.success(vo);
        }
        throw new ServiceException("确认上分失败!");
    }


    public String getUploadFilePath(String imgId) {
       return (String)redisTemplate.execute(RedisScriptStrings.GET_AND_DEL, Collections.singletonList(MimeTypeUtils.UPFILE_VERIFYID_KEY + imgId));
    }

    /**
     * 暂停放蛋,需上传截图
     *
     * @param primaryKey
     * @return
     */
    @GetMapping("/pauseOrder")
    @PreAuthorize("@ss.realNameVerified()")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult pauseOrder(@RequestParam String primaryKey, String imgId) {
        //暂停放蛋
        if (StringUtils.isEmpty(imgId)) {
            return AjaxResult.error("请上传图片后再确定暂停上分!");
        }
        String path = getUploadFilePath(imgId);
        UserOrderDetail orderDetail = new UserOrderDetail();
        orderDetail.setPrimaryId(primaryKey);
        orderDetail.setPayStatus(UserOrderConst.PAY_STATUS_PAUSE);
        orderDetail.setPauseImg(path);
        orderDetail.setSalerPauseTime(new Date());
        orderDetail.setPayWayId(UserOrderConst.PAY_STATUS_PAID);
        int i = userOrderDetailService.updateStatus(orderDetail);
        if (i > 0) {
            //插入成功,交易异常,提示双方联系客服咨询
            orderDetail = userOrderDetailService.selectUserOrderDetailByPrimaryId(primaryKey);
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = loginUser.getUserId();
            if(!userId.equals(orderDetail.getSalerId())){
                throw new ServiceException("非法操作订单！");
            }
            UserMailBox mailBox = new UserMailBox();
            mailBox.setId(snowflakeIdUtils.nextId());
            mailBox.setTitle("订单交易异常!");
            mailBox.setContent("您有一笔购买订单" + orderDetail.getPrimaryId() + "交易异常,请及时联系客服处理!");
            mailBox.setUserNames(orderDetail.getBuyerName());
            mailBox.setUserIds(orderDetail.getBuyerId() + "");
            mailBox.setUserType(Integer.parseInt(loginUser.getUserType()));
            mailBox.setCreatTime(new Date());
            mailBox.setSendTime(new Date());
            mailBox.setState(UserOrderConst.UNREADSTATE);
            mailBox.setRemark(orderDetail.getPrimaryId());
            mailBox.setTopic(UserOrderConst.TOPIC);
            userMailBoxService.insertUserMailBox(mailBox);
            sendMsgToWeb(mailBox);
            OrderDetailVo vo = new OrderDetailVo();
            BeanUtils.copyBeanProp(vo,orderDetail);
            vo.setOperation_1("暂未收到款项");
            vo.setOperation_2("确认收到款项");
            vo.setOperation_url_2("sendBalanceBySaler");
            vo.setSalerRemark(null);
            vo.setBuyerCardName(StringUtils.realNameChange(vo.getBuyerCardName()));
            vo.setBuyerCardAddress(StringUtils.realNameChange(vo.getBuyerCardAddress()));
            vo.setSalerCardName(StringUtils.realNameChange(vo.getSalerCardName()));
            vo.setSalerCardAddress(StringUtils.realNameChange(vo.getSalerCardAddress()));
            return AjaxResult.success(vo);
        }
        throw new ServiceException("暂停上分失败!");
    }

    @GetMapping("/cancelOrder")
    @PreAuthorize("@ss.realNameVerified()")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult cancelOrder(@RequestParam String primaryKey) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return cancelOrderPublic(primaryKey, UserOrderConst.PAY_STATUS_CREATE, loginUser);
    }

    @GetMapping("/cancelOrderByBuyer")
    @PreAuthorize("@ss.realNameVerified()")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult cancelOrderByBuyer(@RequestParam String primaryKey) {
        //买家取消确认订单
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return cancelOrderPublic(primaryKey, UserOrderConst.PAY_STATUS_CONFIRM, loginUser);
    }

    public AjaxResult cancelOrderPublic(String primaryKey, Short beforeStatus, LoginUser loginUser) {
        String oprateName = loginUser.getUsername();
        UserOrderDetail orderDetail = new UserOrderDetail();
        orderDetail.setPrimaryId(primaryKey);
        orderDetail.setPayStatus(UserOrderConst.PAY_STATUS_CANCEL);
        orderDetail.setOperateName(oprateName);
        orderDetail.setOrderCancelTime(new Date());
        orderDetail.setPayWayId(beforeStatus);
        int i = userOrderDetailService.updateStatus(orderDetail);
        if (i > 0) {
            //插入成功,交易取消,更改订单池状态
            orderDetail = userOrderDetailService.selectUserOrderDetailByPrimaryId(primaryKey);
            String referId = orderDetail.getReferId();
            int y = userOrderService.changeOrderStatus(UserOrderConst.ORDER_STATUS_CREATE, referId,UserOrderConst.ORDER_STATUS_WAITING);
            if(y < 1){
                throw new ServiceException("取消订单失败！");
            }
            Long userId = loginUser.getUserId();
            if(!userId.equals(orderDetail.getSalerId()) && !userId.equals(orderDetail.getBuyerId()) && loginUser.getUserType().equals("0")){
                throw new ServiceException("非法操作订单！");
            }
            UserMailBox buyerBox = new UserMailBox();
            buyerBox.setId(snowflakeIdUtils.nextId());
            buyerBox.setTitle("订单取消");
            buyerBox.setContent("您有一笔购买订单" + orderDetail.getPrimaryId() + "已取消交易!");
            buyerBox.setUserNames(orderDetail.getBuyerName());
            buyerBox.setUserIds(orderDetail.getBuyerId() + "");
            buyerBox.setUserType(Integer.parseInt(loginUser.getUserType()));
            buyerBox.setCreatTime(new Date());
            buyerBox.setSendTime(new Date());
            buyerBox.setTopic(UserOrderConst.TOPIC);
            buyerBox.setState(UserOrderConst.UNREADSTATE);
            buyerBox.setRemark(orderDetail.getPrimaryId());
            userMailBoxService.insertUserMailBox(buyerBox);
            sendMsgToWeb(buyerBox);
            if(primaryKey.startsWith("EPAYB")){
                UserMailBox salerBox = new UserMailBox();
                BeanUtils.copyBeanProp(salerBox,buyerBox);
                salerBox.setId(snowflakeIdUtils.nextId());
                salerBox.setContent("您有一笔出售订单" + referId + "已取消交易!");
                salerBox.setUserNames(orderDetail.getSalerName());
                salerBox.setUserIds(orderDetail.getSalerId() + "");
                userMailBoxService.insertUserMailBox(salerBox);
                sendMsgToWeb(salerBox);
            }
            orderDetail.setBuyerCardName(StringUtils.realNameChange(orderDetail.getBuyerCardName()));
            orderDetail.setBuyerCardAddress(StringUtils.realNameChange(orderDetail.getBuyerCardAddress()));
            OrderDetailVo vo = new OrderDetailVo();
            BeanUtils.copyBeanProp(vo,orderDetail);
            vo.setSalerRemark(null);
            vo.setSalerCardName(null);
            vo.setSalerCardAddress(null);
            vo.setSalerCardRemark(null);
            return AjaxResult.success(vo);
        }
        throw new ServiceException("取消订单失败!");
    }

    @GetMapping("/reloadSucPic")
    @PreAuthorize("@ss.realNameVerified()")
    public AjaxResult reloadSucPic(@RequestParam String primaryKey, String imgId) {
        if (StringUtils.isEmpty(imgId)) {
            return AjaxResult.error("请选择完成付款凭证!");
        }
        String path = getUploadFilePath(imgId);
        int i = userOrderDetailService.updateImg(primaryKey, path, 0);
        if (i > 0) {
            UserOrderDetail orderDetail = userOrderDetailService.selectUserOrderDetailByPrimaryId(primaryKey);
            OrderDetailVo vo = new OrderDetailVo();
            BeanUtils.copyBeanProp(vo,orderDetail);
            vo.setOperation_1("等待卖家放蛋");
            vo.setSalerRemark(null);
            vo.setBuyerCardName(StringUtils.realNameChange(vo.getBuyerCardName()));
            vo.setBuyerCardAddress(StringUtils.realNameChange(vo.getBuyerCardAddress()));
            vo.setSalerCardName(StringUtils.realNameChange(vo.getSalerCardName()));
            vo.setSalerCardAddress(StringUtils.realNameChange(vo.getSalerCardAddress()));
            return AjaxResult.success(vo);
        }
        return AjaxResult.error("更新完成付款图片失败!");
    }

    @GetMapping("/reloadPausePic")
    @PreAuthorize("@ss.realNameVerified()")
    public AjaxResult reloadPausePic(@RequestParam String primaryKey, String imgId) {
        if (StringUtils.isEmpty(imgId)) {
            return AjaxResult.error("请选择暂停上分凭证!");
        }
        String path = getUploadFilePath(imgId);
        int i = userOrderDetailService.updateImg(primaryKey, path, 1);
        if (i > 0) {
            UserOrderDetail orderDetail = userOrderDetailService.selectUserOrderDetailByPrimaryId(primaryKey);
            OrderDetailVo vo = new OrderDetailVo();
            BeanUtils.copyBeanProp(vo,orderDetail);
            vo.setOperation_1("暂未收到款项");
            vo.setOperation_2("确认收到款项");
            vo.setOperation_url_2("sendBalanceBySaler");
            vo.setSalerRemark(null);
            vo.setBuyerCardName(StringUtils.realNameChange(vo.getBuyerCardName()));
            vo.setBuyerCardAddress(StringUtils.realNameChange(vo.getBuyerCardAddress()));
            vo.setSalerCardName(StringUtils.realNameChange(vo.getSalerCardName()));
            vo.setSalerCardAddress(StringUtils.realNameChange(vo.getSalerCardAddress()));
            return AjaxResult.success(vo);
        }
        return AjaxResult.error("更新暂停付款图片失败!");
    }


    @GetMapping("/queryInfo")
    @PreAuthorize("@ss.realNameVerified()")
    public AjaxResult queryInfo(@RequestParam String primaryKey) {
        Long userId = SecurityUtils.getUserId();
        OrderDetailVo item = userOrderDetailService.selectById(primaryKey, userId);
        return AjaxResult.success(item);
    }

    /**
     * 取消我的卖蛋记录
     * @return
     */
    @GetMapping("/buyRecord")
    @PreAuthorize("@ss.realNameVerified()")
    public TableDataInfo buyRecord(Map<String,Object> param) {
        startPage();
        param.put("uid",SecurityUtils.getUserId());
        return getDataTable(userOrderDetailService.selectMyBuyOrder(param));
    }

    /**
     * 取消我的挂单记录
     * @return
     */
    @GetMapping("/cancelRecord")
    @PreAuthorize("@ss.realNameVerified()")
    public AjaxResult cancelRecord(String primaryKey) {
        Long userId = SecurityUtils.getUserId();
        int i = userOrderService.cancelUserOrder(primaryKey,userId);
        if(i > 0){
            UserOrder userOrder = userOrderService.selectUserOrderById(primaryKey);
            //退还会员钱包余额
            WalletVo walletVo = new WalletVo();
            walletVo.setRefId(userOrder.getId());
            walletVo.setRemark("会员取消卖蛋退还金额");
            walletVo.setAmount(userOrder.getTotalAmout());
            walletVo.setIp(IpUtils.getCurrentReqIp());
            walletVo.setTreasureTypeEnum(TreasureTypeEnum.WITHDRAW_REFUND);
            walletVo.setUserName(userOrder.getUserName());
            Long uid = userOrder.getUserId();
            walletVo.setUid(uid);
            walletVo.setMerId(memberService.selectMemberByUid(uid).getMerchantId());
            iWalletService.updateWalletByUid(walletVo);
            return AjaxResult.success(userOrder);
        }
        return AjaxResult.error("取消订单失败!");
    }

    /**
     * 查询我的卖蛋记录
     * @return
     */
    @GetMapping("/saleRecord")
    @PreAuthorize("@ss.realNameVerified()")
    public TableDataInfo saleRecord(Map<String,Object> param) {
        startPage();
        param.put("uid",SecurityUtils.getUserId());
        return getDataTable(userOrderService.selectMyOrder(param));
    }


}

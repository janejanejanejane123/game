package com.ruoyi.web.controller.order;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.ReportConstant;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.enums.TreasureTypeEnum;
import com.ruoyi.common.enums.UserOrderConst;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.member.service.IUserMailBoxService;
import com.ruoyi.order.domain.UserOrderDetail;
import com.ruoyi.order.domain.vo.OrderMessageVo;
import com.ruoyi.order.service.IUserOrderDetailService;
import com.ruoyi.order.service.IUserOrderService;
import com.ruoyi.pay.domain.TReport;
import com.ruoyi.pay.domain.vo.WalletVo;
import com.ruoyi.pay.service.ITReportService;
import com.ruoyi.pay.service.IWalletService;
import com.ruoyi.payment.domain.DfReport;
import com.ruoyi.payment.service.IDfReportService;
import com.ruoyi.payment.service.IProcedureFeeService;
import com.ruoyi.system.service.IMemberService;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * orderController
 *
 * @author mc_dog
 * @date 2022-03-20
 */
@RestController
@RequestMapping("/system/detail")
public class UserOrderDetailController extends BaseController
{
    private Logger logger = LoggerFactory.getLogger(UserOrderDetailController.class);

    @Resource
    private IUserOrderDetailService userOrderDetailService;

    @Resource
    IWalletService iWalletService;

    @Resource
    IUserMailBoxService userMailBoxService;

    @Resource
    IUserOrderService userOrderService;

    @Resource
    RocketMQTemplate rocketMQTemplate;

    @Resource
    IProcedureFeeService procedureFeeService;

    @Resource
    SnowflakeIdUtils snowflakeIdUtils;

    @Resource
    IDfReportService dfReportService;

    @Resource
    IMemberService memberService;

    @Resource
    ITReportService itReportService;

    /**
     * 查询order列表
     */
    @PreAuthorize("@ss.hasPermi('system:detail:list')")
    @GetMapping("/list")
    public TableDataInfo list(UserOrderDetail userOrderDetail)
    {
        userOrderDetail.setOperateName("B");
        startPage();
        List<UserOrderDetail> list = userOrderDetailService.selectUserList(userOrderDetail);
        return getDataTable(list);
    }

    /**
     * 导出order列表
     */
    @PreAuthorize("@ss.hasPermi('system:detail:list')")
    @Log(title = "order", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserOrderDetail userOrderDetail)
    {
        userOrderDetail.setOperateName("B");
        List<UserOrderDetail> list = userOrderDetailService.selectUserList(userOrderDetail);
        ExcelUtil<UserOrderDetail> util = new ExcelUtil<UserOrderDetail>(UserOrderDetail.class);
        util.exportExcel(response, list, "order数据");
    }

    /**
     * 查询order代付列表
     */
    @PreAuthorize("@ss.hasPermi('system:detail:dflist')")
    @GetMapping("/dfList")
    public TableDataInfo dfList(UserOrderDetail userOrderDetail)
    {
        userOrderDetail.setOperateName("M");
        startPage();
        List<UserOrderDetail> list = userOrderDetailService.selectUserList(userOrderDetail);
        return getDataTable(list);
    }

    /**
     * 导出order代付列表
     */
    @PreAuthorize("@ss.hasPermi('system:detail:dflist')")
    @Log(title = "order", businessType = BusinessType.EXPORT)
    @PostMapping("/dfExport")
    public void dfExport(HttpServletResponse response, UserOrderDetail userOrderDetail)
    {
        userOrderDetail.setOperateName("M");
        List<UserOrderDetail> list = userOrderDetailService.selectUserList(userOrderDetail);
        ExcelUtil<UserOrderDetail> util = new ExcelUtil<UserOrderDetail>(UserOrderDetail.class);
        util.exportExcel(response, list, "order数据");
    }

    /**
     * 获取order详细信息
     */
//    @PreAuthorize("@ss.hasPermi('system:detail:query')")
//    @GetMapping(value = "/{primaryId}")
//    public AjaxResult getInfo(@PathVariable("primaryId") String primaryId)
//    {
//        return AjaxResult.success(userOrderDetailService.selectUserOrderDetailByPrimaryId(primaryId));
//    }

    /**
     * 新增order
     */
//    @PreAuthorize("@ss.hasPermi('system:detail:add')")
//    @Log(title = "order", businessType = BusinessType.INSERT)
//    @PostMapping
//    public AjaxResult add(@RequestBody UserOrderDetail userOrderDetail)
//    {
//        return toAjax(userOrderDetailService.insertUserOrderDetail(userOrderDetail));
//    }

    /**
     * 修改order
     */
//    @PreAuthorize("@ss.hasPermi('system:detail:edit')")
//    @Log(title = "order", businessType = BusinessType.UPDATE)
//    @PutMapping
//    public AjaxResult edit(@RequestBody UserOrderDetail userOrderDetail)
//    {
//        return toAjax(userOrderDetailService.updateUserOrderDetail(userOrderDetail));
//    }

    /**
     * 删除order
     */
//    @PreAuthorize("@ss.hasPermi('system:detail:remove')")
//    @Log(title = "order", businessType = BusinessType.DELETE)
//    @DeleteMapping("/{primaryIds}")
//    public AjaxResult remove(@PathVariable String[] primaryIds)
//    {
//        return toAjax(userOrderDetailService.deleteUserOrderDetailByPrimaryIds(primaryIds));
//    }

    /**
     * 后台管理取消订单
     * @param primaryKey
     * @return
     */
    @GetMapping("/cancelOrderByAdmin")
    @PreAuthorize("@ss.hasPermi('system:detail:edit')")
    @Log(title = "抢单订单管理员确认取消", businessType = BusinessType.CLEAN)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult cancelOrderByAdmin(@RequestParam String primaryKey) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String oprateName = loginUser.getUsername();
        UserOrderDetail orderDetail = new UserOrderDetail();
        orderDetail.setPrimaryId(primaryKey);
        orderDetail.setPayStatus(UserOrderConst.PAY_STATUS_CANCEL);
        orderDetail.setOperateName(oprateName);
        orderDetail.setOrderCancelTime(new Date());
        int i = userOrderDetailService.cancelOrderByAdmin(orderDetail);
        if (i > 0) {
            //插入成功,交易取消,更改订单池状态
            orderDetail = userOrderDetailService.selectUserOrderDetailByPrimaryId(primaryKey);
            int x = userOrderService.changeOrderStatus(UserOrderConst.ORDER_STATUS_CREATE,orderDetail.getReferId(),UserOrderConst.ORDER_STATUS_WAITING);
            if(x < 1){
                new ServiceException("订单取消失败!");
            }
            UserMailBox buyerBox = new UserMailBox();
            buyerBox.setId(snowflakeIdUtils.nextId());
            buyerBox.setTitle("订单取消");
            buyerBox.setContent("您有一笔购买订单" + orderDetail.getPrimaryId() + "已取消交易!");
            buyerBox.setUserNames(orderDetail.getBuyerName());
            buyerBox.setUserIds(orderDetail.getBuyerId() + "");
            buyerBox.setUserType(0);
            buyerBox.setCreatTime(new Date());
            buyerBox.setSendTime(new Date());
            buyerBox.setTopic(UserOrderConst.TOPIC);
            buyerBox.setRemark(orderDetail.getPrimaryId());
            buyerBox.setState(UserOrderConst.UNREADSTATE);
            userMailBoxService.insertUserMailBox(buyerBox);
            sendMsgToWeb(buyerBox);
            if(orderDetail.getPrimaryId().startsWith("EPAYB")){
                UserMailBox salerBox = new UserMailBox();
                BeanUtils.copyBeanProp(salerBox,buyerBox);
                salerBox.setId(snowflakeIdUtils.nextId());
                salerBox.setContent("您有一笔出售订单" + orderDetail.getReferId() + "已取消交易!");
                salerBox.setUserNames(orderDetail.getSalerName());
                salerBox.setUserIds(orderDetail.getSalerId() + "");
                userMailBoxService.insertUserMailBox(salerBox);
                sendMsgToWeb(salerBox);
            }
            return AjaxResult.success();
        }
        throw new ServiceException("取消订单失败!");
    }

    public void sendMsgToWeb(UserMailBox mailBox){
        MessageBuilder builder = MessageBuilder.withPayload(mailBox);
        SendResult result = rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder.build());
        if(!SendStatus.SEND_OK.equals(result.getSendStatus())){
            throw new ServiceException("消息发送失败!");
        }
    }

    /**
     * 管理员确认代付订单
     * @param primaryKey
     * @return
     */
    @GetMapping("/confirm")
    @PreAuthorize("@ss.hasPermi('system:detail:edit')")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult preConfirm(@RequestParam String primaryKey) {
        UserOrderDetail orderDetail = new UserOrderDetail();
        orderDetail.setPrimaryId(primaryKey);
        orderDetail.setPayStatus(UserOrderConst.PAY_STATUS_CONFIRM);
        orderDetail.setSalerCheckTime(new Date());
        orderDetail.setPayWayId(UserOrderConst.PAY_STATUS_CREATE);
        int i = userOrderDetailService.updateStatus(orderDetail);
        orderDetail = userOrderDetailService.selectUserOrderDetailByPrimaryId(primaryKey);
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
            mailBox.setUserType(0);
            mailBox.setCreatTime(new Date());
            mailBox.setSendTime(new Date());
            mailBox.setState(UserOrderConst.UNREADSTATE);
            mailBox.setRemark(orderDetail.getPrimaryId());
            userMailBoxService.insertUserMailBox(mailBox);
            sendMsgToWeb(mailBox);
            return AjaxResult.success();
        }
        throw new ServiceException("确认订单失败!");
    }


    /**
     * 后台管理员确认放蛋
     * @param primaryKey
     * @return
     */
    @GetMapping("/sendBalanceByAdmin")
    @PreAuthorize("@ss.hasPermi('system:detail:edit')")
    @Log(title = "抢单订单管理员确认放蛋", businessType = BusinessType.CONFIRM)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult sendBalanceByAdmin(@RequestParam String primaryKey) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String loginName = loginUser.getUsername();
        UserOrderDetail orderDetail = new UserOrderDetail();
        orderDetail.setPrimaryId(primaryKey);
        orderDetail.setSalerConfirmTime(new Date());
        orderDetail.setOrderFinishTime(new Date());
        orderDetail.setPayStatus(UserOrderConst.PAY_STATUS_FINISH);
        orderDetail.setOperateName(loginName);
        //更改订单池状态 完成并锁定
        int i = userOrderDetailService.sendBalanceByAdmin(orderDetail);
        if (i > 0) {
            //插入成功,通知卖家确认订单
            orderDetail = userOrderDetailService.selectUserOrderDetailByPrimaryId(primaryKey);
            String referId = orderDetail.getReferId();
            int x = userOrderService.changeOrderStatus(UserOrderConst.ORDER_STATUS_FINISH, referId,UserOrderConst.ORDER_STATUS_WAITING);
            if(x < 1){
                 logger.error("购买记录{}", JSON.toJSONString(orderDetail));
                 logger.error("出售对应订单id{}", referId);
                 throw new ServiceException("确认放蛋失败!");
            }
            boolean isPayment = primaryKey.startsWith("EPAYM");
            BigDecimal saleAmout = orderDetail.getSaleAmout();
            //卖家扣除钱包冻结余额
            WalletVo salerVo = new WalletVo();
            salerVo.setRefId(referId);
            salerVo.setAmount(isPayment ? saleAmout.add(BigDecimal.ONE) : saleAmout);
            salerVo.setIp(IpUtils.getCurrentReqIp());
            salerVo.setTreasureTypeEnum(TreasureTypeEnum.WITHDRAW_2);
            salerVo.setUserName(orderDetail.getSalerName());
            Long salerId = orderDetail.getSalerId();
            Long sale_mid = null;
            salerVo.setUid(salerId);
            if(isPayment){
                sale_mid = salerId;
                salerVo.setMerId(salerId);
                salerVo.setRemark("代付成功");
            }else{
                sale_mid = memberService.selectMemberByUid(salerId).getMerchantId();
                salerVo.setMerId(sale_mid);
                salerVo.setRemark("会员卖蛋");
            }
            iWalletService.updateWalletByUid(salerVo);
            TReport tReport = new TReport();
            tReport.setAmount(saleAmout);
            tReport.setKey(ReportConstant.SELL_AMOUNT);
            tReport.setPeriod(new Date());
            tReport.setFee(BigDecimal.ZERO);
            tReport.setMerNo(isPayment ? orderDetail.getSalerName() : iWalletService.selectMerchantByUid(sale_mid));
            itReportService.save(tReport);
            //买家增加钱包余额
            WalletVo buyerVo = new WalletVo();
            BeanUtils.copyBeanProp(buyerVo,salerVo);
            buyerVo.setRemark("会员买蛋");
            buyerVo.setRefId(orderDetail.getPrimaryId());
            buyerVo.setTreasureTypeEnum(TreasureTypeEnum.RECHARGE);
            buyerVo.setAmount(saleAmout);
            buyerVo.setUserName(orderDetail.getBuyerName());
            Long buyerId = orderDetail.getBuyerId();
            buyerVo.setUid(buyerId);
            Long merchantId = memberService.selectMemberByUid(buyerId).getMerchantId();
            buyerVo.setMerId(merchantId);
            iWalletService.updateWalletByUid(buyerVo);
            TReport sReport = new TReport();
            sReport.setAmount(saleAmout);
            sReport.setFee(BigDecimal.ZERO);
            sReport.setKey(ReportConstant.BUY_AMOUNT);
            sReport.setPeriod(new Date());
            sReport.setMerNo(iWalletService.selectMerchantByUid(merchantId));
            itReportService.save(sReport);
            //如果是代付，判断上级是否为代理,给上级返水
            if(isPayment){
                Map<String, Object> backInfo = procedureFeeService.getWaterBackInfo(salerId);
                if(backInfo != null){
                    Long pid = Long.parseLong(backInfo.get("pid").toString());
                    String pName = backInfo.get("pName").toString();
                    BigDecimal rate = new BigDecimal(backInfo.get("rate").toString());
                    WalletVo waterVo = new WalletVo();
                    waterVo.setRefId(referId);
                    waterVo.setRemark("代付订单返水");
                    BigDecimal backMoney = saleAmout.multiply(rate).setScale(2, RoundingMode.DOWN);
                    waterVo.setAmount(backMoney);
                    waterVo.setIp(IpUtils.getCurrentReqIp());
                    waterVo.setTreasureTypeEnum(TreasureTypeEnum.RETURN_WATER);
                    waterVo.setUserName(pName);
                    waterVo.setUid(pid);
                    waterVo.setMerId(pid);
                    iWalletService.updateWalletByUid(waterVo);
                    DfReport report = new DfReport();
                    report.setId(snowflakeIdUtils.nextId());
                    report.setUid(salerId);
                    report.setName(orderDetail.getSalerName());
                    report.setRefId(referId);
                    report.setRate(rate);
                    try {
                        report.setPeriod(DateUtils.parseDate(referId.substring(5, 19),"yyyyMMddHHmmss"));
                    } catch (ParseException e) {
                        logger.error("格式化报表时间出错",e);
                    }
                    report.setMoney(saleAmout);
                    report.setMoneyBack(backMoney);
                    dfReportService.insertDfReport(report);
                }
            }
            //通知买卖双方订单已完成
            UserMailBox salerBox = new UserMailBox();
            salerBox.setId(snowflakeIdUtils.nextId());
            salerBox.setTitle("出售订单已成功!");
            salerBox.setContent("您的出售订单号为" + referId + "已完成!");
            salerBox.setUserNames(orderDetail.getSalerName());
            salerBox.setUserIds(salerId + "");
            salerBox.setUserType(0);
            salerBox.setCreatTime(new Date());
            salerBox.setSendTime(new Date());
            salerBox.setRemark(orderDetail.getPrimaryId());
            salerBox.setTopic(UserOrderConst.TOPIC);
            salerBox.setState(UserOrderConst.UNREADSTATE);
            //非商户订单通知卖家
            if(!isPayment){
                userMailBoxService.insertUserMailBox(salerBox);
                sendMsgToWeb(salerBox);
            }
            //通知买家
            UserMailBox buyerBox = new UserMailBox();
            BeanUtils.copyBeanProp(buyerBox,salerBox);
            buyerBox.setId(snowflakeIdUtils.nextId());
            buyerBox.setTitle("购买订单已成功!");
            buyerBox.setContent("您的购买订单号为" + orderDetail.getPrimaryId() + "已完成!");
            buyerBox.setUserNames(orderDetail.getBuyerName());
            buyerBox.setUserIds(buyerId + "");
            userMailBoxService.insertUserMailBox(buyerBox);
            sendMsgToWeb(buyerBox);
            return AjaxResult.success();
        }
        throw new ServiceException("确认放蛋失败!");
    }

    @PostMapping("/deleteImg")
    @PreAuthorize("@ss.hasPermi('system:detail:edit')")
    public AjaxResult deleteImg(@RequestBody Map<String,String> map) {
        int i = userOrderDetailService.deleteImg(map.get("id"), map.get("type"));
        if (i > 0) {
            return AjaxResult.success();
        }
        return AjaxResult.error("删除图片失败!");
    }

    @PostMapping("/reUploadPic")
    @PreAuthorize("@ss.hasPermi('system:detail:edit')")
    public AjaxResult reUploadPic(@RequestBody Map<String,String> map) {
        int i = userOrderDetailService.reUploadPic(map.get("id"), map.get("type"));
        if (i > 0) {
            return AjaxResult.success();
        }
        return AjaxResult.error("后台操作重新上传图片失败!");
    }

    public static void main(String[] args) throws ParseException {
        String s = "EPAYS202205291603525973AAA7A";
        System.out.println(DateUtils.parseDate(s.substring(5, 19),"yyyyMMddHHmmss"));
    }
}

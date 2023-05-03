package com.ruoyi.web.controller.order;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.BusinessType;
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
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.uuid.Seq;
import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.member.util.RocketMqService;
import com.ruoyi.order.domain.UserOrder;
import com.ruoyi.order.domain.vo.OrderMarket;
import com.ruoyi.order.service.IUserOrderService;
import com.ruoyi.pay.domain.Wallet;
import com.ruoyi.pay.domain.vo.WalletVo;
import com.ruoyi.pay.service.IWalletService;
import com.ruoyi.payment.domain.DfReport;
import com.ruoyi.payment.service.IDfReportService;
import com.ruoyi.payment.service.IProcedureFeeService;
import com.ruoyi.system.service.IMemberService;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;

/**
 * orderController
 *
 * @author ruoyi
 * @date 2022-03-23
 */
@RestController
@RequestMapping("/system/order")
public class UserOrderController extends BaseController
{
    @Resource
    private IUserOrderService userOrderService;

    @Resource
    IWalletService iWalletService;

    @Resource
    RedisCache redisCache;

    @Resource
    RocketMQTemplate rocketMQTemplate;

    @Resource
    IMemberService memberService;

    @Resource
    RocketMqService rocketMqService;

    @Resource
    IProcedureFeeService procedureFeeService;

    @Resource
    SnowflakeIdUtils snowflakeIdUtils;

    @Resource
    IDfReportService dfReportService;

    /**
     * 查询order列表
     */
    @PreAuthorize("@ss.hasPermi('system:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(UserOrder userOrder)
    {
        startPage();
        //判断是否为商户
        if(isMerchant()){
            userOrder.setOrderStatus(UserOrderConst.ORDER_STATUS_CREATE);
            userOrder.setIsSplit(UserOrderConst.FREE_SALE);
        }
        List<UserOrder> list = userOrderService.selectUserOrderList(userOrder);
        TableDataInfo table = getDataTable(list);
        List<UserOrder> rows = (List<UserOrder>) table.getRows();
        List<OrderMarket> res = new ArrayList<>();
        rows.forEach(item->{
            OrderMarket market = new OrderMarket();
            BeanUtils.copyBeanProp(market,item);
            res.add(market);
        });
        table.setRows(res);
        return table;
    }

    /**
     * 查询order列表
     */
    @PreAuthorize("@ss.hasPermi('payment:order:list')")
    @GetMapping("/dfList")
    public TableDataInfo dfList(UserOrder userOrder)
    {
        userOrder.setIsSplit(UserOrderConst.MERCHANT_SALE);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //判断是否为商户
        if(isPayMent()){
            userOrder.setUserId(loginUser.getUserId());
        }
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        List<UserOrder> list = userOrderService.selectUserOrderList(userOrder);
        double waitSum = list.stream().filter(item -> UserOrderConst.ORDER_STATUS_CREATE.equals(item.getOrderStatus())).mapToDouble(x -> x.getTotalAmout().doubleValue()).sum();
        double processSum = list.stream().filter(item -> UserOrderConst.ORDER_STATUS_WAITING.equals(item.getOrderStatus())).mapToDouble(x -> x.getTotalAmout().doubleValue()).sum();
        double manualSum = list.stream().filter(item -> UserOrderConst.ORDER_STATUS_FINISH.equals(item.getOrderStatus()) && StringUtils.isNotEmpty(item.getSaleRemark())).mapToDouble(x -> x.getTotalAmout().doubleValue()).sum();
        double finishSum = list.stream().filter(item -> UserOrderConst.ORDER_STATUS_FINISH.equals(item.getOrderStatus())).mapToDouble(x -> x.getTotalAmout().doubleValue()).sum();
        double cancelSum = list.stream().filter(item -> UserOrderConst.ORDER_STATUS_CANCEL.equals(item.getOrderStatus())).mapToDouble(x -> x.getTotalAmout().doubleValue()).sum();
        TableDataInfo rspData = new TableDataInfo();
        int total = list.size();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list.subList(pageSize * (pageNum - 1),pageSize * pageNum > total ? total : pageSize * pageNum));
        rspData.setCurrPage(pageNum);
        rspData.setPageSize(pageSize);
        rspData.setTotal(total);
        rspData.setTotalPage(total % pageSize == 0 ?total / pageSize : total / pageSize + 1);
        rspData.put("waitSum",waitSum);
        rspData.put("processSum",processSum);
        rspData.put("manualSum",manualSum);
        rspData.put("finishSum",finishSum);
        rspData.put("cancelSum",cancelSum);
        return rspData;
    }

    /**
     * 下载订单导入模板
     * @param response
     */
    @PostMapping("/importTemplate")
    @PreAuthorize("@ss.hasPermi('payment:order:add')")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<UserOrder> util = new ExcelUtil<>(UserOrder.class);
        util.importTemplateExcel(response, "代付订单");
    }

    /**
     * 导出order列表
     */
    @PreAuthorize("@ss.hasPermi('system:order:list || payment:order:list')")
    @Log(title = "order", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserOrder userOrder)
    {
        List<UserOrder> list = userOrderService.selectUserOrderList(userOrder);
        ExcelUtil<UserOrder> util = new ExcelUtil<UserOrder>(UserOrder.class);
        util.exportExcel(response, list, "订单记录");
    }

    @PostMapping("/upload")
    @PreAuthorize("@ss.hasPermi('payment:order:add')")
    public AjaxResult upload(@RequestParam("file") MultipartFile file)
    {
        LoginUser user = SecurityUtils.getLoginUser();
        Long userId = user.getUserId();
        String name = user.getUsername();
        ExcelUtil<UserOrder> util = new ExcelUtil<UserOrder>(UserOrder.class);
        List<UserOrder> userList;
        try {
            userList = util.importExcel(file.getInputStream());
        } catch (Exception e) {
            logger.error("导入代付订单异常");
            throw new ServiceException("导入代付订单异常!");
        }
        if(!CollectionUtils.isEmpty(userList)){
            double sum = userList.stream().mapToDouble(order -> order.getTotalAmout().doubleValue()).sum();
            BigDecimal balance = iWalletService.selectWalletByUid(userId).getBalance();
            if(sum + userList.size() - balance.doubleValue() > 0){
                throw new ServiceException("代付订单过多导致钱包余额不足!");
            }
            userList.forEach(item->{
                item.setUserId(userId);
                item.setUserName(name);
                item.setUserImage(user.getUser().getAvatar());
                String id = Seq.generateId("S");
                item.setId(id);
                item.setPayWay(Short.valueOf("0"));
                item.setIsSplit(UserOrderConst.MERCHANT_SALE);
                item.setCreateTime(new Date());
                item.setOrderStatus(UserOrderConst.ORDER_STATUS_CREATE);
                userOrderService.insertUserOrder(item);
                WalletVo walletVo = new WalletVo();
                walletVo.setRefId(id);
                walletVo.setRemark("商家代付");
                walletVo.setAmount(item.getTotalAmout());
                walletVo.setFee(BigDecimal.ONE);
                walletVo.setIp(IpUtils.getCurrentReqIp());
                walletVo.setTreasureTypeEnum(TreasureTypeEnum.WITHDRAW_MERCHANT);
                walletVo.setUserName(name);
                walletVo.setUid(userId);
                walletVo.setMerId(userId);
                iWalletService.updateWalletByUid(walletVo);
            });
        }
        return AjaxResult.success();
    }


    @PostMapping("/uploadOrder")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PreAuthorize("@ss.hasPermi('payment:order:add')")
    public AjaxResult uploadOrder(@RequestBody Map<String,Object> map) {
        String googleCode = map.get("code").toString();
        LoginUser user = SecurityUtils.getLoginUser();
        validateGoogleCode(user.getUser().getSecret(),Long.parseLong(googleCode));
        Long userId = user.getUserId();
        String name = user.getUsername();
        JSONArray userList = JSONArray.parseArray(map.get("list").toString());
        if(!CollectionUtils.isEmpty(userList)){
            double sum = userList.stream().mapToDouble(order -> ((JSONObject)order).getDouble("amount")).sum();
            BigDecimal balance = iWalletService.selectWalletByUid(userId).getBalance();
            if(sum + userList.size() - balance.doubleValue() > 0){
                throw new ServiceException("代付订单过多导致钱包余额不足!");
            }
            for (int i = 0; i < userList.size(); i++) {
                JSONObject json = userList.getJSONObject(i);
                BigDecimal amount = json.getBigDecimal("amount");
                UserOrder item = new UserOrder();
                String id = Seq.generateId("S");
                item.setId(id);
                item.setUserId(userId);
                item.setUserName(name);
                item.setUserImage(user.getUser().getAvatar());
                item.setPayWay(Short.valueOf("0"));
                item.setTotalAmout(amount);
                item.setUserCardRemark(json.getString("remark").trim());
                item.setUserRealName(json.getString("name").trim());
                item.setUserCardAddress(json.getString("address").trim());
                item.setIsSplit(UserOrderConst.MERCHANT_SALE);
                item.setCreateTime(new Date());
                item.setOrderStatus(UserOrderConst.ORDER_STATUS_CREATE);
                userOrderService.insertUserOrder(item);
                WalletVo walletVo = new WalletVo();
                walletVo.setRefId(id);
                walletVo.setRemark("商家代付");
                walletVo.setAmount(amount);
                walletVo.setFee(BigDecimal.ONE);
                walletVo.setIp(IpUtils.getCurrentReqIp());
                walletVo.setTreasureTypeEnum(TreasureTypeEnum.WITHDRAW_MERCHANT);
                walletVo.setUserName(name);
                walletVo.setUid(userId);
                walletVo.setMerId(userId);
                iWalletService.updateWalletByUid(walletVo);
            }
        }
        return AjaxResult.success();
    }

    /**
     * 商户卖蛋（代付）
     * 1扣除代付商家押金，扣除单笔手续费（暂定1元）
     * 2同步挂单市场
     * 3插入代付商家流水
     * @param userOrder
     * @return
     */
    @PostMapping("/add")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PreAuthorize("@ss.hasPermi('payment:order:add')")
    public AjaxResult query(@RequestBody UserOrder userOrder) {
        String googleCode = userOrder.getSaleRemark();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        validateGoogleCode(loginUser.getUser().getSecret(),Long.parseLong(googleCode));
        Long userId = loginUser.getUserId();
        Wallet wallet = iWalletService.selectWalletByUid(userId);
        BigDecimal balance = wallet.getBalance();
        Assert.test(userOrder.getTotalAmout().compareTo(balance.subtract(BigDecimal.ONE)) > 0,"walletvo.amount.notEnough");
        userOrder.setId(Seq.generateId("S"));
        userOrder.setUserName(loginUser.getUsername());
        userOrder.setUserId(userId);
        userOrder.setUserImage(loginUser.getUser().getAvatar());
        //无需审核
        userOrder.setCreateTime(new Date());
        userOrder.setOrderStatus(UserOrderConst.ORDER_STATUS_CREATE);
        userOrder.setIsSplit(UserOrderConst.MERCHANT_SALE);
        int i = userOrderService.insertUserOrder(userOrder);
        if(i > 0){
            //卖家扣除钱包余额
            WalletVo walletVo = new WalletVo();
            walletVo.setRefId(userOrder.getId());
            walletVo.setRemark("商家代付");
            walletVo.setAmount(userOrder.getTotalAmout());
            walletVo.setFee(BigDecimal.ONE);
            walletVo.setIp(IpUtils.getCurrentReqIp());
            walletVo.setTreasureTypeEnum(TreasureTypeEnum.WITHDRAW_MERCHANT);
            walletVo.setUserName(loginUser.getUsername());
            walletVo.setUid(userId);
            walletVo.setMerId(userId);
            iWalletService.updateWalletByUid(walletVo);
            return AjaxResult.success("提交成功,请留意订单变化!");
        }
        throw new ServiceException("系统繁忙,请稍后再试!");
    }
//
//    /**
//     * 获取order详细信息
//     */
//    @PreAuthorize("@ss.hasPermi('system:order:query')")
//    @GetMapping(value = "/{id}")
//    public AjaxResult getInfo(@PathVariable("id") String id)
//    {
//        return AjaxResult.success(UserOrderService.selectUserOrderById(id));
//    }
//
//    /**
//     * 新增order
//     */
//    @PreAuthorize("@ss.hasPermi('system:order:add')")
//    @Log(title = "order", businessType = BusinessType.INSERT)
//    @PostMapping
//    public AjaxResult add(@RequestBody UserOrder userOrder)
//    {
//        return toAjax(UserOrderService.insertUserOrder(userOrder));
//    }
//

    /**
     * 查询待审核条目数
     */
    @PreAuthorize("@ss.hasPermi('system:order:edit')")
    @GetMapping("/queryNo")
    public AjaxResult queryNo()
    {
        UserInfoApplyEnums enums = UserInfoApplyEnums.SALE_COIN_VERIFY;
        String redisKey = enums.getRedisKey();
        Assert.test(enums == null, "system.error");
        boolean b = redisCache.hasKey(enums.getPerm());
        Integer count;
        if(b){
            count = redisCache.getCacheObject(redisKey);
        }else{
            count = userOrderService.queryUnCheckNo();
            redisCache.setCacheObject(redisKey,count);
        }
        return AjaxResult.success(count < 0 ? 0 : count);
    }

    /**
     * 修改order
     */
    @PreAuthorize("@ss.hasPermi('system:order:edit')")
    @Log(title = "order", businessType = BusinessType.UPDATE)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody Map<String,String> map)
    {
        String status = map.get("status");
        //是否更改为通过(待购买)或者不通过(驳回)状态
        boolean isVerify = "1".equals(status) || "5".equals(status);
        boolean walletChange = "4".equals(status) || "5".equals(status);
        if(isVerify){
            map.put("before","0");
        }else if("4".equals(status)){
            map.put("before","1");
        }else{
            throw new ServiceException("当前状态暂不支持此操作!");
        }
        int i = userOrderService.updateUserOrderStatus(map);
        if(i == 1){
            UserOrder userOrder = userOrderService.selectUserOrderById(map.get("id"));
            if(walletChange){
                //插入成功后，根据状态进行钱包操作
                WalletVo salerVo = new WalletVo();
                salerVo.setRefId(userOrder.getId());
                salerVo.setRemark(("4".equals(status) ? "取消" : "驳回") + "卖蛋");
                salerVo.setIp(IpUtils.getCurrentReqIp());
                salerVo.setAmount(userOrder.getTotalAmout());
                salerVo.setTreasureTypeEnum(TreasureTypeEnum.WITHDRAW_REFUND);
                salerVo.setUserName(userOrder.getUserName());
                salerVo.setUid(userOrder.getUserId());
                salerVo.setSysName(SecurityUtils.getUsername());
                salerVo.setMerId(memberService.selectMemberByUid(userOrder.getUserId()).getMerchantId());
                iWalletService.updateWalletByUid(salerVo);
            }
           if(isVerify){
               //redis中去掉待审核消息内容，并发送消息给后台管理员。
               String title = "1".equals(status) ? "审核通过" : "审核驳回";
               long decr = redisCache.decr(UserInfoApplyEnums.SALE_COIN_VERIFY.getRedisKey(), 1);
               rocketMqService.applyToAdmin(UserInfoApplyEnums.SALE_COIN_VERIFY,decr);
               UserMailBox buyerBox = new UserMailBox();
               buyerBox.setTitle(title);
               buyerBox.setContent("您有一笔出售订单" + userOrder.getId() + (title));
               buyerBox.setUserNames(userOrder.getUserName());
               buyerBox.setUserIds(userOrder.getUserId() + "");
               buyerBox.setUserType(0);
               buyerBox.setTopic(UserOrderConst.SALE_TOPIC);
               MessageBuilder builder = MessageBuilder.withPayload(buyerBox);
               SendResult result = rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder.build());
               if(!SendStatus.SEND_OK.equals(result.getSendStatus())){
                   throw new ServiceException("消息发送失败!");
               }
           }
            return toAjax(i);
        }
        throw new ServiceException("订单状态更改失败!");
    }

    /**
     * 代付商户取消订单
     */
    @PreAuthorize("@ss.hasPermi('payment:order:add')")
    @Log(title = "order", businessType = BusinessType.UPDATE)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @GetMapping("/cancelOrder")
    public AjaxResult cancelOrder(@RequestParam String id)
    {
        Map<String,String> map = new HashMap<>();
        map.put("id",id);
        map.put("before","1");
        map.put("status","4");
        map.put("operateName",SecurityUtils.getUsername());
        int i = userOrderService.updateUserOrderStatus(map);
        if(i == 1){
            //插入成功后，根据状态进行钱包操作，redis中去掉待审核消息内容，并发送消息给后台管理员。
            UserOrder userOrder = userOrderService.selectUserOrderById(map.get("id"));
            WalletVo salerVo = new WalletVo();
            salerVo.setRefId(userOrder.getId());
            salerVo.setRemark("取消代付");
            salerVo.setIp(IpUtils.getCurrentReqIp());
            salerVo.setAmount(userOrder.getTotalAmout().add(BigDecimal.ONE));
            salerVo.setTreasureTypeEnum(TreasureTypeEnum.WITHDRAW_MERCHANT_REFUND);
            salerVo.setUserName(userOrder.getUserName());
            salerVo.setUid(userOrder.getUserId());
            salerVo.setSysName(SecurityUtils.getUsername());
            salerVo.setMerId(userOrder.getUserId());
            iWalletService.updateWalletByUid(salerVo);
            return toAjax(i);
        }
        throw new ServiceException("订单状态更改失败!");
    }

    /**
     * 代付商户完成订单
     */
    @PreAuthorize("@ss.hasPermi('system:order:edit')")
    @Log(title = "order", businessType = BusinessType.UPDATE)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @GetMapping("/confirmOrder")
    public AjaxResult confirmOrder(@RequestParam String id)
    {
        Map<String,String> map = new HashMap<>();
        map.put("id",id);
        map.put("before","1");
        map.put("status","3");
        map.put("operateName",SecurityUtils.getUsername());
        int i = userOrderService.updateUserOrderStatus(map);
        if(i == 1){
            //插入成功后，根据状态进行钱包操作，redis中去掉待审核消息内容，并发送消息给后台管理员。
            UserOrder userOrder = userOrderService.selectUserOrderById(map.get("id"));
            String refId = userOrder.getId();
            Long userId = userOrder.getUserId();
            String userName = userOrder.getUserName();
            BigDecimal totalMoney = userOrder.getTotalAmout();
            WalletVo salerVo = new WalletVo();
            salerVo.setRefId(refId);
            salerVo.setRemark("代付成功");
            salerVo.setIp(IpUtils.getCurrentReqIp());
            salerVo.setAmount(totalMoney.add(BigDecimal.ONE));
            salerVo.setTreasureTypeEnum(TreasureTypeEnum.WITHDRAW_2);
            salerVo.setUserName(userName);
            salerVo.setUid(userId);
            salerVo.setSysName(SecurityUtils.getUsername());
            salerVo.setMerId(userId);
            iWalletService.updateWalletByUid(salerVo);
            Map<String, Object> backInfo = procedureFeeService.getWaterBackInfo(userId);
            if(backInfo != null){
                Long pid = Long.parseLong(backInfo.get("pid").toString());
                String pName = backInfo.get("pName").toString();
                BigDecimal rate = new BigDecimal(backInfo.get("rate").toString());
                WalletVo waterVo = new WalletVo();
                waterVo.setRefId(refId);
                waterVo.setRemark("代付订单返水");
                BigDecimal backMoney = totalMoney.multiply(rate).setScale(2, RoundingMode.DOWN);
                waterVo.setAmount(backMoney);
                waterVo.setIp(IpUtils.getCurrentReqIp());
                waterVo.setTreasureTypeEnum(TreasureTypeEnum.RETURN_WATER);
                waterVo.setUserName(pName);
                waterVo.setUid(pid);
                waterVo.setMerId(pid);
                iWalletService.updateWalletByUid(waterVo);
                DfReport report = new DfReport();
                report.setId(snowflakeIdUtils.nextId());
                report.setUid(userId);
                report.setName(userName);
                report.setRefId(refId);
                report.setRate(rate);
                try {
                    report.setPeriod(DateUtils.parseDate(refId.substring(5, 19),"yyyyMMddHHmmss"));
                } catch (ParseException e) {
                    logger.error("格式化报表时间出错",e);
                }
                report.setMoney(totalMoney);
                report.setMoneyBack(backMoney);
                dfReportService.insertDfReport(report);
            }
            return toAjax(i);
        }
        throw new ServiceException("订单状态更改失败!");
    }
//
//    /**
//     * 删除order
//     */
//    @PreAuthorize("@ss.hasPermi('system:order:remove')")
//    @Log(title = "order", businessType = BusinessType.DELETE)
//	@DeleteMapping("/{ids}")
//    public AjaxResult remove(@PathVariable String[] ids)
//    {
//        return toAjax(UserOrderService.deleteUserOrderByIds(ids));
//    }
}

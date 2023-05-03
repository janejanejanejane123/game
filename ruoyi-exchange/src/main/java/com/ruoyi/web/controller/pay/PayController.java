package com.ruoyi.web.controller.pay;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.PayResponEnums;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.pay.domain.AmountRecord;
import com.ruoyi.pay.domain.Merchant;
import com.ruoyi.pay.domain.WithdrawalRecord;
import com.ruoyi.pay.domain.vo.PayNotifyVO;
import com.ruoyi.pay.domain.vo.PayOrderVo;
import com.ruoyi.pay.domain.vo.PayResponVo;
import com.ruoyi.pay.service.*;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 钱包Controller
 *
 * @author ruoyi
 * @date 2022-03-08
 */
@Api("支付类接口")
@RestController
@RequestMapping("/api")
public class PayController {

    private Logger logger = LoggerFactory.getLogger(PayController.class);
    @Resource
    private IMerchantService iMerchantService;
    @Resource
    private IOrderService iOrderService;
    @Resource
    private ISysConfigService iSysConfigService;
    @Resource
    private ISysUserService iSysUserService;
    @Resource
    private IAmountRecordService iAmountRecordService;
    @Resource
    private IWithdrawalRecordService iWithdrawalRecordService;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private IOrderWithdrawalService iOrderWithdrawalService;
    @Resource
    private RedisCache redisCache;

    @ApiOperation("登录接口")
    @PostMapping("/login")
    public AjaxResult login(@RequestBody PayOrderVo orderVo, HttpServletRequest request) {
        checkAccount(orderVo);
        return iOrderService.loginandReg(orderVo, IpUtils.getIpAddr(request));
    }

    @ApiOperation("查询余额接口")
    @PostMapping("/queryBal")
    public AjaxResult queryBal(@RequestBody PayOrderVo orderVo, HttpServletRequest request) {
        logger.info("查询余额请求参数:" + JSONObject.toJSON(orderVo));
        checkAccount(orderVo);
        return iOrderService.queryBal(orderVo, IpUtils.getIpAddr(request));
    }

    private void checkAccount(@RequestBody PayOrderVo orderVo) {
        Merchant merchant = iMerchantService.selectMerchantByNo(orderVo.getMerNo());
        if (merchant == null) {
            throw new ServiceException("签名错误", PayResponEnums.PARAMS_ERR.getCode());
        }
        if (StringUtils.isBlank(orderVo.getAccount())) {
            throw new ServiceException("账号不能为空", PayResponEnums.PARAMS_ERR.getCode());
        }
    }

    @ApiOperation("请求上分接口")
    @PostMapping("/pay")
    public AjaxResult reqyRecharge(@RequestBody PayOrderVo orderVo, HttpServletRequest request) {
        logger.info("【充值请求接口参数】{}", JSONObject.toJSONString(orderVo));
        String api_pay_switch = iSysConfigService.selectConfigByKey("API_PAY_SWITCH");
        if (api_pay_switch != null && api_pay_switch.equals("1")) {
            validateEntity(orderVo);
            //请求充值
            AjaxResult ajaxResult = iOrderService.addPoints(orderVo, IpUtils.getIpAddr(request));
            return ajaxResult;
        } else {
            return AjaxResult.error(PayResponEnums.MAINTAINTAIN.getCode(), "接口维护中");
        }
    }

    /**
     * 一键充值
     *
     * @param orderVo
     * @return
     */
    @ApiOperation("一键上分接口")
    @PostMapping("/oneKeyPay")
    public AjaxResult oneKeyPay(@RequestBody PayOrderVo orderVo, HttpServletRequest request) {
        logger.info("【一键充值请求接口参数】{}", JSONObject.toJSONString(orderVo));
        String api_pay_switch = iSysConfigService.selectConfigByKey("API_PAY_SWITCH");
        if (api_pay_switch != null && api_pay_switch.equals("1")) {
            validateEntity(orderVo);
            //请求充值
            AjaxResult ajaxResult = iOrderService.onekeyUp(orderVo, IpUtils.getIpAddr(request));
            redisCache.setCacheObject("API_WITHDRAWALRECORD_NOTIFY:" + orderVo.getMerNo() + ":" + orderVo.getOrderid(), "1", 60, TimeUnit.SECONDS);
            return ajaxResult;
        } else {
            return AjaxResult.error(PayResponEnums.MAINTAINTAIN.getCode(), "接口维护中");
        }
    }

    private void validateEntity(@RequestBody PayOrderVo orderVo) {
        ValidateUtils.validateEntity(orderVo, PayResponEnums.PARAMS_ERR.getCode());
        checkAccount(orderVo);
    }


    @ApiOperation("会员查询充值订单")
    @GetMapping("/queryRecharge")
    public AjaxResult queryRecharge(String id) {
        logger.info("上分参数{}", id);
        //查询单号
        //返回支付信息
        AmountRecord amountRecord = iOrderService.queryOrder(id);
        Assert.isNull(amountRecord, "订单不存在或已失效");
        PayResponVo payPlatVO = new PayResponVo();
        BeanUtil.copyProperties(amountRecord, payPlatVO);
        amountRecord.setAmount(amountRecord.getAmount());
        amountRecord.setId(null);

        SysUser sysUser = iSysUserService.selectUserByUserName(amountRecord.getMerNo());
        payPlatVO.setNavurl(sysUser.getAvatar());
        payPlatVO.setNickName(sysUser.getNickName());
        Long time = (DateUtils.addMinute(payPlatVO.getCreateTime(), 15).getTime() - new Date().getTime()) / 1000;
        payPlatVO.setEffectiveTime(time);
        payPlatVO.setNotifyUrl(null);
        return AjaxResult.success("success").put("data", payPlatVO);
    }

    @ApiOperation("商户查询充值订单")
    @PostMapping("/getpay")
    public AjaxResult getpay(@RequestBody Map map) {
        logger.info("上分参数{}", map);
        Assert.isNull(map.get("orderid"), "订单号不能为空");
        Assert.isNull(map.get("merNo"), "商户号不能为空");
        Assert.isNull(map.get("sign"), "签名错误");
        String order = map.get("orderid").toString();
        String merNo = map.get("merNo").toString();
        String sign = map.get("sign").toString();

        Merchant merchant = iMerchantService.selectMerchantByNo(merNo);
        if (merchant == null) {
            return AjaxResult.error(PayResponEnums.MER_NULL.getCode(), PayResponEnums.MER_NULL.getMsg());
        }
        Map signMap = JSON.parseObject(JSON.toJSONString(map));
        signMap.remove("sign");
        String signData = MapSortUtil.getStringByMap(signMap);
        boolean b = RSAUtil.rsaVerify(merchant.getPubKey(), sign, signData);
        if (!b) {
            return AjaxResult.error(PayResponEnums.SIGN_ERR.getCode(), PayResponEnums.SIGN_ERR.getMsg());
        }

        AmountRecord queryRecord = new AmountRecord();
        queryRecord.setMerOrder(order);
        queryRecord.setMerNo(merNo);
        queryRecord.setMid(merchant.getUid());
        List<AmountRecord> amountRecordList = iAmountRecordService.selectAmountRecordList(queryRecord);
        Map responVo = new HashMap();
        if (amountRecordList.size() == 0) {
            responVo.put("status", -1);
            return AjaxResult.success("订单不存在").put("data", responVo);
        }
        AmountRecord amountRecord = amountRecordList.get(0);
        responVo.put("status", amountRecord.getStatus());
        responVo.put("amount", amountRecord.getAmount());
        responVo.put("note", amountRecord.getRemark());
        responVo.put("payTime", amountRecord.getPayTime() == null ? null : amountRecord.getPayTime().getTime());
        return AjaxResult.success("success").put("data", responVo);
    }

    @ApiOperation("付款")
    @PostMapping("/payment")
    public AjaxResult payment(@RequestBody Map params) {
        Assert.isBlank(params.get("payPassword") + "", "支付密码不能为空");
        String paymentKey = iOrderService.payment(params);
        redisCache.setCacheObject("API_WITHDRAWALRECORD_NOTIFY:" + paymentKey, "1", 60, TimeUnit.SECONDS);
        return AjaxResult.success();
    }


    @ApiOperation("下发接口")
    @PostMapping("/reqWd")
    public AjaxResult reqWithd(@RequestBody PayOrderVo params, HttpServletRequest request) throws Exception {
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        logger.info("下分参数{}，请求IP:{}", JSON.toJSONString(params), ip);
        String api_pay_switch = iSysConfigService.selectConfigByKey("API_WITHDR_SWICTH");
        if (api_pay_switch == null || !api_pay_switch.equals("1")) {
            return AjaxResult.error(PayResponEnums.MAINTAINTAIN.getCode(), "接口维护中");
        }
        ValidateUtils.validateEntity(params, PayResponEnums.PARAMS_ERR.getCode());
        iOrderWithdrawalService.subPoints(params, IpUtils.getIpAddr(request));
        logger.info("下分成功");
        Map responVo = new HashMap();
        responVo.put("status", 1);
        responVo.put("amount", params.getAmount());
        responVo.put("payTime", new Date().getTime());
        redisCache.setCacheObject("API_WITHDRAWALRECORD_NOTIFY:" + params.getMerNo() + ":" + params.getOrderid(), "1", 60, TimeUnit.SECONDS);
        return AjaxResult.success("success").put("data", responVo);
    }


    /**
     * 蚂蚁蛋跑分API接口
     */
    @PostMapping("/mayiPay")
    public AjaxResult mayiPay(@RequestBody PayOrderVo orderVo) {
        logger.info("【蚂蚁跑分充值请求接口参数】{}", JSONObject.toJSONString(orderVo));
        String api_pay_switch = iSysConfigService.selectConfigByKey("API_PAY_SWITCH");
        if (api_pay_switch != null && api_pay_switch.equals("1")) {
            ValidateUtils.validateEntity(orderVo, PayResponEnums.PARAMS_ERR.getCode());
            return iOrderService.grabPay(orderVo);
        } else {
            return AjaxResult.error(PayResponEnums.MAINTAINTAIN.getCode(), "接口维护中");
        }
    }


    @ApiOperation("API会员查询提现订单")
    @PostMapping("/queryWithdr")
    public AjaxResult queryWithdr(@RequestBody Map map) {
        logger.info("API查询提现订单参数{}", map);
        Assert.isNull(map.get("orderid"), "订单号不能为空");
        Assert.isNull(map.get("merNo"), "商户号不能为空");
        Assert.isNull(map.get("sign"), "签名错误");
        String order = map.get("orderid").toString();
        String merNo = map.get("merNo").toString();
        String sign = map.get("sign").toString();

        Merchant merchant = iMerchantService.selectMerchantByNo(merNo);
        if (merchant == null) {
            return AjaxResult.error(PayResponEnums.MER_NULL.getCode(), PayResponEnums.MER_NULL.getMsg());
        }
        Map signMap = JSON.parseObject(JSON.toJSONString(map));
        signMap.remove("sign");
        String signData = MapSortUtil.getStringByMap(signMap);
        boolean b = RSAUtil.rsaVerify(merchant.getPubKey(), sign, signData);
        if (!b) {
            return AjaxResult.error(PayResponEnums.SIGN_ERR.getCode(), PayResponEnums.SIGN_ERR.getMsg());
        }

        WithdrawalRecord queryRecord = new WithdrawalRecord();
        queryRecord.setOrderNo(order);
        queryRecord.setMerNo(merNo);
        List<WithdrawalRecord> amountRecordList = iWithdrawalRecordService.selectWithdrawalRecordList(queryRecord);
        Map responVo = new HashMap();
        if (amountRecordList.size() == 0) {
            responVo.put("status", -1);
            return AjaxResult.success("订单不存在").put("data", responVo);
        }
        WithdrawalRecord withdrawalRecord = amountRecordList.get(0);
        responVo.put("amount", withdrawalRecord.getAmount());
        responVo.put("payTime", withdrawalRecord.getCreateTime().getTime());
        responVo.put("status", withdrawalRecord.getStatus());
        return AjaxResult.success("success").put("data", responVo);
    }


    public static void main(String[] args) {
        String a = "4325rettferfgdgdgfdgfd";
        int i = a.hashCode();
        System.out.println(i);
    }
}

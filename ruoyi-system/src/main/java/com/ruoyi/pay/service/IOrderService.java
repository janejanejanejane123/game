package com.ruoyi.pay.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pay.domain.AmountRecord;
import com.ruoyi.pay.domain.vo.PayOrderVo;

import java.util.Map;

public interface IOrderService {
    /**登录*/
    AjaxResult loginandReg(PayOrderVo orderVo,String ip);
    /**查询余额***/
    AjaxResult queryBal(PayOrderVo orderVo,String ip);
    /**上分*/
    AjaxResult addPoints(PayOrderVo map,String ip);
    /**一键上分*/
    AjaxResult onekeyUp(PayOrderVo map,String ip);
    /*查询订单***/
    AmountRecord queryOrder(String orderNo);
    /**付款*/
    String payment(Map<String,Object> params);
   /* *//**下分* *//*
    void subPoints(PayOrderVo map,String ip) throws Exception;*/
    /**跑分*/
    AjaxResult grabPay(PayOrderVo orderVo);

}

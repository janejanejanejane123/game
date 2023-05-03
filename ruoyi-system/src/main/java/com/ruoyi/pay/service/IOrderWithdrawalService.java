package com.ruoyi.pay.service;

import com.ruoyi.pay.domain.vo.PayOrderVo;

public interface IOrderWithdrawalService {

    void subPoints(PayOrderVo orderVo, String ip);
}

package com.ruoyi.game.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.game.domain.request.BetRequest;
import com.ruoyi.game.domain.request.BetsTakeEffectRequest;
import com.ruoyi.game.domain.request.ChangeBetMoneyRequest;
import com.ruoyi.game.domain.request.LoginRequest;
import com.ruoyi.game.domain.response.BetResponse;
import com.ruoyi.game.domain.response.BetsTakeEffectResponse;
import com.ruoyi.game.domain.response.ChangeBetMoneyResponse;

import javax.servlet.http.HttpServletRequest;


/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
public interface IForeignSesrvice
{
    AjaxResult channelHandle(LoginRequest loginRequest, HttpServletRequest httpServletRequest) throws Exception;

    BetResponse bet(BetRequest betRequest);

    BetsTakeEffectResponse betsTakeEffect(BetsTakeEffectRequest betsTakeEffectRequest);

    ChangeBetMoneyResponse changeBetMoney(ChangeBetMoneyRequest changeBetMoneyRequest);
}

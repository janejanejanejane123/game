package com.ruoyi.game.service.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.game.domain.request.BetRequest;
import com.ruoyi.game.domain.request.BetsTakeEffectRequest;
import com.ruoyi.game.domain.request.ChangeBetMoneyRequest;
import com.ruoyi.game.domain.request.LoginRequest;
import com.ruoyi.game.domain.response.BetResponse;
import com.ruoyi.game.domain.response.BetsTakeEffectResponse;
import com.ruoyi.game.domain.response.ChangeBetMoneyResponse;
import com.ruoyi.game.service.IAccountService;
import com.ruoyi.game.service.IForeignSesrvice;
import com.ruoyi.game.service.IGameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2023-04-28
 */
@Service("foreignSesrvice")
public class ForeignServiceImpl implements IForeignSesrvice {

    @Autowired
    private IGameUserService iGameUserService;

    @Autowired
    private IAccountService iAccountService;

    @Override
    public AjaxResult channelHandle(LoginRequest loginRequest, HttpServletRequest httpServletRequest) throws Exception {
        return null;
    }


    /**
     * 测试客户端发送消息，测试是否联通
     *
     * @param message
     */

    @Override
    public BetResponse bet(BetRequest betRequest) {
        BetResponse betResponse = new BetResponse();
        {
            int score = 0;
            Random random = new Random();
            int a = random.nextInt(10);
            int b = random.nextInt(10);
            int c = random.nextInt(10);
            if (a == 0 && b == 0 && c == 0) {
                score = 800;
            }
            if (a == 4 && b == 4 && c == 4) {
                score = 400;
            }
            if (a == 6 && b == 6 && c == 6) {
                score = 250;
            }
            if (a == 8 && b == 8 && c == 8) {
                score = 100;
            }
            if (a == 2 && b == 2 && c == 2) {
                score = 100;
            }
            if ((a == 2 && b == 2) || (c == 2 && b == 2) || (c == 2 && a == 2)) {
                score = 30;
            }
            if (a == 2 || b == 2 || c == 2) {
                score = 15;
            }
            //Encrypt.AESDecrypt("1","1",true);
        }
        return betResponse;
    }


    @Override
    public BetsTakeEffectResponse betsTakeEffect(BetsTakeEffectRequest betsTakeEffectRequest) {
        BetsTakeEffectResponse betsTakeEffectResponse = new BetsTakeEffectResponse();
        {
            int score = 0;
            Random random = new Random();
            int a = random.nextInt(10);
            int b = random.nextInt(10);
            int c = random.nextInt(10);
            if (a == 0 && b == 0 && c == 0) {
                score = 800;
            }
            if (a == 4 && b == 4 && c == 4) {
                score = 400;
            }
            if (a == 6 && b == 6 && c == 6) {
                score = 250;
            }
            if (a == 8 && b == 8 && c == 8) {
                score = 100;
            }
            if (a == 2 && b == 2 && c == 2) {
                score = 100;
            }
            if ((a == 2 && b == 2) || (c == 2 && b == 2) || (c == 2 && a == 2)) {
                score = 30;
            }
            if (a == 2 || b == 2 || c == 2) {
                score = 15;
            }
            //Encrypt.AESDecrypt("1","1",true);
        }
        return betsTakeEffectResponse;
    }

    @Override
    public ChangeBetMoneyResponse changeBetMoney(ChangeBetMoneyRequest changeBetMoneyRequest) {
        return null;
    }

}

package com.ruoyi.chat.controller.thirdParty;


import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.Encrypt;
import com.ruoyi.game.domain.request.GetTokenRequest;
import com.ruoyi.game.domain.request.LoginRequest;
import com.ruoyi.game.service.IForeignSesrvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * 游戏配置
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/foreign")
public class ForeignController extends BaseController {

    @Autowired
    private IForeignSesrvice iForeignSesrvice;

    @GetMapping("/getToken")
    public AjaxResult getToken(@RequestBody GetTokenRequest getTokenRequest) throws Exception {
        String token = Encrypt.AESEncrypt(getTokenRequest.getApiKey(), getTokenRequest.getUserId());
        return AjaxResult.success(token);
    }


    @Anonymous
    @GetMapping("/channelHandle")
    public AjaxResult channelHandle(@RequestBody LoginRequest loginRequest, HttpServletRequest httpServletRequest) throws Exception {
        return iForeignSesrvice.channelHandle(loginRequest, httpServletRequest);
    }

    public static void main(String[] args) {

    }
}
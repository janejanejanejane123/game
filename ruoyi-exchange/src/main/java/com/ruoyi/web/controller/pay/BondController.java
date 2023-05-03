package com.ruoyi.web.controller.pay;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.TreasureTypeEnum;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.uuid.Seq;
import com.ruoyi.pay.domain.vo.WalletVo;
import com.ruoyi.pay.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
public class BondController extends BaseController {

    @Autowired
    private IWalletService iWalletService;

    @PostMapping("/bond")
    public AjaxResult bond(HttpServletRequest request){

        WalletVo walletVo = new WalletVo();
        walletVo.setRefId(Seq.generatorOrderNo("BZ"));
        walletVo.setRemark("保证金");
        walletVo.setAmount(new BigDecimal("1"));
        walletVo.setIp(IpUtils.getIpAddr(request));
        walletVo.setTreasureTypeEnum(TreasureTypeEnum.DEPOSIT);
        walletVo.setUserName(getUsername());
        walletVo.setUid(getUserId());
        walletVo.setFee(BigDecimal.ZERO);
        iWalletService.updateWalletByUid(walletVo);
        return AjaxResult.success();
    }
}

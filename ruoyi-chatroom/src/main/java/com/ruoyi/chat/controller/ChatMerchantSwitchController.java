package com.ruoyi.chat.controller;

import com.ruoyi.chatroom.domain.ChatMerchantSwitch;
import com.ruoyi.chatroom.service.IChatMerchantSwitchService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 商户开关Controller
 * 
 * @author ruoyi
 * @date 2022-12-18
 */
@RestController
@RequestMapping("/merchant/switch")
public class ChatMerchantSwitchController extends BaseController
{
    @Autowired
    private IChatMerchantSwitchService chatMerchantSwitchService;

    /**
     * 开关
     */
    @GetMapping("/isChatMerchantSwitch")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult isChatMerchantSwitch() {
        boolean result = chatMerchantSwitchService.isChatMerchantSwitch(getLoginUser().getMerchantId());
        return AjaxResult.success(result);
    }

}

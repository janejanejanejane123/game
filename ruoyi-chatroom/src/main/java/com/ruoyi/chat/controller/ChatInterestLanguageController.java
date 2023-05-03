package com.ruoyi.chat.controller;

import com.ruoyi.chatroom.domain.ChatInterestLanguage;
import com.ruoyi.chatroom.service.IChatInterestLanguageService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 趣语管理Controller
 * 
 * @author nn
 * @date 2022-08-01
 */
@RestController
@RequestMapping("/language")
public class ChatInterestLanguageController extends BaseController
{
    @Autowired
    private IChatInterestLanguageService chatInterestLanguageService;

    /**
     * 查询趣语列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.playerOnly()")
    public TableDataInfo list(ChatInterestLanguage chatInterestLanguage)
    {
        startPage();
        List<ChatInterestLanguage> list = chatInterestLanguageService.selectChatInterestLanguageList(chatInterestLanguage);
        return getDataTable(list);
    }

}

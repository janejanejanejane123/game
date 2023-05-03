package com.ruoyi.chat.controller;

import com.ruoyi.chatroom.domain.ChatEmoji;
import com.ruoyi.chatroom.service.IChatEmojiService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 表情包Controller
 * 
 * @author nn
 * @date 2022-08-01
 */
@RestController
@RequestMapping("/emoji")
public class ChatEmojiController extends BaseController
{
    @Autowired
    private IChatEmojiService chatEmojiService;

    /**
     * 查询表情包列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.playerOnly()")
    public TableDataInfo list(ChatEmoji chatEmoji)
    {
        startPage();
        List<ChatEmoji> list = chatEmojiService.selectChatEmojiList(chatEmoji);
        return getDataTable(list);
    }

}

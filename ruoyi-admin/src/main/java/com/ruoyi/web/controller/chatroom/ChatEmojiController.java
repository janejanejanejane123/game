package com.ruoyi.web.controller.chatroom;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.chatroom.domain.ChatEmoji;
import com.ruoyi.chatroom.service.IChatEmojiService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 表情包Controller
 * 
 * @author nn
 * @date 2022-07-13
 */
@RestController
@RequestMapping("/chatroom/emoji")
public class ChatEmojiController extends BaseController
{
    @Autowired
    private IChatEmojiService chatEmojiService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询表情包列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:emoji:list')")
    @GetMapping("/list")
    public TableDataInfo list(ChatEmoji chatEmoji)
    {
        startPage();
        List<ChatEmoji> list = chatEmojiService.selectChatEmojiList(chatEmoji);
        return getDataTable(list);
    }

    /**
     * 导出表情包列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:emoji:export')")
    @Log(title = "表情包", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ChatEmoji chatEmoji)
    {
        List<ChatEmoji> list = chatEmojiService.selectChatEmojiList(chatEmoji);
        ExcelUtil<ChatEmoji> util = new ExcelUtil<ChatEmoji>(ChatEmoji.class);
        util.exportExcel(response, list, "表情包数据");
    }

    /**
     * 获取表情包详细信息
     */
    @PreAuthorize("@ss.hasPermi('chatroom:emoji:query')")
    @GetMapping(value = "/{emojiId}")
    public AjaxResult getInfo(@PathVariable("emojiId") Long emojiId)
    {
        return AjaxResult.success(chatEmojiService.selectChatEmojiByEmojiId(emojiId));
    }

    /**
     * 新增表情包
     */
    @PreAuthorize("@ss.hasPermi('chatroom:emoji:add')")
    @Log(title = "表情包", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ChatEmoji chatEmoji)
    {
        chatEmoji.setEmojiId(snowflakeIdUtils.nextId());
        chatEmoji.setUploadId(getUserId());
        chatEmoji.setUploadName(getUsername());
        // 上传的ip地址.
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        chatEmoji.setUploadIp(ip);
        chatEmoji.setStatus("0");
        chatEmoji.setUploadTime(new Date());
        return toAjax(chatEmojiService.insertChatEmoji(chatEmoji));
    }

    /**
     * 修改表情包
     */
    @PreAuthorize("@ss.hasPermi('chatroom:emoji:edit')")
    @Log(title = "表情包", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ChatEmoji chatEmoji)
    {
        return toAjax(chatEmojiService.updateChatEmoji(chatEmoji));
    }

    /**
     * 删除表情包
     */
    @PreAuthorize("@ss.hasPermi('chatroom:emoji:remove')")
    @Log(title = "表情包", businessType = BusinessType.DELETE)
	@DeleteMapping("/{emojiIds}")
    public AjaxResult remove(@PathVariable Long[] emojiIds)
    {
        return toAjax(chatEmojiService.deleteChatEmojiByEmojiIds(emojiIds));
    }

    /**
     * 上传表情包
     * @param file
     * @return
     */
    @PostMapping("/uploadEmoji")
    @PreAuthorize("@ss.hasPermi('chatroom:emoji:add')")
    public AjaxResult uploadEmoji(@RequestParam("file") MultipartFile file)
    {
        String path = chatEmojiService.uploadEmoji(file);
        return AjaxResult.success().put("filePath",path);
    }
}

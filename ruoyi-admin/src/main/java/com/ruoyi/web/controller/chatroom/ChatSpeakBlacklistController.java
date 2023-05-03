package com.ruoyi.web.controller.chatroom;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import com.ruoyi.common.constant.UserConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.chatroom.domain.ChatSpeakBlacklist;
import com.ruoyi.chatroom.service.IChatSpeakBlacklistService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 聊天室发言白名单Controller
 * 
 * @author nn
 * @date 2022-08-28
 */
@RestController
@RequestMapping("/chatroom/speakBlacklist")
public class ChatSpeakBlacklistController extends BaseController
{
    @Autowired
    private IChatSpeakBlacklistService chatSpeakBlacklistService;

    /**
     * 查询聊天室发言白名单列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:speakBlacklist:list')")
    @GetMapping("/list")
    public TableDataInfo list(ChatSpeakBlacklist chatSpeakBlacklist)
    {
        startPage();
        List<ChatSpeakBlacklist> list = chatSpeakBlacklistService.selectChatSpeakBlacklistList(chatSpeakBlacklist);
        return getDataTable(list);
    }

    /**
     * 导出聊天室发言白名单列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:speakBlacklist:export')")
    @Log(title = "聊天室发言白名单-导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ChatSpeakBlacklist chatSpeakBlacklist)
    {
        List<ChatSpeakBlacklist> list = chatSpeakBlacklistService.selectChatSpeakBlacklistList(chatSpeakBlacklist);
        ExcelUtil<ChatSpeakBlacklist> util = new ExcelUtil<ChatSpeakBlacklist>(ChatSpeakBlacklist.class);
        util.exportExcel(response, list, "聊天室发言白名单数据");
    }

    /**
     * 获取聊天室发言白名单详细信息
     */
    @PreAuthorize("@ss.hasPermi('chatroom:speakBlacklist:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(chatSpeakBlacklistService.selectChatSpeakBlacklistById(id));
    }

    /**
     * 新增聊天室发言白名单
     */
    @PreAuthorize("@ss.hasPermi('chatroom:speakBlacklist:add')")
    @Log(title = "聊天室发言白名单-新增", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ChatSpeakBlacklist chatSpeakBlacklist)
    {
        if (UserConstants.NOT_UNIQUE.equals(chatSpeakBlacklistService.checkSpeakBlacklistUnique(chatSpeakBlacklist)))
        {
            return AjaxResult.error("新增失败，该会员已存在");
        }
        return toAjax(chatSpeakBlacklistService.insertChatSpeakBlacklist(chatSpeakBlacklist));
    }

    /**
     * 修改聊天室发言白名单
     */
    @PreAuthorize("@ss.hasPermi('chatroom:speakBlacklist:edit')")
    @Log(title = "聊天室发言白名单-修改", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ChatSpeakBlacklist chatSpeakBlacklist)
    {
        if (UserConstants.NOT_UNIQUE.equals(chatSpeakBlacklistService.checkSpeakBlacklistUnique(chatSpeakBlacklist)))
        {
            return AjaxResult.error("修改失败，该会员已存在");
        }
        return toAjax(chatSpeakBlacklistService.updateChatSpeakBlacklist(chatSpeakBlacklist));
    }

    /**
     * 删除聊天室发言白名单
     */
    @PreAuthorize("@ss.hasPermi('chatroom:speakBlacklist:remove')")
    @Log(title = "聊天室发言白名单-删除", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(chatSpeakBlacklistService.deleteChatSpeakBlacklistByIds(ids));
    }

    /**
     * 状态修改.
     */
    @PreAuthorize("@ss.hasPermi('chatroom:speakBlacklist:edit')")
    @Log(title = "聊天室发言白名单-修改状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ChatSpeakBlacklist chatSpeakBlacklist)
    {
        chatSpeakBlacklist.setUpdateBy(getUsername());
        chatSpeakBlacklist.setUpdateTime(new Date());
        return toAjax(chatSpeakBlacklistService.changeStatus(chatSpeakBlacklist));
    }
}

package com.ruoyi.web.controller.chatroom;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
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
import com.ruoyi.chatroom.domain.ChatBanned;
import com.ruoyi.chatroom.service.IChatBannedService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 禁言管理Controller
 * 
 * @author nn
 * @date 2022-07-10
 */
@RestController
@RequestMapping("/chatroom/banned")
public class ChatBannedController extends BaseController
{
    @Autowired
    private IChatBannedService chatBannedService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询禁言管理列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:banned:list')")
    @GetMapping("/list")
    public TableDataInfo list(ChatBanned chatBanned)
    {
        startPage();
        List<ChatBanned> list = chatBannedService.selectChatBannedList(chatBanned);
        return getDataTable(list);
    }

    /**
     * 导出禁言管理列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:banned:export')")
    @Log(title = "禁言管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ChatBanned chatBanned)
    {
        List<ChatBanned> list = chatBannedService.selectChatBannedList(chatBanned);
        ExcelUtil<ChatBanned> util = new ExcelUtil<ChatBanned>(ChatBanned.class);
        util.exportExcel(response, list, "禁言管理数据");
    }

    /**
     * 获取禁言管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('chatroom:banned:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(chatBannedService.selectChatBannedById(id));
    }

    /**
     * 新增禁言管理
     */
    @PreAuthorize("@ss.hasPermi('chatroom:banned:add')")
    @Log(title = "禁言管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ChatBanned chatBanned)
    {
        if (UserConstants.NOT_UNIQUE.equals(chatBannedService.checkChatBannedUnique(chatBanned)))
        {
            return AjaxResult.error("新增失败，该会员已禁言");
        }
        chatBanned.setId(snowflakeIdUtils.nextId());
        chatBanned.setCreateBy(getUsername());
        chatBanned.setCreateTime(new Date());
        return toAjax(chatBannedService.insertChatBanned(chatBanned));
    }

    /**
     * 修改禁言管理
     */
    @PreAuthorize("@ss.hasPermi('chatroom:banned:edit')")
    @Log(title = "禁言管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ChatBanned chatBanned)
    {
        if (UserConstants.NOT_UNIQUE.equals(chatBannedService.checkChatBannedUnique(chatBanned)))
        {
            return AjaxResult.error("修改失败，该会员已禁言");
        }
        return toAjax(chatBannedService.updateChatBanned(chatBanned));
    }

    /**
     * 删除禁言管理
     */
    @PreAuthorize("@ss.hasPermi('chatroom:banned:remove')")
    @Log(title = "禁言管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(chatBannedService.deleteChatBannedByIds(ids));
    }
}

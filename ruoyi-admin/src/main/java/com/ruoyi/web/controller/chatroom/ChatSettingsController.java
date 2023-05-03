package com.ruoyi.web.controller.chatroom;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.chatroom.domain.ChatSettings;
import com.ruoyi.chatroom.service.IChatSettingsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 聊天室配置Controller
 * 
 * @author nn
 * @date 2022-07-16
 */
@RestController
@RequestMapping("/chatroom/settings")
public class ChatSettingsController extends BaseController
{
    @Autowired
    private IChatSettingsService chatSettingsService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询聊天室配置列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:settings:list')")
    @GetMapping("/list1")
    public TableDataInfo list1(ChatSettings chatSettings)
    {
        startPage();
        List<ChatSettings> list = chatSettingsService.selectChatSettingsList(chatSettings);
        return getDataTable(list);
    }

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('chatroom:settings:list')")
    public AjaxResult list(@RequestParam Map<String,Object> params){
        return chatSettingsService.query(params);
    }

    /**
     * 导出聊天室配置列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:settings:export')")
    @Log(title = "聊天室配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ChatSettings chatSettings)
    {
        List<ChatSettings> list = chatSettingsService.selectChatSettingsList(chatSettings);
        ExcelUtil<ChatSettings> util = new ExcelUtil<ChatSettings>(ChatSettings.class);
        util.exportExcel(response, list, "聊天室配置数据");
    }

    /**
     * 获取聊天室配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('chatroom:settings:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(chatSettingsService.selectChatSettingsById(id));
    }

    /**
     * 新增聊天室配置
     */
    @PreAuthorize("@ss.hasPermi('chatroom:settings:add')")
    @Log(title = "聊天室配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ChatSettings chatSettings)
    {
        chatSettings.setId(snowflakeIdUtils.nextId());
        chatSettings.setCreateBy(getUsername());
        chatSettings.setCreateTime(new Date());
        return toAjax(chatSettingsService.insertChatSettings(chatSettings));
    }

    /**
     * 修改聊天室配置
     */
    @PreAuthorize("@ss.hasPermi('chatroom:settings:edit')")
    @Log(title = "聊天室配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ChatSettings chatSettings)
    {
        return toAjax(chatSettingsService.updateChatSettings(chatSettings));
    }

    /**
     * 删除聊天室配置
     */
    @PreAuthorize("@ss.hasPermi('chatroom:settings:remove')")
    @Log(title = "聊天室配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(chatSettingsService.deleteChatSettingsByIds(ids));
    }

    /**
     * 保存聊天室配置
     */
    @PreAuthorize("@ss.hasPermi('chatroom:settings:add')")
    @Log(title = "保存聊天室配置", businessType = BusinessType.INSERT)
    @PostMapping(value = "/saveSettings")
    public AjaxResult saveSettings(@RequestBody ChatSettings chatSettings)
    {
        return chatSettingsService.saveSettings(chatSettings);
    }
}

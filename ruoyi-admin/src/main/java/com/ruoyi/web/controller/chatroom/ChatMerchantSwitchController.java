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
import com.ruoyi.chatroom.domain.ChatMerchantSwitch;
import com.ruoyi.chatroom.service.IChatMerchantSwitchService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 商户开关Controller
 * 
 * @author ruoyi
 * @date 2022-12-18
 */
@RestController
@RequestMapping("/chatroom/switch")
public class ChatMerchantSwitchController extends BaseController
{
    @Autowired
    private IChatMerchantSwitchService chatMerchantSwitchService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询商户开关列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:switch:list')")
    @GetMapping("/list")
    public TableDataInfo list(ChatMerchantSwitch chatMerchantSwitch)
    {
        startPage();
        List<ChatMerchantSwitch> list = chatMerchantSwitchService.selectChatMerchantSwitchList(chatMerchantSwitch);
        return getDataTable(list);
    }

    /**
     * 导出商户开关列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:switch:export')")
    @Log(title = "商户开关", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ChatMerchantSwitch chatMerchantSwitch)
    {
        List<ChatMerchantSwitch> list = chatMerchantSwitchService.selectChatMerchantSwitchList(chatMerchantSwitch);
        ExcelUtil<ChatMerchantSwitch> util = new ExcelUtil<ChatMerchantSwitch>(ChatMerchantSwitch.class);
        util.exportExcel(response, list, "商户开关数据");
    }

    /**
     * 获取商户开关详细信息
     */
    @PreAuthorize("@ss.hasPermi('chatroom:switch:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(chatMerchantSwitchService.selectChatMerchantSwitchById(id));
    }

    /**
     * 新增商户开关
     */
    @PreAuthorize("@ss.hasPermi('chatroom:switch:add')")
    @Log(title = "商户开关", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ChatMerchantSwitch chatMerchantSwitch)
    {
        if (UserConstants.NOT_UNIQUE.equals(chatMerchantSwitchService.checkChatMerchantSwitchUnique(chatMerchantSwitch)))
        {
            return AjaxResult.error("新增失败，该商户已存在");
        }
        chatMerchantSwitch.setId(snowflakeIdUtils.nextId());
        chatMerchantSwitch.setCreateBy(getUsername());
        chatMerchantSwitch.setCreateTime(new Date());
        return toAjax(chatMerchantSwitchService.insertChatMerchantSwitch(chatMerchantSwitch));
    }

    /**
     * 修改商户开关
     */
    @PreAuthorize("@ss.hasPermi('chatroom:switch:edit')")
    @Log(title = "商户开关", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ChatMerchantSwitch chatMerchantSwitch)
    {
        if (UserConstants.NOT_UNIQUE.equals(chatMerchantSwitchService.checkChatMerchantSwitchUnique(chatMerchantSwitch)))
        {
            return AjaxResult.error("修改失败，该商户已存在");
        }
        chatMerchantSwitch.setUpdateBy(getUsername());
        chatMerchantSwitch.setUpdateTime(new Date());
        return toAjax(chatMerchantSwitchService.updateChatMerchantSwitch(chatMerchantSwitch));
    }

    /**
     * 删除商户开关
     */
    @PreAuthorize("@ss.hasPermi('chatroom:switch:remove')")
    @Log(title = "商户开关", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(chatMerchantSwitchService.deleteChatMerchantSwitchByIds(ids));
    }

    /**
     * 状态修改.
     */
    @PreAuthorize("@ss.hasPermi('chatroom:switch:edit')")
    @Log(title = "聊天室发言白名单-修改状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ChatMerchantSwitch chatMerchantSwitch)
    {
        chatMerchantSwitch.setUpdateBy(getUsername());
        chatMerchantSwitch.setUpdateTime(new Date());
        return toAjax(chatMerchantSwitchService.changeStatus(chatMerchantSwitch));
    }

}

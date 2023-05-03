package com.ruoyi.web.controller.chatroom;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.chatroom.domain.ChatInterestLanguage;
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
import com.ruoyi.chatroom.service.IChatInterestLanguageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 趣语管理Controller
 * 
 * @author nn
 * @date 2022-07-11
 */
@RestController
@RequestMapping("/chatroom/language")
public class ChatInterestLanguageController extends BaseController
{
    @Autowired
    private IChatInterestLanguageService chatInterestLanguageService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询趣语列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:language:list')")
    @GetMapping("/list")
    public TableDataInfo list(ChatInterestLanguage chatInterestLanguage)
    {
        startPage();
        List<ChatInterestLanguage> list = chatInterestLanguageService.selectChatInterestLanguageList(chatInterestLanguage);
        return getDataTable(list);
    }

    /**
     * 导出趣语列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:language:export')")
    @Log(title = "趣语", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ChatInterestLanguage chatInterestLanguage)
    {
        List<ChatInterestLanguage> list = chatInterestLanguageService.selectChatInterestLanguageList(chatInterestLanguage);
        ExcelUtil<ChatInterestLanguage> util = new ExcelUtil<ChatInterestLanguage>(ChatInterestLanguage.class);
        util.exportExcel(response, list, "趣语数据");
    }

    /**
     * 获取趣语详细信息
     */
    @PreAuthorize("@ss.hasPermi('chatroom:language:query')")
    @GetMapping(value = "/{interestId}")
    public AjaxResult getInfo(@PathVariable("interestId") Long interestId)
    {
        return AjaxResult.success(chatInterestLanguageService.selectChatInterestLanguageByInterestId(interestId));
    }

    /**
     * 新增趣语
     */
    @PreAuthorize("@ss.hasPermi('chatroom:language:add')")
    @Log(title = "趣语", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ChatInterestLanguage chatInterestLanguage)
    {
        if (UserConstants.NOT_UNIQUE.equals(chatInterestLanguageService.checkInterestLanguageUnique(chatInterestLanguage)))
        {
            return AjaxResult.error("新增失败，该趣语已存在");
        }
        chatInterestLanguage.setInterestId(snowflakeIdUtils.nextId());
        chatInterestLanguage.setCreateBy(getUsername());
        chatInterestLanguage.setCreateTime(new Date());
        return toAjax(chatInterestLanguageService.insertChatInterestLanguage(chatInterestLanguage));
    }

    /**
     * 修改趣语
     */
    @PreAuthorize("@ss.hasPermi('chatroom:language:edit')")
    @Log(title = "趣语", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ChatInterestLanguage chatInterestLanguage)
    {
        if (UserConstants.NOT_UNIQUE.equals(chatInterestLanguageService.checkInterestLanguageUnique(chatInterestLanguage)))
        {
            return AjaxResult.error("修改失败，该趣语已存在");
        }
        return toAjax(chatInterestLanguageService.updateChatInterestLanguage(chatInterestLanguage));
    }

    /**
     * 删除趣语
     */
    @PreAuthorize("@ss.hasPermi('chatroom:language:remove')")
    @Log(title = "趣语", businessType = BusinessType.DELETE)
	@DeleteMapping("/{interestIds}")
    public AjaxResult remove(@PathVariable Long[] interestIds)
    {
        return toAjax(chatInterestLanguageService.deleteChatInterestLanguageByInterestIds(interestIds));
    }
}

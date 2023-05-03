package com.ruoyi.web.controller.chatroom;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.constant.SensitiveWordConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.ruoyi.chatroom.domain.ChatSensitiveWord;
import com.ruoyi.chatroom.service.IChatSensitiveWordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 敏感词库Controller
 * 
 * @author nn
 * @date 2022-07-11
 */
@RestController
@RequestMapping("/chatroom/sensitive")
public class ChatSensitiveWordController extends BaseController
{
    @Autowired
    private IChatSensitiveWordService chatSensitiveWordService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询敏感词库列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:sensitive:list')")
    @GetMapping("/list")
    public TableDataInfo list(ChatSensitiveWord chatSensitiveWord)
    {
        startPage();
        List<ChatSensitiveWord> list = chatSensitiveWordService.selectChatSensitiveWordList(chatSensitiveWord);
        return getDataTable(list);
    }

    /**
     * 导出敏感词库列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:sensitive:export')")
    @Log(title = "敏感词库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ChatSensitiveWord chatSensitiveWord)
    {
        List<ChatSensitiveWord> list = chatSensitiveWordService.selectChatSensitiveWordList(chatSensitiveWord);
        ExcelUtil<ChatSensitiveWord> util = new ExcelUtil<ChatSensitiveWord>(ChatSensitiveWord.class);
        util.exportExcel(response, list, "敏感词库数据");
    }

    /**
     * 获取敏感词库详细信息
     */
    @PreAuthorize("@ss.hasPermi('chatroom:sensitive:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(chatSensitiveWordService.selectChatSensitiveWordById(id));
    }

    /**
     * 新增敏感词库
     */
    @PreAuthorize("@ss.hasPermi('chatroom:sensitive:add')")
    @Log(title = "敏感词库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ChatSensitiveWord chatSensitiveWord)
    {
        if (UserConstants.NOT_UNIQUE.equals(chatSensitiveWordService.checkSensitiveWordUnique(chatSensitiveWord)))
        {
            return AjaxResult.error("新增失败，该敏感词已存在");
        }
        if(SensitiveWordConstants.REPLACE_SENSITIVE.equals(chatSensitiveWord.getFilterType())){
            if(StringUtils.isBlank(chatSensitiveWord.getReplaceStr())){
                return AjaxResult.error("新增失败，替换字符不能为空");
            }
            if(chatSensitiveWord.getSensitiveWord().equals(chatSensitiveWord.getReplaceStr())){
                return AjaxResult.error("新增失败，敏感词和替换词不能相同");
            }
        }else {
            chatSensitiveWord.setReplaceStr("");
        }
        chatSensitiveWord.setId(snowflakeIdUtils.nextId());
        chatSensitiveWord.setCreateBy(getUsername());
        chatSensitiveWord.setCreateTime(new Date());
        return toAjax(chatSensitiveWordService.insertChatSensitiveWord(chatSensitiveWord));
    }

    /**
     * 修改敏感词库
     */
    @PreAuthorize("@ss.hasPermi('chatroom:sensitive:edit')")
    @Log(title = "敏感词库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ChatSensitiveWord chatSensitiveWord)
    {
        if (UserConstants.NOT_UNIQUE.equals(chatSensitiveWordService.checkSensitiveWordUnique(chatSensitiveWord)))
        {
            return AjaxResult.error("修改失败，该敏感词以存在");
        }
        if(SensitiveWordConstants.REPLACE_SENSITIVE.equals(chatSensitiveWord.getFilterType())){
            if(StringUtils.isBlank(chatSensitiveWord.getReplaceStr())){
                return AjaxResult.error("新增失败，替换字符不能为空");
            }
            if(chatSensitiveWord.getSensitiveWord().equals(chatSensitiveWord.getReplaceStr())){
                return AjaxResult.error("新增失败，敏感词和替换词不能相同");
            }
        }else {
            chatSensitiveWord.setReplaceStr("");
        }
        return toAjax(chatSensitiveWordService.updateChatSensitiveWord(chatSensitiveWord));
    }

    /**
     * 删除敏感词库
     */
    @PreAuthorize("@ss.hasPermi('chatroom:sensitive:remove')")
    @Log(title = "敏感词库", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(chatSensitiveWordService.deleteChatSensitiveWordByIds(ids));
    }
}

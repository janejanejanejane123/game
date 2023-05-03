package com.ruoyi.web.controller.chatroom;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.chatroom.domain.ChatServeCustomer;
import com.ruoyi.chatroom.service.IChatServeCustomerService;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 客服列表Controller
 * 
 * @author nn
 * @date 2022-07-09
 */
@RestController
@RequestMapping("/chatroom/customer")
public class ChatServeCustomerController extends BaseController
{
    @Autowired
    private IChatServeCustomerService chatServeCustomerService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询客服列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:customer:list')")
    @GetMapping("/list")
    public TableDataInfo list(ChatServeCustomer chatServeCustomer)
    {
        startPage();
        List<ChatServeCustomer> list = chatServeCustomerService.selectChatServeCustomerList(chatServeCustomer);
        return getDataTable(list);
    }

    /**
     * 导出客服列表
     */
    @PreAuthorize("@ss.hasPermi('chatroom:customer:export')")
    @Log(title = "客服列表-导出客服列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ChatServeCustomer chatServeCustomer)
    {
        List<ChatServeCustomer> list = chatServeCustomerService.selectChatServeCustomerList(chatServeCustomer);
        ExcelUtil<ChatServeCustomer> util = new ExcelUtil<ChatServeCustomer>(ChatServeCustomer.class);
        util.exportExcel(response, list, "客服列表数据");
    }

    /**
     * 获取客服详细信息
     */
    @PreAuthorize("@ss.hasPermi('chatroom:customer:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(chatServeCustomerService.selectChatServeCustomerById(id));
    }

    /**
     * 新增客服
     */
    @PreAuthorize("@ss.hasPermi('chatroom:customer:add')")
    @Log(title = "客服列表-新增客服", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ChatServeCustomer chatServeCustomer)
    {
        if (UserConstants.NOT_UNIQUE.equals(chatServeCustomerService.checkServeCustomerUnique(chatServeCustomer)))
        {
            return AjaxResult.error("新增失败，该客服已存在");
        }
        chatServeCustomer.setId(snowflakeIdUtils.nextId());
        chatServeCustomer.setCreateBy(getUsername());
        chatServeCustomer.setCreateTime(new Date());
        return toAjax(chatServeCustomerService.insertChatServeCustomer(chatServeCustomer));
    }

    /**
     * 修改客服
     */
    @PreAuthorize("@ss.hasPermi('chatroom:customer:edit')")
    @Log(title = "客服列表-修改客服", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ChatServeCustomer chatServeCustomer)
    {
        if (UserConstants.NOT_UNIQUE.equals(chatServeCustomerService.checkServeCustomerUnique(chatServeCustomer)))
        {
            return AjaxResult.error("修改失败，该客服已存在");
        }
        chatServeCustomer.setUpdateBy(getUsername());
        chatServeCustomer.setUpdateTime(new Date());
        return toAjax(chatServeCustomerService.updateChatServeCustomer(chatServeCustomer));
    }

    /**
     * 删除客服
     */
    @PreAuthorize("@ss.hasPermi('chatroom:customer:remove')")
    @Log(title = "客服列表-删除客服", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(chatServeCustomerService.deleteChatServeCustomerByIds(ids));
    }

    /**
     * 状态修改.
     */
    @PreAuthorize("@ss.hasPermi('chatroom:customer:edit')")
    @Log(title = "客服列表-修改状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ChatServeCustomer chatServeCustomer)
    {
        chatServeCustomer.setUpdateBy(getUsername());
        chatServeCustomer.setUpdateTime(new Date());
        return toAjax(chatServeCustomerService.changeStatus(chatServeCustomer));
    }

    /**
     * 查询客服列表.
     */
    @PreAuthorize("@ss.hasPermi('chatroom:customer:list')")
    @GetMapping("/listServeCustomer")
    public AjaxResult listServeCustomer()
    {
        List<ChatServeCustomer> list = chatServeCustomerService.selectChatServeCustomerList(new ChatServeCustomer());
        return AjaxResult.success(list);
    }
}

package com.ruoyi.web.controller.chatroom;

import com.ruoyi.chatroom.service.ChatRoomRecordService;
import com.ruoyi.chatroom.service.MessageManagementService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 *  消息管理
 * @author nn
 * @date 2022/07/29
 */
@RestController
@RequestMapping("/chatroom/message/management")
public class MessageManagementController extends BaseController {

    @Resource
    private MessageManagementService messageManagementService;

    @Resource
    private ChatRoomRecordService chatRoomRecordService;

    /**
     * 列表数据.
     * @param param
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('message:management:list')")
    public TableDataInfo list(@RequestParam Map<String,Object> param){
        TableDataInfo tableDataInfo = messageManagementService.queryPage(param);
        return tableDataInfo;
    }

    /**
     * 删除大厅消息.
     */
    @PreAuthorize("@ss.hasPermi('message:management:remove')")
    @Log(title = "消息管理-删除消息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        messageManagementService.remove(ids);
        return AjaxResult.success("删除成功!");
    }

    /**
     * 定时删除大厅消息.
     *
     * @return
     */
    @PostMapping("/deleteOnTimeChatRoomRecord")
    public AjaxResult deleteOnTimeChatRoomRecord()   {
        chatRoomRecordService.deleteOnTimeChatRoomRecord(7);
        return new AjaxResult();
    }
}

package com.ruoyi.web.controller.chatroom;

import com.ruoyi.chatroom.db.domain.ChatFriendRecord;
import com.ruoyi.chatroom.db.vo.ChatFriendRecordVo;
import com.ruoyi.chatroom.service.ServiceMessageService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.PageUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 *  客服消息
 * @author nn
 * @date 2022/07/26
 */
@RestController
@RequestMapping("chatroom/service/message")
public class ServiceMessageController extends BaseController {

    @Resource
    private ServiceMessageService serviceMessageService;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('service:message:list')")
    public TableDataInfo list(@RequestParam Map<String,Object> param){
        TableDataInfo tableDataInfo = serviceMessageService.queryPage(param);
        return tableDataInfo;
    }

    @GetMapping("/dialogueList")
    @PreAuthorize("@ss.hasPermi('service:message:dialogueList')")
    public AjaxResult dialogueList(@RequestParam Map<String,Object> param){
        List<ChatFriendRecord> list = serviceMessageService.dialogueList(param);
        return AjaxResult.success(list);
    }

}

package com.ruoyi.web.controller.chatroom;

import com.ruoyi.chatroom.service.MessageRecallRecordService;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 *  消息撤回记录
 * @author nn
 * @date 2022/07/24
 */
@RequestMapping("/chatroom/message/recallRecord")
@RestController
public class MessageRecallRecordController {

    @Resource
    private MessageRecallRecordService messageRecallRecordService;

    /**
     * 查询列表
     */
    @PreAuthorize("@ss.hasPermi('message:recall:list')")
    @GetMapping("/list")
    public TableDataInfo getList(@RequestParam Map<String, Object> params) {
        TableDataInfo tableDataInfo = messageRecallRecordService.queryPage(params);
        return  tableDataInfo;
    }

}

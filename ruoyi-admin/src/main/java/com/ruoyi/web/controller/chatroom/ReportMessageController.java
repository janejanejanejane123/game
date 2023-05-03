package com.ruoyi.web.controller.chatroom;

import com.ruoyi.chatroom.service.ReportMessageService;
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
 *  举报记录
 * @author nn
 * @date 2022/07/27
 */
@RequestMapping("/chatroom/reportMessage")
@RestController
public class ReportMessageController extends BaseController {

    @Resource
    private ReportMessageService reportMessageService;
    /**
     * 查询
     */
    @PreAuthorize("@ss.hasPermi('chatroom:reportMessage:list')")
    @GetMapping("/list")
    public TableDataInfo getList(@RequestParam Map<String, Object> params) {
        TableDataInfo tableDataInfo = reportMessageService.queryPage(params);
        return tableDataInfo;
    }

    /**
     * 删除举报记录
     * @param ids
     * @return
     */
    @PreAuthorize("@ss.hasPermi('chatroom:reportMessage:delete')")
    @Log(title = "举报记录-删除", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult delete(@PathVariable Long[] ids){
        Long row = reportMessageService.delete(ids);
        return AjaxResult.success("删除成功!");
    }

}

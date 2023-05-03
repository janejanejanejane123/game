package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.service.ISysNoticeService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告 信息操作处理
 * 
 * @author nn
 */
@RestController
@RequestMapping("/web/system/notice")
public class NoticeController extends BaseController
{
    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 获取通知公告列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysNotice notice)
    {
        startPage();
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        return getDataTable(list);
    }

    /**
     * 获取通知列表
     */
    @GetMapping("/getInformList")
    public AjaxResult getInformList(SysNotice notice)
    {
        notice.setNoticeType("1");
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        return AjaxResult.success(list);
    }

    /**
     * 获取公告列表
     */
    @GetMapping("/getNoticeList")
    @PreAuthorize("@ss.playerOnly()")
    public TableDataInfo getNoticeList(@RequestParam Map<String, Object> param)
    {
        SysNotice notice = new SysNotice();
        param.put("pageNum",1);
        param.put("pageSize",10);
        notice.setParams(param);
        startPage();
        List<SysNotice> list = noticeService.getNoticeList(notice);
        return getDataTable(list);
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @GetMapping(value = "/getNoticeById")
    public AjaxResult getNoticeById(@PathVariable Long noticeId)
    {
        return AjaxResult.success(noticeService.selectNoticeById(noticeId));
    }

}

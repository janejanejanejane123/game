package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.TSession;
import com.ruoyi.system.service.ITSessionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * systemController
 * 
 * @author ry
 * @date 2022-05-24
 */
@RestController
@RequestMapping("/system/session")
public class TSessionController extends BaseController
{
    @Resource
    private ITSessionService tSessionService;

    /**
     * 查询system列表
     */
    @PreAuthorize("@ss.hasPermi('system:session:list')")
    @GetMapping("/list")
    public TableDataInfo list(TSession tSession)
    {
        startPage();
        List<TSession> list = tSessionService.selectTSessionList(tSession);
        return getDataTable(list);
    }

}

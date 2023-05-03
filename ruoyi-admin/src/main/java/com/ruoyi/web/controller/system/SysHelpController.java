package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
import com.ruoyi.system.domain.SysHelp;
import com.ruoyi.system.service.ISysHelpService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 会员帮助Controller
 * 
 * @author nn
 * @date 2022-03-13
 */
@RestController
@RequestMapping("/system/help")
public class SysHelpController extends BaseController
{
    @Autowired
    private ISysHelpService sysHelpService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询会员帮助列表
     */
    @PreAuthorize("@ss.hasPermi('system:help:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysHelp sysHelp)
    {
        startPage();
        List<SysHelp> list = sysHelpService.selectSysHelpList(sysHelp);
        return getDataTable(list);
    }

    /**
     * 导出会员帮助列表
     */
    @PreAuthorize("@ss.hasPermi('system:help:export')")
    @Log(title = "会员帮助", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysHelp sysHelp)
    {
        List<SysHelp> list = sysHelpService.selectSysHelpList(sysHelp);
        ExcelUtil<SysHelp> util = new ExcelUtil<SysHelp>(SysHelp.class);
        util.exportExcel(response, list, "会员帮助数据");
    }

    /**
     * 获取会员帮助详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:help:query')")
    @GetMapping(value = "/{helpId}")
    public AjaxResult getInfo(@PathVariable("helpId") Long helpId)
    {
        return AjaxResult.success(sysHelpService.selectSysHelpByHelpId(helpId));
    }

    /**
     * 新增会员帮助
     */
    @PreAuthorize("@ss.hasPermi('system:help:add')")
    @Log(title = "会员帮助", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysHelp sysHelp) {
        if (UserConstants.NOT_UNIQUE.equals(sysHelpService.checkHelpTypeUnique(sysHelp)))
        {
            return AjaxResult.error("新增帮助失败，该类型帮助已存在");
        }
        sysHelp.setHelpId(snowflakeIdUtils.nextId());
        sysHelp.setCreateBy(getUsername());
        sysHelp.setCreateTime(DateUtils.getNowDate());
        return toAjax(sysHelpService.insertSysHelp(sysHelp));
    }

    /**
     * 修改会员帮助
     */
    @PreAuthorize("@ss.hasPermi('system:help:edit')")
    @Log(title = "会员帮助", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysHelp sysHelp) {
        if (UserConstants.NOT_UNIQUE.equals(sysHelpService.checkHelpTypeUnique(sysHelp)))
        {
            return AjaxResult.error("修改帮助失败，该类型帮助已存在");
        }
        sysHelp.setUpdateBy(getUsername());
        sysHelp.setUpdateTime(DateUtils.getNowDate());
        return toAjax(sysHelpService.updateSysHelp(sysHelp));
    }

    /**
     * 删除会员帮助
     */
    @PreAuthorize("@ss.hasPermi('system:help:remove')")
    @Log(title = "会员帮助", businessType = BusinessType.DELETE)
	@DeleteMapping("/{helpIds}")
    public AjaxResult remove(@PathVariable Long[] helpIds)
    {
        return toAjax(sysHelpService.deleteSysHelpByHelpIds(helpIds));
    }
}

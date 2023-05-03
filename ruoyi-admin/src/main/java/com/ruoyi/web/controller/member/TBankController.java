package com.ruoyi.web.controller.member;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.member.domain.TBank;
import com.ruoyi.member.service.ITBankService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 银行卡列表Controller
 * 
 * @author ruoyi
 * @date 2022-05-20
 */
@RestController
@RequestMapping("/member/bank")
public class TBankController extends BaseController
{
    @Autowired
    private ITBankService tBankService;

    /**
     * 查询银行卡列表列表
     */
    @PreAuthorize("@ss.hasPermi('member:bank:list')")
    @GetMapping("/list")
    public TableDataInfo list(TBank tBank)
    {
        startPage();
        List<TBank> list = tBankService.selectTBankList(tBank);
        return getDataTable(list);
    }

    /**
     * 导出银行卡列表列表
     */
    @PreAuthorize("@ss.hasPermi('member:bank:export')")
    @Log(title = "银行卡列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TBank tBank)
    {
        List<TBank> list = tBankService.selectTBankList(tBank);
        ExcelUtil<TBank> util = new ExcelUtil<TBank>(TBank.class);
        util.exportExcel(response, list, "银行卡列表数据");
    }

    /**
     * 获取银行卡列表详细信息
     */
    @PreAuthorize("@ss.hasPermi('member:bank:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tBankService.selectTBankById(id));
    }

    /**
     * 新增银行卡列表
     */
    @PreAuthorize("@ss.hasPermi('member:bank:add')")
    @Log(title = "银行卡列表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TBank tBank)
    {
        return toAjax(tBankService.insertTBank(tBank));
    }

    /**
     * 修改银行卡列表
     */
    @PreAuthorize("@ss.hasPermi('member:bank:edit')")
    @Log(title = "银行卡列表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TBank tBank)
    {
        return toAjax(tBankService.updateTBank(tBank));
    }

    /**
     * 删除银行卡列表
     */
    @PreAuthorize("@ss.hasPermi('member:bank:remove')")
    @Log(title = "银行卡列表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tBankService.deleteTBankByIds(ids));
    }
}

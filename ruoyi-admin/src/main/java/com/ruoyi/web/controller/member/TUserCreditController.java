package com.ruoyi.web.controller.member;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.member.TUserCredit;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.member.service.ITUserCreditService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
 * 【请填写功能名称】Controller
 * 
 * @author ruoyi
 * @date 2022-03-27
 */
@RestController
@RequestMapping("/member/credit")
public class TUserCreditController extends BaseController
{
    @Resource
    private ITUserCreditService creditService;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('member:credit:list')")
    public TableDataInfo list(TUserCredit credit){
        startPage();
        List<TUserCredit> tUserCredits= creditService.listed(credit);
        return getDataTable(tUserCredits);
    }

    @GetMapping("/delete")
    @Log(title = "删除收款方式", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('member:credit:delete')")
    public AjaxResult delete(@RequestParam("id")Long id){
        return creditService.delete(id);
    }
}

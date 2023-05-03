package com.ruoyi.web.controller.payment;

import java.math.BigDecimal;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.system.service.ISysUserService;
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
import com.ruoyi.payment.domain.ProcedureFee;
import com.ruoyi.payment.service.IProcedureFeeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 代付商户手续费设置Controller
 * 
 * @author nn
 * @date 2022-08-27
 */
@RestController
@RequestMapping("/payment/fee")
public class ProcedureFeeController extends BaseController
{
    @Autowired
    private IProcedureFeeService procedureFeeService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    @Autowired
    private ISysUserService iSysUserService;

    /**
     * 查询代付商户手续费设置列表
     */
    @PreAuthorize("@ss.hasPermi('payment:fee:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProcedureFee procedureFee)
    {
        startPage();
        List<ProcedureFee> list = procedureFeeService.selectProcedureFeeList(procedureFee);
        return getDataTable(list);
    }

    /**
     * 导出代付商户手续费设置列表
     */
    @PreAuthorize("@ss.hasPermi('payment:fee:export')")
    @Log(title = "代付商户手续费设置-导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProcedureFee procedureFee)
    {
        List<ProcedureFee> list = procedureFeeService.selectProcedureFeeList(procedureFee);
        ExcelUtil<ProcedureFee> util = new ExcelUtil<ProcedureFee>(ProcedureFee.class);
        util.exportExcel(response, list, "代付商户手续费设置数据");
    }

    /**
     * 获取代付商户手续费设置详细信息
     */
    @PreAuthorize("@ss.hasPermi('payment:fee:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(procedureFeeService.selectProcedureFeeById(id));
    }

    /**
     * 新增代付商户手续费设置
     */
    @PreAuthorize("@ss.hasPermi('payment:fee:add')")
    @Log(title = "代付商户手续费设置-新增", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProcedureFee procedureFee)
    {
        if (UserConstants.NOT_UNIQUE.equals(procedureFeeService.checkFeeUnique(procedureFee)))
        {
            return AjaxResult.error("新增手续费失败，该代付商户费率已存在");
        }
        //查询父级.
        SysUser sysUser = iSysUserService.selectUserById(procedureFee.getUserId());
        if(sysUser != null && sysUser.getParentId() != null){
            BigDecimal rate = procedureFeeService.getUserRate(sysUser.getParentId());
            if(rate.compareTo(procedureFee.getRate()) == 1){
                return AjaxResult.error("新增手续费失败，下级手续费必须大于等于上级的手续费");
            }
            procedureFee.setParentId(sysUser.getParentId());
            procedureFee.setParentName(sysUser.getParentName());
        }
        procedureFee.setId(snowflakeIdUtils.nextId());
        procedureFee.setCreateBy(getUsername());
        procedureFee.setCreateTime(DateUtils.getNowDate());
        return toAjax(procedureFeeService.insertProcedureFee(procedureFee));
    }

    /**
     * 修改代付商户手续费设置
     */
    @PreAuthorize("@ss.hasPermi('payment:fee:edit')")
    @Log(title = "代付商户手续费设置-修改", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProcedureFee procedureFee)
    {
        if (UserConstants.NOT_UNIQUE.equals(procedureFeeService.checkFeeUnique(procedureFee))) {
            return AjaxResult.error("修改手续费失败，该代付商户费率已存在");
        }
        //查询自己(手续费只能降不能升).
        BigDecimal my = procedureFeeService.getMyRate(procedureFee.getUserId());
        if(my.compareTo(procedureFee.getRate()) == -1){
            return AjaxResult.error("修改手续费失败，手续费只能降不能升!");
        }
        //查询父级(手续费要大于等于上级).
        SysUser sysUser = iSysUserService.selectUserById(procedureFee.getUserId());
        if(sysUser != null && sysUser.getParentId() != null){
            BigDecimal rate = procedureFeeService.getUserRate(sysUser.getParentId());
            if(rate.compareTo(procedureFee.getRate()) == 1){
                return AjaxResult.error("修改手续费失败，下级手续费必须大于等于上级的手续费");
            }
        }
        //查询下级(手续费要小于等于下级).
        ProcedureFee query = new ProcedureFee();
        query.setParentId(procedureFee.getUserId());
        List<ProcedureFee> list = procedureFeeService.selectProcedureFeeList(query);
        if(list != null && list.size() > 0){
            for (ProcedureFee fee : list){
                if(fee.getRate().compareTo(procedureFee.getRate()) < 0){
                    return AjaxResult.error("修改手续费失败，上级手续费必须小于等于下级的手续费");
                }
            }
        }
        procedureFee.setUpdateBy(getUsername());
        procedureFee.setUpdateTime(DateUtils.getNowDate());
        return toAjax(procedureFeeService.updateProcedureFee(procedureFee));
    }

    /**
     * 删除代付商户手续费设置
     */
    @PreAuthorize("@ss.hasPermi('payment:fee:remove')")
    @Log(title = "代付商户手续费设置-删除", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        for(Long id : ids){
            ProcedureFee procedureFee = procedureFeeService.selectProcedureFeeById(id);
            //查询下级.
            ProcedureFee query = new ProcedureFee();
            query.setParentId(procedureFee.getUserId());
            List<ProcedureFee> list = procedureFeeService.selectProcedureFeeList(query);
            if(list != null && list.size() > 0){
                //如果由下级,跳过.
                continue;
            }else {
                procedureFeeService.deleteProcedureFeeById(id);
            }
        }
        return AjaxResult.success("删除成功!有下级的数据将不会删除!");
    }

    /**
     * 根据商家Id查询手续费设置
     */
    @PreAuthorize("@ss.hasPermi('payment:fee:list')")
    @GetMapping("/selectFeeByUserId")
    public AjaxResult selectFeeByUserId(ProcedureFee procedureFee)
    {
        AjaxResult ajaxResult = new AjaxResult();
        BigDecimal rate = procedureFeeService.getUserRate(procedureFee.getUserId());
        ajaxResult.put("rate",rate);
        return ajaxResult;
    }

    /**
     * 设置手续费
     */
    @PreAuthorize("@ss.hasPermi('payment:fee:feeSetting')")
    @Log(title = "设置手续费-代付", businessType = BusinessType.UPDATE)
    @PutMapping("/feeSetting")
    public AjaxResult feeSetting(@RequestBody ProcedureFee procedureFee)
    {
        //查询自己(手续费只能降不能升).
        BigDecimal my = procedureFeeService.getMyRate(procedureFee.getUserId());
        if(my != null && my.compareTo(procedureFee.getRate()) == -1){
            return AjaxResult.error("设置手续费失败，手续费只能降不能升!");
        }
        //查询父级(手续费要大于等于上级).
        SysUser sysUser = iSysUserService.selectUserById(procedureFee.getUserId());
        if(sysUser != null && sysUser.getParentId() != null){
            BigDecimal rate = procedureFeeService.getUserRate(sysUser.getParentId());
            if(rate.compareTo(procedureFee.getRate()) == 1){
                return AjaxResult.error("设置手续费失败，下级手续费必须大于等于上级的手续费");
            }
            procedureFee.setParentId(sysUser.getParentId());
            procedureFee.setParentName(sysUser.getParentName());
        }
        //查询下级(手续费要小于等于下级).
        ProcedureFee query = new ProcedureFee();
        query.setParentId(procedureFee.getUserId());
        List<ProcedureFee> list = procedureFeeService.selectProcedureFeeList(query);
        if(list != null && list.size() > 0){
            for (ProcedureFee fee : list){
                if(fee.getRate().compareTo(procedureFee.getRate()) < 0){
                    return AjaxResult.error("设置手续费失败，上级手续费必须小于等于下级的手续费");
                }
            }
        }
        return toAjax(procedureFeeService.feeSetting(procedureFee));
    }
}

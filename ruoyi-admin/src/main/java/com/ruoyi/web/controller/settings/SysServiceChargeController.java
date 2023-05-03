package com.ruoyi.web.controller.settings;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.constant.FeeTypeConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.settings.domain.SysServiceCharge;
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
import com.ruoyi.settings.service.ISysServiceChargeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 手续费设置Controller
 * 
 * @author nn
 * @date 2022-03-26
 */
@RestController
@RequestMapping("/settings/charge")
public class SysServiceChargeController extends BaseController
{
    @Autowired
    private ISysServiceChargeService sysServiceChargeService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询手续费设置列表
     */
    @PreAuthorize("@ss.hasPermi('settings:charge:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysServiceCharge sysServiceCharge)
    {
        startPage();
        List<SysServiceCharge> list = sysServiceChargeService.selectSysServiceChargeList(sysServiceCharge);
        return getDataTable(list);
    }

    /**
     * 导出手续费设置列表
     */
    @PreAuthorize("@ss.hasPermi('settings:charge:export')")
    @Log(title = "手续费设置-导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysServiceCharge sysServiceCharge)
    {
        List<SysServiceCharge> list = sysServiceChargeService.selectSysServiceChargeList(sysServiceCharge);
        ExcelUtil<SysServiceCharge> util = new ExcelUtil<SysServiceCharge>(SysServiceCharge.class);
        util.exportExcel(response, list, "手续费设置数据");
    }

    /**
     * 获取手续费设置详细信息
     */
    @PreAuthorize("@ss.hasPermi('settings:charge:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(sysServiceChargeService.selectSysServiceChargeById(id));
    }

    /**
     * 新增手续费设置
     */
    @PreAuthorize("@ss.hasPermi('settings:charge:add')")
    @Log(title = "手续费设置-新增", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysServiceCharge sysServiceCharge)
    {
        if (UserConstants.NOT_UNIQUE.equals(sysServiceChargeService.checkFeeTypeToMerchantUnique(sysServiceCharge)))
        {
            return AjaxResult.error("新增手续费失败，该商户该类型费率已存在");
        }
        sysServiceCharge.setId(snowflakeIdUtils.nextId());
        sysServiceCharge.setCreateBy(getUsername());
        sysServiceCharge.setCreateTime(DateUtils.getNowDate());
        return toAjax(sysServiceChargeService.insertSysServiceCharge(sysServiceCharge));
    }

    /**
     * 修改手续费设置
     */
    @PreAuthorize("@ss.hasPermi('settings:charge:edit')")
    @Log(title = "手续费设置-修改", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysServiceCharge sysServiceCharge)
    {
        if (UserConstants.NOT_UNIQUE.equals(sysServiceChargeService.checkFeeTypeToMerchantUnique(sysServiceCharge)))
        {
            return AjaxResult.error("修改手续费失败，该商户该类型费率已存在");
        }
        sysServiceCharge.setUpdateBy(getUsername());
        sysServiceCharge.setUpdateTime(DateUtils.getNowDate());
        return toAjax(sysServiceChargeService.updateSysServiceCharge(sysServiceCharge));
    }

    /**
     * 删除手续费设置
     */
    @PreAuthorize("@ss.hasPermi('settings:charge:remove')")
    @Log(title = "手续费设置-删除", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysServiceChargeService.deleteSysServiceChargeByIds(ids));
    }

    /**
     * 根据商家Id查询所有类型的手续费设置
     */
    @PreAuthorize("@ss.hasPermi('settings:charge:list')")
    @GetMapping("/selectFeeByUserId")
    public AjaxResult selectFeeByUserId(SysServiceCharge sysServiceCharge)
    {
        AjaxResult ajaxResult = new AjaxResult();
        List<SysServiceCharge> list = sysServiceChargeService.selectSysServiceChargeList(sysServiceCharge);
        if(list != null && list.size() != 0){
            for(SysServiceCharge sys : list){
                if(FeeTypeConstants.MARKET_BUY.equals(sys.getFeeType())){
                    ajaxResult.put("marketBuy",sys.getRate());
                }else if(FeeTypeConstants.MARKET_SELL.equals(sys.getFeeType())){
                    ajaxResult.put("marketSell",sys.getRate());
                }else if(FeeTypeConstants.PLAYER_RECHARGE.equals(sys.getFeeType())){
                    ajaxResult.put("playerRecharge",sys.getRate());
                }else if(FeeTypeConstants.PLAYER_WITHDRAW.equals(sys.getFeeType())){
                    ajaxResult.put("playerWithdraw",sys.getRate());
                }else if(FeeTypeConstants.MERCHANT_RECHARGE.equals(sys.getFeeType())){
                    ajaxResult.put("merchantRecharge",sys.getRate());
                }else if(FeeTypeConstants.MERCHANT_WITHDRAW.equals(sys.getFeeType())){
                    ajaxResult.put("merchantWithdraw",sys.getRate());
                }else if(FeeTypeConstants.MERCHANT_ROLL_IN.equals(sys.getFeeType())){
                    ajaxResult.put("merchantRollIn",sys.getRate());
                }else if(FeeTypeConstants.MERCHANT_ROLL_OUT.equals(sys.getFeeType())){
                    ajaxResult.put("merchantRollOut",sys.getRate());
                } else if(FeeTypeConstants.BANK_CARD.equals(sys.getFeeType())){
                    ajaxResult.put("bankCard",sys.getRate());
                } else if(FeeTypeConstants.WECHAT.equals(sys.getFeeType())){
                    ajaxResult.put("weChat",sys.getRate());
                } else if(FeeTypeConstants.ALIPAY.equals(sys.getFeeType())){
                    ajaxResult.put("alipay",sys.getRate());
                } else if(FeeTypeConstants.QQ_WALLET.equals(sys.getFeeType())){
                    ajaxResult.put("qqWallet",sys.getRate());
                }
            }
        }

        return ajaxResult;
    }

    /**
     * 设置手续费
     */
    @PreAuthorize("@ss.hasPermi('settings:charge:feeSetting')")
    @Log(title = "设置手续费", businessType = BusinessType.UPDATE)
    @PutMapping("/feeSetting")
    public AjaxResult feeSetting(@RequestBody SysServiceCharge sysServiceCharge)
    {
        sysServiceCharge.setCreateBy(getUsername());
        sysServiceCharge.setUpdateBy(getUsername());
        return toAjax(sysServiceChargeService.feeSetting(sysServiceCharge));
    }

    /**
     * 一键设置手续费(一种手续费类型)
     */
    @PreAuthorize("@ss.hasPermi('settings:charge:aKeySetRate')")
    @Log(title = "一键设置手续费", businessType = BusinessType.UPDATE)
    @PutMapping("/aKeySetRate")
    public AjaxResult aKeySetRate(@RequestBody SysServiceCharge sysServiceCharge)
    {
        sysServiceCharge.setCreateBy(getUsername());
        sysServiceCharge.setUpdateBy(getUsername());
        return toAjax(sysServiceChargeService.aKeySetRate(sysServiceCharge));
    }

    /**
     * 根据商家Id查询所有类型的手续费设置
     */
    @PreAuthorize("@ss.hasPermi('settings:charge:initialLoginUserFee')")
    @GetMapping("/initialLoginUserFee")
    public AjaxResult initialLoginUserFee(SysServiceCharge sysServiceCharge)
    {
        AjaxResult ajaxResult = new AjaxResult();
        List<SysServiceCharge> list = null;
        LoginUser loginUser = getLoginUser();
        SysUser sysUser = loginUser.getUser();
        //如果有传商户Id说明是系统管理员根据商户查询，如果没传那就是商户查看自己的数据.
        if(sysServiceCharge != null && sysServiceCharge.getUserId() != null){
            list = sysServiceChargeService.selectSysServiceChargeList(sysServiceCharge);
        } else if(UserConstants.SYS_USER_TYPE_MERCHANT.equals(sysUser.getUserType())){
            sysServiceCharge.setUserId(sysUser.getUserId());
            list = sysServiceChargeService.selectSysServiceChargeList(sysServiceCharge);
        }

        if(list != null && list.size() != 0){
            for(SysServiceCharge sys : list){
                if(FeeTypeConstants.MARKET_BUY.equals(sys.getFeeType())){
                    ajaxResult.put("marketBuy",sys.getRate());
                }else if(FeeTypeConstants.MARKET_SELL.equals(sys.getFeeType())){
                    ajaxResult.put("marketSell",sys.getRate());
                }else if(FeeTypeConstants.PLAYER_RECHARGE.equals(sys.getFeeType())){
                    ajaxResult.put("playerRecharge",sys.getRate());
                }else if(FeeTypeConstants.PLAYER_WITHDRAW.equals(sys.getFeeType())){
                    ajaxResult.put("playerWithdraw",sys.getRate());
                }else if(FeeTypeConstants.MERCHANT_RECHARGE.equals(sys.getFeeType())){
                    ajaxResult.put("merchantRecharge",sys.getRate());
                }else if(FeeTypeConstants.MERCHANT_WITHDRAW.equals(sys.getFeeType())){
                    ajaxResult.put("merchantWithdraw",sys.getRate());
                }else if(FeeTypeConstants.MERCHANT_ROLL_IN.equals(sys.getFeeType())){
                    ajaxResult.put("merchantRollIn",sys.getRate());
                }else if(FeeTypeConstants.MERCHANT_ROLL_OUT.equals(sys.getFeeType())){
                    ajaxResult.put("merchantRollOut",sys.getRate());
                }else if(FeeTypeConstants.BANK_CARD.equals(sys.getFeeType())){
                    ajaxResult.put("bankCard",sys.getRate());
                } else if(FeeTypeConstants.WECHAT.equals(sys.getFeeType())){
                    ajaxResult.put("weChat",sys.getRate());
                } else if(FeeTypeConstants.ALIPAY.equals(sys.getFeeType())){
                    ajaxResult.put("alipay",sys.getRate());
                } else if(FeeTypeConstants.QQ_WALLET.equals(sys.getFeeType())){
                    ajaxResult.put("qqWallet",sys.getRate());
                }
            }
        }

        return ajaxResult;
    }
}

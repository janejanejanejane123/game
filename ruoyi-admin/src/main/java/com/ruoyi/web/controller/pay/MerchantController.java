package com.ruoyi.web.controller.pay;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.CreateSecrteKey;
import com.ruoyi.common.utils.StringUtils;
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
import com.ruoyi.pay.domain.Merchant;
import com.ruoyi.pay.service.IMerchantService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 商户Controller
 *
 * @author
 * @date 2022-03-07
 */
@RestController
@RequestMapping("/pay/merchant")
public class MerchantController extends BaseController {
    @Autowired
    private IMerchantService merchantService;
    @Autowired
    private ISysUserService iSysUserService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 查询商户列表
     */
    @PreAuthorize("@ss.hasPermi('pay:merchant:list')")
    @GetMapping("/list")
    public TableDataInfo list(Merchant merchant) {
        startPage();
        List<Merchant> list = merchantService.selectMerchantList(merchant);
        list.forEach(item->{
            item.setPriKey("");
            item.setPubKey("");
        });
        return getDataTable(list);
    }

    /**
     * 导出商户列表
     */
    @PreAuthorize("@ss.hasPermi('pay:merchant:export')")
    @Log(title = "商户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Merchant merchant) {
        List<Merchant> list = merchantService.selectMerchantList(merchant);
        ExcelUtil<Merchant> util = new ExcelUtil<Merchant>(Merchant.class);
        util.exportExcel(response, list, "商户数据");
    }

    /**
     * 获取商户详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:merchant:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        if (redisCache.getCacheObject("Merchant:validateGoogleCode:" + getUserId()) == null) {
            return  AjaxResult.error("请先验证google验证码");
        }
        return AjaxResult.success(merchantService.selectMerchantById(id));
    }

    /**
     * 获取商户详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:merchant:query')")
    @GetMapping(value = "/validateGoogleCode")
    public AjaxResult validateGoogleCode(Long code) {
        validateGoogleCode(iSysUserService.selectUserByUserName(getLoginUser().getUsername()).getSecret(), code);
        redisCache.setCacheObject("Merchant:validateGoogleCode:" + getUserId(), "1", 2, TimeUnit.MINUTES);
        return AjaxResult.success();
    }

    /**
     * 修改商户
     */
    @PreAuthorize("@ss.hasPermi('pay:merchant:edit')")
    @Log(title = "商户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Merchant merchant) {
        if (redisCache.getCacheObject("Merchant:validateGoogleCode:" + getUserId()) == null) {
            return AjaxResult.error("google验证码已过期");
        }
        Merchant newMerchant = new Merchant();
        newMerchant.setUpdateName(getUsername());
        newMerchant.setUpdateTime(new Date());
        newMerchant.setId(merchant.getId());
        Map<String, String> rsa = CreateSecrteKey.getRsa();
        newMerchant.setPubKey(rsa.get("publicKey"));
        newMerchant.setPriKey(rsa.get("privateKey"));
        return toAjax(merchantService.updateMerchant(newMerchant));
    }

    /**
     * 修改商户
     */
    @PreAuthorize("@ss.hasPermi('pay:merchant:edit')")
    @Log(title = "商户IP加白", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/updateIp")
    public AjaxResult updateIp(@RequestBody Merchant merchant) {
        Merchant updateMerchant = new Merchant();
        updateMerchant.setId(merchant.getId());
        updateMerchant.setIp(merchant.getIp());
        updateMerchant.setUpdateName(getLoginUser().getUsername());
        return toAjax(merchantService.updateMerchant(updateMerchant));
    }


    /*  *//**
     * 删除商户
     *//*
    @PreAuthorize("@ss.hasPermi('pay:merchant:remove')")
    @Log(title = "商户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(merchantService.deleteMerchantByIds(ids));
    }*/
}

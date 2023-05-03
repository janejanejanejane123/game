package com.ruoyi.web.controller.system;

import java.util.List;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysPayConfig;
import com.ruoyi.system.service.ISysPayConfigService;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 支付配置Controller
 * 
 * @author nn
 * @date 2022-09-06
 */
@RestController
@RequestMapping("/system/payConfig")
public class SysPayConfigController extends BaseController
{
    @Autowired
    private ISysPayConfigService sysPayConfigService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 查询支付配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:payConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysPayConfig sysPayConfig) {
        startPage();
        List<SysPayConfig> list = sysPayConfigService.selectSysPayConfigList(sysPayConfig);
        return getDataTable(list);
    }

    /**
     * 获取支付配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:payConfig:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(sysPayConfigService.selectSysPayConfigById(id));
    }

    /**
     * 新增支付配置
     */
    @PreAuthorize("@ss.hasPermi('system:payConfig:add')")
    @Log(title = "支付配置-新增", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysPayConfig sysPayConfig) {
        sysPayConfig.setId(snowflakeIdUtils.nextId());
        //商户自己新增.
        if(isMerchant()){
            sysPayConfig.setUserId(getUserId());
            sysPayConfig.setUserName(getUsername());
        }else {
            SysUser sysUser = sysUserService.selectUserById(sysPayConfig.getUserId());
            sysPayConfig.setUserId(sysUser.getUserId());
            sysPayConfig.setUserName(sysUser.getUserName());
        }

        return toAjax(sysPayConfigService.insertSysPayConfig(sysPayConfig));
    }

    /**
     * 修改支付配置
     */
    @PreAuthorize("@ss.hasPermi('system:payConfig:edit')")
    @Log(title = "支付配置-修改", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysPayConfig sysPayConfig) {
        return toAjax(sysPayConfigService.updateSysPayConfig(sysPayConfig));
    }

    /**
     * 初始化商户配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:payConfig:query')")
    @GetMapping(value = "/initialConfig")
    public AjaxResult initialConfig(SysPayConfig sysPayConfig) {
        LoginUser loginUser = getLoginUser();
        SysUser sysUser = loginUser.getUser();
        SysPayConfig result = new SysPayConfig();
        //如果有传商户Id说明是系统管理员根据商户查询，如果没传那就是商户查看自己的数据.
        if(sysPayConfig != null && sysPayConfig.getUserId() != null){
            result =  sysPayConfigService.selectSysPayConfigByUserId(sysPayConfig.getUserId());
        } else if(UserConstants.SYS_USER_TYPE_MERCHANT.equals(sysUser.getUserType())){
            result = sysPayConfigService.selectSysPayConfigByUserId(sysUser.getUserId());
        }

        return AjaxResult.success(result);
    }

    /**
     * 上传Logo
     * @param file
     * @return
     */
    @PostMapping("/uploadLogo")
    @PreAuthorize("@ss.hasPermi('system:payConfig:add')")
    public AjaxResult uploadLogo(@RequestParam("file") MultipartFile file) {
        String path = sysPayConfigService.uploadLogo(file);
        return AjaxResult.success().put("siteLogo",path);
    }
}

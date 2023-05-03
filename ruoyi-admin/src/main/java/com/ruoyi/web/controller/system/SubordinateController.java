package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.CacheConstant;
import com.ruoyi.common.constant.FeeTypeConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.google.GoogleAuthUtils;
import com.ruoyi.common.utils.google.GoogleAuthenticator;
import com.ruoyi.common.utils.google.QRCodeUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.sign.Base64;
import com.ruoyi.settings.domain.SysServiceCharge;
import com.ruoyi.settings.service.ISysServiceChargeService;
import com.ruoyi.system.domain.SysUserOnline;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserOnlineService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户下级信息
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/subordinate")
public class SubordinateController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserOnlineService userOnlineService;

    @Autowired
    private ISysServiceChargeService sysServiceChargeService;

    /**
     * 获取商户下级列表
     */
    @PreAuthorize("@ss.hasPermi('system:subordinate:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user)
    {
        startPage();
        user.setParentId(getUserId());
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "商户下级-导出", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:subordinate:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user)
    {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    @Log(title = "商户下级-导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:subordinate:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:subordinate:query')")
    @GetMapping(value = { "/", "/{userId}" })
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        userService.checkUserDataScope(userId);
        AjaxResult ajax = AjaxResult.success();
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        ajax.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId))
        {
            SysUser sysUser = userService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:subordinate:add')")
    @Log(title = "商户下级-新增", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        if(!isMerchant()){
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败,商户才能新增下级!");
        }
        if(getLoginUser().getUser().getParentId() != null){
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败,一级商户才能新增下级!");
        }

        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName())))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUserId(snowflakeIdUtils.nextId());
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setParentId(getUserId());
        user.setParentName(getUsername());
        user.setDeptId(getLoginUser().getDeptId());
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:subordinate:edit')")
    @Log(title = "商户下级-修改", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:subordinate:remove')")
    @Log(title = "商户下级-删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        if (ArrayUtils.contains(userIds, getUserId()))
        {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:subordinate:resetPwd')")
    @Log(title = "商户下级-重置密码", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:subordinate:edit')")
    @Log(title = "商户下级-修改", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:subordinate:query')")
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId)
    {
        AjaxResult ajax = AjaxResult.success();
        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:subordinate:edit')")
    @Log(title = "商户下级", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds)
    {
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    /**
     * 根据用户名称 + 密钥 生成谷歌验证二维码.
     * @param response
     * @throws IOException
     */
    @GetMapping("/captcha.jpg")
    @PreAuthorize("@ss.hasPermi ('system:subordinate:binding')")
    public AjaxResult captcha(HttpServletResponse response,Long userId,String userName) throws Exception {
        AjaxResult ajax = AjaxResult.success();
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //生成谷歌密钥.
        String secret = GoogleAuthenticator.generateSecretKey();
        SysUser sysUser = null;
        String username = "Trump";
        String imgUrl = null;      //用于插入二维码中的图片.

        //如果loginName不为空，说明是注册用户绑定，是没有用户ID的所以用唯一的用户名查询一次.
        //新增用户时绑定(待优化:只根据用户名判断存在问题,不同的站点可能有相同的用户名,所以必须是指定一个站点,目前这里).已优化(6.12)。
        if((userId == null || "".equals(userId)) && userName != null && !"".equals(userName)){
            username = userName;
            imgUrl = sysUser.getAvatar();
            sysUser = userService.selectUserByUserName(userName);
        }else {     //用户列表绑定.
            sysUser = userService.selectUserById(userId);
            if(sysUser != null ){
                username = sysUser.getUserName();
                imgUrl = sysUser.getAvatar();
                //如果不为空说明生成过二维码了,用之前的秘钥再次生成同一二维码(对应查看二维码功能).
                if(sysUser.getSecret() != null && !"".equals(sysUser.getSecret())){
                    secret = sysUser.getSecret();
                }
            }
        }

        //生成二维码文本.
        String qcode = GoogleAuthUtils.genSecret(username,secret);
        //生成二维码.
        BufferedImage image = QRCodeUtils.createImage(qcode, imgUrl,false);
        if(sysUser != null && (sysUser.getSecret() == null || "".equals(sysUser.getSecret()))){
            sysUser.setSecret(secret);
            sysUser.setIsBinding("1");
            userService.updateUser(sysUser);
        }

        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try
        {
            ImageIO.write(image, QRCodeUtils.FORMAT_NAME, os);
        }
        catch (IOException e)
        {
            return AjaxResult.error(e.getMessage());
        }

        ajax.put("img", Base64.encode(os.toByteArray()));
        return ajax;
    }

    /**
     * 解绑谷歌验证器.
     */
    @Log(title = "解绑谷歌验证码", businessType = BusinessType.UPDATE)
    @PostMapping("/updateBinding")
    @PreAuthorize("@ss.hasPermi ('system:subordinate:unbundling')")
    @Transactional(propagation = Propagation.SUPPORTS)
    public AjaxResult updateBinding(@RequestBody SysUser user) {

        if(user == null){
            return new AjaxResult(200001,"用户不存在");
        }
        int count = 0;
        //管理员不离线,不然就不能登录了,因为只有他自己可以看到自己.
        if(UserConstants.SYSTEM_ADMIN.equals(user.getUserName())){
            count = userService.updateUser(user);
            if(count > 0){
                return  AjaxResult.success();
            }
        }else{
            count = userService.updateUser(user);
            if(count > 0){
                //解绑后踢下线.
                LoginUser loginUser = null;
                //获取所有登录用户的缓存key.
                Collection<String> keys = redisCache.keys(CacheConstant.LOGIN_TOKEN_KEY + "*");
                for (String key : keys) {
                    LoginUser cacheUser = redisCache.getCacheObject(key);
                    if (cacheUser.getUserId().equals(user.getUserId())) {
                        loginUser = cacheUser;
                        break;
                    }
                }
                if(loginUser != null){
                    SysUserOnline sysUserOnline = userOnlineService.selectOnlineByUserName(user.getUserName(),loginUser);
                    if(sysUserOnline != null){
                        redisCache.deleteObject(CacheConstant.LOGIN_TOKEN_KEY + sysUserOnline.getTokenId());
                    }
                }
                return  AjaxResult.success();
            }
        }

        return  AjaxResult.error(400001,"解绑谷歌验证码失败");
    }

    /**
     * 根据用户名称模糊查询查询
     */
    @GetMapping("/selectUserByUserName")
    @PreAuthorize("@ss.hasPermi('system:subordinate:list')")
    public AjaxResult selectUserByUserName(SysUser sysUser)
    {
        List<SysUser> list = userService.selectUserList(sysUser);
        return AjaxResult.success(list);
    }

    /**
     * 根据商家Id查询所有类型的手续费设置
     */
    @PreAuthorize("@ss.hasPermi('subordinate:charge:list')")
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
    @PreAuthorize("@ss.hasPermi('subordinate:charge:feeSetting')")
    @Log(title = "设置手续费", businessType = BusinessType.UPDATE)
    @PutMapping("/feeSetting")
    public AjaxResult feeSetting(@RequestBody SysServiceCharge sysServiceCharge) {

        if(!isMerchant()){
            return AjaxResult.error("设置手续费失败,商户才能设置下级手续费!");
        }
        if(getLoginUser().getUser().getParentId() != null){
            return AjaxResult.error("设置手续费失败,,一级商户才能设置下级手续费!");
        }
        BigDecimal rate = sysServiceChargeService.getUserIdRateByFeeType(getUserId(),sysServiceCharge.getFeeType());
//        int flag = bigdemical.compareTo(bigdemical1)。
//        flag = -1，表示bigdemical小于bigdemical1。
//        flag =0，表示bigdemical等于bigdemical1。
//        flag =1，表示bigdemical大于bigdemical1。
        if(rate.compareTo(sysServiceCharge.getRate()) == 1){
            return AjaxResult.error("设置手续费失败，下级手续费必须大于等于商户的手续费");
        }
        sysServiceCharge.setCreateBy(getUsername());
        sysServiceCharge.setUpdateBy(getUsername());
        return toAjax(sysServiceChargeService.feeSetting(sysServiceCharge));
    }

    /**
     * 一键设置手续费(一种手续费类型)
     */
    @PreAuthorize("@ss.hasPermi('subordinate:charge:aKeySetRate')")
    @Log(title = "一键设置手续费", businessType = BusinessType.UPDATE)
    @PutMapping("/aKeySetRate")
    public AjaxResult aKeySetRate(@RequestBody SysServiceCharge sysServiceCharge)
    {
        if(!isMerchant()){
            return AjaxResult.error("一键设置手续费失败,商户才能一键设置下级手续费!");
        }
        if(getLoginUser().getUser().getParentId() != null){
            return AjaxResult.error("一键设置手续费失败,,一级商户才能一键设置下级手续费!");
        }
        BigDecimal rate = sysServiceChargeService.getUserIdRateByFeeType(getUserId(),sysServiceCharge.getFeeType());
        if(rate.compareTo(sysServiceCharge.getRate()) == 1){
            return AjaxResult.error("一键设置手续费失败，下级手续费必须大于等于商户的手续费");
        }
        sysServiceCharge.setCreateBy(getUsername());
        sysServiceCharge.setUpdateBy(getUsername());
        return toAjax(sysServiceChargeService.aKeySetRate(sysServiceCharge));
    }

}

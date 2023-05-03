package com.ruoyi.web.controller.system;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Decrypt;
import com.ruoyi.common.annotation.DecryptLong;
import com.ruoyi.common.constant.CacheConstant;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.google.GoogleAuthUtils;
import com.ruoyi.common.utils.google.GoogleAuthenticator;
import com.ruoyi.common.utils.google.QRCodeUtils;
import com.ruoyi.common.utils.sign.Base64;
import com.ruoyi.pay.domain.Merchant;
import com.ruoyi.pay.service.IMerchantService;
import com.ruoyi.system.domain.SysUserOnline;
import com.ruoyi.system.service.ISysUserOnlineService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 用户信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {
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
    private IMerchantService iMerchantService;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "用户管理-导出", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    @Log(title = "用户管理-导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        userService.checkUserDataScope(userId);
        AjaxResult ajax = AjaxResult.success();
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        ajax.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId)) {
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
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理-新增用户", businessType = BusinessType.INSERT)
    @PostMapping
    @DecryptLong
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUserId(snowflakeIdUtils.nextId());
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理-修改用户", businessType = BusinessType.UPDATE)
    @PutMapping
    @DecryptLong
    public AjaxResult edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理-删除用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        if (ArrayUtils.contains(userIds, getUserId())) {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理-重置密码", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    @DecryptLong
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理-状态修改", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(getUsername());
        iMerchantService.updateMerchantStauts(user);
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 设置用户代理
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理-设置用户代理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeAgency")
    public AjaxResult changeAgency(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUserAgency(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId) {
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
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理-授权角色", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds) {
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    /**
     * 根据用户名称 + 密钥 生成谷歌验证二维码.
     *
     * @param response
     * @throws IOException
     */
    @GetMapping("/captcha.jpg")
    @PreAuthorize("@ss.hasPermi ('system:user:binding')")
    public AjaxResult captcha(HttpServletResponse response, Long userId, String userName) throws Exception {
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
        if ((userId == null || "".equals(userId)) && userName != null && !"".equals(userName)) {
            username = userName;
            imgUrl = sysUser.getAvatar();
            sysUser = userService.selectUserByUserName(userName);
        } else {     //用户列表绑定.
            sysUser = userService.selectUserById(userId);
            if (sysUser != null) {
                username = sysUser.getUserName();
                imgUrl = sysUser.getAvatar();
                //如果不为空说明生成过二维码了,用之前的秘钥再次生成同一二维码(对应查看二维码功能).
                if (sysUser.getSecret() != null && !"".equals(sysUser.getSecret())) {
                    secret = sysUser.getSecret();
                }
            }
        }

        //生成二维码文本.
        String qcode = GoogleAuthUtils.genSecret(username, secret);
        //生成二维码.
        BufferedImage image = QRCodeUtils.createImage(qcode, imgUrl, false);
        if (sysUser != null && (sysUser.getSecret() == null || "".equals(sysUser.getSecret()))) {
            sysUser.setSecret(secret);
            sysUser.setIsBinding("1");
            userService.updateGoogleCode(sysUser);
        }

        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, QRCodeUtils.FORMAT_NAME, os);
        } catch (IOException e) {
            return AjaxResult.error(e.getMessage());
        }

        ajax.put("img", Base64.encode(os.toByteArray()));
        return ajax;
    }

    /**
     * 解绑谷歌验证器.
     */
    @Log(title = "用户管理-解绑谷歌验证码", businessType = BusinessType.UPDATE)
    @PostMapping("/updateBinding")
    @PreAuthorize("@ss.hasPermi ('system:user:unbundling')")
    @Transactional(propagation = Propagation.SUPPORTS)
    @DecryptLong
    public AjaxResult updateBinding(@RequestBody SysUser user) {

        if (user == null) {
            return new AjaxResult(200001, "用户不存在");
        }
        int count = userService.updateGoogleCode(user);
        //管理员不离线,不然就不能登录了,因为只有他自己可以看到自己.
        if (UserConstants.SYSTEM_ADMIN.equals(user.getUserName())) {
            if (count > 0) {
                return AjaxResult.success();
            }
        } else {
            if (count > 0) {
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
                if (loginUser != null) {
                    SysUserOnline sysUserOnline = userOnlineService.selectOnlineByUserName(user.getUserName(), loginUser);
                    if (sysUserOnline != null) {
                        redisCache.deleteObject(CacheConstant.LOGIN_TOKEN_KEY + sysUserOnline.getTokenId());
                    }
                }
                return AjaxResult.success();
            }
        }

        return AjaxResult.error(400001, "解绑谷歌验证码失败");
    }

    /**
     * 根据用户名称查询
     */
    @GetMapping("/selectUserByUserName")
    @PreAuthorize("@ss.hasPermi('system:user:selectUserByUserName')")
    public AjaxResult selectUserByUserName(SysUser sysUser) {
        List<SysUser> list = userService.selectUserList(sysUser);
        return AjaxResult.success(list);
    }
}

package com.ruoyi.member.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.annotation.RedisLockOperate;
import com.ruoyi.common.bussiness.RequestContext;
import com.ruoyi.common.bussiness.constants.Constant;
import com.ruoyi.common.constant.ConfigKeyConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.TopicConstants;
import com.ruoyi.common.core.auth.ApiAuthKey;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.redis.RedisLock;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.enums.EmailEnums;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.vo.MemberMessageVo;
import com.ruoyi.member.domain.TUserDataConfig;
import com.ruoyi.member.domain.TUserPhoto;
import com.ruoyi.member.domain.TUserProxyApply;
import com.ruoyi.member.domain.TUserRegisteredApply;
import com.ruoyi.member.mapper.TUserMapper;
import com.ruoyi.member.service.*;
import com.ruoyi.member.util.ShortUrlUtil;
import com.ruoyi.member.util.Util;
import com.ruoyi.member.vo.*;
import com.ruoyi.pay.domain.Wallet;
import com.ruoyi.pay.service.IWalletService;
import com.ruoyi.system.service.IMemberService;
import com.ruoyi.system.service.ISysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2022-03-15
 */
@Service
@Slf4j
public class TUserServiceImpl implements ITUserService {
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private IMemberService memberService;
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;
    @Resource
    private ISysConfigService configService;
    @Resource
    private RedisCache redisCache;

    @Resource
    private TUserMapper tUserMapper;
    @Resource
    private ITUserRegisteredApplyService registeredApplyService;
    @Resource
    private IWalletService walletService;
    @Resource
    private IWalletService iWalletService;
    @Resource
    private ITEmailConfigService emailConfigService;
    @Resource
    private ITUserPhotoService userPhotoService;
    @Resource
    private ITUserDataConfigService userDataConfigService;
    @Resource
    private ShortUrlUtil shortUrlUtil;
    @Resource
    private ITUserProxyApplyService itUserProxyApplyService;
    @Resource
    private ITUserConfigService userConfigService;
    @Resource
    private RocketMQTemplate rocketMQTemplate;


    @Override
    @RedisLockOperate
    public AjaxResult applyProxy() {
        LoginMember loginUser = (LoginMember) SecurityUtils.getLoginUser();
        short type = loginUser.gettUser().getType();
        Assert.test(type >= 1, "member.info.type.proxyAlready");
        TUserProxyApply tUserProxyApply = new TUserProxyApply();
        tUserProxyApply.setUid(SecurityUtils.getUserId());
        tUserProxyApply.setCreatetTime(new Date());
        tUserProxyApply.setId(snowflakeIdUtils.nextId());
        tUserProxyApply.setStatus(Constants.APPLY_WAIT);
        tUserProxyApply.setUsername(SecurityUtils.getUsername());

        Assert.test(itUserProxyApplyService.countByUid(SecurityUtils.getUserId()) > 0,
                "member.info.type.proxyApplying");

        itUserProxyApplyService.insertTUserProxyApply(tUserProxyApply);
        return AjaxResult.success();
    }

    @Override
    public List<TUserPhoto> getAvailablePhotos() {
        return userPhotoService.getAvailablePhotos();
    }

    @Override
    @RedisLockOperate
    public AjaxResult multiFunctionalEmailVerify(short type) {
        EmailEnums emailType = EmailEnums.getEmailType(type);
        Assert.test(emailType == null, "system.error");
        LoginMember loginUser = (LoginMember) SecurityUtils.getLoginUser();
        String email = loginUser.getEmail();
        Assert.test(StringUtils.isBlank(email), "member.info.email.notExist");
        return emailConfigService.sendMessage(emailType, email, Util.randomNum(), loginUser.getUserId());
    }

    @Override
    @RedisLockOperate
    public void modifyPhoto(Long id) {
        TUserPhoto tUserPhoto = userPhotoService.queryById(id);
        Assert.test(tUserPhoto == null, "system.error");
        String src = tUserPhoto.getSrc();
        TUser tUser = new TUser();
        tUser.setUid(SecurityUtils.getUserId());
        tUser.setUsername(SecurityUtils.getUsername());
        tUser.setPhoto(src);
        memberService.updateUser(tUser);
        MemberMessageVo memberMessageVo = new MemberMessageVo();
        memberMessageVo.setUserHead(src);
        memberMessageVo.setSmallHead(src);
        memberMessageVo.setType((byte) 2);
        updateComplete(memberMessageVo);
    }

    @Override
    public AjaxResult confirmReset(JSONObject object) {
        String verify = object.getString("verify");
        String username = object.getString("username");
        String newPassword = object.getString("newPassword");
        TUser user = memberService.queryMemberByUsername(username);
        String redisKey = EmailEnums.RESETPASSWORD.redisKey(user.getUid());
        String cacheVerify = redisCache.getCacheObject(redisKey);

        Assert.test(StringUtils.isBlank(verify), "captcha.wrong.time");
        Assert.test(StringUtils.isBlank(cacheVerify), "captcha.out.time");
        Assert.test(!verify.equals(cacheVerify), "captcha.wrong.time");
        Assert.test(!CheckPatternUtils.match(newPassword, Constant.PASSWORD_PATTEN), "member.info.passwordPatternError");

        TUser tUser
                = new TUser();
        tUser.setUid(user.getUid());
        tUser.setUsername(user.getUsername());
        tUser.setPassword(SecurityUtils.encryptPassword(newPassword));
        memberService.updateUser(tUser);
        return AjaxResult.success();
    }

    @Override
    public AjaxResult resetPassword(JSONObject object) {
        String username = object.getString("username");
        TUser user = memberService.queryMemberByUsername(username);
        Assert.test(StringUtils.isBlank(user.getEmail()), "system.error");
        return emailConfigService.sendMessage(EmailEnums.RESETPASSWORD, user.getEmail(), Util.randomNum(), user.getUid());
    }

    @Override
    @RedisLockOperate
    public AjaxResult emailVerify(JSONObject object) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        String email = ((LoginMember) loginUser).gettUser().getEmail();

        String firstEmail = object.getString("email");


        if (StringUtils.isBlank(email)) {
            Assert.test(StringUtils.isBlank(firstEmail), "system.error");
            Assert.test(!CheckPatternUtils.match(firstEmail, Constant.EMAIL_PATTEN), "member.info.email.patternError");
            email = firstEmail;
        }

        String code = Util.randomNum();
        return emailConfigService.sendMessage(EmailEnums.BINDEMAIL, email, code, loginUser.getUserId());
    }

    @Override
    public void checkEmailVerify(EmailVo body) {
        ValidateUtils.validateEntity(body);
    }

    @Override
    public void bindEmail(EmailVo vo) {


        String email = vo.getEmail();

        LoginMember loginUser = (LoginMember) SecurityUtils.getLoginUser();


        String verify = vo.getVerify();
        String redisKey = EmailEnums.BINDEMAIL.redisKey(loginUser.getUserId());
        String cacheVerify = redisCache.getCacheObject(redisKey);

        Assert.test(StringUtils.isBlank(verify), "captcha.status.not.complete");
        Assert.test(StringUtils.isBlank(cacheVerify), "captcha.out.time");
        Assert.test(!cacheVerify.equals(verify), "captcha.wrong.time");

        TUser entity = new TUser();
        entity.setUid(loginUser.getUserId());
        entity.setUsername(loginUser.getUsername());
        entity.setEmail(email);
        loginUser.gettUser().setEmail(email);

        memberService.updateUser(entity);
    }

    @Override
    public void modifyNickName(String nickName, Long uid) {
        Assert.test(checkNickName(nickName), "member.info.nickname.exist");

        TUser tUser = new TUser();

        tUser.setUid(uid);
        tUser.setUsername(SecurityUtils.getUsername());
        tUser.setNickname(nickName);
        memberService.updateUser(tUser);
        MemberMessageVo memberMessageVo = new MemberMessageVo();
        memberMessageVo.setType((byte) 3);
        memberMessageVo.setUserNikeName(nickName);
        updateComplete(memberMessageVo);

    }

    private void uniqueCode(TUser tUser, boolean nickName) {
        Long uniqueCode = shortUrlUtil.getShortUrl("t_user:",
                RandomUtils.nextInt(1, 8),
                () -> {
                    Long max = tUserMapper.findMax();
                    max = max == null ? 0 : max;
                    return max < 4000L ? 4000L : max;
                });
        tUser.setUniqueCode(uniqueCode);
        String base36 = NumberUtils.defL2s(uniqueCode);
        tUser.setUrl(base36);
        if (nickName) {
            tUser.setNickname("用户" + base36);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginMember registeredMember(RegisteredInfo registeredInfo) {

        ValidateUtils.validateEntity(registeredInfo);

        String userName = registeredInfo.getUsername();
        TUser tUser = new TUser();
        if (!StringUtils.isBlank(registeredInfo.getInvite())) {
            TUser pid = memberService.queryMemberByInvite(registeredInfo.getInvite());
            Assert.test(pid == null, "member.info.uniqueCode.not.exist");
            tUser.setPid(pid.getUid());
            tUser.setPidArray(pid.getUsername());
        }

        Assert.test(checkUsernameMethod(userName), "member.info.username.exist");
        Assert.test(checkEmailMethod(registeredInfo.getEmail()), "member.info.email.exist");
        tUser.setUsername(userName);
        tUser.setRealname(registeredInfo.getRealName());
        tUser.setDisabled(1L);
        tUser.setEmail(registeredInfo.getEmail());
        tUser.setSalt(Constants.REGISTER_FRONT);
        tUser.setTelephone(registeredInfo.getTelephone());
        tUser.setUid(snowflakeIdUtils.nextId());
        tUser.setUserLevel(0L);
        tUser.setPhoto(defaultPhoto());
        tUser.setPassword(SecurityUtils.encryptPassword(registeredInfo.getPassword()));
        tUser.setPayPassword(SecurityUtils.encryptPassword(registeredInfo.getPassword()));
        tUser.setCreateTime(new Date());
        tUser.setMerchantId(Constants.MERCHANT_ID_DEFAULT);
        uniqueCode(tUser, true);
        tUser.setLastLoginIp(IpUtils.getCurrentReqIp());
        int insert = memberService.insert(tUser);
        if (insert == 1) {
            insertWallet(tUser);
            insertUserData(tUser);
            LoginMember loginMember = new LoginMember(tUser);
            loginMember.setUserType(Constants.USER_TYPE_PLAYER);
            registerComplete(tUser);
            return loginMember;
        } else {
            throw new ServiceException(MessageUtils.message("system.error"));
        }
    }

    private String defaultPhoto() {
        List<TUserPhoto> availablePhotos = userPhotoService.getAvailablePhotos();
        if (availablePhotos != null && availablePhotos.size() > 0) {
            return availablePhotos.get(0).getSrc();
        }
        return "";
    }

    //TODO
    private void registerComplete(TUser tUser) {
        MemberMessageVo memberMessageVo = new MemberMessageVo();
        memberMessageVo.setShortUrl(tUser.getUrl());
        memberMessageVo.setUserId(tUser.getUid());
        memberMessageVo.setUserName(tUser.getUsername());
        memberMessageVo.setUserNikeName(tUser.getNickname());
        memberMessageVo.setSmallHead(tUser.getPhoto());
        memberMessageVo.setUserHead(tUser.getPhoto());
        memberMessageVo.setType((byte)1);
        rocketMQTemplate.syncSend(TopicConstants.REGISTER_USER_INFO_TOPIC+":"+TopicConstants.REGISTER_USER_INFO_TOPIC_SORT,memberMessageVo);
    }

    //TODO
    private void updateComplete(MemberMessageVo memberMessageVo) {
        rocketMQTemplate.syncSend(TopicConstants.UPDATE_USER_INFO_TOPIC+":"+TopicConstants.UPDATE_USER_INFO_TOPIC_SORT,memberMessageVo);
    }

    private void insertUserData(TUser tUser) {
        TUserDataConfig tUserDataConfig = new TUserDataConfig();
        tUserDataConfig.setUid(tUser.getUid());
        tUserDataConfig.setEmailForSell((short) 0);
        tUserDataConfig.setPayPasswordForSell((short) 1);
        userDataConfigService.update(tUserDataConfig);
    }

    private boolean needToApply() {

        Boolean idCard = Convert.toBool(configService.selectConfigByKey(ConfigKeyConstants.USER_IDCARD_REGISTERED));
        Boolean video = Convert.toBool(configService.selectConfigByKey(ConfigKeyConstants.USER_VIDEO_REGISTERED));
        RequestContext.setParam("idCard", idCard);
        RequestContext.setParam("video", video);

        return idCard || video;
    }


    @Override
    @RedisLockOperate(key = "'t_user_registered_apply'+#username")
    public void registeredApply(String username,String realName,Long videoID,Long cardID,String captchaKey){

        Long userId = SecurityUtils.getLoginUser().getUserId();

        TUser tUser = memberService.selectMemberByUid(userId);
        Short verifiedRealName = tUser.getVerifiedRealName();
        Assert.test(Constants.DONE == verifiedRealName, "member.info.realNameVerify.done");
        Assert.test(registeredApplyService.countByUid(userId) > 0, "member.info.realNameVerify.verifying", 200);


        Boolean idCard = Convert.toBool(RequestContext.getParam("idCard"));
        Boolean video = Convert.toBool(RequestContext.getParam("video"));
        Assert.test(StringUtils.isBlank(realName),"member.info.realName");
        TUserRegisteredApply tUserRegisteredApply = new TUserRegisteredApply();
        tUserRegisteredApply.setUid(userId);
        tUserRegisteredApply.setComments(realName);
        tUserRegisteredApply.setAppTime(new Date());
        tUserRegisteredApply.setId(snowflakeIdUtils.nextId());
        tUserRegisteredApply.setUsername(username);
        tUserRegisteredApply.setStatus(Constants.APPLY_WAIT);
        if (video) {
            Assert.test(StringUtils.isBlank(captchaKey) || videoID == null, "params.error");

            String captcha = redisCache.getAndDelete("Captcha2Read:" + captchaKey);

            Assert.test(StringUtils.isBlank(captcha), "user.jcaptcha.expire", 8001);

            tUserRegisteredApply.setCaptchaContent(captcha);


            String s = MimeTypeUtils.UPFILE_VERIFYID_KEY + videoID;
            String videoPath = redisCache.getAndDelete(s);
            tUserRegisteredApply.setVideo(videoPath);
        } else {
            tUserRegisteredApply.setVideo("视频审核未开启");
        }

        if (idCard) {
            String s = configService.selectConfigByKey(ConfigKeyConstants.PIC_4_REAL_NAME);
            Integer integer = Convert.toInt(s, 0);
            Assert.test(cardID == null, "params.error");

            String photoCacheKey = MimeTypeUtils.UPFILE_VERIFYID_KEY + cardID;
            String idCardPath = redisCache.getAndDelete(photoCacheKey);
            tUserRegisteredApply.setIdCard(idCardPath + "," + integer);
        } else {
            tUserRegisteredApply.setIdCard("图片审核未开启");
        }

        registeredApplyService.insertTUserRegisteredApply(tUserRegisteredApply);
    }

    @Override
    public void memberLogin(LoginInfo loginInfo) {
        try {
            RequestContext.setParam("userType", Constants.USER_TYPE_PLAYER);
            String loginKey = "user:trylogin:" + loginInfo.getUsername();
            long incr = redisCache.incr(loginKey, 1);
            redisCache.expire(loginKey, 1800, TimeUnit.SECONDS);
            Assert.test(incr >= 5, "user.password.retry.limit.exceed", new Object[]{5});

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginInfo.getUsername(), loginInfo.getPassword()));

            redisCache.deleteObject(loginKey);
        } catch (Exception e) {
            if (e instanceof InternalAuthenticationServiceException) {
                Assert.error("member.login.authenError");
            } else if (e instanceof BadCredentialsException) {
                Assert.error("member.login.authenError");
            } else if (e instanceof ServiceException) {
                throw e;
            } else {
                log.error("memberLogin error", e);
                Assert.error("system.error");
            }
        }
    }

    @Override
    public AjaxResult checkUsername(String username) {
        String ipAddr = IpUtils.getIpAddr(ServletUtils.getRequest());
        return RedisLock.lockInOneSecondOperate("t_user:checkUsername:" + ipAddr, () -> {
            if (checkUsernameMethod(username)) {
                return AjaxResult.success(false);
            }
            return AjaxResult.success(true);
        });
    }


    private boolean checkUsernameMethod(String name) {
        TUser tUser = new TUser();
        tUser.setUsername(name);
        long l = memberService.count(tUser);
        return l != 0;
    }


    private boolean checkEmailMethod(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        TUser tUser = new TUser();
        tUser.setEmail(email);
        return memberService.count(tUser) != 0;
    }


    private boolean checkNickName(String nickName) {
        TUser tUser = new TUser();
        tUser.setNickname(nickName);
        long l = memberService.count(tUser);
        return l != 0;
    }


    @Override
    public AjaxResult modifyMemberPassword(ModifyPasswordVo modifyVo) {

        LoginUser loginUser = SecurityUtils.getLoginUser();

        TUser user = memberService.selectMemberByUid(loginUser.getUserId());

        ValidateUtils.validateEntity(modifyVo);

        return RedisLock.lockInOneSecondOperate("t_user:updatePassword:" + loginUser.getUserId(), () -> {

            String password = modifyVo.getPassword();

            Assert.test(!SecurityUtils.matchesPassword(password, user.getPassword()), "member.info.passwordNotMatch");

            Assert.test(password.equals(modifyVo.getNewPassword()), "member.info.passwordSame");

            Assert.test(modifyVo.getNewPassword().equals(loginUser.getUsername()), "member.info.passwordUsernameSame");


            TUser tUser = new TUser();
            tUser.setUid(loginUser.getUserId());
            tUser.setUsername(loginUser.getUsername());
            tUser.setPassword(SecurityUtils.encryptPassword(modifyVo.getNewPassword()));

            memberService.updateUser(tUser);
            return AjaxResult.success();

        });

    }

    @Override
    public AjaxResult modifyMemberPayPassword(ModifyPayPasswordVo modifyPayPasswordVo) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        ValidateUtils.validateEntity(modifyPayPasswordVo);

        TUser user = memberService.selectMemberByUid(loginUser.getUserId());
        return RedisLock.lockInOneSecondOperate("t_user:updatePayPassword:" + loginUser.getUserId(), () -> {
            //刚注册的账号,
            if (!"0".equals(user.getPayPassword())){

                Assert.test(StringUtils.isBlank(modifyPayPasswordVo.getPassword()),"member.info.oldPayPasswordEmpty");

                Assert.test(!SecurityUtils.matchesPassword(modifyPayPasswordVo.getPassword(),user.getPayPassword()),
                        "member.info.passwordNotMatch");
            }

            Assert.test(modifyPayPasswordVo.getPassword().equals(modifyPayPasswordVo.getPayPassword()),
                    "member.info.payPasswordSame");

            Assert.test(SecurityUtils.matchesPassword(modifyPayPasswordVo.getPayPassword(), user.getPassword()),
                    "member.info.payPasswordSame");

            Assert.test(modifyPayPasswordVo.getPayPassword().equals(loginUser.getUsername()),
                    "member.info.passwordUsernameSame");


            TUser tUser = new TUser();
            tUser.setUid(loginUser.getUserId());
            tUser.setUsername(loginUser.getUsername());
            tUser.setPayPassword(SecurityUtils.encryptPassword(modifyPayPasswordVo.getPayPassword()));
            memberService.updateUser(tUser);

            return AjaxResult.success();
        });
    }

    @Override
    public AjaxResult getInfo() {
        LoginMember loginUser = (LoginMember) SecurityUtils.getLoginUser();
        TUser user = loginUser.gettUser();
        TUserDataConfig tUserDataConfig = userDataConfigService.queryByUid(loginUser.getUserId());
        int userConf = parseUserConf(tUserDataConfig);

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("username",user.getUsername());
        hashMap.put("name",user.getNickname());
        hashMap.put("photo",user.getPhoto());
        hashMap.put("email",user.getEmail()==null?"":user.getEmail());
        hashMap.put("userLevel",user.getUserLevel());
        hashMap.put("realName",user.getRealname());
        hashMap.put("realNameVerify",user.getVerifiedRealName());
        Wallet wallet = walletService.selectWalletByUid(loginUser.getUserId());
        hashMap.put("availableBalance",wallet==null ? 0.00f : wallet.getBalance());
        hashMap.put("effectiveAmount",wallet==null?0.0f:wallet.getEffectiveBal());
        hashMap.put("totalBalance", 0.00f );
        hashMap.put("tradingBalance", 0.00f );
        hashMap.put("address",wallet==null?"":wallet.getAddress());
        hashMap.put("userConf",userConf);
        hashMap.put("invite",user.getUrl());
        hashMap.put("needInitPayPassword","0".equals(user.getPayPassword()));
        return AjaxResult.success().put("info",hashMap);
    }

    private int parseUserConf(TUserDataConfig tUserDataConfig) {
//        if (tUserDataConfig.getEmailForSell() == 1 && tUserDataConfig.getPayPasswordForSell() == 0) {
//            return 1;
//        } else if (tUserDataConfig.getPayPasswordForSell() == 1 && tUserDataConfig.getEmailForSell() == 0) {
//            return 2;
//        } else if (tUserDataConfig.getEmailForSell() == 1 && tUserDataConfig.getPayPasswordForSell() == 1) {
//            return 3;
//        }

        return 2;
    }

    @Override
    public List<Map<String, Object>> getChildList() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return RedisLock.lockInOneSecondOperate("Redis:getChildList:" + loginUser.getUserId(),
                () -> tUserMapper.selectChildByPid(loginUser.getUserId()));

    }

    @Override
    public AjaxResult verified(String realName,Long cardId, Long videoId,String captchaKey) {
        TUser tUser = tUserMapper.selectTUserByUid(SecurityUtils.getUserId());

        if (StringUtils.isNotBlank(tUser.getRealname())){
            realName=tUser.getRealname();
        }

        Assert.test(tUser.getVerifiedRealName()==1,"member.info.registered.has.done");


        Assert.test(!needToApply(),"member.info.registered.apply.off");

        String username = SecurityUtils.getLoginUser().getUsername();

        ITUserService proxy = (ITUserService)AopContext.currentProxy();
        proxy.registeredApply(username,realName,videoId,cardId,captchaKey);

        return AjaxResult.success(MessageUtils.message("member.info.registered.apply.success"));
    }


    //===============================================后台操作开始================================================================================================================================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedisLockOperate
    public AjaxResult registeredChildMember(RegisteredChildVo registeredChildVo) {
        ValidateUtils.validateEntity(registeredChildVo);
        String username = registeredChildVo.getUsername();
        String password = registeredChildVo.getPassword();

        Assert.test(StringUtils.isBlank(username) || StringUtils.isBlank(password),
                "member.info.notComplete");

        Assert.test(checkUsernameMethod(username), "member.info.username.exist");


        TUser user = new TUser();
        user.setUsername(username);
        user.setRealname(registeredChildVo.getRealName());
        user.setDisabled(1L);
        user.setEmail("");
        user.setSalt(Constants.REGISTER_FRONT);
        user.setTelephone("");
        user.setPassword(SecurityUtils.encryptPassword(password));
        user.setPayPassword(SecurityUtils.encryptPassword(password));
        user.setUid(snowflakeIdUtils.nextId());
        user.setUserLevel(0L);
        user.setPid(SecurityUtils.getUserId());
        user.setPidArray(SecurityUtils.getUsername());
        user.setCreateTime(new Date());
        user.setPhoto(defaultPhoto());

        uniqueCode(user, true);
        int i = tUserMapper.insertTUser(user);
        if (i == 1) {
            insertWallet(user);
            insertUserData(user);
            registerComplete(user);
        }
        return AjaxResult.success();
    }


    /**
     * 查询用户
     *
     * @param uid 用户主键
     * @return 用户
     */
    @Override
    public TUser selectTUserByUid(Long uid) {
        return tUserMapper.selectTUserByUid(uid);
    }

    /**
     * 查询用户列表
     *
     * @param tUser 用户
     * @return 用户
     */
    @Override
    public List<TUser> selectTUserList(TUser tUser) {
        return tUserMapper.selectTUserList(tUser);
    }

    /**
     * 新增用户
     *
     * @param tUser 用户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTUser(TUser tUser) {
        ValidateUtils.validateEntity(tUser);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return RedisLock.lockInOneSecondOperate("t_user:insertBySystemUser:" + loginUser.getUserId(), () -> {
            Assert.isTrue(tUser.getUsername().equals(tUser.getPassword()) || tUser.getUsername().equals(tUser.getPayPassword())
                    , "账号不得和密码或支付密码相同");

            TUser user = new TUser();
            user.setUsername(tUser.getUsername());
            long count = tUserMapper.count(user);

            Assert.isTrue(count > 0, "账号已经存在");


            user.setUsername(null);
            user.setNickname(tUser.getNickname());
            count = tUserMapper.count(user);

            Assert.isTrue(count > 0, "昵称已经存在");

            tUser.setUid(snowflakeIdUtils.nextId());
            tUser.setPassword(SecurityUtils.encryptPassword(tUser.getPassword()));
            tUser.setSalt(Constants.REGISTER_BACK);
            if (StringUtils.isBlank(tUser.getPayPassword())) {
                tUser.setPayPassword(SecurityUtils.encryptPassword(tUser.getPassword()));
            } else {
                tUser.setPayPassword(SecurityUtils.encryptPassword(tUser.getPayPassword()));
            }
            uniqueCode(tUser, false);
            tUser.setUserLevel(0L);
            insertWallet(tUser);
            insertUserData(tUser);
            return tUserMapper.insertTUser(tUser);
        });
    }

    private void insertWallet(TUser user) {
        Wallet wallet = new Wallet();
        wallet.setUid(user.getUid());
        wallet.setName(user.getUsername());
        wallet.setType(0L);
        iWalletService.insertWallet(wallet);
    }

    /**
     * 修改用户
     *
     * @param tUser 用户
     * @return 结果
     */
    @Override
    public int updateTUser(TUser tUser) {

        TUser tUserRecord = memberService.selectMemberByUid(tUser.getUid());

        tUser.setUsername(null);
        tUser.setRealname(null);
        if (!StringUtils.isBlank(tUser.getPassword())) {
            Assert.test(!CheckPatternUtils.match(tUser.getPassword(), Constant.PASSWORD_PATTEN),
                    "member.info.passwordPatternError");

            tUser.setPassword(SecurityUtils.encryptPassword(tUser.getPassword()));
            RequestContext.setParam("userEditType", "password");
        }
        if (!StringUtils.isBlank(tUser.getPayPassword())) {

            Assert.test(!CheckPatternUtils.match(tUser.getPayPassword(), Constant.PASSWORD_PATTEN),
                    "member.info.passwordPatternError");

            tUser.setPayPassword(SecurityUtils.encryptPassword(tUser.getPayPassword()));
            RequestContext.setParam("userEditType", "payPassword");
        }


        tUser.setUsername(tUserRecord.getUsername());
        memberService.updateUser(tUser);
        return 1;
    }

    /**
     * 批量删除用户
     *
     * @param uids 需要删除的用户主键
     * @return 结果
     */
    @Override
    public int deleteTUserByUids(Long[] uids) {
        return tUserMapper.deleteTUserByUids(uids);
    }

    /**
     * 删除用户信息
     *
     * @param uid 用户主键
     * @return 结果
     */
    @Override
    public int deleteTUserByUid(Long uid) {
        return tUserMapper.deleteTUserByUid(uid);
    }

    @Override
    public AjaxResult verifiedConfig() {
        String s = configService.selectConfigByKey(ConfigKeyConstants.PIC_4_REAL_NAME);
        Integer integer = Convert.toInt(s, 0);
        return AjaxResult.success().put("verifiedImgType", integer);
    }

    @Override
    public TUser selectByName(String name) {
        return tUserMapper.selectUserByUsername(name);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TUser generatorApiUser(String username,Map<ApiAuthKey,Object> data) {


        String merchantNumber = (String)data.get(ApiAuthKey.MERCHANT_NUMBER);
        TUser tUser = new TUser();
        tUser.setUid(snowflakeIdUtils.nextId());
        tUser.setCreateTime(new Date());
        tUser.setUsername(StringUtils.apiUsername(username,merchantNumber));
        tUser.setPassword(SecurityUtils.encryptPassword(StringUtils.apiPassword(username,merchantNumber)));
        tUser.setPayPassword("0");
        tUser.setSalt(Constants.REGISTER_API);
        tUser.setPhoto(defaultPhoto());
        tUser.setDisabled(1L);
        tUser.setUserLevel(0L);
        String realName = (String) data.get(ApiAuthKey.REAL_NAME);
        if(StringUtils.isNotBlank(realName)){
            tUser.setRealname(realName);
            tUser.setVerifiedRealName((short)1);
        }

        tUser.setMerchantId((Long) data.get(ApiAuthKey.MERCHANT_ID));

        uniqueCode(tUser,true);

        int i = tUserMapper.insertTUser(tUser);
        if (i==1){
            insertWallet(tUser);
            insertUserData(tUser);
        }

        return tUser;
    }


}

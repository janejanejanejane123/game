package com.ruoyi.member.service.impl;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.core.domain.model.member.TUserCredit;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.redis.RedisLock;
import com.ruoyi.common.enums.EmailEnums;
import com.ruoyi.common.enums.UserInfoApplyEnums;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.member.mapper.TUserCreditMapper;
import com.ruoyi.member.service.ITUserCreditService;
import com.ruoyi.member.service.ITUserInfoApplyService;
import com.ruoyi.member.vo.CreditVo;
import com.ruoyi.system.service.IMemberService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2022-03-27
 */
@Service
public class TUserCreditServiceImpl implements ITUserCreditService  {

    @Resource
    private TUserCreditMapper tUserCreditMapper;
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;
    @Resource
    private ITUserInfoApplyService userInfoApplyService;
    @Resource
    private IMemberService memberService;
    @Resource
    private RedisCache redisCache;



    @Override
    public AjaxResult addCredit(CreditVo creditVo) {
        ValidateUtils.validateEntity(creditVo);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();

        TUser tUser = memberService.selectMemberByUid(userId);

        Assert.test(!(loginUser instanceof LoginMember),"system.error");


        Assert.test(StringUtils.isBlank(tUser.getRealname()),"credit.realName.must");

        checkPayPassword(tUser.getPayPassword());

        Assert.test(!SecurityUtils.matchesPassword(creditVo.getPayPassword(),tUser.getPayPassword()),
                "credit.payPassword.notMatch");

//        String cacheVerify= redisCache.getCacheObject(EmailEnums.ADDCARD.redisKey(userId));
//
//        Assert.test(!creditVo.getVerify().equals(cacheVerify),"email.verify.not.match");




        return RedisLock.lockInOneSecondOperate("t_user_credit:addCredit:"+userId,()->{
            TUserCredit userCredit = tUserCreditMapper.selectCreditByContent(creditVo.getContent());
            if (userCredit!=null){

                Assert.test(!userCredit.getUid().equals(userId),"credit.exist");

                Assert.test(!userCredit.getIsDelete().equals(Constants.CREDIT_DELETE),"credit.repeat");

                userCredit.setIsDelete(Constants.CREDIT_ON_USE);
                tUserCreditMapper.updateTUserCredit(userCredit);
                return AjaxResult.success();

            }


            TUserCredit tUserCredit = new TUserCredit();
            tUserCredit.setContent(creditVo.getContent());
            tUserCredit.setType((long) Constants.CREDIT_TYPE_CARD);
            tUserCredit.setAddTime(new Date());
            tUserCredit.setId(snowflakeIdUtils.nextId());
            tUserCredit.setCreditAddress(creditVo.getCreditAddress());
            tUserCredit.setCreditName(tUser.getRealname());
            tUserCredit.setIsDelete(Constants.CREDIT_ON_USE);
            tUserCredit.setCreditBank(creditVo.getCreditBank());
            tUserCredit.setUid(userId);

            tUserCreditMapper.insertTUserCredit(tUserCredit);
            return AjaxResult.success();
        });
    }

    @Override
    public AjaxResult deleteCredit(CreditVo vo) {




        LoginUser loginUser = SecurityUtils.getLoginUser();

        Long userId = loginUser.getUserId();


        TUser tUser = memberService.selectMemberByUid(userId);

        TUserCredit tUserCredit = new TUserCredit();
        tUserCredit.setUid(userId);
        tUserCredit.setIsDelete(1L);




        List<TUserCredit> tUserCredits = tUserCreditMapper.selectTUserCreditList(tUserCredit);
        Optional<TUserCredit> first = tUserCredits.stream().filter(credit -> credit.getId().equals(vo.getId())).findFirst();
        TUserCredit userCredit=null;
        Assert.test(!first.isPresent(),"credit.not.match");

        userCredit = first.get();

        if (isbankCard(userCredit)){
//            String cacheVerify = redisCache.getCacheObject(EmailEnums.DELETECREDIT.redisKey(userId));
//            Assert.test(!vo.getVerify().equals(cacheVerify),"captcha.wrong.time");
            long count = tUserCredits.stream()
                    .filter(credit -> credit.getType()==Constants.CREDIT_TYPE_CARD).count();
            Assert.test(count==1,"credit.last.one");
            tUserCredit.setId(userCredit.getId());
            tUserCredit.setIsDelete(Constants.CREDIT_DELETE);
            tUserCreditMapper.updateTUserCredit(tUserCredit);
        }else if (isVxOrZFB(userCredit)){

            Assert.test(!SecurityUtils.matchesPassword(vo.getPayPassword(),tUser.getPayPassword()),
                    "credit.payPassword.notMatch");
            tUserCreditMapper.deleteTUserCreditById(userCredit.getId());
//            userCredit.setIsDelete(Constants.CREDIT_DELETE);

        }else {
            Assert.error("system.error");
        }

        return AjaxResult.success();
    }

    @Override
    public AjaxResult list() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        return RedisLock.lockInOneSecondOperate("t_user_credit:list:"+userId,()->{
            TUserCredit tUserCredit = new TUserCredit();
            tUserCredit.setUid(userId);
            tUserCredit.setIsDelete(Constants.CREDIT_ON_USE);
            List<TUserCredit> tUserCredits = tUserCreditMapper.selectTUserCreditList(tUserCredit);
            return AjaxResult.success().put("list",tUserCredits);
        });

    }




    private boolean isVxOrZFB(TUserCredit userCredit) {
        return Constants.CREDIT_TYPE_VX==userCredit.getType()
                ||Constants.CREDIT_TYPE_ZFB==userCredit.getType()
                ||Constants.CREDIT_TYPE_QQ==userCredit.getType()
                ||Constants.CREDIT_TYPE_ZFBA==userCredit.getType();
    }

    private boolean isbankCard(TUserCredit userCredit) {
        return Constants.CREDIT_TYPE_CARD==userCredit.getType();
    }


    private void checkPayPassword(String payPassword){
        Assert.test("0".equals(payPassword),"credit.payPassword.notSet");
    }

    @Override
    public AjaxResult applyCredit(CreditVo creditVo) {
        Long userId = SecurityUtils.getLoginUser().getUserId();

        TUser tUser = memberService.selectMemberByUid(userId);
        String encPassword = tUser.getPayPassword();

        Assert.test(StringUtils.isBlank(creditVo.getRealName()),"credit.realName.must");

        String payPassword = creditVo.getPayPassword();

        checkPayPassword(tUser.getPayPassword());

        Assert.test(!SecurityUtils.matchesPassword(payPassword,encPassword),"credit.payPassword.notMatch");

        short creditType = creditVo.getCreditType();
        UserInfoApplyEnums enums=null;
        String cont;
        if (creditType== Constants.CREDIT_TYPE_ZFBA) {
            enums=UserInfoApplyEnums.ZFB_ACOUNT;
            cont=creditVo.getContent();
        }else {
            cont = redisCache.getAndDelete(MimeTypeUtils.UPFILE_VERIFYID_KEY + creditVo.getContent());

            Assert.test(StringUtils.isBlank(cont),"system.error");

            if (creditType==Constants.CREDIT_TYPE_VX){
                enums=UserInfoApplyEnums.VX_QR_CODE;
            }else if (creditType==Constants.CREDIT_TYPE_ZFB){
                enums=UserInfoApplyEnums.ZFB_QR_CODE;
            }else if (creditType==Constants.CREDIT_TYPE_QQ){
                enums=UserInfoApplyEnums.QQ_QR_CODE;
            }else {
                Assert.error("system.error");
            }
        }
        userInfoApplyService.apply2Check(cont+";"+creditVo.getRealName(),enums);
        return AjaxResult.success(MessageUtils.message("success"));
    }

    @Override
    public TUserCredit SelectById(Long id) {
        return tUserCreditMapper.selectTUserCreditById(id);
    }

    @Override
    public List<TUserCredit> listed(TUserCredit credit) {
        if (credit.getUid()==null){
            throw new ServiceException("参数错误");
        }
        return tUserCreditMapper.selectTUserCreditList(credit);
    }

    @Override
    public AjaxResult delete(Long id) {
        tUserCreditMapper.deleteTUserCreditById(id);
        return AjaxResult.success();
    }
}

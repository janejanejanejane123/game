package com.ruoyi.member.service.impl;

import com.ruoyi.common.bussiness.constants.enums.TopicEnum;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.member.TUserCredit;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.redis.RedisLock;
import com.ruoyi.common.enums.UserInfoApplyEnums;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.member.domain.TUserInfoApply;
import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.member.mapper.TUserCreditMapper;
import com.ruoyi.member.mapper.TUserInfoApplyMapper;
import com.ruoyi.member.service.ITUserInfoApplyService;
import com.ruoyi.member.util.RocketMqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 用户资料申请与审核Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-03-28
 */
@Service
public class TUserInfoApplyServiceImpl implements ITUserInfoApplyService
{
    @Autowired
    private TUserInfoApplyMapper tUserInfoApplyMapper;
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;
    @Resource
    private TUserCreditMapper tUserCreditMapper;
    @Resource
    private RocketMqService rocketMqService;
    @Resource
    private RedisCache redisCache;

    /**
     * 查询用户资料申请与审核
     * 
     * @param id 用户资料申请与审核主键
     * @return 用户资料申请与审核
     */
    @Override
    public TUserInfoApply selectTUserInfoApplyById(Long id)
    {
        return tUserInfoApplyMapper.selectTUserInfoApplyById(id);
    }

    /**
     * 查询用户资料申请与审核列表
     * 
     * @param tUserInfoApply 用户资料申请与审核
     * @return 用户资料申请与审核
     */
    @Override
    public List<TUserInfoApply> selectTUserInfoApplyList(TUserInfoApply tUserInfoApply)
    {
        //tUserInfoApply.setStatus(Constants.APPLY_WAIT);
        return tUserInfoApplyMapper.selectTUserInfoApplyList(tUserInfoApply);
    }

    /**
     * 新增用户资料申请与审核
     * 
     * @param tUserInfoApply 用户资料申请与审核
     * @return 结果
     */
    @Override
    public int insertTUserInfoApply(TUserInfoApply tUserInfoApply)
    {
        return tUserInfoApplyMapper.insertTUserInfoApply(tUserInfoApply);
    }

    /**
     * 修改用户资料申请与审核
     * 
     * @param tUserInfoApply 用户资料申请与审核
     * @return 结果
     */
    @Override
    public int updateTUserInfoApply(TUserInfoApply tUserInfoApply)
    {
        TUserInfoApply record= tUserInfoApplyMapper.selectTUserInfoApplyById(tUserInfoApply.getId());

        tUserInfoApply.setUpSysUsername(SecurityUtils.getUsername());
        tUserInfoApply.setUpTime(new Date());

        int i = tUserInfoApplyMapper.updateTUserInfoApply(tUserInfoApply);
        if (i==1){
            String code="message.mq.refuse";
            if (Constants.APPLY_PASS.equals(tUserInfoApply.getStatus())){
                code="message.mq.pass";
                String[] split = record.getContent().split(";");
                TUserCredit tUserCredit = new TUserCredit();
                tUserCredit.setType(record.getType().longValue());
                tUserCredit.setUid(record.getUid());
                tUserCredit.setContent(split[0]);
                String bank="";
                if (record.getType()==Constants.CREDIT_TYPE_VX){
                    bank="微信收款码";
                }else if (record.getType()==Constants.CREDIT_TYPE_ZFB){
                    bank="支付宝收款码";
                }else if(record.getType()==Constants.CREDIT_TYPE_QQ){
                    bank="QQ收款码";
                }else if (record.getType()==Constants.CREDIT_TYPE_ZFBA) {
                    bank="支付宝账号";
                }else {
                    Assert.error("system.error");
                }
                tUserCredit.setCreditBank(bank);
                tUserCredit.setCreditName(split[1]);
                tUserCredit.setCreditAddress(split[1]);
                tUserCredit.setIsDelete(Constants.CREDIT_ON_USE);
                tUserCredit.setId(snowflakeIdUtils.nextId());
                tUserCredit.setAddTime(new Date());
                tUserCreditMapper.insertTUserCredit(tUserCredit);
            }
            long decr = redisCache.decr(UserInfoApplyEnums.VX_QR_CODE.getRedisKey(), 1);
            rocketMqService.applyToAdmin(UserInfoApplyEnums.VX_QR_CODE,decr);
            rocketMqService.applyComplete(code,record.getUid(),"message.type."+record.getType(),TopicEnum.APPLYCOMPLETE);
        }
        return i;
    }

    /**
     * 批量删除用户资料申请与审核
     * 
     * @param ids 需要删除的用户资料申请与审核主键
     * @return 结果
     */
    @Override
    public int deleteTUserInfoApplyByIds(Long[] ids)
    {
        return tUserInfoApplyMapper.deleteTUserInfoApplyByIds(ids);
    }

    /**
     * 删除用户资料申请与审核信息
     * 
     * @param id 用户资料申请与审核主键
     * @return 结果
     */
    @Override
    public int deleteTUserInfoApplyById(Long id)
    {
        return tUserInfoApplyMapper.deleteTUserInfoApplyById(id);
    }

    /**
     * 前台用户申请审核;
     *
     */
    @Override
    public void apply2Check(String content, UserInfoApplyEnums infoApplyEnums) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        RedisLock.lockInOneSecondOperate("t_user_info_apply:apply2check:"+userId,()->{
            preCheck(userId,infoApplyEnums);

            TUserInfoApply tUserInfoApply = new TUserInfoApply();


            tUserInfoApply.setContent(content);
            tUserInfoApply.setId(snowflakeIdUtils.nextId());
            tUserInfoApply.setAppTime(new Date());
            tUserInfoApply.setUsername(loginUser.getUsername());
            tUserInfoApply.setType(infoApplyEnums.getType());
            tUserInfoApply.setUid(userId);
            tUserInfoApply.setStatus((short) 0);
            tUserInfoApplyMapper.insertTUserInfoApply(tUserInfoApply);
            long incr = redisCache.incr(UserInfoApplyEnums.VX_QR_CODE.getRedisKey(), 1);
            rocketMqService.applyToAdmin(UserInfoApplyEnums.VX_QR_CODE,incr);
            return null;
        });




    }

    private void preCheck(Long userId, UserInfoApplyEnums infoApplyEnums) {
        TUserInfoApply tUserInfoApply = new TUserInfoApply();
        tUserInfoApply.setUid(userId);
        tUserInfoApply.setType(infoApplyEnums.getType());
        tUserInfoApply.setStatus((short)0);
        Assert.test(tUserInfoApplyMapper.countByCause(tUserInfoApply) >= infoApplyEnums.getMulti()
        ,"multi.applay.info");
    }
}

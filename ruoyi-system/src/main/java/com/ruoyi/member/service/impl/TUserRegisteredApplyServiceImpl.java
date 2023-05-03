package com.ruoyi.member.service.impl;

import com.ruoyi.common.bussiness.constants.enums.TopicEnum;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.EmailEnums;
import com.ruoyi.common.enums.UserInfoApplyEnums;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.member.domain.TUserRegisteredApply;
import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.member.mapper.TUserMapper;
import com.ruoyi.member.mapper.TUserRegisteredApplyMapper;
import com.ruoyi.member.service.ITEmailConfigService;
import com.ruoyi.member.service.ITUserRegisteredApplyService;
import com.ruoyi.member.util.RocketMqService;
import com.ruoyi.system.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户注册申请Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-03-29
 */
@Service
public class TUserRegisteredApplyServiceImpl implements ITUserRegisteredApplyService
{
    @Autowired
    private TUserRegisteredApplyMapper tUserRegisteredApplyMapper;
    @Resource
    private IMemberService memberService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private RocketMqService rocketMqService;

    @Override
    public long countByUid(Long userId) {

        return tUserRegisteredApplyMapper.countByUid(userId);
    }

    /**
     * 查询用户注册申请
     * 
     * @param id 用户注册申请主键
     * @return 用户注册申请
     */
    @Override
    public TUserRegisteredApply selectTUserRegisteredApplyById(Long id)
    {
        return tUserRegisteredApplyMapper.selectTUserRegisteredApplyById(id);
    }

    /**
     * 查询用户注册申请列表
     * 
     * @param tUserRegisteredApply 用户注册申请
     * @return 用户注册申请
     */
    @Override
    public List<TUserRegisteredApply> selectTUserRegisteredApplyList(TUserRegisteredApply tUserRegisteredApply)
    {
//        tUserRegisteredApply.setStatus(Constants.APPLY_WAIT);
        return tUserRegisteredApplyMapper.selectTUserRegisteredApplyList(tUserRegisteredApply);
    }

    /**
     * 新增用户注册申请
     * 
     * @param tUserRegisteredApply 用户注册申请
     * @return 结果
     */
    @Override
    public int insertTUserRegisteredApply(TUserRegisteredApply tUserRegisteredApply)
    {
        int i = tUserRegisteredApplyMapper.insertTUserRegisteredApply(tUserRegisteredApply);
        if (i==1){
            long data=redisCache.incr(UserInfoApplyEnums.REAL_NAME_VERIFY.getRedisKey(),1);
            rocketMqService.applyToAdmin(UserInfoApplyEnums.REAL_NAME_VERIFY,data);
        }
        return i;
    }

    /**
     * 修改用户注册申请
     * 
     * @param tUserRegisteredApply 用户注册申请
     * @return 结果
     */
    @Override
    public int updateTUserRegisteredApply(TUserRegisteredApply tUserRegisteredApply)
    {
        TUserRegisteredApply record = tUserRegisteredApplyMapper.selectTUserRegisteredApplyById(tUserRegisteredApply.getId());
        TUserRegisteredApply apply = new TUserRegisteredApply();
        apply.setStatus(tUserRegisteredApply.getStatus());
        apply.setId(tUserRegisteredApply.getId());
        apply.setUpTime(new Date());
        apply.setUpSysUser(SecurityUtils.getUsername());
        int i = tUserRegisteredApplyMapper.updateTUserRegisteredApply(apply);
        if (i==1){
            String code="message.mq.refuse";
            if (Constants.APPLY_PASS.equals(apply.getStatus())) {
                code="message.mq.pass";
                TUser user = new TUser();
                user.setUid(record.getUid());
                user.setVerifiedRealName((short)1);
                user.setRealname(record.getComments());
                memberService.updateUser(user);
            }
            long decr = redisCache.decr(UserInfoApplyEnums.REAL_NAME_VERIFY.getRedisKey(), 1);
            rocketMqService.applyToAdmin(UserInfoApplyEnums.REAL_NAME_VERIFY,decr);
            rocketMqService.applyComplete(code,record.getUid(),"message.type.realnameverify",TopicEnum.VERIFIEDCOMPLETE);
        }
        return i;
    }

    /**
     * 批量删除用户注册申请
     * 
     * @param ids 需要删除的用户注册申请主键
     * @return 结果
     */
    @Override
    public int deleteTUserRegisteredApplyByIds(Long[] ids)
    {
        return tUserRegisteredApplyMapper.deleteTUserRegisteredApplyByIds(ids);
    }

    /**
     * 删除用户注册申请信息
     * 
     * @param id 用户注册申请主键
     * @return 结果
     */
    @Override
    public int deleteTUserRegisteredApplyById(Long id)
    {
        return tUserRegisteredApplyMapper.deleteTUserRegisteredApplyById(id);
    }
}

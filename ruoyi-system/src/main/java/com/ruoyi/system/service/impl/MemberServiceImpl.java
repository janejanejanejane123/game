package com.ruoyi.system.service.impl;

import com.ruoyi.common.annotation.Overtime;
import com.ruoyi.common.bussiness.RequestContext;
import com.ruoyi.common.core.domain.model.member.TUserVo;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.NumberUtils;
import com.ruoyi.common.utils.chat.GeneralUtils;
import com.ruoyi.member.mapper.TUserMapper;
import com.ruoyi.system.service.IMemberService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/21,11:21
 * @return:
 **/
@Service("memberService")
public class MemberServiceImpl implements IMemberService {

    @Resource
    private TUserMapper tUserMapper;

    @Resource
    private RedisCache redisCache;

    @Override
    @Cacheable(unless = "#result == null",key = "'t_user:queryApiMemberByUsername:'+#apiUsername",value = "redisCache4Spring")
    @Overtime(3600)
    public TUser queryApiMemberByUsername(String apiUsername) {
        return tUserMapper.selectApiUserByUsername(apiUsername);
    }

    @Override
    @Cacheable(unless = "#result == null",key = "'t_user:queryMemberByUsername:'+#username",value = "redisCache4Spring")
    @Overtime(3600)
    public TUser queryMemberByUsername(String username) {
        return tUserMapper.selectUserByUsername(username);
    }

    @Override
    @Cacheable(unless = "#result == null",key = "'t_user:queryMemberByUid:'+#uid",value = "redisCache4Spring")
    @Overtime(3600)
    public TUser selectMemberByUid(Long uid) {
        return tUserMapper.selectTUserByUid(uid);
    }

    @Override
    public List<TUser> queryAllUsers() {
        return tUserMapper.selectTUserList(new TUserVo());
    }

    @Override
    public TUser queryMemberByInvite(String invite) {
        Long aLong = NumberUtils.defS2l(invite);
        TUser tUser = new TUser();
        tUser.setUniqueCode(aLong);
        List<TUser> tUsers = tUserMapper.selectTUserList(tUser);
        if (tUsers==null||tUsers.size()==0){
            return null;
        }
        return tUsers.get(0);
    }

    @Override
    public Long count(TUser tUser) {
        return tUserMapper.count(tUser);
    }

    @Override
    public int insert(TUser tUser) {
        return tUserMapper.insertTUser(tUser);
    }

    @Override
    public void updateUser(TUser tUser) {
        int i = tUserMapper.updateTUser(tUser);
        if (i==1){
            redisCache.deleteObject("t_user:queryMemberByUsername:"+tUser.getUsername());
            redisCache.deleteObject("t_user:queryApiMemberByUsername:"+tUser.getUsername());
            redisCache.deleteObject("t_user:queryMemberByUid:"+tUser.getUid());
            if (tUser.getDisabled()!=null&&tUser.getDisabled()==0L){
                //踢人下线
                GeneralUtils.loginCacheHandler(false).kickLoginUser(tUser.getUid());
            }else {
                //更新登录缓存
                tUser.setUsername(null);
                GeneralUtils.loginCacheHandler(false).updateStream(tUser.getUid())
                        .updateAllFieldsFromSource(tUser).flush();
            }
            if ("password".equals(RequestContext.getParam("userEditType"))){
                redisCache.deleteObject("user:trylogin:"+tUser.getUsername());
            }

        }else {
            Assert.error("system.error");
        }

    }
}

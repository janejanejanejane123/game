package com.ruoyi.member.service.impl;

import java.util.Date;
import java.util.List;

import com.ruoyi.common.annotation.Overtime;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ValidateUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.member.service.ITUserService;
import com.ruoyi.system.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.ruoyi.member.mapper.TUserNicknameBlackListMapper;
import com.ruoyi.member.domain.TUserNicknameBlackList;
import com.ruoyi.member.service.ITUserNicknameBlackListService;

import javax.annotation.Resource;

/**
 * 用户昵称黑名单Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-08-09
 */
@Service
public class TUserNicknameBlackListServiceImpl implements ITUserNicknameBlackListService 
{
    @Autowired
    private TUserNicknameBlackListMapper tUserNicknameBlackListMapper;
    @Resource
    private IMemberService memberService;
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;
    @Resource
    private RedisCache redisCache;

    /**
     * 查询用户昵称黑名单
     * 
     * @param id 用户昵称黑名单主键
     * @return 用户昵称黑名单
     */
    @Override
    public TUserNicknameBlackList selectTUserNicknameBlackListById(Long id)
    {
        return tUserNicknameBlackListMapper.selectTUserNicknameBlackListById(id);
    }

    /**
     * 查询用户昵称黑名单列表
     * 
     * @param tUserNicknameBlackList 用户昵称黑名单
     * @return 用户昵称黑名单
     */
    @Override
    public List<TUserNicknameBlackList> selectTUserNicknameBlackListList(TUserNicknameBlackList tUserNicknameBlackList)
    {
        return tUserNicknameBlackListMapper.selectTUserNicknameBlackListList(tUserNicknameBlackList);
    }

    /**
     * 新增用户昵称黑名单
     * 
     * @param tUserNicknameBlackList 用户昵称黑名单
     * @return 结果
     */
    @Override
    @CacheEvict(key = "'nickname:black:'+#tUserNicknameBlackList.username",value = "redisCache4Spring")
    public int insertTUserNicknameBlackList(TUserNicknameBlackList tUserNicknameBlackList)
    {
        ValidateUtils.validateEntity(tUserNicknameBlackList);
        String username = tUserNicknameBlackList.getUsername();

        TUser tUser = memberService.queryMemberByUsername(username);
        Assert.test(tUser==null,"admin.user.notFound");


        tUserNicknameBlackList.setId(snowflakeIdUtils.nextId());
        tUserNicknameBlackList.setAddAdmin(SecurityUtils.getUsername());
        tUserNicknameBlackList.setAddTime(new Date());
        tUserNicknameBlackList.setNickname(tUser.getNickname());
        tUserNicknameBlackList.setUid(tUser.getUid());

        return tUserNicknameBlackListMapper.insertTUserNicknameBlackList(tUserNicknameBlackList);
    }

    /**
     * 修改用户昵称黑名单
     * 
     * @param tUserNicknameBlackList 用户昵称黑名单
     * @return 结果
     */
    @Override
    public int updateTUserNicknameBlackList(TUserNicknameBlackList tUserNicknameBlackList)
    {
        TUserNicknameBlackList record = new TUserNicknameBlackList();
        record.setId(tUserNicknameBlackList.getId());
        record.setComment(tUserNicknameBlackList.getComment());
        return tUserNicknameBlackListMapper.updateTUserNicknameBlackList(record);
    }

    /**
     * 批量删除用户昵称黑名单
     * 
     * @param ids 需要删除的用户昵称黑名单主键
     * @return 结果
     */
    @Override
    public int deleteTUserNicknameBlackListByIds(Long[] ids)
    {
        return tUserNicknameBlackListMapper.deleteTUserNicknameBlackListByIds(ids);

    }

    /**
     * 删除用户昵称黑名单信息
     * 
     * @param id 用户昵称黑名单主键
     * @return 结果
     */
    @Override
    public int deleteTUserNicknameBlackListById(Long id)
    {
        TUserNicknameBlackList tUserNicknameBlackList = tUserNicknameBlackListMapper.selectTUserNicknameBlackListById(id);
        int i=0;
        if (tUserNicknameBlackList!=null){
            i = tUserNicknameBlackListMapper.deleteTUserNicknameBlackListById(id);
            if (i>0){
                String username = tUserNicknameBlackList.getUsername();
                redisCache.deleteObject("nickname:black:"+username);
            }
        }
        return i;
    }

    @Override
    @Cacheable(key = "'nickname:black:'+#member.username",value = "redisCache4Spring")
    @Overtime(24*3600)
    public boolean checkBlackList(LoginMember member) {
        Long userId = member.getUserId();
        long i= tUserNicknameBlackListMapper.count(userId);
        return i > 0;
    }
}

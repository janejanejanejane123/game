package com.ruoyi.member.service.impl;

import com.ruoyi.common.annotation.Overtime;
import com.ruoyi.common.annotation.RedisLockOperate;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.member.domain.TUserDataConfig;
import com.ruoyi.member.mapper.TUserDataConfigMapper;
import com.ruoyi.member.mapper.TUserMapper;
import com.ruoyi.member.service.ITUserDataConfigService;
import com.ruoyi.member.vo.UserDataConfVo;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户自我配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-06-24
 */
@Service
public class TUserDataConfigServiceImpl implements ITUserDataConfigService 
{
    @Autowired
    private TUserDataConfigMapper tUserDataConfigMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    private TUserMapper tUserMapper;


    @Override
    @Cacheable(key = "'t_user_data_config:'+#uid",cacheNames = "redisCache4Spring")
    @Overtime(3600)
    public TUserDataConfig queryByUid(Long uid) {
        TUserDataConfig tUserDataConfig = tUserDataConfigMapper.selectTUserDataConfigByUid(uid);
        if (tUserDataConfig!=null){
            tUserDataConfig.setUid(null);
        }else {
            tUserDataConfig=new TUserDataConfig();
            tUserDataConfig.setPayPasswordForSell((short)0);
            tUserDataConfig.setEmailForSell((short)0);
        }
        return tUserDataConfig;
    }

    @Override
    @CacheEvict(key = "'t_user_data_config:'+#config.uid",cacheNames = "redisCache4Spring")
    public void update(TUserDataConfig config){
        TUserDataConfig tUserDataConfig = tUserDataConfigMapper.selectTUserDataConfigByUid(config.getUid());
        if (tUserDataConfig==null){
            tUserDataConfigMapper.insertTUserDataConfig(config);
        }
        tUserDataConfigMapper.updateTUserDataConfig(config);
    }

    @Override
    @RedisLockOperate
    public AjaxResult update(UserDataConfVo object) {
        TUserDataConfig tUserDataConfig = new TUserDataConfig();
        Long userId = SecurityUtils.getUserId();
        tUserDataConfig.setUid(userId);
        Short email = object.getEmail4Sell();

        Short sell = object.getPayPassword4Sell();

        Assert.test(!checkConf(email,sell),"member.conf.allClose", MessageUtils.message("member.conf.sell"));
        tUserDataConfig.setPayPasswordForSell(sell);
        tUserDataConfig.setEmailForSell(email);
        ITUserDataConfigService o = (ITUserDataConfigService)AopContext.currentProxy();
        o.update(tUserDataConfig);
        return AjaxResult.success();
    }

    private boolean checkConf(Short email, Short sell) {
        if (email==null||email==0){
            return sell != null && sell != 0;
        }
        return true;
    }
}

package com.ruoyi.settings.service.impl;

import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.constant.FeeTypeConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.FeeTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.chat.GeneralUtils;
import com.ruoyi.settings.domain.SysServiceCharge;
import com.ruoyi.settings.mapper.SysServiceChargeMapper;
import com.ruoyi.settings.service.ISysServiceChargeService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 手续费设置Service业务层处理
 *
 * @author nn
 * @date 2022-03-26
 */
@Service
public class SysServiceChargeServiceImpl implements ISysServiceChargeService
{
    @Resource
    private SysServiceChargeMapper sysServiceChargeMapper;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;

    @Resource
    private RedisCache redisCache;

    /**
     * 设置cache key
     *
     * @param userId 商户Id
     * @param feeType 手续费类型
     * @return 缓存键key
     */
    private String getCacheKey(Long userId,String feeType)
    {
        return CacheKeyConstants.SYS_SERVICE_CHARGE_KEY + userId + ":" + feeType;
    }

    /**
     * 查询手续费设置
     *
     * @param id 手续费设置主键
     * @return 手续费设置
     */
    @Override
    public SysServiceCharge selectSysServiceChargeById(Long id)
    {
        return sysServiceChargeMapper.selectSysServiceChargeById(id);
    }

    /**
     * 查询手续费设置列表
     *
     * @param sysServiceCharge 手续费设置
     * @return 手续费设置
     */
    @Override
    public List<SysServiceCharge> selectSysServiceChargeList(SysServiceCharge sysServiceCharge)
    {
        return sysServiceChargeMapper.selectSysServiceChargeList(sysServiceCharge);
    }

    /**
     * 新增手续费设置
     *
     * @param sysServiceCharge 手续费设置
     * @return 结果
     */
    @Override
    public int insertSysServiceCharge(SysServiceCharge sysServiceCharge)
    {
        int row = sysServiceChargeMapper.insertSysServiceCharge(sysServiceCharge);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(sysServiceCharge.getUserId(),sysServiceCharge.getFeeType()), sysServiceCharge.getRate(),3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 修改手续费设置
     *
     * @param sysServiceCharge 手续费设置
     * @return 结果
     */
    @Override
    public int updateSysServiceCharge(SysServiceCharge sysServiceCharge)
    {
        //先把修改前的缓存删除.
        SysServiceCharge serviceCharge = sysServiceChargeMapper.selectSysServiceChargeById(sysServiceCharge.getId());
        redisCache.deleteObject(getCacheKey(serviceCharge.getUserId(),serviceCharge.getFeeType()));
        int row = sysServiceChargeMapper.updateSysServiceCharge(sysServiceCharge);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(sysServiceCharge.getUserId(),sysServiceCharge.getFeeType()), sysServiceCharge.getRate(),3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 批量删除手续费设置
     *
     * @param ids 需要删除的手续费设置主键
     * @return 结果
     */
    @Override
    public int deleteSysServiceChargeByIds(Long[] ids)
    {
        for (Long id : ids) {
            SysServiceCharge sysServiceCharge = selectSysServiceChargeById(id);
            redisCache.deleteObject(getCacheKey(sysServiceCharge.getUserId(),sysServiceCharge.getFeeType()));
        }
        return sysServiceChargeMapper.deleteSysServiceChargeByIds(ids);
    }

    /**
     * 删除手续费设置信息
     *
     * @param id 手续费设置主键
     * @return 结果
     */
    @Override
    public int deleteSysServiceChargeById(Long id)
    {
        SysServiceCharge sysServiceCharge = selectSysServiceChargeById(id);
        redisCache.deleteObject(getCacheKey(sysServiceCharge.getUserId(),sysServiceCharge.getFeeType()));
        return sysServiceChargeMapper.deleteSysServiceChargeById(id);
    }

    /**
     * 校验该商户该类型是否唯一
     * @param sysServiceCharge 检测的对象
     * @return
     */
    @Override
    public String checkFeeTypeToMerchantUnique(SysServiceCharge sysServiceCharge) {
        Long id = StringUtils.isNull(sysServiceCharge.getId()) ? -1L : sysServiceCharge.getId();
        SysServiceCharge unique = sysServiceChargeMapper.checkFeeTypeToMerchantUnique(sysServiceCharge);
        if (StringUtils.isNotNull(unique) && unique.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;

    }

    /**
     * 设置手续费
     * @param sysServiceCharge 手续费对象
     * @return
     */
    @Override
    public int feeSetting(SysServiceCharge sysServiceCharge) {
        int count = 0;
        SysServiceCharge query = new SysServiceCharge();
        query.setUserId(sysServiceCharge.getUserId());
        query.setFeeType(sysServiceCharge.getFeeType());
        SysServiceCharge result = sysServiceChargeMapper.checkFeeTypeToMerchantUnique(query);
        if(result == null){
            sysServiceCharge.setId(snowflakeIdUtils.nextId());
            sysServiceCharge.setCreateTime(new Date());
            sysServiceCharge.setUpdateBy(null);
            sysServiceCharge.setRemark("设置手续费");
            count = sysServiceChargeMapper.insertSysServiceCharge(sysServiceCharge);
            redisCache.setCacheObject(getCacheKey(sysServiceCharge.getUserId(),sysServiceCharge.getFeeType()), sysServiceCharge.getRate(),3600 * 24, TimeUnit.SECONDS);
        }else {
            result.setRate(sysServiceCharge.getRate());
            result.setUpdateTime(new Date());
            result.setRemark("设置手续费");
            redisCache.deleteObject(getCacheKey(result.getUserId(),result.getFeeType()));
            count = sysServiceChargeMapper.updateSysServiceCharge(result);
            redisCache.setCacheObject(getCacheKey(result.getUserId(),result.getFeeType()), result.getRate(),3600 * 24, TimeUnit.SECONDS);
        }
        return count;
    }

    /**
     * 一键设置手续费(一种手续费类型)
     *
     * @param sysServiceCharge 手续费对象
     * @return 结果
     */
    @Override
    public int aKeySetRate(SysServiceCharge sysServiceCharge) {

        int count = 0;
        //查询出所有存在的商户(包括停用的，不包括删除的).
        SysUser sysUser = new SysUser();
        sysUser.setUserType(UserConstants.SYS_USER_TYPE_MERCHANT);
        sysUser.setDelFlag("0");
        LoginUser loginUser = GeneralUtils.getCurrentLoginUser();
        if(loginUser.getUser().getUserType().equals(UserConstants.SYS_USER_TYPE_MERCHANT)){
            sysUser.setParentId(loginUser.getUserId());   //商户只能一键设置下级商户.
        }
        List<SysUser> userList = sysUserService.selectUserList(sysUser);
        if(userList != null && userList.size() != 0){
            SysServiceCharge query = null;
            SysServiceCharge result = null;
            for(SysUser user : userList){
                //如果是商户，可以直接设置，如果是管理员，只能设置一级商户(ParentId为null)
                if(loginUser.getUser().getUserType().equals(UserConstants.SYS_USER_TYPE_ADMIN) && user.getParentId() != null){
                    continue;
                }
                query = new SysServiceCharge();
                query.setUserId(user.getUserId());
                query.setFeeType(sysServiceCharge.getFeeType());
                result = sysServiceChargeMapper.checkFeeTypeToMerchantUnique(query);
                if(result == null){
                    sysServiceCharge.setId(snowflakeIdUtils.nextId());
                    sysServiceCharge.setUserId(user.getUserId());
                    sysServiceCharge.setUserName(user.getUserName());
                    sysServiceCharge.setRemark("一键设置手续费-新增");
                    sysServiceCharge.setUpdateBy(null);
                    sysServiceCharge.setCreateTime(new Date());
                    sysServiceChargeMapper.insertSysServiceCharge(sysServiceCharge);
                    redisCache.setCacheObject(getCacheKey(sysServiceCharge.getUserId(),sysServiceCharge.getFeeType()), sysServiceCharge.getRate(),3600 * 24, TimeUnit.SECONDS);
                    count ++;
                }else {
                    result.setRate(sysServiceCharge.getRate());
                    result.setRemark("一键设置手续费-修改");
                    result.setUpdateBy(sysServiceCharge.getUpdateBy());
                    result.setUpdateTime(new Date());
                    redisCache.deleteObject(getCacheKey(result.getUserId(),result.getFeeType()));
                    sysServiceChargeMapper.updateSysServiceCharge(result);
                    redisCache.setCacheObject(getCacheKey(result.getUserId(),result.getFeeType()), result.getRate(),3600 * 24, TimeUnit.SECONDS);
                    count ++;
                }
            }
        }

        return count;
    }

    /**
     * 根据手续费类型获取商户的手续费.
     *
     * @param userId 商户Id
     * @param feeType 手续费类型
     * @return 结果
     */
    @Override
    public BigDecimal getUserIdRateByFeeType(Long userId, String feeType) {
        BigDecimal rate = redisCache.getCacheObject(getCacheKey(userId,feeType));
        if(rate == null){
            SysServiceCharge query = new SysServiceCharge();
            query.setUserId(userId);
            query.setFeeType(feeType);
            SysServiceCharge sysServiceCharge  = sysServiceChargeMapper.getSysServiceCharge(query);
            if(sysServiceCharge != null){
                rate = sysServiceCharge.getRate();
                redisCache.setCacheObject(getCacheKey(userId,feeType), rate,3600 * 24, TimeUnit.SECONDS);
            }else {
                throw new ServiceException("请给商家设置" + FeeTypeEnum.getDescByType(feeType) + "的手续费!");
            }
        }
        return rate;
    }
}

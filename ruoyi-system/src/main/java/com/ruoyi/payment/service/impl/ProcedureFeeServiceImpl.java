package com.ruoyi.payment.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.chat.GeneralUtils;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.payment.mapper.ProcedureFeeMapper;
import com.ruoyi.payment.domain.ProcedureFee;
import com.ruoyi.payment.service.IProcedureFeeService;

import javax.annotation.Resource;

/**
 * 代付商户手续费设置Service业务层处理
 * 
 * @author nn
 * @date 2022-08-27
 */
@Service
public class ProcedureFeeServiceImpl implements IProcedureFeeService 
{
    @Autowired
    private ProcedureFeeMapper procedureFeeMapper;

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
     * @return 缓存键key
     */
    private String getCacheKey(Long userId)
    {
        return CacheKeyConstants.SYS_PROCEDURE_FEE_KEY + userId ;
    }

    /**
     * 查询代付商户手续费设置
     * 
     * @param id 代付商户手续费设置主键
     * @return 代付商户手续费设置
     */
    @Override
    public ProcedureFee selectProcedureFeeById(Long id)
    {
        return procedureFeeMapper.selectProcedureFeeById(id);
    }

    /**
     * 查询代付商户手续费设置列表
     * 
     * @param procedureFee 代付商户手续费设置
     * @return 代付商户手续费设置
     */
    @Override
    public List<ProcedureFee> selectProcedureFeeList(ProcedureFee procedureFee)
    {
        return procedureFeeMapper.selectProcedureFeeList(procedureFee);
    }

    /**
     * 新增代付商户手续费设置
     * 
     * @param procedureFee 代付商户手续费设置
     * @return 结果
     */
    @Override
    public int insertProcedureFee(ProcedureFee procedureFee) {
        int row = procedureFeeMapper.insertProcedureFee(procedureFee);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(procedureFee.getUserId()), procedureFee,3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 修改代付商户手续费设置
     * 
     * @param procedureFee 代付商户手续费设置
     * @return 结果
     */
    @Override
    public int updateProcedureFee(ProcedureFee procedureFee) {
        //先把修改前的缓存删除.
        redisCache.deleteObject(getCacheKey(procedureFee.getUserId()));
        int row = procedureFeeMapper.updateProcedureFee(procedureFee);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(procedureFee.getUserId()), procedureFee,3600 * 24, TimeUnit.SECONDS);
        }
        return row;
    }

    /**
     * 批量删除代付商户手续费设置
     * 
     * @param ids 需要删除的代付商户手续费设置主键
     * @return 结果
     */
    @Override
    public int deleteProcedureFeeByIds(Long[] ids)
    {
        for(Long id : ids){
            ProcedureFee procedureFee = procedureFeeMapper.selectProcedureFeeById(id);
            if(procedureFee != null){
                redisCache.deleteObject(getCacheKey(procedureFee.getUserId()));
            }
        }
        return procedureFeeMapper.deleteProcedureFeeByIds(ids);
    }

    /**
     * 删除代付商户手续费设置信息
     * 
     * @param id 代付商户手续费设置主键
     * @return 结果
     */
    @Override
    public int deleteProcedureFeeById(Long id)
    {
        ProcedureFee procedureFee = procedureFeeMapper.selectProcedureFeeById(id);
        if(procedureFee != null){
            redisCache.deleteObject(getCacheKey(procedureFee.getUserId()));
        }
        return procedureFeeMapper.deleteProcedureFeeById(id);
    }

    /**
     * 校验该商户该类型是否唯一
     * @param procedureFee 检测的对象
     * @return
     */
    @Override
    public String checkFeeUnique(ProcedureFee procedureFee) {
        Long id = StringUtils.isNull(procedureFee.getId()) ? -1L : procedureFee.getId();
        ProcedureFee unique = procedureFeeMapper.checkFeeUnique(procedureFee);
        if (StringUtils.isNotNull(unique) && unique.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;

    }

    /**
     * 获取商户的手续费.
     *
     * @param userId 商户Id
     * @return 结果
     */
    @Override
    public BigDecimal getUserRate(Long userId) {
        ProcedureFee procedureFee = redisCache.getCacheObject(getCacheKey(userId));
        if(procedureFee == null){
            ProcedureFee query = new ProcedureFee();
            query.setUserId(userId);
            procedureFee  = procedureFeeMapper.getProcedureFee(query);
            if(procedureFee != null){
                redisCache.setCacheObject(getCacheKey(userId), procedureFee,3600 * 24, TimeUnit.SECONDS);
            }else {
                throw new ServiceException("请先给商户设置手续费!");
            }
        }
        return procedureFee.getRate();
    }

    /**
     * 获取商户手续费
     * @param userId
     * @return
     */
    @Override
    public ProcedureFee selectProcedureFeeByUid(Long userId) {
        if (redisCache.hasKey(getCacheKey(userId))) {
            return redisCache.getCacheObject(getCacheKey(userId));
        }
        ProcedureFee query = new ProcedureFee();
        query.setUserId(userId);
        ProcedureFee procedureFee = procedureFeeMapper.getProcedureFee(query);
        if (procedureFee != null) {
            redisCache.setCacheObject(getCacheKey(userId), procedureFee, 3600 * 24, TimeUnit.SECONDS);
            return procedureFee;
        } else {
            throw new ServiceException("请给商家设置手续费!");
        }
    }

    /**
     * 获取上级应该返水的费率
     * @param userId
     * @return
     */
    @Override
    public Map<String,Object> getWaterBackInfo(Long userId) {
        try {
            ProcedureFee low_fee = selectProcedureFeeByUid(userId);
            Long parentId = low_fee.getParentId();
            if(parentId == null){
                return null;
            }
            boolean flag = sysUserService.isAgency(parentId);
            if(!flag){
                return null;
            }
            ProcedureFee up_fee = selectProcedureFeeByUid(parentId);
            BigDecimal sub = low_fee.getRate().subtract(up_fee.getRate());
            if(BigDecimal.ZERO.compareTo(sub) >= 0){
                return null;
            }
            Map<String,Object> map = new HashMap<>();
            map.put("pid",parentId);
            map.put("pName",low_fee.getParentName());
            map.put("rate",sub.divide(new BigDecimal("1000")));
            return map;
        }catch (ServiceException e){
            return null;
        }
    }

    /**
     * 设置手续费
     * @param procedureFee 手续费对象
     * @return
     */
    @Override
    public int feeSetting(ProcedureFee procedureFee) {
        LoginUser loginUser = GeneralUtils.getCurrentLoginUser();
        int count = 0;
        ProcedureFee query = new ProcedureFee();
        query.setUserId(procedureFee.getUserId());
        ProcedureFee result = procedureFeeMapper.getProcedureFee(query);
        if(result == null){
            procedureFee.setId(snowflakeIdUtils.nextId());
            procedureFee.setCreateTime(new Date());
            procedureFee.setCreateBy(loginUser.getUsername());
            procedureFee.setRemark("用户管理-新增手续费");
            count = procedureFeeMapper.insertProcedureFee(procedureFee);
            redisCache.setCacheObject(getCacheKey(procedureFee.getUserId()), procedureFee,3600 * 24, TimeUnit.SECONDS);
        }else {
            result.setRate(procedureFee.getRate());
            result.setUpdateTime(new Date());
            result.setUpdateBy(loginUser.getUsername());
            result.setRemark("用户管理-修改手续费");
            redisCache.deleteObject(getCacheKey(result.getUserId()));
            count = procedureFeeMapper.updateProcedureFee(result);
            redisCache.setCacheObject(getCacheKey(result.getUserId()), result,3600 * 24, TimeUnit.SECONDS);
        }
        return count;
    }


    /**
     * 获取当前商户的手续费(用于设置手续费时验证自己的手续费只能降不能升，所以允许返回null{说明该商户还未设置过手续费}).
     *
     * @param userId 商户Id
     * @return 结果
     */
    @Override
    public BigDecimal getMyRate(Long userId) {
        ProcedureFee procedureFee = redisCache.getCacheObject(getCacheKey(userId));
        if(procedureFee == null){
            ProcedureFee query = new ProcedureFee();
            query.setUserId(userId);
            procedureFee  = procedureFeeMapper.getProcedureFee(query);
            if(procedureFee != null){
                redisCache.setCacheObject(getCacheKey(userId), procedureFee,3600 * 24, TimeUnit.SECONDS);
            }else {
                //允许返回null{说明该商户还未设置过手续费}
                return null;
            }
        }
        return procedureFee.getRate();
    }
}

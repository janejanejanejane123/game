package com.ruoyi.member.service.impl;

import java.util.Date;
import java.util.List;

import com.ruoyi.common.annotation.Overtime;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.ruoyi.member.mapper.TBankMapper;
import com.ruoyi.member.domain.TBank;
import com.ruoyi.member.service.ITBankService;

import javax.annotation.Resource;

/**
 * 银行卡列表Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-05-20
 */
@Service
public class TBankServiceImpl implements ITBankService 
{
    @Autowired
    private TBankMapper tBankMapper;
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;

    @Override
    @Cacheable(key = "'t_bank:bankList'",value = "redisCache4Spring")
    @Overtime(3600)
    public AjaxResult list() {
        TBank tBank = new TBank();
        tBank.setStatus((short)1);
        List<TBank> tBanks = tBankMapper.selectTBankList(tBank);
        return AjaxResult.success(tBanks);
    }

    /**
     * 查询银行卡列表
     * 
     * @param id 银行卡列表主键
     * @return 银行卡列表
     */
    @Override
    public TBank selectTBankById(Long id)
    {
        return tBankMapper.selectTBankById(id);
    }

    /**
     * 查询银行卡列表列表
     * 
     * @param tBank 银行卡列表
     * @return 银行卡列表
     */
    @Override
    public List<TBank> selectTBankList(TBank tBank)
    {
        return tBankMapper.selectTBankList(tBank);
    }

    /**
     * 新增银行卡列表
     * 
     * @param tBank 银行卡列表
     * @return 结果
     */
    @Override
    @CacheEvict(key = "'t_bank:bankList'",value = "redisCache4Spring")
    public int insertTBank(TBank tBank)
    {
        tBank.setId(snowflakeIdUtils.nextId());
        tBank.setAddAdmin(SecurityUtils.getUsername());
        tBank.setStatus((short)1);
        tBank.setAddTime(new Date());
        return tBankMapper.insertTBank(tBank);
    }

    /**
     * 修改银行卡列表
     * 
     * @param tBank 银行卡列表
     * @return 结果
     */
    @Override
    @CacheEvict(key = "'t_bank:bankList'",value = "redisCache4Spring")
    public int updateTBank(TBank tBank)
    {
        tBank.setUpAdmin(SecurityUtils.getUsername());
        tBank.setUpTime(new Date());
        return tBankMapper.updateTBank(tBank);
    }

    /**
     * 批量删除银行卡列表
     * 
     * @param ids 需要删除的银行卡列表主键
     * @return 结果
     */
    @Override
    public int deleteTBankByIds(Long[] ids)
    {
        return tBankMapper.deleteTBankByIds(ids);
    }

    /**
     * 删除银行卡列表信息
     * 
     * @param id 银行卡列表主键
     * @return 结果
     */
    @Override
    public int deleteTBankById(Long id)
    {
        return tBankMapper.deleteTBankById(id);
    }
}

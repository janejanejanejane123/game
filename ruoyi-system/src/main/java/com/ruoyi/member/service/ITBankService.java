package com.ruoyi.member.service;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.member.domain.TBank;

/**
 * 银行卡列表Service接口
 * 
 * @author ruoyi
 * @date 2022-05-20
 */
public interface ITBankService 
{

    AjaxResult list();
    /**
     * 查询银行卡列表
     * 
     * @param id 银行卡列表主键
     * @return 银行卡列表
     */
    public TBank selectTBankById(Long id);

    /**
     * 查询银行卡列表列表
     * 
     * @param tBank 银行卡列表
     * @return 银行卡列表集合
     */
    public List<TBank> selectTBankList(TBank tBank);

    /**
     * 新增银行卡列表
     * 
     * @param tBank 银行卡列表
     * @return 结果
     */
    public int insertTBank(TBank tBank);

    /**
     * 修改银行卡列表
     * 
     * @param tBank 银行卡列表
     * @return 结果
     */
    public int updateTBank(TBank tBank);

    /**
     * 批量删除银行卡列表
     * 
     * @param ids 需要删除的银行卡列表主键集合
     * @return 结果
     */
    public int deleteTBankByIds(Long[] ids);

    /**
     * 删除银行卡列表信息
     * 
     * @param id 银行卡列表主键
     * @return 结果
     */
    public int deleteTBankById(Long id);


}

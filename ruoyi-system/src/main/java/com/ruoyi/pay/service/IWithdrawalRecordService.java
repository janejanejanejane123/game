package com.ruoyi.pay.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.pay.domain.WithdrawalRecord;

/**
 * 提现记录Service接口
 *
 * @author ry
 * @date 2022-03-26
 */
public interface IWithdrawalRecordService
{
    /**
     * 查询提现记录
     *
     * @param id 提现记录主键
     * @return 提现记录
     */
    public WithdrawalRecord selectWithdrawalRecordById(Long id);

    /**
     * 查询提现记录列表
     *
     * @param withdrawalRecord 提现记录
     * @return 提现记录集合
     */
    public List<WithdrawalRecord> selectWithdrawalRecordList(WithdrawalRecord withdrawalRecord);

    /**
     * 新增提现记录
     *
     * @param withdrawalRecord 提现记录
     * @return 结果
     */
    public int insertWithdrawalRecord(WithdrawalRecord withdrawalRecord);

    /**
     * 修改提现记录
     *
     * @param withdrawalRecord 提现记录
     * @return 结果
     */
    public int updateWithdrawalRecord(WithdrawalRecord withdrawalRecord);

    /**
     * 批量删除提现记录
     *
     * @param ids 需要删除的提现记录主键集合
     * @return 结果
     */
    public int deleteWithdrawalRecordByIds(Long[] ids);

    /**
     * 删除提现记录信息
     *
     * @param id 提现记录主键
     * @return 结果
     */
    public int deleteWithdrawalRecordById(Long id);

    int selectWithdCount(WithdrawalRecord withdrawalRecord);

    Map selectWithdrSum(WithdrawalRecord record);
}

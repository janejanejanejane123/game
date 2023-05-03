package com.ruoyi.pay.service.impl;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pay.mapper.WithdrawalRecordMapper;
import com.ruoyi.pay.domain.WithdrawalRecord;
import com.ruoyi.pay.service.IWithdrawalRecordService;

import javax.annotation.Resource;

/**
 * 提现记录Service业务层处理
 *
 * @author ry
 * @date 2022-03-26
 */
@Service
public class WithdrawalRecordServiceImpl implements IWithdrawalRecordService {
    @Autowired
    private WithdrawalRecordMapper withdrawalRecordMapper;
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;
    /**
     * 查询提现记录
     *
     * @param id 提现记录主键
     * @return 提现记录
     */
    @Override
    public WithdrawalRecord selectWithdrawalRecordById(Long id) {
        return withdrawalRecordMapper.selectWithdrawalRecordById(id);
    }

    /**
     * 查询提现记录列表
     *
     * @param withdrawalRecord 提现记录
     * @return 提现记录
     */
    @Override
    public List<WithdrawalRecord> selectWithdrawalRecordList(WithdrawalRecord withdrawalRecord) {
        return withdrawalRecordMapper.selectWithdrawalRecordList(withdrawalRecord);
    }

    /**
     * 新增提现记录
     *
     * @param withdrawalRecord 提现记录
     * @return 结果
     */
    @Override
    public int insertWithdrawalRecord(WithdrawalRecord withdrawalRecord) {
        withdrawalRecord.setCreateTime(DateUtils.getNowDate());
        withdrawalRecord.setId(snowflakeIdUtils.nextId());
        return withdrawalRecordMapper.insertWithdrawalRecord(withdrawalRecord);
    }

    /**
     * 修改提现记录
     *
     * @param withdrawalRecord 提现记录
     * @return 结果
     */
    @Override
    public int updateWithdrawalRecord(WithdrawalRecord withdrawalRecord) {
        return withdrawalRecordMapper.updateWithdrawalRecord(withdrawalRecord);
    }

    /**
     * 批量删除提现记录
     *
     * @param ids 需要删除的提现记录主键
     * @return 结果
     */
    @Override
    public int deleteWithdrawalRecordByIds(Long[] ids) {
        return withdrawalRecordMapper.deleteWithdrawalRecordByIds(ids);
    }

    /**
     * 删除提现记录信息
     *
     * @param id 提现记录主键
     * @return 结果
     */
    @Override
    public int deleteWithdrawalRecordById(Long id) {
        return withdrawalRecordMapper.deleteWithdrawalRecordById(id);
    }

    /**
     * 查询提现人数
     * @param withdrawalRecord
     * @return
     */
    @Override
    public int selectWithdCount(WithdrawalRecord withdrawalRecord) {
        return withdrawalRecordMapper.selectWithdCount(withdrawalRecord).size();
    }

    @Override
    public Map selectWithdrSum(WithdrawalRecord record) {
        return withdrawalRecordMapper.selectWithdrawalRecordSum(record);
    }
}

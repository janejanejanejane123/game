package com.ruoyi.pay.service.impl;

import java.util.Date;
import java.util.List;

import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pay.mapper.TransferRecordMapper;
import com.ruoyi.pay.domain.TransferRecord;
import com.ruoyi.pay.service.ITransferRecordService;

/**
 * 转账记录Service业务层处理
 *
 * @author ruoyi
 * @date 2022-07-29
 */
@Service
public class TransferRecordServiceImpl implements ITransferRecordService {
    @Autowired
    private TransferRecordMapper transferRecordMapper;
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询转账记录
     *
     * @param id 转账记录主键
     * @return 转账记录
     */
    @Override
    public TransferRecord selectTransferRecordById(Long id) {
        return transferRecordMapper.selectTransferRecordById(id);
    }

    /**
     * 查询转账记录列表
     *
     * @param transferRecord 转账记录
     * @return 转账记录
     */
    @Override
    public List<TransferRecord> selectTransferRecordList(TransferRecord transferRecord) {
        return transferRecordMapper.selectTransferRecordList(transferRecord);
    }

    /**
     * 新增转账记录
     *
     * @param transferRecord 转账记录
     * @return 结果
     */
    @Override
    public int insertTransferRecord(TransferRecord transferRecord) {
        transferRecord.setId(snowflakeIdUtils.nextId());
        transferRecord.setCreateDate(new Date());
        return transferRecordMapper.insertTransferRecord(transferRecord);
    }

    /**
     * 修改转账记录
     *
     * @param transferRecord 转账记录
     * @return 结果
     */
    @Override
    public int updateTransferRecord(TransferRecord transferRecord) {
        return transferRecordMapper.updateTransferRecord(transferRecord);
    }

    /**
     * 批量删除转账记录
     *
     * @param ids 需要删除的转账记录主键
     * @return 结果
     */
    @Override
    public int deleteTransferRecordByIds(Long[] ids) {
        return transferRecordMapper.deleteTransferRecordByIds(ids);
    }

    /**
     * 删除转账记录信息
     *
     * @param id 转账记录主键
     * @return 结果
     */
    @Override
    public int deleteTransferRecordById(Long id) {
        return transferRecordMapper.deleteTransferRecordById(id);
    }
}

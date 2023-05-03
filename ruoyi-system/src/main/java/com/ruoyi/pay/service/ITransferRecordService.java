package com.ruoyi.pay.service;

import java.util.List;
import com.ruoyi.pay.domain.TransferRecord;

/**
 * 转账记录Service接口
 *
 * @author ruoyi
 * @date 2022-07-29
 */
public interface ITransferRecordService
{
    /**
     * 查询转账记录
     *
     * @param id 转账记录主键
     * @return 转账记录
     */
    public TransferRecord selectTransferRecordById(Long id);

    /**
     * 查询转账记录列表
     *
     * @param transferRecord 转账记录
     * @return 转账记录集合
     */
    public List<TransferRecord> selectTransferRecordList(TransferRecord transferRecord);

    /**
     * 新增转账记录
     *
     * @param transferRecord 转账记录
     * @return 结果
     */
    public int insertTransferRecord(TransferRecord transferRecord);

    /**
     * 修改转账记录
     *
     * @param transferRecord 转账记录
     * @return 结果
     */
    public int updateTransferRecord(TransferRecord transferRecord);

    /**
     * 批量删除转账记录
     *
     * @param ids 需要删除的转账记录主键集合
     * @return 结果
     */
    public int deleteTransferRecordByIds(Long[] ids);

    /**
     * 删除转账记录信息
     *
     * @param id 转账记录主键
     * @return 结果
     */
    public int deleteTransferRecordById(Long id);
}

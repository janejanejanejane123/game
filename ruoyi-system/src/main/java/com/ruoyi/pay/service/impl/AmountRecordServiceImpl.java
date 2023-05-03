package com.ruoyi.pay.service.impl;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pay.mapper.AmountRecordMapper;
import com.ruoyi.pay.domain.AmountRecord;
import com.ruoyi.pay.service.IAmountRecordService;

/**
 * 额度转换Service业务层处理
 *
 * @author
 * @date 2022-03-13
 */
@Service
public class AmountRecordServiceImpl implements IAmountRecordService {
    @Autowired
    private AmountRecordMapper amountRecordMapper;
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询额度转换
     *
     * @param id 额度转换主键
     * @return 额度转换
     */
    @Override
    public AmountRecord selectAmountRecordById(Long id) {
        return amountRecordMapper.selectAmountRecordById(id);
    }

    /**
     * 查询额度转换列表
     *
     * @param amountRecord 额度转换
     * @return 额度转换
     */
    @Override
    public List<AmountRecord> selectAmountRecordList(AmountRecord amountRecord) {
        return amountRecordMapper.selectAmountRecordList(amountRecord);
    }

    /**
     * 新增额度转换
     *
     * @param amountRecord 额度转换
     * @return 结果
     */
    @Override
    public int insertAmountRecord(AmountRecord amountRecord) {
        amountRecord.setCreateTime(DateUtils.getNowDate());
        amountRecord.setId(snowflakeIdUtils.nextId());
        return amountRecordMapper.insertAmountRecord(amountRecord);
    }

    /**
     * 修改额度转换
     *
     * @param amountRecord 额度转换
     * @return 结果
     */
    @Override
    public int updateAmountRecord(AmountRecord amountRecord) {
        return amountRecordMapper.updateAmountRecord(amountRecord);
    }

    /**
     * 批量删除额度转换
     *
     * @param ids 需要删除的额度转换主键
     * @return 结果
     */
    @Override
    public int deleteAmountRecordByIds(Long[] ids) {
        return amountRecordMapper.deleteAmountRecordByIds(ids);
    }

    /**
     * 删除额度转换信息
     *
     * @param id 额度转换主键
     * @return 结果
     */
    @Override
    public int deleteAmountRecordById(Long id) {
        return amountRecordMapper.deleteAmountRecordById(id);
    }

    @Override
    public AmountRecord selectAmountRecordByOrderNo(String orderNo) {
        return amountRecordMapper.selectAmountRecordByOrderNo(orderNo);
    }

    @Override
    public int selectRechCount(AmountRecord record) {
        return amountRecordMapper.selectRechgeCount(record).size();
    }

    @Override
    public Map selectAmountRecordSum(AmountRecord record) {
        return amountRecordMapper.selectAmountRecordSum(record);
    }

}

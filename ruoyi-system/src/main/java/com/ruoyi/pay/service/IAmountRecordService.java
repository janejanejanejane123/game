package com.ruoyi.pay.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.pay.domain.AmountRecord;

/**
 * 额度转换Service接口
 *
 * @author 
 * @date 2022-03-13
 */
public interface IAmountRecordService
{
    /**
     * 查询额度转换
     *
     * @param id 额度转换主键
     * @return 额度转换
     */
    public AmountRecord selectAmountRecordById(Long id);

    /**
     * 查询额度转换列表
     *
     * @param amountRecord 额度转换
     * @return 额度转换集合
     */
    public List<AmountRecord> selectAmountRecordList(AmountRecord amountRecord);

    /**
     * 新增额度转换
     *
     * @param amountRecord 额度转换
     * @return 结果
     */
    public int insertAmountRecord(AmountRecord amountRecord);

    /**
     * 修改额度转换
     *
     * @param amountRecord 额度转换
     * @return 结果
     */
    public int updateAmountRecord(AmountRecord amountRecord);

    /**
     * 批量删除额度转换
     *
     * @param ids 需要删除的额度转换主键集合
     * @return 结果
     */
    public int deleteAmountRecordByIds(Long[] ids);

    /**
     * 删除额度转换信息
     *
     * @param id 额度转换主键
     * @return 结果
     */
    public int deleteAmountRecordById(Long id);

    AmountRecord selectAmountRecordByOrderNo(String orderNo);

    int selectRechCount(AmountRecord record);

    Map selectAmountRecordSum(AmountRecord record);
}

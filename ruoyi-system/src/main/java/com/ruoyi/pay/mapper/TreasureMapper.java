package com.ruoyi.pay.mapper;


import com.ruoyi.pay.domain.Treasure;

import java.util.List;
import java.util.Map;

/**
 * 交易流水Mapper接口
 *
 * @author 
 * @date 2022-03-12
 */
public interface TreasureMapper
{
    /**
     * 查询交易流水
     *
     * @param id 交易流水主键
     * @return 交易流水
     */
    public Treasure selectTreasureById(Long id);

    /**
     * 查询交易流水列表
     *
     * @param treasure 交易流水
     * @return 交易流水集合
     */
    public List<Treasure> selectTreasureList(Treasure treasure);

    /**
     * 汇总
     * @param treasure
     * @return
     */
    public Map selectTreasureSum(Treasure treasure);

    /**
     * 新增交易流水
     *
     * @param treasure 交易流水
     * @return 结果
     */
    public int insertTreasure(Treasure treasure);

    /**
     * 修改交易流水
     *
     * @param treasure 交易流水
     * @return 结果
     */
    public int updateTreasure(Treasure treasure);

    /**
     * 删除交易流水
     *
     * @param id 交易流水主键
     * @return 结果
     */
    public int deleteTreasureById(Long id);

    /**
     * 批量删除交易流水
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTreasureByIds(Long[] ids);
}

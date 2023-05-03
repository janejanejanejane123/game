package com.ruoyi.pay.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.pay.domain.Treasure;
import com.ruoyi.pay.mapper.TreasureMapper;
import com.ruoyi.pay.service.ITreasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 交易流水Service业务层处理
 *
 * @author
 * @date 2022-03-12
 */
@Service
public class TreasureServiceImpl implements ITreasureService {
    @Autowired
    private TreasureMapper treasureMapper;
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询交易流水
     *
     * @param id 交易流水主键
     * @return 交易流水
     */
    @Override
    public Treasure selectTreasureById(Long id) {
        return treasureMapper.selectTreasureById(id);
    }

    /**
     * 查询交易流水列表
     *
     * @param treasure 交易流水
     * @return 交易流水
     */
    @Override
    public List<Treasure> selectTreasureList(Treasure treasure) {
        return treasureMapper.selectTreasureList(treasure);
    }

    @Override
    public Map selectTreasureSum(Treasure treasure) {
        return treasureMapper.selectTreasureSum(treasure);
    }

    /**
     * 新增交易流水
     *
     * @param treasure 交易流水
     * @return 结果
     */
    @Override
    public int insertTreasure(Treasure treasure) {
        treasure.setId(snowflakeIdUtils.nextId());
        treasure.setAddTime(new Date());
        return treasureMapper.insertTreasure(treasure);
    }

    /**
     * 修改交易流水
     *
     * @param treasure 交易流水
     * @return 结果
     */
    @Override
    public int updateTreasure(Treasure treasure) {
        return treasureMapper.updateTreasure(treasure);
    }

    /**
     * 批量删除交易流水
     *
     * @param ids 需要删除的交易流水主键
     * @return 结果
     */
    @Override
    public int deleteTreasureByIds(Long[] ids) {
        return treasureMapper.deleteTreasureByIds(ids);
    }

    /**
     * 删除交易流水信息
     *
     * @param id 交易流水主键
     * @return 结果
     */
    @Override
    public int deleteTreasureById(Long id) {
        return treasureMapper.deleteTreasureById(id);
    }
}

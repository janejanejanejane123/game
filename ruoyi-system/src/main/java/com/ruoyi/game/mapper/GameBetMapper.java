package com.ruoyi.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.game.domain.GameBet;


import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
public interface GameBetMapper  extends BaseMapper<GameBet>
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public GameBet selectGameBetById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param gameBet 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<GameBet> selectGameBetList(GameBet gameBet);

    /**
     * 新增【请填写功能名称】
     * 
     * @param gameBet 【请填写功能名称】
     * @return 结果
     */
    public int insertGameBet(GameBet gameBet);

    /**
     * 修改【请填写功能名称】
     * 
     * @param gameBet 【请填写功能名称】
     * @return 结果
     */
    public int updateGameBet(GameBet gameBet);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteGameBetById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGameBetByIds(String[] ids);
}

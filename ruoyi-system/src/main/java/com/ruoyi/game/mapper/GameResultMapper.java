package com.ruoyi.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.game.domain.GameResult;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
public interface GameResultMapper  extends BaseMapper<GameResult>
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public GameResult selectGameResultById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param gameResult 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<GameResult> selectGameResultList(GameResult gameResult);

    /**
     * 新增【请填写功能名称】
     * 
     * @param gameResult 【请填写功能名称】
     * @return 结果
     */
    public int insertGameResult(GameResult gameResult);

    /**
     * 修改【请填写功能名称】
     * 
     * @param gameResult 【请填写功能名称】
     * @return 结果
     */
    public int updateGameResult(GameResult gameResult);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteGameResultById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGameResultByIds(String[] ids);
}

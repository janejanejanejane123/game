package com.ruoyi.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.game.domain.GameConfig;


import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
public interface GameConfigMapper  extends BaseMapper<GameConfig>
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public GameConfig selectGameConfigById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param gameConfig 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<GameConfig> selectGameConfigList(GameConfig gameConfig);

    /**
     * 新增【请填写功能名称】
     * 
     * @param gameConfig 【请填写功能名称】
     * @return 结果
     */
    public int insertGameConfig(GameConfig gameConfig);

    /**
     * 修改【请填写功能名称】
     * 
     * @param gameConfig 【请填写功能名称】
     * @return 结果
     */
    public int updateGameConfig(GameConfig gameConfig);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteGameConfigById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGameConfigByIds(String[] ids);
}

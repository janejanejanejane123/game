package com.ruoyi.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.game.domain.ApiDetail;

import java.util.List;


/**
 * 【请填写功能名称】Mapper接口
 *
 * @author ruoyi
 * @date 2023-04-28
 */
public interface ApiDetailMapper extends BaseMapper<ApiDetail> {
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public ApiDetail selectApiDetailById(Long id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param apiDetail 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ApiDetail> selectApiDetailList(ApiDetail apiDetail);

    /**
     * 新增【请填写功能名称】
     *
     * @param apiDetail 【请填写功能名称】
     * @return 结果
     */
    public int insertApiDetail(ApiDetail apiDetail);

    /**
     * 修改【请填写功能名称】
     *
     * @param apiDetail 【请填写功能名称】
     * @return 结果
     */
    public int updateApiDetail(ApiDetail apiDetail);

    /**
     * 删除【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteApiDetailById(Long id);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteApiDetailByIds(String[] ids);
}

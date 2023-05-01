package com.ruoyi.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.game.domain.UserAuthRel;

import java.util.List;

/**
 * 用户验证关联Mapper接口
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
public interface UserAuthRelMapper  extends BaseMapper<UserAuthRel>
{
    /**
     * 查询用户验证关联
     * 
     * @param id 用户验证关联主键
     * @return 用户验证关联
     */
    public UserAuthRel selectUserAuthRelById(Long id);

    /**
     * 查询用户验证关联列表
     * 
     * @param userAuthRel 用户验证关联
     * @return 用户验证关联集合
     */
    public List<UserAuthRel> selectUserAuthRelList(UserAuthRel userAuthRel);

    /**
     * 新增用户验证关联
     * 
     * @param userAuthRel 用户验证关联
     * @return 结果
     */
    public int insertUserAuthRel(UserAuthRel userAuthRel);

    /**
     * 修改用户验证关联
     * 
     * @param userAuthRel 用户验证关联
     * @return 结果
     */
    public int updateUserAuthRel(UserAuthRel userAuthRel);

    /**
     * 删除用户验证关联
     * 
     * @param id 用户验证关联主键
     * @return 结果
     */
    public int deleteUserAuthRelById(Long id);

    /**
     * 批量删除用户验证关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserAuthRelByIds(String[] ids);
}

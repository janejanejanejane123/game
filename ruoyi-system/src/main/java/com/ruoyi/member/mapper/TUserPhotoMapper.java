package com.ruoyi.member.mapper;

import java.util.List;
import com.ruoyi.member.domain.TUserPhoto;

/**
 * 用户头像Mapper接口
 * 
 * @author ruoyi
 * @date 2022-05-22
 */
public interface TUserPhotoMapper 
{
    /**
     * 查询用户头像
     * 
     * @param id 用户头像主键
     * @return 用户头像
     */
    public TUserPhoto selectTUserPhotoById(Long id);

    /**
     * 查询用户头像列表
     * 
     * @param tUserPhoto 用户头像
     * @return 用户头像集合
     */
    public List<TUserPhoto> selectTUserPhotoList(TUserPhoto tUserPhoto);

    /**
     * 新增用户头像
     * 
     * @param tUserPhoto 用户头像
     * @return 结果
     */
    public int insertTUserPhoto(TUserPhoto tUserPhoto);

    /**
     * 修改用户头像
     * 
     * @param tUserPhoto 用户头像
     * @return 结果
     */
    public int updateTUserPhoto(TUserPhoto tUserPhoto);

    /**
     * 删除用户头像
     * 
     * @param id 用户头像主键
     * @return 结果
     */
    public int deleteTUserPhotoById(Long id);

    /**
     * 批量删除用户头像
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTUserPhotoByIds(Long[] ids);
}

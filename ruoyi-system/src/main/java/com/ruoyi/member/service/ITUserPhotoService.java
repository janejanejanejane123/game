package com.ruoyi.member.service;

import java.util.List;
import com.ruoyi.member.domain.TUserPhoto;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户头像Service接口
 * 
 * @author ruoyi
 * @date 2022-05-22
 */
public interface ITUserPhotoService 
{

    List<TUserPhoto> getAvailablePhotos();




    TUserPhoto queryById(Long id);
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
     * 批量删除用户头像
     * 
     * @param ids 需要删除的用户头像主键集合
     * @return 结果
     */
    public int deleteTUserPhotoByIds(Long[] ids);

    /**
     * 删除用户头像信息
     * 
     * @param id 用户头像主键
     * @return 结果
     */
    public int deleteTUserPhotoById(Long id);

    /**
     * 上传头像
     * @param file
     * @return
     */
    String upload(MultipartFile file);
}

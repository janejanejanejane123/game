package com.ruoyi.member.service.impl;

import java.util.Date;
import java.util.List;

import com.ruoyi.common.annotation.Overtime;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.file.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.ruoyi.member.mapper.TUserPhotoMapper;
import com.ruoyi.member.domain.TUserPhoto;
import com.ruoyi.member.service.ITUserPhotoService;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 用户头像Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-05-22
 */
@Service
public class TUserPhotoServiceImpl implements ITUserPhotoService 
{
    @Autowired
    private TUserPhotoMapper tUserPhotoMapper;
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;
    @Resource
    private MinioUtil minioUtil;

    @Override
    @Cacheable(key = "'t_user_phots:photos'",value = "redisCache4Spring")
    @Overtime( 3600 * 24 )
    public List<TUserPhoto> getAvailablePhotos() {
        TUserPhoto tUserPhoto = new TUserPhoto();
        tUserPhoto.setStatus(Constants.OPEN);
        return tUserPhotoMapper.selectTUserPhotoList(tUserPhoto);
    }

    @Override
    public TUserPhoto queryById(Long id) {
        return tUserPhotoMapper.selectTUserPhotoById(id);
    }

    /**
     * 查询用户头像
     * 
     * @param id 用户头像主键
     * @return 用户头像
     */
    @Override
    public TUserPhoto selectTUserPhotoById(Long id)
    {
        return tUserPhotoMapper.selectTUserPhotoById(id);
    }

    /**
     * 查询用户头像列表
     * 
     * @param tUserPhoto 用户头像
     * @return 用户头像
     */
    @Override
    public List<TUserPhoto> selectTUserPhotoList(TUserPhoto tUserPhoto)
    {
        return tUserPhotoMapper.selectTUserPhotoList(tUserPhoto);
    }

    /**
     * 新增用户头像
     * 
     * @param tUserPhoto 用户头像
     * @return 结果
     */
    @Override
    @CacheEvict(key = "'t_user_phots:photos'",value = "redisCache4Spring")
    public int insertTUserPhoto(TUserPhoto tUserPhoto)
    {
        tUserPhoto.setAddAdmin(SecurityUtils.getUsername());
        tUserPhoto.setAddTime(new Date());
        tUserPhoto.setId(snowflakeIdUtils.nextId());
        tUserPhoto.setStatus((Constants.OPEN));
        return tUserPhotoMapper.insertTUserPhoto(tUserPhoto);
    }

    /**
     * 修改用户头像
     * 
     * @param tUserPhoto 用户头像
     * @return 结果
     */
    @Override
    @CacheEvict(key = "'t_user_photo:photos'",value = "redisCache4Spring")
    public int updateTUserPhoto(TUserPhoto tUserPhoto)
    {
        return tUserPhotoMapper.updateTUserPhoto(tUserPhoto);
    }

    /**
     * 批量删除用户头像
     * 
     * @param ids 需要删除的用户头像主键
     * @return 结果
     */
    @Override
    public int deleteTUserPhotoByIds(Long[] ids)
    {
        return tUserPhotoMapper.deleteTUserPhotoByIds(ids);
    }

    /**
     * 删除用户头像信息
     * 
     * @param id 用户头像主键
     * @return 结果
     */
    @Override
    public int deleteTUserPhotoById(Long id)
    {
        return tUserPhotoMapper.deleteTUserPhotoById(id);
    }

    @Override
    public String upload(MultipartFile file) {
        return  minioUtil.upload(file, false);
    }
}

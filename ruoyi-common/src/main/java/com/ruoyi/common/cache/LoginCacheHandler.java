package com.ruoyi.common.cache;

import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.SecurityUtils;

import java.util.Map;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/9/6,13:07
 * @return:
 **/
public interface LoginCacheHandler {



    void kickLoginUser(Long userId);
    /**
     * 获取登录用户
     * @return l
     */
    LoginUser getLoginUser(Long userId);

    /**
     * 修改登录缓存中的支付密码
     * @param encryptedPassword 加密后的支付密码;
     */
    void updatePayPasswordCache(String encryptedPassword);
    void updatePayPasswordCache(Long uid,String encryptedPassword);
    void updatePayPasswordCache(Long uid,String encryptedPassword,boolean immediatelyFlush);

    /**
     * 修改缓存中的照片
     * @param photo 照片
     */
    void updatePhotoCache(String photo);
    void updatePhotoCache(Long uid,String photo);
    void updatePhotoCache(Long uid,String photo,boolean immediatelyFlush);
    /**
     * 修改缓存中的昵称
     * @param nickName 昵称
     */
    void updateNickNameCache(String nickName);
    void updateNickNameCache(Long uid,String nickName);
    void updateNickNameCache(Long uid,String nickName,boolean immediatelyFlush);

    /**
     * 设置缓存属性
     * @param k 键
     * @param v 值
     */

    /**
     * 批量修改属性
     * @return UpdateData
     */
    default UpdateData updateStream(Long uid){
        return new UpdateData(uid,this);
    }

    default UpdateData updateStream(){
        return updateStream(null);
    }

    /**
     * 将本地缓存刷到缓存管理之中
     */
    void flush();

}

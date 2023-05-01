package com.ruoyi.framework.security.handle;

import com.ruoyi.common.cache.LoginCacheHandler;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.web.service.TokenService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/9/6,13:05
 * @return:
 **/
@Slf4j
public class LoginUpdateHandler implements LoginCacheHandler {

    @Resource
    protected TokenService tokenService;


    @Override
    public void kickLoginUser(Long userId) {
        LoginUser loginUser = getLoginUser(userId);
        log.info("自己踢自己下线:"+loginUser.getUsername());

        tokenService.delLoginUser(loginUser.getToken());
    }

    @Override
    public LoginUser getLoginUser(Long userId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (userId!=null&& !userId.equals(loginUser.getUserId())){
            throw new ServiceException(MessageUtils.message("system.error"));
        }
        return loginUser;
    }

    @Override
    public void updatePayPasswordCache(String encryptedPassword) {
        updatePayPasswordCache(null,encryptedPassword);
    }

    @Override
    public void updatePayPasswordCache(Long uid,String encryptedPassword) {
        updatePayPasswordCache(uid,encryptedPassword,true);
    }

    @Override
    public void updatePayPasswordCache(Long uid,String encryptedPassword, boolean immediatelyFlush) {
        LoginUser loginUser = getLoginUser(uid);
        if (loginUser==null){
            return;
        }
        if (loginUser instanceof LoginMember){
            ((LoginMember) loginUser).gettUser().setPayPassword(encryptedPassword);
        }
        if (immediatelyFlush){
            this.flush();
        }

    }

    @Override
    public void updatePhotoCache(String photo) {
        updatePhotoCache(null,photo);
    }


    @Override
    public void updatePhotoCache(Long uid,String photo) {
        updatePhotoCache(uid,photo,true);
    }

    @Override
    public void updatePhotoCache(Long uid,String photo, boolean immediatelyFlush) {
        LoginUser loginUser = getLoginUser(uid);
        if (loginUser==null){
            return;
        }
        if (loginUser instanceof LoginMember){
            ((LoginMember) loginUser).gettUser().setPhoto(photo);
        }else {
            loginUser.getUser().setAvatar(photo);
        }
        if (immediatelyFlush){
            this.flush();
        }

    }

    @Override
    public void updateNickNameCache(String nickName) {
        updateNickNameCache(null,nickName);
    }

    @Override
    public void updateNickNameCache(Long uid,String nickName) {
        updateNickNameCache(uid,nickName,true);
    }

    @Override
    public void updateNickNameCache(Long uid,String nickName, boolean immediatelyFlush) {
        LoginUser loginUser = getLoginUser(uid);
        if (loginUser==null){
            return;
        }
        if (loginUser instanceof LoginMember){
            ((LoginMember) loginUser).gettUser().setNickname(nickName);
        }else {
            loginUser.getUser().setNickName(nickName);
        }
        if (immediatelyFlush){
            this.flush();
        }
    }

    @Override
    public void flush() {
        LoginUser loginUser = getLoginUser(null);
        tokenService.setLoginUser(loginUser);
    }

}

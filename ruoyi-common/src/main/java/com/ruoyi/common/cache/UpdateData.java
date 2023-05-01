package com.ruoyi.common.cache;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/9/6,14:20
 * @return:
 **/
public class UpdateData {
    private LoginCacheHandler loginCacheHandler;
    private volatile boolean flushed=false;
    private Long uid;
    UpdateData(Long uid,LoginCacheHandler loginCacheHandler){
        this.uid=uid;
        this.loginCacheHandler=loginCacheHandler;
    }


    public UpdateData setPhoto(String photo){
        checkFlushed();
        loginCacheHandler.updatePhotoCache(uid,photo,false);
        return this;
    }


    public UpdateData setNickName(String nickName){
        checkFlushed();
        loginCacheHandler.updateNickNameCache(uid,nickName,false);
        return this;
    }

    public UpdateData setPayPassword(String encryptPayPassword){
        checkFlushed();
        loginCacheHandler.updatePayPasswordCache(uid,encryptPayPassword,false);
        return this;
    }


    public void flush(){
        checkFlushed();
        loginCacheHandler.flush();
        this.flushed=true;
    }

    private void checkFlushed(){
        Assert.test(flushed,"system.error");
    }


    public UpdateData updateAllFieldsFromSource(Object source){
        LoginUser loginUser = loginCacheHandler.getLoginUser(uid);
        if (loginUser instanceof LoginMember){
            Assert.test(!(source instanceof TUser),"system.error");
            CglibBeanCopierUtils.copyPropertiesIgnoreNull(((LoginMember) loginUser).gettUser(),source);
        }else if (loginUser!=null){
            Assert.test(!(source instanceof SysUser),"system.error");
            CglibBeanCopierUtils.copyPropertiesIgnoreNull(loginUser.getUser(),source);
        }
        return this;
    }

}

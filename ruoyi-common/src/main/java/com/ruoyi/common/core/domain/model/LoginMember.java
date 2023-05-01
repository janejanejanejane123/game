package com.ruoyi.common.core.domain.model;

import com.ruoyi.common.core.domain.model.member.TUser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/21,18:48
 * @return:
 **/

public class LoginMember extends LoginUser  {


    private TUser tUser;


    public TUser gettUser() {
        return tUser;
    }

    public void settUser(TUser tUser) {
        this.tUser = tUser;
    }

    private Map<String,Object> attributes=new HashMap<>(8);

    public LoginMember(){
    }

    public LoginMember(TUser tUser){
        this.tUser=tUser;
        this.setUserId(tUser.getUid());

        this.setPermissions(Collections.singleton("ROLE_PLAYER"));
    }


    public String getRealName(){
        return tUser.getRealname();
    }


    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key){
        return (T)attributes.get(key);
    }


    public void setAttribute(String key,Object value){
        this.attributes.put(key,value);
    }

    @Override
    public String redisKickKey(){
        return  "t_player_user:singleLogin:" + getUserId();
    }

    @Override
    public String getUsername()
    {
        return tUser.getUsername();
    }

    @Override
    public String getPassword()
    {
        return tUser.getPassword();
    }

    @Override
    public String   getPayPassword(){
        return tUser.getPayPassword();
    }

    public String getEmail(){
        return tUser.getEmail();
    }

    @Override
    public boolean isEnabled()
    {
        return tUser.getDisabled()==1;
    }

    public Short getRealNameVerify(){
        return tUser.getVerifiedRealName();
    }
    @Override
    public String getNickName(){
        return tUser.getNickname();
    }
    @Override
    public String getPhoto(){
        return tUser.getPhoto();
    }
    @Override
    public String getShortUrl(){
        return tUser.getUrl();
    }
    @Override
    public Long  getUniqueCode(){
        return tUser.getUniqueCode();
    };

    @Override
    public Long getMerchantId() {
        return tUser.getMerchantId();
    }
}

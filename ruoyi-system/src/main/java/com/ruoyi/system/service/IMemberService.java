package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.model.member.TUser;

import java.util.List;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/21,11:20
 * @return:
 **/
public interface IMemberService {

    TUser queryApiMemberByUsername(String apiUsername);
    /**
     * 通过用户名查询用户
     * @param username 用户名
     * @return tuser
     */
    TUser queryMemberByUsername(String username);

    List<TUser> queryAllUsers();

    TUser queryMemberByInvite(String invite);


    TUser selectMemberByUid(Long uid);

    /**
     *
     * @param tUser 计数
     * @return 数量
     */
    Long count(TUser tUser);



    int insert(TUser tUser);

    void updateUser(TUser tUser);



}

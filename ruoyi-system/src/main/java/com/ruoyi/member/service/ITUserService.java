package com.ruoyi.member.service;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.auth.ApiAuthKey;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginMember;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.member.domain.TUserPhoto;
import com.ruoyi.member.vo.*;

import java.util.List;
import java.util.Map;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2022-03-15
 */
public interface ITUserService {

    AjaxResult applyProxy();


    List<TUserPhoto> getAvailablePhotos();


    AjaxResult multiFunctionalEmailVerify(short type);

    void modifyPhoto(Long id);

    AjaxResult confirmReset(JSONObject object);


    AjaxResult resetPassword(JSONObject object);

    AjaxResult emailVerify(JSONObject object);

    void checkEmailVerify(EmailVo body);

    void bindEmail(EmailVo email);

    void modifyNickName(String nickName,Long uid);
    /**
     * 注册
     * @param registeredInfo
     */
    LoginMember registeredMember(RegisteredInfo registeredInfo);
    /**
     * 登录
     * @param loginInfo
     * @return
     */
    void memberLogin(LoginInfo loginInfo);


    AjaxResult checkUsername(String username);

    AjaxResult modifyMemberPassword(ModifyPasswordVo modifyVo);


    AjaxResult modifyMemberPayPassword(ModifyPayPasswordVo modifyPayPasswordVo);


    void registeredApply(String username,String realName,Long videoID,Long cardID,String captchaKey);


    /**
     * 获取用户信息
     * @return 用户信息
     */
    AjaxResult getInfo();



    List<Map<String,Object>> getChildList();

    AjaxResult verified(String realName,Long cardId, Long videoId,String captchaKey);
    //==========================================后台接口开始==================================================


    AjaxResult registeredChildMember(RegisteredChildVo registeredChildVo);

    /**
     * 查询用户
     *
     * @param uid 用户主键
     * @return 用户
     */
    public TUser selectTUserByUid(Long uid);

    /**
     * 查询用户列表
     *
     * @param tUser 用户
     * @return 用户集合
     */
    public List<TUser> selectTUserList(TUser tUser);

    /**
     * 新增用户
     *
     * @param tUser 用户
     * @return 结果
     */
    public int insertTUser(TUser tUser);

    /**
     * 修改用户
     *
     * @param tUser 用户
     * @return 结果
     */
    public int updateTUser(TUser tUser);

    /**
     * 批量删除用户
     *
     * @param uids 需要删除的用户主键集合
     * @return 结果
     */
    public int deleteTUserByUids(Long[] uids);

    /**
     * 删除用户信息
     *
     * @param uid 用户主键
     * @return 结果
     */
    public int deleteTUserByUid(Long uid);


    AjaxResult verifiedConfig();

    TUser generatorApiUser(String username,Map<ApiAuthKey,Object> data);

    TUser selectByName(String name);
}

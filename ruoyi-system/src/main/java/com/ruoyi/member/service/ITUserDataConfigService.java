package com.ruoyi.member.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.member.domain.TUserDataConfig;
import com.ruoyi.member.vo.UserDataConfVo;

/**
 * 用户自我配置Service接口
 * 
 * @author ruoyi
 * @date 2022-06-24
 */
public interface ITUserDataConfigService 
{
    TUserDataConfig queryByUid(Long uid);

    void update(TUserDataConfig config );

    AjaxResult update(UserDataConfVo object);
}

package com.ruoyi.game.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.game.domain.request.LoginRequest;

import javax.servlet.http.HttpServletRequest;


/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
public interface IForeignSesrvice
{
    AjaxResult channelHandle(LoginRequest loginRequest, HttpServletRequest httpServletRequest) throws Exception;
}

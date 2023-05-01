package com.ruoyi.common.exception.user;

/**
 * 谷歌验证码错误异常类
 * 
 * @author ruoyi
 */
public class GoogleCodeException extends UserException
{
    private static final long serialVersionUID = 1L;

    public GoogleCodeException()
    {
        super("user.googleCode.error", null);
    }
}

package com.ruoyi.game.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.game.domain.User;
import com.ruoyi.game.mapper.UserMapper;
import com.ruoyi.game.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * 用户Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
@Service
public class UserServiceImpl  extends ServiceImpl<UserMapper, User> implements IUserService
{
}

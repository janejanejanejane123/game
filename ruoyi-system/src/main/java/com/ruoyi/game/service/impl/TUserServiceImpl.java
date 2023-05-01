package com.ruoyi.game.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.game.domain.TUser;
import com.ruoyi.game.mapper.TUserMapper;
import com.ruoyi.game.service.ITUserService;
import org.springframework.stereotype.Service;

/**
 * 用户Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
@Service
public class TUserServiceImpl  extends ServiceImpl<TUserMapper, TUser> implements ITUserService
{
}

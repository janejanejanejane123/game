package com.ruoyi.game.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.game.domain.UserAuthRel;
import com.ruoyi.game.mapper.UserAuthRelMapper;
import com.ruoyi.game.service.IUserAuthRelService;
import org.springframework.stereotype.Service;

/**
 * 用户验证关联Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-04-28
 */
@Service
public class UserAuthRelServiceImpl  extends ServiceImpl<UserAuthRelMapper, UserAuthRel> implements IUserAuthRelService
{
}

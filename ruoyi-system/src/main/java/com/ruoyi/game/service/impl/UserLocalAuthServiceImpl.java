package com.ruoyi.game.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.game.domain.UserLocalAuth;
import com.ruoyi.game.mapper.UserLocalAuthMapper;
import com.ruoyi.game.service.IUserLocalAuthService;
import org.springframework.stereotype.Service;

/**
 * 本地用户Service业务层处理
 *
 * @author ruoyi
 * @date 2023-04-28
 */
@Service
public class UserLocalAuthServiceImpl extends ServiceImpl<UserLocalAuthMapper, UserLocalAuth> implements IUserLocalAuthService {
}

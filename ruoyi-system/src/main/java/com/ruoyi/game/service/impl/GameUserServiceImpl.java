package com.ruoyi.game.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.game.domain.GameUser;
import com.ruoyi.game.mapper.GameUserMapper;
import com.ruoyi.game.service.IGameUserService;
import org.springframework.stereotype.Service;

/**
 * 游戏用户 服务层
 *
 * @author ruoyi
 */
@Service
public class GameUserServiceImpl extends ServiceImpl<GameUserMapper, GameUser> implements IGameUserService {

}

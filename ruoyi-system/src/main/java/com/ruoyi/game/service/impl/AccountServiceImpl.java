package com.ruoyi.game.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.game.domain.Account;
import com.ruoyi.game.mapper.AccountMapper;
import com.ruoyi.game.service.IAccountService;
import org.springframework.stereotype.Service;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2023-04-28
 */
@Service("accountService")
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements IAccountService {
}

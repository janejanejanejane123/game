package com.ruoyi.pay.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.util.StringUtil;
import com.ruoyi.common.enums.PayResponEnums;
import com.ruoyi.common.enums.TreasureTypeEnum;
import com.ruoyi.common.enums.UserOrderConst;
import com.ruoyi.common.enums.WalletEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ValidateUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.hash.Block;
import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.pay.domain.Treasure;
import com.ruoyi.pay.domain.vo.WalletVo;
import com.ruoyi.pay.service.ITreasureService;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import com.ruoyi.pay.mapper.WalletMapper;
import com.ruoyi.pay.domain.Wallet;
import com.ruoyi.pay.service.IWalletService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 钱包Service业务层处理
 *
 * @author ruoyi
 * @date 2022-03-08
 */
@Service
public class WalletServiceImpl implements IWalletService {

    private Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);
    // 保留的有效位
    public static final Integer DECIMAL_PLACE = 2;
    @Autowired
    private WalletMapper walletMapper;
    @Autowired
    private ITreasureService iTreasureService;
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 查询钱包
     *
     * @param id 钱包主键
     * @return 钱包
     */
    @Override
    public Wallet selectWalletById(Long id) {
        return walletMapper.selectWalletById(id);
    }

    @Override
    public Wallet selectWlletByaddress(String address) {
        return walletMapper.selectWalletByaddress(address);
    }

    /**
     * 查询钱包列表
     *
     * @param wallet 钱包
     * @return 钱包
     */
    @Override
    public List<Wallet> selectWalletList(Wallet wallet) {
        return walletMapper.selectWalletList(wallet);
    }

    /**
     * 新增钱包
     *
     * @param wallet 钱包
     * @return 结果
     */
    @Override
    public int insertWallet(Wallet wallet) {
        wallet.setId(snowflakeIdUtils.nextId());
        Block block = new Block(wallet.getUid() + "", wallet.getName());
        wallet.setAddress(block.getHash());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setFrozenBalance(BigDecimal.ZERO);
        wallet.setUptime(new Date());
        wallet.setDepositBalance(BigDecimal.ZERO);
        wallet.setBuyAmount(BigDecimal.ZERO);
        return walletMapper.insertWallet(wallet);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWalletByUid(WalletVo walletVo) {
        //验证实体
        logger.info("更新钱包VO:{}", JSON.toJSONString(walletVo));
        ValidateUtils.validateEntity(walletVo);
        WalletEnum walletEnum = walletVo.getTreasureTypeEnum().getWalletEnum();
        BigDecimal buAmont = BigDecimal.ZERO;
        if (walletVo.getTreasureTypeEnum() == TreasureTypeEnum.RECHARGE || walletVo.getTreasureTypeEnum() == TreasureTypeEnum.BALANCE_OUT) {
            buAmont = walletVo.getAmount();
        }
        Long uid = walletVo.getUid();
        Wallet wallet = selectWalletByUid(uid);
        String treType = walletVo.getTreasureTypeEnum().getType();
        BigDecimal commission = walletVo.getFee();
        BigDecimal subtract = treType.equals("1") ? walletVo.getAmount().subtract(commission) : walletVo.getAmount().add(commission);
        BigDecimal oldBalance = wallet.getBalance();
        //通过枚举类型进行钱包操作
        switchWallet(walletEnum, uid, wallet, subtract, buAmont);
        Wallet newWallet = selectWalletByUid(uid);
        BigDecimal newBalance = newWallet.getBalance();
        if (!walletEnum.equals(WalletEnum.DEDUCT_COST)) {
            insertTreasure(walletVo, oldBalance, newBalance, wallet.getType().toString());
        }
        UserMailBox mailBox = new UserMailBox();
        String content = wallet.getType() == 1L ? newBalance + "" : newBalance + "|" + (newBalance.subtract(newWallet.getBuyAmount()));
        mailBox.setContent(content);
        mailBox.setUserIds(wallet.getUid() + "");
        mailBox.setTopic(UserOrderConst.TOPIC_WALLET);
        MessageBuilder builder = MessageBuilder.withPayload(mailBox);
        mailBox.setUserType(wallet.getType().intValue());
        rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder.build());
        return 1;
    }

    private void switchWallet(WalletEnum walletEnum, Long uid, Wallet wallet, BigDecimal subtract, BigDecimal buyAmount) {
        switch (walletEnum) {
            case FREEZE:
                this.verifyAndFreezeUserWalletByUserId(uid, subtract.toString(), wallet);
                break;
            case SUBTRACT:
                this.subtractBalanceUserWalletByUserId(uid, subtract.toString(), wallet, buyAmount);
                break;
            case DEDUCT_COST:
                this.deductUserCostAmountByUserId(uid, subtract.toString(), wallet);
                break;
            case INCREASE:
                this.increaseBalanceUserWalletByUserId(uid, subtract.toString(), wallet, buyAmount);
                break;
            case RETURN_FREEZE:
                this.returnFreezeUserWalletByUserId(uid, subtract.toString(), wallet);
                break;
            case DEDUCT_DEPOSIT:
                this.deductDeposit(uid, subtract.toString(), wallet);
                break;
            case RETURN_DEPOSIT:
                this.returnDepositWallet(uid, subtract.toString(), wallet);
                break;
            case DEPOSIT:
                this.depositWallet(uid, subtract.toString(), wallet);
                break;
            case FREEZE_DEPOSIT:
                this.freezeDeposit(uid, subtract.toString(), wallet);
            default:
                throw new ServiceException("暂不支持此方式");
        }
    }


    @Override
    public Wallet selectWalletByUid(Long uid) {
        return walletMapper.selectWalletByUid(uid);
    }

    @Override
    @Cacheable(unless = "#result == null", key = "'t_wallet:queryMerchantName:'+#uid", value = "redisCache4Spring")
    public String selectMerchantByUid(Long uid) {
        return walletMapper.selectMerchantByUid(uid);
    }

    @Override
    public Wallet selectWalletSum(Wallet wallet) {
        return walletMapper.selectWalletSum(wallet);
    }

    @Override
    public Wallet selectByName(String name) {
        Wallet wallet = new Wallet();
        wallet.setName(name);
        List<Wallet> wallets = walletMapper.selectWalletList(wallet);
        return wallets.size() > 0 ? wallets.get(0) : null;
    }

    int insertTreasure(WalletVo walletVo, BigDecimal oldAmount, BigDecimal newAmount, String accountType) {
        Treasure treasure = new Treasure();
        treasure.setUid(walletVo.getUid());
        String treType = walletVo.getTreasureTypeEnum().getType();
        BigDecimal amount = treType.equals("1") ? walletVo.getAmount() : walletVo.getAmount().divide(new BigDecimal("-1"));
        treasure.setAmount(amount);
        treasure.setOldMoney(oldAmount);
        treasure.setNewMoney(newAmount);
        treasure.setType(walletVo.getTreasureTypeEnum().getCode());
        treasure.setRmk(walletVo.getRemark());
        treasure.setName(walletVo.getUserName());
        treasure.setIp(walletVo.getIp());
        treasure.setNumber(walletVo.getRefId());
        treasure.setFee(walletVo.getFee());
        treasure.setSysname(walletVo.getSysName());
        treasure.setAccountType(accountType);
        treasure.setMerId(walletVo.getMerId());
        return iTreasureService.insertTreasure(treasure);

    }

    /**
     * 冻结金额
     *
     * @param userId
     * @param amount
     * @param
     * @return
     */
    private void verifyAndFreezeUserWalletByUserId(Long userId, String amount, final Wallet wallet) {
        BigDecimal amountInt = new BigDecimal(amount).setScale(DECIMAL_PLACE, BigDecimal.ROUND_DOWN);
        amount = amountInt.toString();
        BigDecimal avaAmtInt = wallet.getBalance();

        Wallet modifyAccount = new Wallet();
        modifyAccount.setUid(userId);
        if (amountInt.compareTo(avaAmtInt) <= 0) {
            // 修改用户余额信息 可用金额-amount，冻结金额+amount，充值总金额、消费总金额不变
            BigDecimal avaamt = avaAmtInt.subtract(amountInt);
            BigDecimal frzAmt = wallet.getFrozenBalance().add(amountInt);
            wallet.setBalance(avaamt);
            wallet.setFrozenBalance(frzAmt);

            modifyAccount.setBalance(new BigDecimal(amount).multiply(new BigDecimal("-1")));
            modifyAccount.setFrozenBalance(new BigDecimal(amount));
            modifyAccount.setUptime(new Date());
        } else {
            String reMsg = "余额不足";
            logger.error("冻结金额失败=====>用户ID:{}-错误消息resultMap:{}", userId, reMsg);
            throw new ServiceException(reMsg, PayResponEnums.BALANCE_NO.getCode());
        }

        int updateResult = walletMapper.modifyWalletByUserId(modifyAccount);
        if (updateResult != 1) {
            throw new ServiceException("冻结余额失败", 500);
        }

    }

    /**
     * 退还金额
     *
     * @param userId
     * @param amount
     * @param
     * @return
     */
    private void returnFreezeUserWalletByUserId(Long userId, String amount, Wallet wallet) {
        BigDecimal amountInt = new BigDecimal(amount).setScale(DECIMAL_PLACE, BigDecimal.ROUND_DOWN);
        amount = amountInt.toString();
        BigDecimal frzamtInt = wallet.getFrozenBalance();
        if (amountInt.compareTo(frzamtInt) <= 0) {
            Wallet modifyAccount = new Wallet();
            modifyAccount.setUid(userId);
            // 退回可用余额
            BigDecimal avaAmtInt = wallet.getBalance();
            BigDecimal avaamt = avaAmtInt.add(amountInt);
            wallet.setBalance(avaamt);
            modifyAccount.setBalance(amountInt);
            // 修改用户余额信息 可用金额+amount，冻结金额-amount，充值总金额、消费总金额不变
            BigDecimal frzAmt = frzamtInt.subtract(amountInt);
            wallet.setFrozenBalance(frzAmt);
            modifyAccount.setUptime(new Date());
            modifyAccount.setFrozenBalance(new BigDecimal(amount).multiply(new BigDecimal("-1")));
            int updateResult = walletMapper.modifyWalletByUserId(modifyAccount);

            if (updateResult != 1) {
                logger.error("返还冻结金额失败=====>用户ID-返还金额amount:{}-失败原因:{}", userId, amount);
                throw new ServiceException("返还冻结金额失败", 500);
            }
        } else {
            logger.error("返还冻结金额失败=====>用户ID{}-返还金额amount:{}-失败原因:{}", userId, amount, "用户余额不足");
            throw new ServiceException("账户可用余额不足", PayResponEnums.BALANCE_NO.getCode());
        }

    }


    /**
     * 交易成功扣款
     *
     * @param userId
     * @param amount
     * @param
     * @return
     */
    private void deductUserCostAmountByUserId(Long userId, String amount, Wallet wallet) {
        BigDecimal amountInt = new BigDecimal(amount).setScale(DECIMAL_PLACE, BigDecimal.ROUND_DOWN);
        amount = amountInt.toString();
        BigDecimal frzamtInt = wallet.getFrozenBalance();
        if (amountInt.compareTo(frzamtInt) <= 0) {
            String frzamt = frzamtInt.subtract(amountInt).toString();
            wallet.setFrozenBalance(new BigDecimal(frzamt));

            Wallet modifyAccount = new Wallet();
            modifyAccount.setUid(userId);
            modifyAccount.setFrozenBalance(new BigDecimal(amount).multiply(new BigDecimal("-1")));
            modifyAccount.setUptime(new Date());
            int updateResult = walletMapper.modifyWalletByUserId(modifyAccount);
            if (updateResult != 1) {
                String reMsg = "扣除金额失败";
                logger.error(reMsg);
                throw new ServiceException(reMsg, 500);
            }
        } else {
            logger.error("扣除冻结金额失败=====>用户ID{}-返还金额amount:{}-失败原因:{}", userId, amount, "您的余额不足");
            throw new ServiceException("账户余额不足", PayResponEnums.BALANCE_NO.getCode());
        }
    }


    /**
     * 直接增加余额
     *
     * @param uid
     * @param amount
     * @return
     */
    private void increaseBalanceUserWalletByUserId(Long uid, String amount, Wallet wallet, BigDecimal buyAmount) {
        BigDecimal amountInt = new BigDecimal(amount).setScale(DECIMAL_PLACE, BigDecimal.ROUND_DOWN);
        amount = amountInt.toString();
        BigDecimal avaAmtInt = wallet.getBalance();
        Wallet modifyAccount = new Wallet();
        modifyAccount.setUid(uid);
        // 修改用户余额信息 可用金额-amount，冻结金额+amount，充值总金额、消费总金额不变
        BigDecimal avaamt = avaAmtInt.add(amountInt);
        wallet.setBalance(avaamt);
        modifyAccount.setBalance(new BigDecimal(amount));
        modifyAccount.setUptime(new Date());
        modifyAccount.setBuyAmount(buyAmount);
        int updateResult = walletMapper.modifyWalletByUserId(modifyAccount);
        if (updateResult != 1) {
            throw new ServiceException("增加余额失败", 500);
        }
    }


    /**
     * 直接减余额
     *
     * @param uid
     * @param amount
     * @return
     */
    private void subtractBalanceUserWalletByUserId(Long uid, String amount, final Wallet wallet, BigDecimal buyAmount) {
        BigDecimal amountInt = new BigDecimal(amount).setScale(DECIMAL_PLACE, BigDecimal.ROUND_DOWN);
        amount = amountInt.toString();
        BigDecimal avaAmtInt = wallet.getBalance();
        if (amountInt.compareTo(avaAmtInt) <= 0) {
            Wallet modifyAccount = new Wallet();
            modifyAccount.setUid(uid);
            if (amountInt.compareTo(avaAmtInt) <= 0) {
                // 修改用户余额信息 可用金额-amount，冻结金额+amount，充值总金额、消费总金额不变
                BigDecimal avaamt = avaAmtInt.subtract(amountInt);
                wallet.setBalance(avaamt);
                modifyAccount.setBalance(new BigDecimal(amount).multiply(new BigDecimal("-1")));
                modifyAccount.setUptime(new Date());
                BigDecimal buy = wallet.getBuyAmount().compareTo(buyAmount) < 0 ? wallet.getBuyAmount() : buyAmount;
                modifyAccount.setBuyAmount(buy.multiply(new BigDecimal("-1")));
            }
            int updateResult = walletMapper.modifyWalletByUserId(modifyAccount);
            if (updateResult != 1) {
                throw new ServiceException("扣除余额失败", 500);
            }
        } else {
            String reMsg = "余额不足";
            logger.error("扣除余额失败=====>用户ID:{}-错误消息resultMap:{}", uid, reMsg);
            throw new ServiceException("账户可用余额不足", PayResponEnums.BALANCE_NO.getCode());
        }
    }

    /**
     * 余额转到保证金
     *
     * @param userId
     * @param amount
     * @param
     * @return
     */
    private void depositWallet(Long userId, String amount, final Wallet wallet) {
        BigDecimal amountInt = new BigDecimal(amount).setScale(DECIMAL_PLACE, BigDecimal.ROUND_DOWN);
        amount = amountInt.toString();
        BigDecimal avaAmtInt = wallet.getBalance();
        if (amountInt.compareTo(avaAmtInt) > 0) {
            String reMsg = "余额不足";
            logger.error("转保证金失败=====>用户ID:{}-错误消息resultMap:{}", userId, reMsg);
            throw new ServiceException(reMsg, PayResponEnums.BALANCE_NO.getCode());
        }

        Wallet modifyAccount = new Wallet();
        modifyAccount.setUid(userId);
        if (amountInt.compareTo(avaAmtInt) <= 0) {
            // 修改用户余额信息 可用金额-amount，冻结金额+amount，充值总金额、消费总金额不变
            BigDecimal avaamt = avaAmtInt.subtract(amountInt);
            BigDecimal frzAmt = wallet.getDepositBalance().add(amountInt);
            wallet.setBalance(avaamt);
            wallet.setFrozenBalance(frzAmt);

            modifyAccount.setBalance(new BigDecimal(amount).multiply(new BigDecimal("-1")));
            modifyAccount.setDepositBalance(new BigDecimal(amount));
            modifyAccount.setUptime(new Date());
            int updateResult = walletMapper.modifyWalletByUserId(modifyAccount);
            if (updateResult != 1) {
                throw new ServiceException("转保证金失败", 500);
            }
        } else {
            String reMsg = "余额不足";
            logger.error("转保证金失败=====>用户ID:{}-错误消息resultMap:{}", userId, reMsg);
            throw new ServiceException(reMsg, PayResponEnums.BALANCE_NO.getCode());
        }


    }


    /**
     * 退还压金
     *
     * @param userId
     * @param amount
     * @param
     * @return
     */
    private void returnDepositWallet(Long userId, String amount, Wallet wallet) {
        BigDecimal amountInt = new BigDecimal(amount).setScale(DECIMAL_PLACE, BigDecimal.ROUND_DOWN);
        amount = amountInt.toString();
        BigDecimal frzamtInt = wallet.getDepositBalance();
        if (amountInt.compareTo(frzamtInt) <= 0) {
            Wallet modifyAccount = new Wallet();
            modifyAccount.setUid(userId);
            // 退回可用余额
            BigDecimal avaAmtInt = wallet.getBalance();
            BigDecimal avaamt = avaAmtInt.add(amountInt);
            wallet.setBalance(avaamt);
            modifyAccount.setBalance(amountInt);

            // 修改用户余额信息 可用金额+amount，冻结金额-amount，充值总金额、消费总金额不变
            BigDecimal frzAmt = frzamtInt.subtract(amountInt);
            wallet.setFrozenBalance(frzAmt);
            modifyAccount.setUptime(new Date());
            modifyAccount.setDepositBalance(new BigDecimal(amount).multiply(new BigDecimal("-1")));
            int updateResult = walletMapper.modifyWalletByUserId(modifyAccount);

            if (updateResult != 1) {
                logger.error("返还保证金额失败=====>用户ID-返还金额amount:{}-失败原因:{}", userId, amount);
                throw new ServiceException("返还冻结金额失败", 500);
            }
        } else {
            logger.error("返还保证金额失败=====>用户ID{}-返还金额amount:{}-失败原因:{}", userId, amount, "用户余额不足");
            throw new ServiceException("用户账号可用余额不足", PayResponEnums.BALANCE_NO.getCode());
        }

    }

    /**
     * 扣除压金
     *
     * @param userId
     * @param amount
     * @param
     * @return
     */
    private void deductDeposit(Long userId, String amount, Wallet wallet) {
        BigDecimal amountInt = new BigDecimal(amount).setScale(DECIMAL_PLACE, BigDecimal.ROUND_DOWN);
        amount = amountInt.toString();
        BigDecimal frzamtInt = wallet.getFrozenBalance();

        if (amountInt.compareTo(frzamtInt) <= 0) {
            String frzamt = frzamtInt.subtract(amountInt).toString();
            wallet.setFrozenBalance(new BigDecimal(frzamt));

            Wallet modifyAccount = new Wallet();
            modifyAccount.setUid(userId);
            modifyAccount.setDepositBalance(new BigDecimal(amount).multiply(new BigDecimal("-1")));
            modifyAccount.setUptime(new Date());
            int updateResult = walletMapper.modifyWalletByUserId(modifyAccount);
            if (updateResult != 1) {
                String reMsg = "扣除保证金失败";
                logger.error(reMsg);
                throw new ServiceException(reMsg, 500);
            }
        } else {
            String reMsg = "余额不足";
            logger.error("扣除冻结金额失败=====>用户ID{}-返还金额amount:{}-失败原因:{}", userId, amount, reMsg);
            throw new ServiceException(reMsg, PayResponEnums.BALANCE_NO.getCode());
        }
    }

    /**
     * 冻结金额
     *
     * @param userId
     * @param amount
     * @param
     * @return
     */
    private void freezeDeposit(Long userId, String amount, final Wallet wallet) {
        BigDecimal amountInt = new BigDecimal(amount).setScale(DECIMAL_PLACE, BigDecimal.ROUND_DOWN);
        amount = amountInt.toString();
        BigDecimal avaAmtInt = wallet.getBalance();

        Wallet modifyAccount = new Wallet();
        modifyAccount.setUid(userId);
        if (amountInt.compareTo(avaAmtInt) <= 0) {
            // 修改用户余额信息 可用金额-amount，冻结金额+amount，充值总金额、消费总金额不变
            BigDecimal avaamt = avaAmtInt.subtract(amountInt);
            BigDecimal frzAmt = wallet.getFrozenBalance().add(amountInt);
            wallet.setBalance(avaamt);
            wallet.setFrozenBalance(frzAmt);

            modifyAccount.setDepositBalance(new BigDecimal(amount).multiply(new BigDecimal("-1")));
            modifyAccount.setFrozenBalance(new BigDecimal(amount));
            modifyAccount.setUptime(new Date());
        } else {
            String reMsg = "余额不足";
            logger.error("冻结金额失败=====>用户ID:{}-错误消息resultMap:{}", userId, reMsg);
            throw new ServiceException(reMsg, PayResponEnums.BALANCE_NO.getCode());
        }

        int updateResult = walletMapper.modifyWalletByUserId(modifyAccount);
        if (updateResult != 1) {
            throw new ServiceException("冻结余额失败", 500);
        }

    }
}

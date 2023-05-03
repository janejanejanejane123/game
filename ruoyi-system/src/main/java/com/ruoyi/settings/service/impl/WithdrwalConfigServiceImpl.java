package com.ruoyi.settings.service.impl;

import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.enums.UserOrderConst;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.order.domain.UserOrder;
import com.ruoyi.order.service.IUserOrderService;
import com.ruoyi.pay.domain.WithdrawalRecord;
import com.ruoyi.pay.service.IWithdrawalRecordService;
import com.ruoyi.settings.domain.WithdrwalConfig;
import com.ruoyi.settings.mapper.WithdrwalConfigMapper;
import com.ruoyi.settings.service.IWithdrwalConfigService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 提现配置Service业务层处理
 *
 * @author ruoyi
 * @date 2022-09-13
 */
@Service
public class WithdrwalConfigServiceImpl implements IWithdrwalConfigService {
    private Logger logger = LoggerFactory.getLogger(WithdrwalConfigServiceImpl.class);
    @Resource
    private WithdrwalConfigMapper withdrwalConfigMapper;
    @Resource
    private IWithdrawalRecordService iWithdrawalRecordService;
    @Resource
    private IUserOrderService iUserOrderService;

    /**
     * 查询提现配置
     *
     * @param id 提现配置主键
     * @return 提现配置
     */
    @Override
    public WithdrwalConfig selectWithdrwalConfigById(Integer id) {
        return withdrwalConfigMapper.selectWithdrwalConfigById(id);
    }

    /**
     * 查询提现配置列表
     *
     * @param withdrwalConfig 提现配置
     * @return 提现配置
     */
    @Override
    public List<WithdrwalConfig> selectWithdrwalConfigList(WithdrwalConfig withdrwalConfig) {
        return withdrwalConfigMapper.selectWithdrwalConfigList(withdrwalConfig);
    }

    /**
     * 新增提现配置
     *
     * @param withdrwalConfig 提现配置
     * @return 结果
     */
    @Override
    public int insertWithdrwalConfig(WithdrwalConfig withdrwalConfig) {
        withdrwalConfig.setCreateTime(DateUtils.getNowDate());
        return withdrwalConfigMapper.insertWithdrwalConfig(withdrwalConfig);
    }

    /**
     * 修改提现配置
     *
     * @param withdrwalConfig 提现配置
     * @return 结果
     */
    @Override
    public int updateWithdrwalConfig(WithdrwalConfig withdrwalConfig) {
        return withdrwalConfigMapper.updateWithdrwalConfig(withdrwalConfig);
    }

    /**
     * 批量删除提现配置
     *
     * @param ids 需要删除的提现配置主键
     * @return 结果
     */
    @Override
    public int deleteWithdrwalConfigByIds(Integer[] ids) {
        return withdrwalConfigMapper.deleteWithdrwalConfigByIds(ids);
    }

    /**
     * 删除提现配置信息
     *
     * @param id 提现配置主键
     * @return 结果
     */
    @Override
    public int deleteWithdrwalConfigById(Integer id) {
        return withdrwalConfigMapper.deleteWithdrwalConfigById(id);
    }

    @Override
    public boolean verUserisRecharge(String merNo, TUser tUser) {
        logger.info("验证提现限制:{},{}", merNo, tUser.getUsername());
        List<WithdrwalConfig> withdrawalsConfigList = getWithdrwalConfigs(merNo);
        for (WithdrwalConfig config : withdrawalsConfigList) {
            logger.info("验证姓名和账号是否在限制列表");
            boolean isNames = isNames(tUser, config);
            if (isNames) {
                Map map = getWithdSum(tUser.getUid(), config);
                logger.info("提现金额和次数{}", map);
                BigDecimal withSum = BigDecimal.ZERO;
                int withCount = 0;
                if (map != null && map.get("amountSum") != null) {
                    //提现金额
                    withSum = new BigDecimal(map.get("amountSum").toString());
                    //提现次数
                    withCount = Integer.valueOf(map.get("count").toString());
                }
                boolean b = compareWithdrAmount(tUser, config, withSum, withCount);
                if (!b) return false;
            }
        }
        return true;
    }

    private boolean compareWithdrAmount(TUser tUser, WithdrwalConfig config, BigDecimal withSum, int withCount) {
        //需要购买数量
        double sellAmountPercent = config.getSellAmountPercent().doubleValue() / 100;
        //提现次数 和 提现金额
        boolean c = config.getWithdrawalCount() != null && config.getWithdrawalCount() > 0 && withCount >= config.getWithdrawalCount();
        boolean a = config.getWithdrawalAmount() != null && config.getWithdrawalAmount().compareTo(BigDecimal.ZERO) > 0 && withSum.compareTo(config.getWithdrawalAmount()) >= 0;
        if (c || a) {
            BigDecimal sellAmountPercentAmountLimit = withSum.multiply(BigDecimal.valueOf(sellAmountPercent));
            BigDecimal userSellSum = getUserSellSum(tUser.getUid(), config);
            logger.info("已售金额{}", userSellSum);
            if (sellAmountPercentAmountLimit.compareTo(userSellSum) > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isNames(TUser tUser, WithdrwalConfig config) {
        //姓名和会员账号判断
        if (!StringUtils.isBlank(config.getUserNames())) {
            List split = Arrays.asList(config.getUserNames().split(","));
            if (split.contains(tUser.getUsername())) {
                return true;
            }
        }
        //真实姓名
        if (!StringUtils.isBlank(config.getRealName())) {
            List split = Arrays.asList(config.getRealName().split(","));
            if (split.contains(tUser.getRealname())) {
                return true;
            }
        }
        return false;
    }

    private List<WithdrwalConfig> getWithdrwalConfigs(String merNo) {
        WithdrwalConfig withdrwalConfig = new WithdrwalConfig();
        withdrwalConfig.setMerNo(merNo);
        withdrwalConfig.setStatus(0);
        //根据商户查提现配置
        List<WithdrwalConfig> withdrwalConfigs = withdrwalConfigMapper.selectWithdrwalConfigList(withdrwalConfig);
        return withdrwalConfigs;
    }

    @NotNull
    private Map getWithdSum(Long uid, WithdrwalConfig config) {
        WithdrawalRecord withdrawalRecord = new WithdrawalRecord();
        withdrawalRecord.setStartDate(DateUtils.addDay(DateUtils.getDateToDayTime(), (config.getDay() - 1) * -1));
        withdrawalRecord.setEndDate(DateUtils.getDateToDayTime());
        withdrawalRecord.setUid(uid);
        withdrawalRecord.setStatus(1);
        return iWithdrawalRecordService.selectWithdrSum(withdrawalRecord);
    }

    private BigDecimal getUserSellSum(Long uid, WithdrwalConfig config) {
        UserOrder userOrder = new UserOrder();
        userOrder.setUserId(uid);
        userOrder.setOrderStatus(UserOrderConst.ORDER_STATUS_FINISH);
        userOrder.setStartDate(DateUtils.addDay(DateUtils.getDateToDayTime(), (config.getDay() - 1) * -1));
        userOrder.setEndDate(DateUtils.getDateToDayTime());
        return iUserOrderService.selectUserOrderSum(userOrder);
    }
}

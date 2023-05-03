package com.ruoyi.pay.controller;

import java.math.BigDecimal;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateTime;
import com.ruoyi.common.constant.FeeTypeConstants;
import com.ruoyi.common.core.redis.RedisLock;
import com.ruoyi.common.enums.TreasureTypeEnum;
import com.ruoyi.common.enums.UserInfoApplyEnums;
import com.ruoyi.common.enums.UserOrderConst;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ValidateUtils;
import com.ruoyi.common.utils.google.GoogleAuthUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.uuid.Seq;
import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.member.util.RocketMqService;
import com.ruoyi.pay.domain.Wallet;
import com.ruoyi.pay.domain.vo.TransferRecordVo;
import com.ruoyi.pay.domain.vo.WalletVo;
import com.ruoyi.pay.service.IWalletService;
import com.ruoyi.settings.service.ISysServiceChargeService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.controller.member.TUserController;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pay.domain.TransferRecord;
import com.ruoyi.pay.service.ITransferRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 转账记录Controller
 *
 * @author ruoyi
 * @date 2022-07-29
 */
@RestController
@RequestMapping("/pay/transferRecord")
public class TransferRecordController extends BaseController {
    @Autowired
    private ITransferRecordService transferRecordService;
    @Autowired
    private IWalletService iWalletService;
    @Autowired
    private ISysServiceChargeService iSysServiceChargeService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private ISysUserService iSysUserService;
    @Resource
    private RocketMqService rocketMqService;


    /**
     * 查询转账记录列表
     */
    @PreAuthorize("@ss.hasPermi('pay:transferRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(TransferRecord transferRecord) {
        startPage();
        if (transferRecord.getStartDate() == null) {
            transferRecord.setStartDate(DateUtils.getDateToDayTime());
        }
        if (isMerchant()) {
            transferRecord.setTransefName(getUsername());
        }
        List<TransferRecord> list = transferRecordService.selectTransferRecordList(transferRecord);
        return getDataTable(list);
    }

    /**
     * 导出转账记录列表
     */
    @PreAuthorize("@ss.hasPermi('pay:transferRecord:export')")
    @Log(title = "转账记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TransferRecord transferRecord) {
        List<TransferRecord> list = transferRecordService.selectTransferRecordList(transferRecord);
        ExcelUtil<TransferRecord> util = new ExcelUtil<TransferRecord>(TransferRecord.class);
        util.exportExcel(response, list, "转账记录数据");
    }

    /**
     * 获取转账记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:transferRecord:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(transferRecordService.selectTransferRecordById(id));
    }

    /**
     * 新增转账记录
     */
    @PreAuthorize("@ss.hasPermi('pay:transferRecord:add')")
    @Log(title = "转账记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody TransferRecordVo transferRecord, HttpServletRequest request) {
        if (!isMerchant()) {
            return AjaxResult.error("管理员不能使用转账功能");
        }
        ValidateUtils.validateEntity(transferRecord);
        Wallet wallet = iWalletService.selectWlletByaddress(transferRecord.getAddress());
        //验证谷歌验证码
        validateGoogleCode(iSysUserService.selectUserByUserName(getLoginUser().getUsername()).getSecret(), transferRecord.getCode());
        //TODO 验证密码

        Assert.isNull(wallet, "收款人地址不存在");
        Assert.isTrue(wallet.getType() == 0, "不能转账给玩家");
        transferRecord.setTransefUid(getUserId());
        transferRecord.setTransefName(getUsername());
        transferRecord.setPayeeUid(wallet.getUid());
        transferRecord.setPayeeName(wallet.getName());
        transferRecord.setOrderno(Seq.getId());
        transferRecord.setVersion(0);
        BigDecimal userIdRateByFee = iSysServiceChargeService.getUserIdRateByFeeType(getUserId(), FeeTypeConstants.MERCHANT_ROLL_OUT);
        transferRecord.setFee(transferRecord.getAmount().multiply(new BigDecimal("0.001")).multiply(userIdRateByFee));

        WalletVo walletVo = new WalletVo();
        walletVo.setUid(transferRecord.getTransefUid());
        walletVo.setRemark("转账");
        walletVo.setUserName(transferRecord.getTransefName());
        walletVo.setFee(transferRecord.getFee());
        walletVo.setRefId(transferRecord.getOrderno());
        walletVo.setTreasureTypeEnum(TreasureTypeEnum.MERCHANT_TREASUE_OUT);
        walletVo.setIp(IpUtils.getIpAddr(request));
        walletVo.setAmount(transferRecord.getAmount());
        iWalletService.updateWalletByUid(walletVo);
        transferRecordService.insertTransferRecord(transferRecord);

        TransferRecord queryRecord = new TransferRecord();
        queryRecord.setStatus(0);
        int size = transferRecordService.selectTransferRecordList(queryRecord).size();
        rocketMqService.applyToAdmin(UserInfoApplyEnums.TRANSFER_VERIFY, size);
        return toAjax(1);
    }

    /**
     * 审核
     */
    @PreAuthorize("@ss.hasPermi('pay:transferRecord:edit')")
    @Log(title = "转账记录", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    @Transactional
    public AjaxResult edit(@RequestBody TransferRecord params, HttpServletRequest request) {
        if (isMerchant()) {
            return AjaxResult.error("非管理员不能使用转账功能");
        }
        TransferRecord transferRecord = transferRecordService.selectTransferRecordById(params.getId());
        Assert.isNull(transferRecord, "订单不存在");
        Assert.isFalse(transferRecord.getStatus().equals(0), "订单非审核状态");
        try {
            if (RedisLock.lock("TRANSSFER:" + params.getId() + "")) {
                transferRecord.setStatus(params.getStatus());
                transferRecord.setUpdateTime(new DateTime());
                transferRecord.setSysUsername(getUsername());
                int i = transferRecordService.updateTransferRecord(transferRecord);
                if (i > 0) {
                    if (params.getStatus().equals(1)) {
                        WalletVo walletVo = new WalletVo();
                        walletVo.setUid(transferRecord.getTransefUid());
                        walletVo.setRemark("转账");
                        walletVo.setUserName(transferRecord.getTransefName());
                        walletVo.setFee(transferRecord.getFee());
                        walletVo.setRefId(transferRecord.getOrderno());
                        walletVo.setTreasureTypeEnum(TreasureTypeEnum.MERCHANT_TREASUE_OUT_SUCCESS);
                        walletVo.setIp(IpUtils.getIpAddr(request));
                        walletVo.setAmount(transferRecord.getAmount());
                        iWalletService.updateWalletByUid(walletVo);


                        WalletVo payeeVo = new WalletVo();
                        payeeVo.setUid(transferRecord.getPayeeUid());
                        payeeVo.setRemark("转账收入");
                        payeeVo.setUserName(transferRecord.getPayeeName());
                        payeeVo.setFee(BigDecimal.ZERO);
                        payeeVo.setRefId(transferRecord.getOrderno());
                        payeeVo.setTreasureTypeEnum(TreasureTypeEnum.MERCHANT_TREASUE_IN);
                        payeeVo.setIp(IpUtils.getIpAddr(request));
                        payeeVo.setAmount(transferRecord.getAmount());
                        iWalletService.updateWalletByUid(payeeVo);
                    } else {
                        WalletVo walletVo = new WalletVo();
                        walletVo.setUid(transferRecord.getTransefUid());
                        walletVo.setRemark("转账");
                        walletVo.setUserName(transferRecord.getTransefName());
                        walletVo.setFee(transferRecord.getFee());
                        walletVo.setRefId(transferRecord.getOrderno());
                        walletVo.setTreasureTypeEnum(TreasureTypeEnum.MERCHANT_TREASUE_OUT_RETURN);
                        walletVo.setIp(IpUtils.getIpAddr(request));
                        walletVo.setAmount(transferRecord.getAmount());
                        iWalletService.updateWalletByUid(walletVo);
                    }
                }
                return AjaxResult.success("审核成功");
            }
        } finally {
            RedisLock.unLock();
        }
        return AjaxResult.error("请勿重复提交");
    }

    /**
     * 查询待审核的次数
     */
    @PreAuthorize("@ss.hasPermi('pay:transferRecord:edit')")
    @GetMapping("/queryNo")
    public AjaxResult queryNo() {
        TransferRecord transferRecord = new TransferRecord();
        transferRecord.setStatus(0);
        return AjaxResult.success(transferRecordService.selectTransferRecordList(transferRecord).size());
    }

    /**
     * 删除转账记录
     */
    @PreAuthorize("@ss.hasPermi('pay:transferRecord:remove')")
    @Log(title = "转账记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(transferRecordService.deleteTransferRecordByIds(ids));
    }

}

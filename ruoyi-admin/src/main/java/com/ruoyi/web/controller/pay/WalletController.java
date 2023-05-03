package com.ruoyi.web.controller.pay;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.FeeTypeConstants;
import com.ruoyi.common.constant.ReportConstant;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.member.TUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.enums.TreasureTypeEnum;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.uuid.Seq;
import com.ruoyi.member.service.ITUserService;
import com.ruoyi.pay.domain.TReport;
import com.ruoyi.pay.domain.Wallet;
import com.ruoyi.pay.domain.vo.WalletOptVo;
import com.ruoyi.pay.domain.vo.WalletVo;
import com.ruoyi.pay.service.ITReportService;
import com.ruoyi.pay.service.IWalletService;
import com.ruoyi.payment.service.IProcedureFeeService;
import com.ruoyi.payment.service.impl.ProcedureFeeServiceImpl;
import com.ruoyi.settings.service.ISysServiceChargeService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 钱包Controller
 *
 * @author ruoyi
 * @date 2022-03-08
 */
@RestController
@RequestMapping("/pay/wallet")
public class WalletController extends BaseController {
    @Autowired
    private IWalletService walletService;
    @Autowired
    private ITReportService itReportService;
    @Autowired
    private ISysServiceChargeService iSysServiceChargeService;
    @Autowired
    private ISysUserService iSysUserService;
    @Autowired
    private ITUserService itUserService;
    @Resource
    private IProcedureFeeService procedureFeeService;

    /**
     * 查询钱包列表
     */
    @PreAuthorize("@ss.hasPermi('pay:wallet:list')")
    @GetMapping("/list")
    public TableDataInfo list(Wallet wallet) {
        startPage();
        List<Wallet> list = walletService.selectWalletList(wallet);
        return getDataTable(list);
    }

    /**
     * 导出钱包列表
     */
    @PreAuthorize("@ss.hasPermi('pay:wallet:export')")
    @Log(title = "钱包", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Wallet wallet) {
        List<Wallet> list = walletService.selectWalletList(wallet);
        ExcelUtil<Wallet> util = new ExcelUtil<Wallet>(Wallet.class);
        util.exportExcel(response, list, "钱包数据");
    }

    /**
     * 获取钱包详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:wallet:query')")
    @GetMapping(value = "/queryBal")
    public AjaxResult queryBal() {
        if (isMerchant()) {
            return AjaxResult.success(walletService.selectWalletByUid(getUserId()));
        } else {
            return AjaxResult.success(walletService.selectWalletSum(new Wallet()));
        }
    }

    /**
     * 获取钱包详细信息
     */
    @PreAuthorize("@ss.hasPermi('payment:order:list')")
    @GetMapping(value = "/queryDfBal")
    public AjaxResult queryDfBal() {
        Wallet wallet = walletService.selectWalletByUid(getUserId());
        return AjaxResult.success(wallet == null ? null : wallet.getBalance());
    }


    /**
     * 获取钱包详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:wallet:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(walletService.selectWalletById(id));
    }

    /**
     * 加款
     */
    @PreAuthorize("@ss.hasPermi('pay:wallet:add')")
    @Log(title = "钱包上分", businessType = BusinessType.UPDATE)
    @PostMapping(value = "add")
    public AjaxResult add(@RequestBody WalletOptVo walletOptVo, HttpServletRequest request) {
        if (isMerchant()) {
            return AjaxResult.error("非管理员不能使用转账功能");
        }

        //验证谷歌验证码
        validateGoogleCode(iSysUserService.selectUserByUserName(getLoginUser().getUsername()).getSecret(), walletOptVo.getCode());
        WalletVo walletVo = creatWalletVo(walletOptVo, request);
        if (walletOptVo.getType().equals("1")) {
            walletVo.setTreasureTypeEnum(TreasureTypeEnum.MERCHANT_RECHARGE);
            SysUser sysUser = iSysUserService.selectUserById(walletOptVo.getUid());
            if (sysUser.getUserType().equals("22")) {
                BigDecimal userRate = procedureFeeService.getUserRate(walletOptVo.getUid()).multiply(walletOptVo.getAmount()).multiply(new BigDecimal("0.001")).setScale(2, BigDecimal.ROUND_UP);
                walletVo.setFee(userRate);
            } else {
                walletVo.setFee(selectFee(FeeTypeConstants.MERCHANT_RECHARGE, walletVo.getAmount(), walletVo.getUid()));
            }
        } else {
            walletVo.setTreasureTypeEnum(TreasureTypeEnum.ADMIN_RECHARGE);
        }
        walletVo.setRemark(StringUtils.isBlank(walletVo.getRemark()) ? TreasureTypeEnum.ADMIN_RECHARGE.getMsg() : walletVo.getRemark());
        //更新报表
        if (walletOptVo.getType().equals("1")) {
            saveReport(walletVo.getAmount(), walletVo.getFee(), ReportConstant.MER_RE, walletVo.getUserName());
        }
        return toAjax(walletService.updateWalletByUid(walletVo));
    }

    /**
     * 扣款
     */
    @PreAuthorize("@ss.hasPermi('pay:wallet:subtract')")
    @Log(title = "钱包下分", businessType = BusinessType.UPDATE)
    @PostMapping("/subtract")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult subtract(@RequestBody WalletOptVo walletOptVo, HttpServletRequest request) {
        if (isMerchant()) {
            return AjaxResult.error("非管理员不能使用转账功能");
        }
        //验证谷歌验证码
        validateGoogleCode(iSysUserService.selectUserByUserName(getLoginUser().getUsername()).getSecret(), walletOptVo.getCode());
        WalletVo walletVo = creatWalletVo(walletOptVo, request);
        if (walletOptVo.getType().equals("1")) {
            walletVo.setTreasureTypeEnum(TreasureTypeEnum.MERCHANT_WITHDRAW);
            walletVo.setFee(selectFee(FeeTypeConstants.MERCHANT_WITHDRAW, walletVo.getAmount(), walletVo.getUid()));
        } else {
            walletVo.setTreasureTypeEnum(TreasureTypeEnum.ADMIN_DEDUCE);
        }
        walletVo.setRemark(StringUtils.isBlank(walletVo.getRemark()) ? TreasureTypeEnum.ADMIN_DEDUCE.getMsg() : walletVo.getRemark());
        walletService.updateWalletByUid(walletVo);
        if (walletOptVo.getType().equals("1")) {
            saveReport(walletVo.getAmount(), walletVo.getFee(), ReportConstant.MER_TX, walletVo.getUserName());
        }
        return toAjax(1);
    }

    public WalletVo creatWalletVo(WalletOptVo walletOptVo, HttpServletRequest request) {
        Assert.isNull(walletOptVo.getUid(), "参数错误");
        Assert.isBlank(walletOptVo.getName(), "参数错误");
        Assert.isNull(walletOptVo.getAmount(), "参数错误");
        Wallet model = walletService.selectWalletById(walletOptVo.getId());
        Assert.isNull(model, "该用户不存在");
        Assert.isNull(!model.getName().equals(walletOptVo.getName()), "参数错误");

        WalletVo walletVo = new WalletVo();
        walletVo.setUid(walletOptVo.getUid());
        walletVo.setAmount(walletOptVo.getAmount());
        walletVo.setRemark(walletOptVo.getRemark());
        walletVo.setRefId(Seq.getId());
        walletVo.setUserName(walletOptVo.getName());
        walletVo.setSysName(getUsername());
        walletVo.setIp(IpUtils.getIpAddr(request));
        walletVo.setFee(BigDecimal.ZERO);
        if (model.getType() == 0) {
            walletVo.setMerId(itUserService.selectTUserByUid(model.getUid()).getMerchantId());
        } else {
            walletVo.setMerId(walletVo.getUid());
        }
        return walletVo;
    }

    private void saveReport(BigDecimal amount, BigDecimal fee, String key, String merNo) {
        TReport tReport = new TReport();
        tReport.setAmount(amount);
        tReport.setFee(fee);
        tReport.setKey(key);
        tReport.setPeriod(new Date());
        tReport.setMerNo(merNo);
        itReportService.save(tReport);
    }

    /**
     * 查询手续费
     *
     * @param key
     * @param amount
     * @param uid
     * @return
     */
    private BigDecimal selectFee(String key, BigDecimal amount, Long uid) {
        BigDecimal fee = iSysServiceChargeService.getUserIdRateByFeeType(uid, key);
        return amount.multiply(new BigDecimal("0.001")).multiply(fee);
    }

}

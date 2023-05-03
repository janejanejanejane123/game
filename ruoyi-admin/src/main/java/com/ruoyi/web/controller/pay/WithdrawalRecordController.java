package com.ruoyi.web.controller.pay;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ruoyi.pay.domain.WithdrawalRecord;
import com.ruoyi.pay.service.IWithdrawalRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 提现记录Controller
 *
 * @author ry
 * @date 2022-03-26
 */
@RestController
@RequestMapping("/pay/withdRecord")
public class WithdrawalRecordController extends BaseController {
    @Autowired
    private IWithdrawalRecordService withdrawalRecordService;

    /**
     * 查询提现记录列表
     */
    @PreAuthorize("@ss.hasPermi('pay:withdrecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(WithdrawalRecord withdrawalRecord) {
        startPage();
        if (isMerchant()) {
            withdrawalRecord.setMid(getLoginUser().getUserId());
        }
        if (withdrawalRecord.getStartDate() == null) {
            withdrawalRecord.setStartDate(DateUtils.getDateToDayTime());
        }
        List<WithdrawalRecord> list = withdrawalRecordService.selectWithdrawalRecordList(withdrawalRecord);
        Map map = withdrawalRecordService.selectWithdrSum(withdrawalRecord);
        TableDataInfo tableDataInfo = getDataTable(list);
        tableDataInfo.setSum(map == null ? "0" : map.get("amountSum") + "");
        return tableDataInfo;
    }

    /**
     * 导出提现记录列表
     */
    @PreAuthorize("@ss.hasPermi('pay:withdrecord:export')")
    @Log(title = "提现记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WithdrawalRecord withdrawalRecord) {
        if (isMerchant()) {
            withdrawalRecord.setMid(getUserId());
        }
        List<WithdrawalRecord> list = withdrawalRecordService.selectWithdrawalRecordList(withdrawalRecord);
        ExcelUtil<WithdrawalRecord> util = new ExcelUtil<WithdrawalRecord>(WithdrawalRecord.class);
        util.exportExcel(response, list, "提现记录数据");
    }

    /**
     * 获取提现记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:withdrecord:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(withdrawalRecordService.selectWithdrawalRecordById(id));
    }


    /**
     * 获取提现记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:withdrecord:sum')")
    @GetMapping(value = "/getWithdCount")
    public AjaxResult getWithdCount() {
        if (isMerchant()) {
            WithdrawalRecord record = new WithdrawalRecord();
            record.setMid(getUserId());
            return AjaxResult.success(withdrawalRecordService.selectWithdCount(record));
        } else {
            return AjaxResult.success(withdrawalRecordService.selectWithdCount(new WithdrawalRecord()));
        }

    }


}

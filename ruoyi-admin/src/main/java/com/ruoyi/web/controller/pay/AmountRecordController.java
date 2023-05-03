package com.ruoyi.web.controller.pay;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pay.domain.AmountRecord;
import com.ruoyi.pay.service.IAmountRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 额度转换Controller
 *
 * @author
 * @date 2022-03-13
 */
@RestController
@RequestMapping("/pay/record")
public class AmountRecordController extends BaseController {
    @Autowired
    private IAmountRecordService amountRecordService;

    /**
     * 查询额度转换列表
     */
    @PreAuthorize("@ss.hasPermi('pay:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(AmountRecord amountRecord) {
        startPage();
        if (isMerchant()) {
            amountRecord.setMid(getUserId());
            amountRecord.setStatus(1);
        } else {
            if (amountRecord.getStatus() == null) {
                amountRecord.setStatus(1);
            }
        }
        if (StringUtils.isNull(amountRecord.getStartDate())) {
            amountRecord.setStartDate(DateUtils.getDateToDayTime());
        }
        List<AmountRecord> list = amountRecordService.selectAmountRecordList(amountRecord);
        Map map = amountRecordService.selectAmountRecordSum(amountRecord);
        TableDataInfo dataTable = getDataTable(list);
        dataTable.setSum(map == null ? "0" : map.get("amountSum") + "");
        return dataTable;
    }

    /**
     * 导出额度转换列表
     */
    @PreAuthorize("@ss.hasPermi('pay:record:export')")
    @Log(title = "额度转换", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AmountRecord amountRecord) {
        if (isMerchant()) {
            amountRecord.setMid(getUserId());
            amountRecord.setStatus(1);
        }
        List<AmountRecord> list = amountRecordService.selectAmountRecordList(amountRecord);
        ExcelUtil<AmountRecord> util = new ExcelUtil<AmountRecord>(AmountRecord.class);
        util.exportExcel(response, list, "额度转换数据");
    }

    /**
     * 获取额度转换详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(amountRecordService.selectAmountRecordById(id));
    }

    /**
     * 获取额度转换详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:record:sum')")
    @GetMapping(value = "/getRechCount")
    public AjaxResult getRechCount() {
        if (isMerchant()) {
            AmountRecord record = new AmountRecord();
            record.setMid(getUserId());
            return AjaxResult.success(amountRecordService.selectRechCount(record));
        } else {
            return AjaxResult.success(amountRecordService.selectRechCount(new AmountRecord()));
        }

    }
}

package com.ruoyi.web.controller.pay;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pay.domain.Treasure;
import com.ruoyi.pay.domain.vo.WalletVo;
import com.ruoyi.pay.service.ITreasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 交易流水Controller
 *
 * @author
 * @date 2022-03-12
 */
@RestController
@RequestMapping("/pay/treasure")
public class TreasureController extends BaseController {
    @Autowired
    private ITreasureService treasureService;

    @Autowired
    SnowflakeIdUtils snowflakeIdUtils;

    /**
     * 查询交易流水列表
     */
    @PreAuthorize("@ss.hasPermi('pay:treasure:list')")
    @GetMapping("/list")
    public TableDataInfo list(Treasure treasure) {
        startPage();
        if (StringUtils.isNull(treasure.getStartDate())) {
            treasure.setStartDate(DateUtils.getDateToDayTime());
        }
        if (isMerchant()) {
            treasure.setMerId(getLoginUser().getUserId());
        }
        List<Treasure> list = treasureService.selectTreasureList(treasure);
        if(isMerchant()){
            list.forEach(item->{
                item.setSysname("");
            });
        }
        Map map = treasureService.selectTreasureSum(treasure);
        TableDataInfo dataTable = getDataTable(list);
        dataTable.put("amountSum", map == null ? "0" : map.get("amountSum"));
        dataTable.put("feeSum", map == null ? "0" : map.get("feeSum"));
        return dataTable;
    }

    /**
     * 导出交易流水列表
     */
    @PreAuthorize("@ss.hasPermi('pay:treasure:export')")
    @Log(title = "交易流水", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Treasure treasure) {
        if (isMerchant()) {
            treasure.setMerId(getLoginUser().getUserId());
        }
        List<Treasure> list = treasureService.selectTreasureList(treasure);
        ExcelUtil<Treasure> util = new ExcelUtil<Treasure>(Treasure.class);
        util.exportExcel(response, list, "交易流水数据");
    }

    /**
     * 获取交易流水详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:treasure:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(treasureService.selectTreasureById(id));
    }


//    @PostMapping("test")
//    public AjaxResult addTest()
//    {
//        for (int i = 0; i < 10; i++) {
//            Treasure treasure = new Treasure();
//            BigDecimal decimal = BigDecimal.ONE.multiply(new BigDecimal(i));
//            treasure.setId(snowflakeIdUtils.nextId());
//            treasure.setUid(i*1L);
//            treasure.setCreateTime(new Date());
//            treasure.setAmount(decimal);
//            treasure.setOldMoney(BigDecimal.ZERO);
//            treasure.setNewMoney(decimal);
//            treasure.setCreateTime(new Date());
//            treasure.setType(1L);
//            treasure.setRmk("测试");
//            treasure.setName("jy");
//            treasure.setAddTime(new Date());
//            treasure.setNumber(i + "");
//            treasure.setCreateTime(new Date());
//            treasureService.insertTreasure(treasure);
//        }
//        return AjaxResult.success();
//    }

    /**
     * 修改交易流水
     */
    @PreAuthorize("@ss.hasPermi('pay:treasure:edit')")
    @Log(title = "交易流水", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Treasure treasure) {
        return toAjax(treasureService.updateTreasure(treasure));
    }

    /**
     * 删除交易流水
     */
    @PreAuthorize("@ss.hasPermi('pay:treasure:remove')")
    @Log(title = "交易流水", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(treasureService.deleteTreasureByIds(ids));
    }
}

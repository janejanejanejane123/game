package com.ruoyi.web.controller.order;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.order.domain.GrabOrder;
import com.ruoyi.order.service.IGrabOrderService;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * grabController
 * 
 * @author ry
 * @date 2022-07-15
 */
@RestController
@RequestMapping("/grab/order")
public class GrabOrderController extends BaseController
{
    @Resource
    private IGrabOrderService grabOrderService;

    /**
     * 查询grab列表
     */
    @PreAuthorize("@ss.hasPermi('grab:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(GrabOrder grabOrder)
    {
        startPage();
        List<GrabOrder> list = grabOrderService.selectGrabOrderList(grabOrder);
        return getDataTable(list);
    }

    /**
     * 导出grab列表
     */
    @PreAuthorize("@ss.hasPermi('grab:order:export')")
    @Log(title = "grab", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GrabOrder grabOrder)
    {
        List<GrabOrder> list = grabOrderService.selectGrabOrderList(grabOrder);
        ExcelUtil<GrabOrder> util = new ExcelUtil<GrabOrder>(GrabOrder.class);
        util.exportExcel(response, list, "grab数据");
    }

    /**
     * 按天统计报表
     */
    @PreAuthorize("@ss.hasPermi('grab:order:report')")
    @Log(title = "grab", businessType = BusinessType.GRANT)
    @PostMapping("/report")
    public void report(String date)
    {
        
    }

    /**
     * 获取grab详细信息
     */
    @PreAuthorize("@ss.hasPermi('grab:order:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(grabOrderService.selectGrabOrderById(id));
    }

    /**
     * 新增grab
     */
    @PreAuthorize("@ss.hasPermi('grab:order:add')")
    @Log(title = "grab", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GrabOrder grabOrder)
    {
        return toAjax(grabOrderService.insertGrabOrder(grabOrder));
    }

    /**
     * 修改grab
     */
    @PreAuthorize("@ss.hasPermi('grab:order:edit')")
    @Log(title = "grab", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GrabOrder grabOrder)
    {
        return toAjax(grabOrderService.updateGrabOrder(grabOrder));
    }

    /**
     * 删除grab
     */
    @PreAuthorize("@ss.hasPermi('grab:order:remove')")
    @Log(title = "grab", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(grabOrderService.deleteGrabOrderByIds(ids));
    }
}

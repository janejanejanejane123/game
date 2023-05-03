package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.DictTypeConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典信息
 * 
 * @author nn
 */
@RestController
@RequestMapping("/web/dict/data")
public class DictDataController extends BaseController
{
    @Autowired
    private ISysDictDataService dictDataService;

    @Autowired
    private ISysDictTypeService dictTypeService;

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult dictType(@PathVariable String dictType)
    {
        List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
        if (StringUtils.isNull(data))
        {
            data = new ArrayList<SysDictData>();
        }
        return AjaxResult.success(data);
    }

    /**
     * 根据字典类型查询字典数据信息(趣图)
     */
    @GetMapping(value = "/dictTypeEmoji")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult dictTypeEmoji()
    {
        List<SysDictData> data = dictTypeService.selectDictDataByType(DictTypeConstants.EMOJI_TYPE);
        if (StringUtils.isNull(data))
        {
            data = new ArrayList<SysDictData>();
        }
        return AjaxResult.success(data);
    }

    /**
     * 根据字典类型查询字典数据信息(趣语)
     */
    @GetMapping(value = "/dictTypeLanguage")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult dictTypeLanguage()
    {
        List<SysDictData> data = dictTypeService.selectDictDataByType(DictTypeConstants.T_INTEREST_LANGUAGE);
        if (StringUtils.isNull(data))
        {
            data = new ArrayList<SysDictData>();
        }
        return AjaxResult.success(data);
    }

    /**
     * 根据字典类型查询字典数据信息(敏感词类型-举报类型) sensitive_word_type
     */
    @GetMapping(value = "/dictTypeReport")
    @PreAuthorize("@ss.playerOnly()")
    public AjaxResult dictTypeReport()
    {
        List<SysDictData> data = dictTypeService.selectDictDataByType(DictTypeConstants.SENSITIVE_WORD_TYPE);
        if (StringUtils.isNull(data))
        {
            data = new ArrayList<SysDictData>();
        }
        return AjaxResult.success(data);
    }

}

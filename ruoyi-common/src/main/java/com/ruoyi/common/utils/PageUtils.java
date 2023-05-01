package com.ruoyi.common.utils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.utils.sql.SqlUtil;

/**
 * 分页工具类
 *
 * @author ruoyi
 */
public class PageUtils extends PageHelper {
    /**
     * 设置请求分页数据
     */
    public static Page startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        Boolean reasonable = pageDomain.getReasonable();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            return PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
        }
        return PageHelper.startPage(1, 10, "").setReasonable(reasonable);
    }


    public static void startPage(long pageSize, long begin){
        int pageNo = 1;
        if (pageSize != 0) {
            pageNo = (int) Math.floor((begin * 1.0d) / pageSize) + 1;
        }
        PageHelper.startPage(pageNo,(int)pageSize, "").setReasonable(true);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage()
    {
        PageHelper.clearPage();
    }

}

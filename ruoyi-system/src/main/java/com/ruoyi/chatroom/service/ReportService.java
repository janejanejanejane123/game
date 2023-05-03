package com.ruoyi.chatroom.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;

import java.util.Map;

/**
 * 举报管理业务类
 * @author nn
 * @date 2022/07/19
 */
public interface ReportService {

    /**
     * 分页查询(举报列表)
     * @param params
     * @return
     */
    TableDataInfo queryPage(Map<String, Object> params);

    /**
     * 分页查询(消息详情)
     * @param params
     * @return
     */
    TableDataInfo messageList(Map<String, Object> params);

    /**
     * 删除
     * @param params
     * @return
     */
    AjaxResult remove(Map<String, Object> params);

    /**
     * 误报
     * @param params
     * @return
     */
    AjaxResult recover(Map<String, Object> params);

}

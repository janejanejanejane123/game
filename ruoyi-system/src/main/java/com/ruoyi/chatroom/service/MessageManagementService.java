package com.ruoyi.chatroom.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;

import java.util.Map;

/**
 * 消息管理业务类
 * @author nn
 * @date 2022/07/29
 */
public interface MessageManagementService {

    /**
     * 分页查询
     * @param params
     * @return
     */
    TableDataInfo queryPage(Map<String, Object> params);

    /**
     * 删除
     * @param ids
     * @return
     */
    AjaxResult remove(Long[] ids);

}

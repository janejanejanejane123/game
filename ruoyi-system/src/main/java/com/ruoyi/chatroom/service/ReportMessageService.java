package com.ruoyi.chatroom.service;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.PageUtils;

import java.util.Map;

/**
 * @description: 举报记录业务类
 * @author: nn
 * @create: 2022-07-19 11:42
 **/
public interface ReportMessageService {

    /**
     * 分页查询举报记录.
     * @param params
     * @return
     */
    TableDataInfo queryPage(Map<String, Object> params);

    /**
     * 删除举报记录(逻辑删除).
     * @param ids  Id
     * @return
     */
    Long delete(Long[] ids);
}

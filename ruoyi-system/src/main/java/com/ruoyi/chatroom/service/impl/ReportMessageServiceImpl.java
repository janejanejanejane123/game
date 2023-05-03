package com.ruoyi.chatroom.service.impl;

import com.ruoyi.chatroom.db.domain.ReportMessage;
import com.ruoyi.chatroom.db.repository.ReportMessageRepository;
import com.ruoyi.chatroom.db.vo.ReportMessageVo;
import com.ruoyi.chatroom.service.ReportMessageService;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;
import com.ruoyi.common.utils.clazz.ClassUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 举报记录业务实现
 * @author: nn
 * @create: 2022-07-31 13:29
 **/
@Service
public class ReportMessageServiceImpl implements ReportMessageService {

    @Resource
    private ReportMessageRepository reportMessageRepository;

    @Override
    public TableDataInfo queryPage(Map<String, Object> params) {

        long pageNum = ClassUtil.formatObject(params.get("pageNum"),long.class);
        int pageSize = ClassUtil.formatObject(params.get("pageSize"),int.class);

        Long messageId = ClassUtil.formatObject(params.get("messageId"),Long.class);
        String userName = ClassUtil.formatObject(params.get("userName"),String.class);
        String informerName = ClassUtil.formatObject(params.get("informerName"),String.class);

        Criteria criteria = Criteria.where("messageId").is(messageId);
        //被举报人.
        if (StringUtils.isNotBlank(userName)) {
            criteria.and("userName").is(userName);
        }
        //举报人.
        if (StringUtils.isNotBlank(informerName)) {
            criteria.and("informerName").is(informerName);
        }

        Query query = new Query();
        query.addCriteria(criteria);

        TableDataInfo tableDataInfo = new TableDataInfo();

        int count = reportMessageRepository.queryReportMessageCount(query);
        List<ReportMessage> reportMessageList = new ArrayList<>();
        if (count > 0) {
            query.skip((pageNum-1)* pageSize);
            query.limit(pageSize);
            query.with(Sort.by(Sort.Order.desc("reportTime")));
            reportMessageList = reportMessageRepository.queryReportMessageList(query);
//            for(ReportMessage reportMessage : reportMessageList){
//                ReportMessageVo reportMessageVo = new ReportMessageVo();
//                CglibBeanCopierUtils.copyProperties(reportMessageVo, reportMessage);
//                reportMessageVoList.add(reportMessageVo);
//            }
        }
        tableDataInfo.setTotal(count);
        tableDataInfo.setRows(reportMessageList);
        return tableDataInfo;
    }

    @Override
    public Long delete(Long[] ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("reportId").in(ids));
        Update update = new Update();
        update.set("isDelete",1);
        return reportMessageRepository.updateReportMessage(query,update);
    }

}

package com.ruoyi.chatroom.db.repository;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.ruoyi.chatroom.db.domain.ReportMessage;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 举报记录
 * @author: nn
 * @create: 2022-07-19 11:42
 **/
@Service
@Lazy
public class ReportMessageRepository {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 查询举报记录集合.
     * @param query  查询对象.
     * @return
     */
    public List<ReportMessage> queryReportMessageList(Query query){
        List<ReportMessage> reportMessageList = mongoTemplate.find(query, ReportMessage.class);
        return reportMessageList;
    }

    /**
     * 查询举报记录数.
     * @param query  查询条件对象.
     * @return
     */
    public int queryReportMessageCount(Query query){
        long count = mongoTemplate.count(query, ReportMessage.class);
        return (int)count;
    }

    /**
     * 查询举报记录.
     * @param query  查询对象.
     * @return
     */
    public ReportMessage getReportMessage(Query query){
        ReportMessage reportMessage = mongoTemplate.findOne(query, ReportMessage.class);
        return reportMessage;
    }

    /**
     * 根据id查询举报记录.
     * @param reportId  举报Id.
     * @return
     */
    public ReportMessage getReportMessageByReportId(Long reportId){
        Query query = new Query();
        query.addCriteria(Criteria.where("reportId").is(reportId));
        ReportMessage reportMessage = mongoTemplate.findOne(query, ReportMessage.class);
        return reportMessage;
    }

    /**
     * 保存举报记录.
     * @param reportMessage  保存对象.
     * @return
     */
    public ReportMessage saveReportMessage(ReportMessage reportMessage){
        return mongoTemplate.insert(reportMessage);
    }

    /**
     * 修改举报记录.
     * @param query  查询条件对象.
     * @param update 修改赋值对象.
     * @return
     */
    public Long updateReportMessage(Query query, Update update){
        UpdateResult updateResult = mongoTemplate.updateFirst(query,update, ReportMessage.class);
        return updateResult.getModifiedCount();
    }

    /**
     * 删除举报记录
     * @param query
     */
    public Long deleteReportMessage(Query query){
        DeleteResult deleteResult = mongoTemplate.remove(query, ReportMessage.class);
        return deleteResult.getDeletedCount();
    }

}

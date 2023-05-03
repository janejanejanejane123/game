package com.ruoyi.chatroom.service.impl;

import com.ruoyi.chatroom.db.domain.ChatFriendRecord;
import com.ruoyi.chatroom.db.domain.ChatRoomRecord;
import com.ruoyi.chatroom.db.domain.ReportMessage;
import com.ruoyi.chatroom.db.repository.ReportMessageRepository;
import com.ruoyi.chatroom.db.repository.ReportRepostory;
import com.ruoyi.chatroom.db.vo.ChatFriendRecordVo;
import com.ruoyi.chatroom.db.vo.ChatRoomRecordVo;
import com.ruoyi.chatroom.service.ReportService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;
import com.ruoyi.common.utils.clazz.ClassUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
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
 * @description: 举报管理业务实现类.
 * @author: nn
 * @create: 2022-07-19 11:42
 **/
@Service
@Lazy
public class ReportServiceImpl implements ReportService {

    @Resource
    private ReportRepostory reportRepostory;

    @Resource
    private ReportMessageRepository reportMessageRepository;

    /**
     * 分页查询(举报列表)
     * @param params
     * @return
     */
    public TableDataInfo queryPage(Map<String, Object> params) {

        long pageNum = ClassUtil.formatObject(params.get("pageNum"),long.class);
        int pageSize = ClassUtil.formatObject(params.get("pageSize"),int.class);

        String reportSource = "hall";// ClassUtil.formatObject(params.get("reportSource"),String.class);
        String nickName = ClassUtil.formatObject(params.get("nickName"),String.class);
        String userName = ClassUtil.formatObject(params.get("userName"),String.class);
        Byte msgStatus = ClassUtil.formatObject(params.get("msgStatus"),Byte.class);

        Criteria criteria = Criteria.where("isReport").is(true);
        if(msgStatus != null){
            criteria.and("msgStatus").is(msgStatus);
        }
        if (StringUtils.isNotBlank(nickName)) {
            if("hall".equals(reportSource) ){
                criteria.and("nickName").is(nickName);
            }else if("friend".equals(reportSource)){
                criteria.and("sendUserNickName").is(nickName);
            }
        }
        if (StringUtils.isNotBlank(userName)) {
            if("hall".equals(reportSource) ){
                criteria.and("userName").is(userName);
            }else if("friend".equals(reportSource)){
                criteria.and("sendUserName").is(userName);
            }
        }
        Query query = new Query();
        query.addCriteria(criteria);

        /**
         * 大厅举报:hall.
         * 私聊举报:friend.
         */
        TableDataInfo tableDataInfo = new TableDataInfo();
        if("hall".equals(reportSource)){  //大厅举报
            int count = reportRepostory.selectChatRoomRecordCount(query);
            List<ChatRoomRecord> chatRoomRecordList = new ArrayList<>();
            if (count > 0) {
                query.skip((pageNum-1)* pageSize);
                query.limit(pageSize);
                query.with(Sort.by(Sort.Order.desc("sendTime")));
                chatRoomRecordList = reportRepostory.selectChatRoomRecordList(query);
//                for(ChatRoomRecord chatRoomRecord : chatRoomRecordList){
//                    ChatRoomRecordVo chatRoomRecordVo = new ChatRoomRecordVo();
//                    CglibBeanCopierUtils.copyProperties(chatRoomRecordVo, chatRoomRecord);
//                    chatRoomRecordVoList.add(chatRoomRecordVo);
//                }
            }

            tableDataInfo.setTotal(count);
            tableDataInfo.setRows(chatRoomRecordList);
            return tableDataInfo;
        }else if("friend".equals(reportSource)){  //私聊举报
            int count = reportRepostory.selectChatFriendRecordCount(query);
            List<ChatFriendRecord> chatFriendRecordList = new ArrayList<>();
            if (count > 0) {
                query.skip((pageNum-1)* pageSize);
                query.limit(pageSize);
                query.with(Sort.by(Sort.Order.desc("sendTime")));
                chatFriendRecordList = reportRepostory.selectChatFriendRecordList(query);
//                for(ChatFriendRecord chatFriendRecord : chatFriendRecordList){
//                    ChatFriendRecordVo chatFriendRecordVo = new ChatFriendRecordVo();
//                    CglibBeanCopierUtils.copyProperties(chatFriendRecordVo, chatFriendRecord);
//                    chatFriendRecordVoList.add(chatFriendRecordVo);
//                }
            }

            tableDataInfo.setTotal(count);
            tableDataInfo.setRows(chatFriendRecordList);
            return tableDataInfo;
        }else {  //其他
            throw new ServiceException("参数有误!");
        }
    }

    /**
     * 分页查询(消息详情)
     * @param params
     * @return
     */
    public TableDataInfo messageList(Map<String, Object> params) {

        long pageNum = ClassUtil.formatObject(params.get("pageNum"),long.class);
        int pageSize = ClassUtil.formatObject(params.get("pageSize"),int.class);

        Long reportId = ClassUtil.formatObject(params.get("reportId"), Long.class);
        String userName = ClassUtil.formatObject(params.get("userName"),String.class);
        String nickName = ClassUtil.formatObject(params.get("nickName"),String.class);


        Query queryReportMessage = new Query();
        Criteria criteriaReportMessage = Criteria.where("reportId").is(reportId);
        queryReportMessage.addCriteria(criteriaReportMessage);
        ReportMessage reportMessage = reportMessageRepository.getReportMessage(queryReportMessage);
        if(null == reportMessage){
            throw new ServiceException("没有举报的数据,无法查看详情!");
        }

        Criteria criteria = new Criteria();
        criteria.andOperator(
            new Criteria().orOperator(
                Criteria.where("userId").is(reportMessage.getUserId()),
                Criteria.where("userId").is(reportMessage.getInformerId())
            )
        );
        if (StringUtils.isNotBlank(userName)) {
            criteria.and("userName").is(userName);
        }

        if (StringUtils.isNotBlank(nickName)) {
            criteria.and("nickName").is(nickName);
        }

        Query query = new Query();
        query.addCriteria(criteria);
        TableDataInfo tableDataInfo = new TableDataInfo();
        int count = reportRepostory.selectChatRoomRecordCount(query);
        List<ChatRoomRecord> chatRoomRecordList = new ArrayList<>();
        if (count > 0) {
            query.skip((pageNum-1)* pageSize);
            query.limit(pageSize);
            query.with(Sort.by(Sort.Order.desc("sendTime")));
            chatRoomRecordList = reportRepostory.selectChatRoomRecordList(query);
//            for(ChatRoomRecord chatRoomRecord : chatRoomRecordList){
//                ChatRoomRecordVo chatRoomRecordVo = new ChatRoomRecordVo();
//                CglibBeanCopierUtils.copyProperties(chatRoomRecordVo, chatRoomRecord);
//                chatRoomRecordVoList.add(chatRoomRecordVo);
//            }
        }

        tableDataInfo.setTotal(count);
        tableDataInfo.setRows(chatRoomRecordList);
        return tableDataInfo;

    }

    /**
     * 删除
     * @param params
     * @return
     */
    public AjaxResult remove(Map<String, Object> params) {
        Long[] ids = ClassUtil.formatObject(params.get("ids"), Long[].class);

        String reportSource = ClassUtil.formatObject(params.get("reportSource"), String.class);
        Query query = new Query();
        Criteria criteria = Criteria.where("messageId").in(ids);
        query.addCriteria(criteria);
        Update update = new Update();
        update.set("msgStatus", (byte) 0);
        /**举报来源(1:聊天室,2：好友消息)*/
        byte source = 0;
        if("hall".equals(reportSource) ){
            source = 1;
            reportRepostory.updateChatRoomRecord(query,update);
        }else if("friend".equals(reportSource)){
            source = 2;
            reportRepostory.updateChatFriendRecord(query,update);
        }

        //删除举报详情记录.
        Query deleteCRRM = new Query();
        Criteria crrm = Criteria.where("messageId").in(ids).and("reportSource").is(source);
        deleteCRRM.addCriteria(crrm);
        reportMessageRepository.deleteReportMessage(deleteCRRM);
        return new AjaxResult();
    }

    /**
     * 误报.
     * @param params
     * @return
     */
    public AjaxResult recover(Map<String, Object> params) {
        Long[] ids = ClassUtil.formatObject(params.get("ids"), Long[].class);
        Query query = new Query();
        Criteria criteria = Criteria.where("messageId").in(ids);
        query.addCriteria(criteria);
        Update update = new Update();
        update.set("isReport", false);

        reportRepostory.updateChatRoomRecord(query,update);
        return new AjaxResult();
    }

}

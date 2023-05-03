package com.ruoyi.chatroom.service.impl;

import com.ruoyi.chatroom.db.domain.ChatFriendRecord;
import com.ruoyi.chatroom.db.domain.ChatRoomRecord;
import com.ruoyi.chatroom.db.domain.ReportMessage;
import com.ruoyi.chatroom.db.repository.MessageManagementRepostory;
import com.ruoyi.chatroom.db.repository.ReportMessageRepository;
import com.ruoyi.chatroom.db.vo.ChatFriendRecordVo;
import com.ruoyi.chatroom.db.vo.ChatRoomRecordVo;
import com.ruoyi.chatroom.service.MessageManagementService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.ChatMessageType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;
import com.ruoyi.common.utils.clazz.ClassUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 消息管理业务实现类.
 * @author: nn
 * @create: 2022-07-29 11:42
 **/
@Service
@Lazy
public class MessageManagementServiceImpl implements MessageManagementService {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource
    private MessageManagementRepostory messageManagementRepostory;

    @Resource
    private ReportMessageRepository reportMessageRepository;

    /**
     * 分页查询(消息列表)
     * @param params
     * @return
     */
    public TableDataInfo queryPage(Map<String, Object> params) {

        long pageNum = ClassUtil.formatObject(params.get("pageNum"),long.class);
        int pageSize = ClassUtil.formatObject(params.get("pageSize"),int.class);

        String userName = ClassUtil.formatObject(params.get("userName"),String.class);
        String nickName = ClassUtil.formatObject(params.get("nickName"),String.class);
        Byte msgStatus = ClassUtil.formatObject(params.get("msgStatus"),Byte.class);
        Date startTime = ClassUtil.formatObject(params.get("sendTime[0]"),Date.class);
        Date endTime = ClassUtil.formatObject(params.get("sendTime[1]"),Date.class);

        Criteria criteria = new Criteria();
        if(msgStatus != null){
            criteria.and("msgStatus").is(msgStatus);
        }
        if (StringUtils.isNotBlank(userName)) {
            criteria.and("userName").is(userName);
        }
        if (StringUtils.isNotBlank(nickName)) {
            criteria.and("nickName").is(nickName);
        }
        if(startTime != null && startTime != null){
            criteria.andOperator(
                    criteria.where("sendTime").gte(startTime),
                    criteria.where("sendTime").lte(endTime)
            );
        }

        Query query = new Query();
        query.addCriteria(criteria);

        int count =messageManagementRepostory.selectChatRoomRecordCount(query);
        List<ChatRoomRecord> chatRoomRecordList = new ArrayList<>();
        if (count > 0) {
            query.skip((pageNum-1)* pageSize);
            query.limit(pageSize);
            query.with(Sort.by(Sort.Order.desc("sendTime")));
            chatRoomRecordList = messageManagementRepostory.selectChatRoomRecordList(query);
//            for (ChatRoomRecord chatRoomRecord : chatRoomRecordList) {
//                ChatRoomRecordVo chartRoomRecordVo  = new ChatRoomRecordVo();
//                CglibBeanCopierUtils.copyProperties(chartRoomRecordVo, chatRoomRecord);
//                chartRoomRecordVoList.add(chartRoomRecordVo);
//            }
        }
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setTotal(count);
        tableDataInfo.setRows(chatRoomRecordList);
        return tableDataInfo;
    }

    /**
     * 删除大厅消息
     * @param ids
     * @return
     */
    public AjaxResult remove(Long[] ids) {
        Criteria criteria = Criteria.where("messageId").in(ids);
        Query query = new Query();
        query.addCriteria(criteria);
        Update update = new Update();
        update.set("msgStatus", (byte) 0);
        messageManagementRepostory.updateChatRoomRecord(query,update);
        return new AjaxResult();
    }

}

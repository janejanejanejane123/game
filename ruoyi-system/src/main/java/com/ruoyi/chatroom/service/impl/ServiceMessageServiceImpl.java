package com.ruoyi.chatroom.service.impl;

import com.ruoyi.chatroom.db.domain.ChatFriendRecord;
import com.ruoyi.chatroom.db.repository.ServiceMessageRepostory;
import com.ruoyi.chatroom.db.vo.ChatFriendRecordVo;
import com.ruoyi.chatroom.service.IChatServeCustomerService;
import com.ruoyi.chatroom.service.ServiceMessageService;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;
import com.ruoyi.common.utils.clazz.ClassUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @description: 客服消息业务实现类.
 * @author: nn
 * @create: 2022-07-26 11:42
 **/
@Service
@Lazy
public class ServiceMessageServiceImpl implements ServiceMessageService {

    @Resource
    private ServiceMessageRepostory serviceMessageRepostory;

    @Resource
    private IChatServeCustomerService  chatServeCustomerService;

    /**
     * 分页查询(消息列表)
     * @param params
     * @return
     */
    public TableDataInfo queryPage(Map<String, Object> params) {

        long pageNum = ClassUtil.formatObject(params.get("pageNum"),long.class);
        int pageSize = ClassUtil.formatObject(params.get("pageSize"),int.class);

        int recently = ClassUtil.formatObject(params.get("recently"),int.class);

        TableDataInfo tableDataInfo = new TableDataInfo();

        //查看最近一天，三天，七天私聊记录.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -recently);
        Date sendTime = calendar.getTime();

        List<Long> serveCustomerIdList = chatServeCustomerService.serveCustomerUserIdList();
        Criteria criteria = Criteria.where("sendUserId").in(serveCustomerIdList).and("sendTime").gte(sendTime);

        String[] groupKeys = new String[2];
        groupKeys[0] = "userIdentifier";
        groupKeys[1] = "friendIdentifier";

        GroupOperation groupOp = Aggregation.group(groupKeys);

        //总记录数.
        Aggregation aggregationCount = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC,"sendTime"),
                groupOp.first("sendTime").as("sendTime")
        );
        int count =serviceMessageRepostory.selectAggregationResultsConut(aggregationCount);
        if(count == 0 ){
            return  tableDataInfo;
        }

        //分页查询.
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                //降序排序取分组第一条.
                Aggregation.sort(Sort.Direction.DESC,"sendTime"),
                //取分组排序后的第一条数据.
                groupOp.first("sendTime").as("sendTime"),
                //分页数据整体排序.
                Aggregation.sort(Sort.Direction.DESC,"sendTime"),
                Aggregation.skip((pageNum-1)* pageSize),
                Aggregation.limit(pageSize)
        );

        AggregationResults<ChatFriendRecord> aggregationResults =serviceMessageRepostory.selectAggregationResults(aggregation);
        List<ChatFriendRecord> data = aggregationResults.getMappedResults();

        if(data == null || data.size() == 0){
            return tableDataInfo;
        }

//        List<String> userIdentifier = new ArrayList<>();
//        List<String> friendIdentifier = new ArrayList<>();
//
//        List<ChatFriendRecordVo> chatFriendRecordVoList = new ArrayList<>();
//        for(ChatFriendRecord friendChatRecord : data){
//            userIdentifier.add(friendChatRecord.getSendUserIdentifier());
//            friendIdentifier.add(friendChatRecord.getFriendIdentifier());
//
//            Criteria criteria1 = Criteria.where("userIdentifier").is(friendChatRecord.getSendUserIdentifier()).and("friendIdentifier").is(friendChatRecord.getFriendIdentifier()).and("sendTime").is(friendChatRecord.getSendTime());
//            Query query = new Query();
//            query.addCriteria(criteria1);
//            ChatFriendRecord friendChatRecord1 = serviceMessageRepostory.getChatFriendRecord(query);
//            //赋值对象
//            ChatFriendRecordVo chatFriendRecordVo  = new ChatFriendRecordVo();
//            CglibBeanCopierUtils.copyProperties(chatFriendRecordVo, friendChatRecord1);
//            chatFriendRecordVoList.add(chatFriendRecordVo);
//        }

        tableDataInfo.setTotal(count);
        tableDataInfo.setRows(data);
        return tableDataInfo;

    }

    /**
     * 客服对话列表
     * @param params
     * @return
     */
    public List<ChatFriendRecord> dialogueList(Map<String, Object> params) {

        String userIdentifier = ClassUtil.formatObject(params.get("userIdentifier"),String.class);
        String friendIdentifier = ClassUtil.formatObject(params.get("friendIdentifier"),String.class);

        //查看最近半个月的私聊记录.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -15);
        Date sendTime = calendar.getTime();

        Query query = new Query();
        Criteria criteria = Criteria.where("sendTime").gte(sendTime)
                .andOperator(
                        new Criteria().orOperator(
                                Criteria.where("userIdentifier").is(userIdentifier).and("friendIdentifier").is(friendIdentifier),
                                Criteria.where("userIdentifier").is(friendIdentifier).and("friendIdentifier").is(userIdentifier)
                        )
                );
        query.addCriteria(criteria);
        query.with(Sort.by(
                Sort.Order.asc("sendTime")
        ));

        List<ChatFriendRecord> friendChatRecordList = serviceMessageRepostory.selectFriendChatRecordList(query);
//        List<ChatFriendRecordVo> chatFriendRecordVoList = new ArrayList<>();
//        for (ChatFriendRecord friendChatRecord : friendChatRecordList) {
//            //赋值对象
//            ChatFriendRecordVo chatFriendRecordVo  = new ChatFriendRecordVo();
//            CglibBeanCopierUtils.copyProperties(chatFriendRecordVo, friendChatRecord);
//            chatFriendRecordVoList.add(chatFriendRecordVo);
//        }
        return friendChatRecordList;
    }

}

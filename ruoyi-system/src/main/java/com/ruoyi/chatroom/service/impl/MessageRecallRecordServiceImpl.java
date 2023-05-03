package com.ruoyi.chatroom.service.impl;

import com.ruoyi.chatroom.db.domain.MessageRecallRecord;
import com.ruoyi.chatroom.db.repository.MessageRecallRecordRepository;
import com.ruoyi.chatroom.db.vo.MessageRecallRecordVo;
import com.ruoyi.chatroom.service.MessageRecallRecordService;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.ChatMessageType;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.clazz.CglibBeanCopierUtils;
import com.ruoyi.common.utils.clazz.ClassUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 消息撤回 业务实现类
 * @author: nn
 * @create: 2022-07-19 11:42
 **/
@Service
@Lazy
public class MessageRecallRecordServiceImpl implements MessageRecallRecordService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private MessageRecallRecordRepository messageRecallRecordRepository;

    /**
     * @Description: 1:消息撤回记录.
     * @param params
     * @return
     */
    @Override
    public TableDataInfo queryPage(Map<String, Object> params) {

        long pageNum = ClassUtil.formatObject(params.get("pageNum"),long.class);
        int pageSize = ClassUtil.formatObject(params.get("pageSize"),int.class);

        Byte recallSource = ClassUtil.formatObject(params.get("recallSource"),byte.class);
        String recallUserName = ClassUtil.formatObject(params.get("recallUserName"),String.class);
        String sendUserName = ClassUtil.formatObject(params.get("sendUserName"),String.class);
        String sendNickName = ClassUtil.formatObject(params.get("sendNickName"),String.class);
        Byte recallType = ClassUtil.formatObject(params.get("recallType"),Byte.class);

        Criteria criteria = new Criteria();
        if(recallSource != null){
            criteria.and("recallSource").is(recallSource);
        }
        if(recallType != null){
            criteria.and("recallType").is(recallType);
        }
        if (StringUtils.isNotBlank(recallUserName)) {
            criteria.and("recallUserName").is(recallUserName);
        }
        if (StringUtils.isNotBlank(sendUserName)) {
            criteria.and("sendUserName").is(sendUserName);
        }
        if (StringUtils.isNotBlank(sendNickName)) {
            criteria.and("sendNickName").is(sendNickName);
        }

        Query query = new Query();
        query.addCriteria(criteria);

        int count = messageRecallRecordRepository.queryMessageRecallRecordCount(query);
        List<MessageRecallRecord> messageRecallRecordList = new ArrayList<>();
        if (count > 0) {
            query.skip((pageNum-1)* pageSize);
            query.limit(pageSize);
            query.with(Sort.by(Sort.Order.desc("recallTime")));
            messageRecallRecordList = messageRecallRecordRepository.queryMessageRecallRecordList(query);
//            for(MessageRecallRecord messageRecallRecord : messageRecallRecordList){
//                MessageRecallRecordVo messageRecallRecordVo = new MessageRecallRecordVo();
//                CglibBeanCopierUtils.copyProperties(messageRecallRecordVo, messageRecallRecord);
//                messageRecallRecordVoList.add(messageRecallRecordVo);
//            }
        }
        TableDataInfo tableDataInfo =new TableDataInfo();
        tableDataInfo.setTotal(count);
        tableDataInfo.setRows(messageRecallRecordList);
        return tableDataInfo;
    }

    /**
     * @Description:：保存撤回消息记录
     * @param messageRecallRecord
     * @return
     */
    @Override
    public MessageRecallRecord addMessageRecallRecord(MessageRecallRecord messageRecallRecord) {
        return messageRecallRecordRepository.saveMessageRecallRecord(messageRecallRecord);
    }
}

package com.ruoyi.web.mq;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.enums.UserOrderConst;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.member.service.IUserMailBoxService;
import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.order.domain.UserOrderDetail;
import com.ruoyi.order.domain.vo.OrderMessageVo;
import com.ruoyi.order.service.IUserOrderDetailService;
import com.ruoyi.order.service.IUserOrderService;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Component
@RocketMQMessageListener(topic = "ECOIN_CHANGE_STATUS", selectorExpression = "*", consumerGroup = "changeECoinOrderStatus")
public class RocketMQMsgListener implements RocketMQListener<OrderMessageVo> {

    final private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    RocketMQTemplate rocketMQTemplate;

    @Resource
    SnowflakeIdUtils snowflakeIdUtils;

    @Resource
    private IUserOrderDetailService userOrderDetailService;

    @Resource
    IUserOrderService userOrderService;

    @Resource
    IUserMailBoxService userMailBoxService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(OrderMessageVo obj) {
        Short afterStatus = obj.getOrderAfterStatus();
        Short beforeStatus = obj.getOrderBeforeStatus();
        String detailId = obj.getOrderDetailId();
        UserOrderDetail queryVo = new UserOrderDetail();
        queryVo.setPrimaryId(detailId);
        queryVo.setOrderCancelTime(new Date());
        queryVo.setPayWayId(beforeStatus);
        queryVo.setPayStatus(afterStatus);
        UserOrderDetail orderDetail = userOrderDetailService.selectUserOrderDetailByPrimaryId(detailId);
        if(orderDetail.getPayStatus().equals(beforeStatus)){
            int i = userOrderDetailService.updateStatus(queryVo);
            int x = userOrderService.changeOrderStatus(UserOrderConst.ORDER_STATUS_CREATE, obj.getOrderPoolId(), UserOrderConst.ORDER_STATUS_WAITING);
            //如果成功取消，更改抢单池中的注单重新分配
            if (i > 0 && x > 0) {
                //发送站内信和websocket消息
                if(detailId.startsWith("EPAYB")){
                    UserMailBox mailBox = new UserMailBox();
                    mailBox.setId(snowflakeIdUtils.nextId());
                    mailBox.setTitle("出售订单超时取消!");
                    mailBox.setContent("您有一笔出售订单" + orderDetail.getReferId() + "已经超时取消!");
                    mailBox.setUserNames(orderDetail.getSalerName());
                    mailBox.setUserIds(orderDetail.getSalerId() + "");
                    mailBox.setUserType(0);
                    mailBox.setCreatTime(new Date());
                    mailBox.setSendTime(new Date());
                    mailBox.setRemark(orderDetail.getPrimaryId());
                    mailBox.setState(0);
                    mailBox.setTopic(UserOrderConst.TOPIC);
                    userMailBoxService.insertUserMailBox(mailBox);
                    MessageBuilder builder = MessageBuilder.withPayload(mailBox);
                    SendResult result = rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder.build());
                    if (!SendStatus.SEND_OK.equals(result.getSendStatus())) {
                        logger.error("消息发送失败", JSON.toJSONString(mailBox));
                        throw new ServiceException("订单超时失败!");
                    }
                }
                UserMailBox mailBox1 = new UserMailBox();
                mailBox1.setId(snowflakeIdUtils.nextId());
                mailBox1.setTitle("购买订单超时取消!");
                mailBox1.setContent("您有一笔购买订单" + detailId + "已经超时取消!");
                mailBox1.setUserNames(orderDetail.getBuyerName());
                mailBox1.setUserIds(orderDetail.getBuyerId() + "");
                mailBox1.setUserType(0);
                mailBox1.setCreatTime(new Date());
                mailBox1.setSendTime(new Date());
                mailBox1.setRemark(orderDetail.getPrimaryId());
                mailBox1.setState(0);
                mailBox1.setTopic(UserOrderConst.TOPIC);
                userMailBoxService.insertUserMailBox(mailBox1);
                MessageBuilder builder1 = MessageBuilder.withPayload(mailBox1);
                SendResult result1 = rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder1.build());
                if (!SendStatus.SEND_OK.equals(result1.getSendStatus())) {
                    logger.error("消息发送失败", JSON.toJSONString(mailBox1));
                    throw new ServiceException("订单超时失败!");
                }
                logger.info("取消订单id:{}成功!", detailId);
            } else {
                throw new ServiceException(detailId + "订单超时取消失败");
            }
        }

    }
}

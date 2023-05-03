package com.ruoyi.web.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.enums.UserOrderConst;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.member.domain.UserMailBox;
import com.ruoyi.member.service.IUserMailBoxService;
import com.ruoyi.order.domain.GrabOrder;
import com.ruoyi.order.domain.UserOrderDetail;
import com.ruoyi.order.domain.vo.OrderMessageVo;
import com.ruoyi.order.service.IGrabOrderService;
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

import javax.annotation.Resource;
import java.util.Date;

@Component
@RocketMQMessageListener(topic = "GRAB_CHANGE_STATUS",selectorExpression="*",consumerGroup = "changeGrabOrderStatus")
public class GrabMQMsgListener implements RocketMQListener<OrderMessageVo> {

    final private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    IGrabOrderService grabOrderService;

    @Override
    public void onMessage(OrderMessageVo obj) {
        Short afterStatus = obj.getOrderAfterStatus();
        Short beforeStatus = obj.getOrderBeforeStatus();
        GrabOrder grabOrder = new GrabOrder();
        grabOrder.setId(obj.getOrderDetailId());
        grabOrder.setOrderStatus(afterStatus);
        grabOrder.setCancelTime(new Date());
        grabOrder.setRemark(beforeStatus + "");
        int i = grabOrderService.updateGrabOrderStatus(grabOrder);
        if(i > 0 && UserOrderConst.PAY_STATUS_CANCEL.equals(afterStatus) ){

        }
        if (i < 1) {
            logger.error("跑分抢单超时异常:", JSONObject.toJSONString(obj));
            throw new ServiceException("跑分抢单超时异常");
        }
    }
}
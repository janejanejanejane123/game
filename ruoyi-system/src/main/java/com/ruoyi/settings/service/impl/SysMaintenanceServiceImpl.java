package com.ruoyi.settings.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.constant.CacheKeyConstants;
import com.ruoyi.common.constant.TopicConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.member.domain.UserMailBox;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import com.ruoyi.settings.mapper.SysMaintenanceMapper;
import com.ruoyi.settings.domain.SysMaintenance;
import com.ruoyi.settings.service.ISysMaintenanceService;

import javax.annotation.Resource;

/**
 * 系统维护Service业务层处理
 * 
 * @author nn
 * @date 2022-04-12
 */
@Service
public class SysMaintenanceServiceImpl implements ISysMaintenanceService {

    private static Logger logger = LoggerFactory.getLogger(SysMaintenanceServiceImpl.class);

    @Autowired
    private SysMaintenanceMapper sysMaintenanceMapper;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private RedisCache redisCache;

    /**
     * 设置cache key
     *
     * @return 缓存键key
     */
    private String getCacheKey(String way)
    {
        return CacheKeyConstants.SYS_MAINTENANCE + way;
    }

    /**
     * 查询系统维护
     * 
     * @param id 系统维护主键
     * @return 系统维护
     */
    @Override
    public SysMaintenance selectSysMaintenanceById(Long id)
    {
        return sysMaintenanceMapper.selectSysMaintenanceById(id);
    }

    /**
     * 查询系统维护列表
     * 
     * @param sysMaintenance 系统维护
     * @return 系统维护
     */
    @Override
    public List<SysMaintenance> selectSysMaintenanceList(SysMaintenance sysMaintenance)
    {
        return sysMaintenanceMapper.selectSysMaintenanceList(sysMaintenance);
    }

    /**
     * 新增系统维护
     * 
     * @param sysMaintenance 系统维护
     * @return 结果
     */
    @Override
    public int insertSysMaintenance(SysMaintenance sysMaintenance)
    {
        return sysMaintenanceMapper.insertSysMaintenance(sysMaintenance);
    }

    /**
     * 修改系统维护
     * 
     * @param sysMaintenance 系统维护
     * @return 结果
     */
    @Override
    public int updateSysMaintenance(SysMaintenance sysMaintenance)
    {
        int row = sysMaintenanceMapper.updateSysMaintenance(sysMaintenance);
        if(row > 0){
            redisCache.deleteObject(getCacheKey("getMaintenance"));
            redisCache.deleteObject(getCacheKey("getSysMaintenance"));
        }

        return row;
    }

    /**
     * 批量删除系统维护
     * 
     * @param ids 需要删除的系统维护主键
     * @return 结果
     */
    @Override
    public int deleteSysMaintenanceByIds(Long[] ids)
    {
        return sysMaintenanceMapper.deleteSysMaintenanceByIds(ids);
    }

    /**
     * 删除系统维护信息
     * 
     * @param id 系统维护主键
     * @return 结果
     */
    @Override
    public int deleteSysMaintenanceById(Long id)
    {
        return sysMaintenanceMapper.deleteSysMaintenanceById(id);
    }

    /**
     * 获取维护信息(对象)
     * @return
     */
    @Override
    public SysMaintenance getMaintenance() {
        SysMaintenance sysMaintenance = redisCache.getCacheObject(getCacheKey("getMaintenance"));
        if(sysMaintenance == null){
            List<SysMaintenance>  list = sysMaintenanceMapper.selectSysMaintenanceList(new SysMaintenance());
            //只有一条数据.
            sysMaintenance = list.get(0);
            redisCache.setCacheObject(getCacheKey("getMaintenance"), sysMaintenance,3600 * 24, TimeUnit.SECONDS);
        }
        return sysMaintenance;
    }

    /**
     * 获取维护信息(状态)
     * @return
     */
    @Override
    public String getSysMaintenance() {
        String status = redisCache.getCacheObject(getCacheKey("getSysMaintenance"));
        if(status == null || "".equals(status)){
            SysMaintenance sysMaintenance = new SysMaintenance();
            List<SysMaintenance>  list = sysMaintenanceMapper.selectSysMaintenanceList(sysMaintenance);
            //只有一条数据.0=正常,1=关闭
            status = list.get(0).getStatus();
            redisCache.setCacheObject(getCacheKey("getSysMaintenance"), status,3600 * 24, TimeUnit.SECONDS);
        }

        return status;
    }

    /**
     * 修改维护状态
     *
     * @param sysMaintenance 用户信息
     * @return 结果
     */
    @Override
    public int updateStatus(SysMaintenance sysMaintenance)
    {
        int count = sysMaintenanceMapper.updateSysMaintenance(sysMaintenance);
        redisCache.deleteObject(getCacheKey("getMaintenance"));
        redisCache.deleteObject(getCacheKey("getSysMaintenance"));
        sysMaintenance = sysMaintenanceMapper.selectSysMaintenanceById(sysMaintenance.getId());
        UserMailBox mailBox = new UserMailBox();
        mailBox.setContent(sysMaintenance.getContent());
        //所有用户.
        mailBox.setUserIds(null);
        mailBox.setUserType(0);
        mailBox.setTopic(TopicConstants.SYSTEM_MAINTAIN);
        MessageBuilder builder = MessageBuilder.withPayload(mailBox);
        SendResult result = rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder.build());
        if (!SendStatus.SEND_OK.equals(result.getSendStatus())) {
            logger.error("系统维护-消息发送失败!");
        }
        redisCache.setCacheObject(getCacheKey("getMaintenance"), sysMaintenance,3600 * 24, TimeUnit.SECONDS);
        redisCache.setCacheObject(getCacheKey("getSysMaintenance"), sysMaintenance.getStatus(),3600 * 24, TimeUnit.SECONDS);
        return count;
    }

    /**
     * 定时任务调用
     */
    @Override
    public void getMaintenanceTask() {

        SysMaintenance sysMaintenance = redisCache.getCacheObject(getCacheKey("getMaintenance"));
        if(sysMaintenance == null){
            List<SysMaintenance>  list = sysMaintenanceMapper.selectSysMaintenanceList(new SysMaintenance());
            //只有一条数据.
            sysMaintenance = list.get(0);
            redisCache.setCacheObject(getCacheKey("getMaintenance"), sysMaintenance,3600 * 24, TimeUnit.SECONDS);
        }

        Date now = new Date();
        Date startTime = sysMaintenance.getStartTime();
        Date endTime = sysMaintenance.getEndTime();
        String status = sysMaintenance.getStatus();

        //int res = date1.compareTo(date2)，相等则返回0，date1大返回1，否则返回-1。
        int start = now.compareTo(startTime);
        int end = now.compareTo(endTime);
        //now < start - end   现在小于开始时间  说明未开启维护.
        //start - now - end   现在介于开始，结束之间 维护中.
        //start - end < now   现在大于结束时间  说明维护已结束.
        if(start < 0 || end > 0){     //现在小于开始时间，说明未开启维护. 现在大于结束时间  说明维护已结束.
            //0=正常,1=维护中.
            if("1".equals(status)){
                sysMaintenance.setStatus("0");
                sysMaintenanceMapper.updateSysMaintenance(sysMaintenance);
                //发送消息给用户.
                UserMailBox mailBox = new UserMailBox();
                mailBox.setContent(sysMaintenance.getContent());
                //所有用户
                mailBox.setUserIds(null);
                mailBox.setUserType(0);
                mailBox.setTopic(TopicConstants.SYSTEM_MAINTAIN);
                MessageBuilder builder = MessageBuilder.withPayload(mailBox);
                SendResult result = rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder.build());
                if (!SendStatus.SEND_OK.equals(result.getSendStatus())) {
                    logger.error("系统维护-消息发送失败!");
                }
            }
        }else if(start > 0 && end < 0 ){  //现在介于开始，结束之间 维护中.
            //0=正常,1=维护中.
            if("0".equals(status)){
                sysMaintenance.setStatus("1");
                sysMaintenanceMapper.updateSysMaintenance(sysMaintenance);
                //发送消息给用户.
                UserMailBox mailBox = new UserMailBox();
                mailBox.setContent(sysMaintenance.getContent());
                //所有用户
                mailBox.setUserIds(null);
                mailBox.setUserType(0);
                mailBox.setTopic(TopicConstants.SYSTEM_MAINTAIN);
                MessageBuilder builder = MessageBuilder.withPayload(mailBox);
                SendResult result = rocketMQTemplate.syncSend("ECOIN_MSG_TO_WEB:websocketMsg", builder.build());
                if (!SendStatus.SEND_OK.equals(result.getSendStatus())) {
                    logger.error("系统维护-消息发送失败!");
                }
            }
        }
    }
}

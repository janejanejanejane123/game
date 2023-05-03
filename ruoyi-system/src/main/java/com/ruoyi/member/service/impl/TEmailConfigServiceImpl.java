package com.ruoyi.member.service.impl;

import java.util.List;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.EmailEnums;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.member.domain.TEmailConfig;
import com.ruoyi.member.mapper.TEmailConfigMapper;
import com.ruoyi.member.service.ITEmailConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 邮箱配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-03-29
 */
@Service
@Slf4j
public class TEmailConfigServiceImpl implements ITEmailConfigService
{
    @Autowired
    private TEmailConfigMapper tEmailConfigMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;


    /**
     * 查询邮箱配置
     * 
     * @param id 邮箱配置主键
     * @return 邮箱配置
     */
    @Override
    public TEmailConfig selectTEmailConfigById(Long id)
    {
        return tEmailConfigMapper.selectTEmailConfigById(id);
    }

    /**
     * 查询邮箱配置列表
     * 
     * @param tEmailConfig 邮箱配置
     * @return 邮箱配置
     */
    @Override
    public List<TEmailConfig> selectTEmailConfigList(TEmailConfig tEmailConfig)
    {
        return tEmailConfigMapper.selectTEmailConfigList(tEmailConfig);
    }

    /**
     * 新增邮箱配置
     * 
     * @param tEmailConfig 邮箱配置
     * @return 结果
     */
    @Override
    public int insertTEmailConfig(TEmailConfig tEmailConfig)
    {
        tEmailConfig.setId(snowflakeIdUtils.nextId());
        tEmailConfig.setStatus((short)1);
        return tEmailConfigMapper.insertTEmailConfig(tEmailConfig);
    }

    /**
     * 修改邮箱配置
     * 
     * @param tEmailConfig 邮箱配置
     * @return 结果
     */
    @Override
    public int updateTEmailConfig(TEmailConfig tEmailConfig)
    {
        return tEmailConfigMapper.updateTEmailConfig(tEmailConfig);
    }

    /**
     * 批量删除邮箱配置
     * 
     * @param ids 需要删除的邮箱配置主键
     * @return 结果
     */
    @Override
    public int deleteTEmailConfigByIds(Long[] ids)
    {
        return tEmailConfigMapper.deleteTEmailConfigByIds(ids);
    }

    /**
     * 删除邮箱配置信息
     * 
     * @param id 邮箱配置主键
     * @return 结果
     */
    @Override
    public int deleteTEmailConfigById(Long id)
    {
        return tEmailConfigMapper.deleteTEmailConfigById(id);
    }




    private boolean sendEmail(TEmailConfig tEmailConfig, String toEmail,String messageText) {
        //发送的邮箱

        String from = tEmailConfig.getEmailAccount();
        String username =tEmailConfig.getEmailAccount();
        String password = tEmailConfig.getEmailPassword();
        // Assuming you are sending email through relay.jangosmtp.net
        String host = tEmailConfig.getHost();
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", tEmailConfig.getPort());

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toEmail));

            // Set Subject: header field
            message.setSubject(tEmailConfig.getSubject());

            // Now set the actual message
            //message.setText(validate.getContent());
            message.setContent(messageText,"text/html;charset=utf-8");
            // Send message
            Transport.send(message);

            System.out.println("发送邮件成功!!.");
            log.info("==================发送邮件成功=======================");
            return true;

        } catch (MessagingException e) {
            log.error("邮件发送失败,发送邮件:{},错误:{}!", JSON.toJSONString(tEmailConfig),e.getMessage());
            return false;
        }
    }

    @Override
    public AjaxResult sendMessage(EmailEnums email, String toEmail, String verify, Long uid) {


        short type = email.getType();
        TEmailConfig config = new TEmailConfig();
        config.setStatus((short)0);
        config.setType(type);
        List<TEmailConfig> tEmailConfigs = tEmailConfigMapper.findEmail(config);
        for (TEmailConfig tEmailConfig : tEmailConfigs) {
            Long outTime = tEmailConfig.getOutTime();

            String message = MessageUtils.message("email.message.text", email.getMsg(), verify, outTime);

            if (this.sendEmail(tEmailConfig,toEmail,message)){

                String redisKey = email.redisKey(uid);
                redisCache.setCacheObject(redisKey,verify,outTime.intValue(), TimeUnit.MINUTES);
                return AjaxResult.success(outTime * 60);
            };
        }

        return AjaxResult.error(MessageUtils.message("emmil.sendMessage.error"));
//        return AjaxResult.success(120);

    }
}

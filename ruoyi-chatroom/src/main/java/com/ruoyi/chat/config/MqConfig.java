package com.ruoyi.chat.config;

import com.ruoyi.chat.component.message.ChatMessageListener;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/8/19,11:11
 * @return:
 **/
@EnableConfigurationProperties({
        BootstrapConfig.class})
@Configuration
public class MqConfig {

    /**
     * 集群环境下的广播消息监听器
     * @param bootstrapConfig
     * @return
     */
    @Bean(name = "chatMessageListener")
    public ChatMessageListener chatMessageListener(BootstrapConfig bootstrapConfig){

        if ("cluster".equals(bootstrapConfig.getMode())){
            return new ChatMessageListener();
        }else {
            return null;
        }
    }
}

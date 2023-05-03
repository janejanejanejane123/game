package com.ruoyi.chat.config;

import com.ruoyi.chat.component.handler.DefaultMessageHandler;
import com.ruoyi.chat.component.message.ChatMessageListener;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.Resource;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/10,19:55
 * @return:
 **/
@Data
@ConfigurationProperties(prefix = "netty")
public class BootstrapConfig {

    @Resource
    private DefaultMessageHandler defaultMessageHandler;

    private String mode;

    private Integer businessThreadCount;

    private Integer soRcvbuf;

    private Integer soSndbuf;

    private Integer backlog;

    private Integer readDuration;

    private Integer writeDuration;
    /**
     * 地址复用
     */
    private Boolean reuseaddr;

    private Integer maxContext;

}

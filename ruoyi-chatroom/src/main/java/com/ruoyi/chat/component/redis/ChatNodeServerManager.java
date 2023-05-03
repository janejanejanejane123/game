package com.ruoyi.chat.component.redis;

import com.ruoyi.chat.config.CIMConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/23,11:13
 * @return:
 **/
@Component
public class ChatNodeServerManager {


    @Resource
    private RedisCache cache;
    @Resource
    private CIMConfig cimConfig;



    /**
     * 将用户的identifier 和连接的服务器的nodeId对应
     * @param identifier 用户identifier
     */
    public void storeMapping(String identifier){
        Assert.test(StringUtils.isBlank(identifier),"system.error");
        cache.setCacheObject("nodeMapping:"+identifier,cimConfig.getNodeId());
    }


    public String getMapping(String identifier){
        Assert.test(StringUtils.isBlank(identifier),"system.error");
        return cache.getCacheObject("nodeMapping:"+identifier);
    }
}

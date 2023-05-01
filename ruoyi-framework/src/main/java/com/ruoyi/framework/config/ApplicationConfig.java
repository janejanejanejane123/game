package com.ruoyi.framework.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.ruoyi.common.cache.LoginCacheHandler;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.framework.security.handle.LoginUpdateHandler;
import com.ruoyi.framework.security.handle.LoginUpdateHandler4Other;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.TimeZone;

/**
 * 程序注解配置
 *
 * @author ruoyi
 */
@Configuration
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 指定要扫描的Mapper类的包的路径
@MapperScan("com.ruoyi.**.mapper")
public class ApplicationConfig
{
    /**
     * 时区配置
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization()
    {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
    }

    /**
     * 雪花工具类.
     * @return
     */
    @Bean
    public SnowflakeIdUtils snowflakeIdUtils(){
        return new SnowflakeIdUtils();
    }

    @Bean
    public ServletRegistrationBean<StatViewServlet> druidStatViewServlet() {
        ServletRegistrationBean<StatViewServlet> registrationBean = new ServletRegistrationBean<>(new StatViewServlet(),  "/druid/*");
        registrationBean.addInitParameter("allow", "127.0.0.1");// IP白名单 (没有配置或者为空，则允许所有访问)
        registrationBean.addInitParameter("deny", "");// IP黑名单 (存在共同时，deny优先于allow)
        registrationBean.addInitParameter("loginUsername", "root");
        registrationBean.addInitParameter("loginPassword", "1234");
        registrationBean.addInitParameter("resetEnable", "false");
        return registrationBean;
    }

    @Bean
    public LoginCacheHandler selfLoginCacheHandler(){
        return new LoginUpdateHandler();
    }

    @Bean
    public LoginCacheHandler otherLoginCacheHandler(){
        return new LoginUpdateHandler4Other();
    }
}

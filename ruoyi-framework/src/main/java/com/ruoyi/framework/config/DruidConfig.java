//package com.ruoyi.framework.config;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.sql.DataSource;
//
//import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
//import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
//import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
//import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
//import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
//import com.alibaba.druid.util.Utils;
//import com.ruoyi.common.enums.DataSourceType;
//import com.ruoyi.common.utils.spring.SpringUtils;
//import com.ruoyi.framework.config.properties.DruidProperties;
//import com.ruoyi.framework.datasource.DynamicDataSource;
//
///**
// * druid 配置多数据源
// *
// * @author ruoyi
// */
//@Configuration
//public class DruidConfig
//{
//    @Bean
//    @ConfigurationProperties("spring.datasource.druid.master")
//    @ConditionalOnProperty(prefix = "spring.datasource.druid.master", name = "enabled", havingValue = "true")
//    public DataSource masterDataSource(DruidProperties druidProperties) throws SQLException {
//        DruidDataSource masterDataSource = DruidDataSourceBuilder.create().build();
//        Map<String, DataSource> dataSourceMap = new HashMap<>();
//        dataSourceMap.put("master", masterDataSource);
////        dataSourceMap.put("order2", order2DataSource);
//
//        // t_treasure 表规则配置
//        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration("t_treasure", "master.t_treasure_$->{1..3}");
//        // 配置分库策略
////        orderTableRuleConfig.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "order$->{user_id % 2 + 1}"));
//        // 配置分表策略
//        orderTableRuleConfig.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("uid", "t_treasure_$->{uid % 3 + 1}"));
//        // 分布式主键
////        orderTableRuleConfig.setKeyGeneratorConfig(new KeyGeneratorConfiguration("SNOWFLAKE", "id"));
//
//        // 配置分片规则
//        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
//        shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);
//        // 获取数据源对象
//        DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, getProperties());
//        return druidProperties.dataSource((DruidDataSource) dataSource);
//    }
//
//
//    /**
//     * 系统参数配置
//     */
//    private Properties getProperties()
//    {
//        Properties shardingProperties = new Properties();
//        shardingProperties.put("sql.show", true);
//        return shardingProperties;
//    }
//
//    @Bean
//    @ConfigurationProperties("spring.datasource.druid.slave")
//    @ConditionalOnProperty(prefix = "spring.datasource.druid.slave", name = "enabled", havingValue = "true")
//    public DataSource slaveDataSource(DruidProperties druidProperties)
//    {
//        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
//        return druidProperties.dataSource(dataSource);
//    }
//
//    @Bean(name = "dynamicDataSource")
//    @Primary
//    public DynamicDataSource dataSource(DataSource masterDataSource)
//    {
//        Map<Object, Object> targetDataSources = new HashMap<>();
//        targetDataSources.put(DataSourceType.MASTER.name(), masterDataSource);
//        setDataSource(targetDataSources, DataSourceType.SLAVE.name(), "slaveDataSource");
////        setDataSource(targetDataSources, DataSourceType.SHARDING.name(), "shardingDataSource");
//        return new DynamicDataSource(masterDataSource, targetDataSources);
//    }
//
//    /**
//     * 设置数据源
//     *
//     * @param targetDataSources 备选数据源集合
//     * @param sourceName 数据源名称
//     * @param beanName bean名称
//     */
//    public void setDataSource(Map<Object, Object> targetDataSources, String sourceName, String beanName)
//    {
//        try
//        {
//            DataSource dataSource = SpringUtils.getBean(beanName);
//            targetDataSources.put(sourceName, dataSource);
//        }
//        catch (Exception e)
//        {
//        }
//    }
//
//    /**
//     * 去除监控页面底部的广告
//     */
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    @Bean
//    @ConditionalOnProperty(name = "spring.datasource.druid.statViewServlet.enabled", havingValue = "true")
//    public FilterRegistrationBean removeDruidFilterRegistrationBean(DruidStatProperties properties)
//    {
//        // 获取web监控页面的参数
//        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
//        // 提取common.js的配置路径
//        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
//        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
//        final String filePath = "support/http/resources/js/common.js";
//        // 创建filter进行过滤
//        Filter filter = new Filter()
//        {
//            @Override
//            public void init(javax.servlet.FilterConfig filterConfig) throws ServletException
//            {
//            }
//
//            @Override
//            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//                    throws IOException, ServletException
//            {
//                chain.doFilter(request, response);
//                // 重置缓冲区，响应头不会被重置
//                response.resetBuffer();
//                // 获取common.js
//                String text = Utils.readFromResource(filePath);
//                // 正则替换banner, 除去底部的广告信息
//                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
//                text = text.replaceAll("powered.*?shrek.wang</a>", "");
//                response.getWriter().write(text);
//            }
//
//            @Override
//            public void destroy()
//            {
//            }
//        };
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(filter);
//        registrationBean.addUrlPatterns(commonJsPattern);
//        return registrationBean;
//    }
//}

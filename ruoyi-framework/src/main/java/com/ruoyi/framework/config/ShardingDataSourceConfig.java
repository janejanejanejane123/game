//package com.ruoyi.framework.config;
//
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//import javax.sql.DataSource;
//import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
//import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
//import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
//import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
//import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
//import com.ruoyi.framework.config.properties.DruidProperties;
//
///**
// * sharding 配置信息
// *
// * @author ruoyi
// */
//@Configuration
//public class ShardingDataSourceConfig
//{
//    @Bean
//    @ConfigurationProperties("spring.datasource.druid.order")
//    @ConditionalOnProperty(prefix = "spring.datasource.druid.master", name = "enabled", havingValue = "true")
//    public DataSource orderDataSource(DruidProperties druidProperties)
//    {
//        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
//        return druidProperties.dataSource(dataSource);
//    }
//
////    @Bean
////    @ConfigurationProperties("spring.datasource.druid.order2")
////    @ConditionalOnProperty(prefix = "spring.datasource.druid.order2", name = "enabled", havingValue = "true")
////    public DataSource order2DataSource(DruidProperties druidProperties)
////    {
////        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
////        return druidProperties.dataSource(dataSource);
////    }
//
//    @Bean(name = "masterDataSource")
//    public DataSource shardingDataSource(@Qualifier("orderDataSource") DataSource orderDataSource) throws SQLException
//    {
//        Map<String, DataSource> dataSourceMap = new HashMap<>();
//        dataSourceMap.put("order", orderDataSource);
////        dataSourceMap.put("order2", order2DataSource);
//
//        // t_treasure 表规则配置
//        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration("t_treasure", "order.t_treasure_$->{0..2}");
//        // 配置分库策略
////        orderTableRuleConfig.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "order$->{user_id % 2 + 1}"));
//        // 配置分表策略
//        orderTableRuleConfig.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("uid", "t_treasure_$->{uid % 3}"));
//        // 分布式主键
////        orderTableRuleConfig.setKeyGeneratorConfig(new KeyGeneratorConfiguration("SNOWFLAKE", "id"));
//
//        // 配置分片规则
//        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
//        shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);
//        // 获取数据源对象
//        DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, getProperties());
//        return dataSource;
//    }
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
//}
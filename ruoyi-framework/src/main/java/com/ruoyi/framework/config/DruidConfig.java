package com.ruoyi.framework.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.config.properties.DruidProperties;
import com.ruoyi.framework.datasource.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.servlet.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * druid 配置多数据源
 *
 * @author ruoyi
 */
@Configuration
//@EnableConfigurationProperties(DruidProperties.class)
//@AutoConfigureBefore({DataSourceAutoConfiguration.class, DynamicDataSourceAutoConfiguration.class, SpringBootConfiguration.class})
//@ConditionalOnClass(DruidDataSource.class)
@MapperScan(basePackages = {"com.ruoyi.**.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
@Slf4j
public class DruidConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.druid.master")
    public DataSource masterDataSource(DruidProperties druidProperties)
    {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.slave")
    @ConditionalOnProperty(prefix = "spring.datasource.druid.slave", name = "enabled", havingValue = "true")
    public DataSource slaveDataSource(DruidProperties druidProperties)
    {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dataSource(DataSource masterDataSource)
    {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.MASTER.name(), masterDataSource);
        setDataSource(targetDataSources, DataSourceType.SLAVE.name(), "slaveDataSource");
        return new DynamicDataSource(masterDataSource, targetDataSources);
    }

    /**
     * 设置数据源
     *
     * @param targetDataSources 备选数据源集合
     * @param sourceName 数据源名称
     * @param beanName bean名称
     */
    public void setDataSource(Map<Object, Object> targetDataSources, String sourceName, String beanName)
    {
        try
        {
            DataSource dataSource = SpringUtils.getBean(beanName);
            targetDataSources.put(sourceName, dataSource);
        }
        catch (Exception e)
        {
        }
    }

    /**
     * 去除监控页面底部的广告
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.druid.statViewServlet.enabled", havingValue = "true")
    public FilterRegistrationBean removeDruidFilterRegistrationBean(DruidStatProperties properties)
    {
        // 获取web监控页面的参数
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
        // 提取common.js的配置路径
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
        final String filePath = "support/http/resources/js/common.js";
        // 创建filter进行过滤
        Filter filter = new Filter()
        {
            @Override
            public void init(javax.servlet.FilterConfig filterConfig) throws ServletException
            {
            }
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException
            {
                chain.doFilter(request, response);
                // 重置缓冲区，响应头不会被重置
                response.resetBuffer();
                // 获取common.js
                String text = Utils.readFromResource(filePath);
                // 正则替换banner, 除去底部的广告信息
                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
                text = text.replaceAll("powered.*?shrek.wang</a>", "");
                response.getWriter().write(text);
            }
            @Override
            public void destroy()
            {
            }
        };
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(commonJsPattern);
        return registrationBean;
    }
//
//    /**
//     * 分表数据源名称
//     */
//    public static final String SHARDING_DATA_SOURCE_NAME = "sharding-data-source";
//    /**
//     * 动态数据源配置项
//     */
//    @Autowired
//    private DynamicDataSourceProperties properties;
//
//    /**
//     * sharding-jdbc有四种数据源，需要根据业务注入不同的数据源
//     * <p>
//     * 1.未使用分片, 脱敏的名称(默认): shardingDataSource;
//     * 2.主从数据源: masterSlaveDataSource;
//     * 3.脱敏数据源：encryptDataSource;
//     * 4.影子数据源：shadowDataSource
//     */
//    @Lazy
//    @Resource(name = "shardingDataSource")
//    private AbstractDataSourceAdapter shardingDataSource;
//
//    @Bean
//    public DynamicDataSourceProvider dynamicDataSourceProvider() {
//        Map<String, DataSourceProperty> datasourceMap = properties.getDatasource();
//        return new AbstractDataSourceProvider() {
//            @Override
//            public Map<String, DataSource> loadDataSources() {
//                Map<String, DataSource> dataSourceMap = createDataSourceMap(datasourceMap);
//                // 将 shardingjdbc 管理的数据源也交给动态数据源管理
//                dataSourceMap.put(SHARDING_DATA_SOURCE_NAME, shardingDataSource);
//                return dataSourceMap;
//            }
//        };
//    }
//
//    /**
//     * 将动态数据源设置为首选的
//     * 当spring存在多个数据源时, 自动注入的是首选的对象
//     * 设置为主要的数据源之后，就可以支持sharding-jdbc原生的配置方式了
//     */
//    @Primary
//    @Bean
//    public DataSource dataSource(DynamicDataSourceProvider dynamicDataSourceProvider) {
//        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
//        dataSource.setPrimary(properties.getPrimary());
//        dataSource.setStrict(properties.getStrict());
//        dataSource.setStrategy(properties.getStrategy());
//        dataSource.setP6spy(properties.getP6spy());
//        dataSource.setSeata(properties.getSeata());
//        return dataSource;
//    }
//
////    @Bean(name = "dataSource")
////    @Primary
////    public DataSource dataSource() {
////        if (StringUtils.isEmpty(properties.getUrl())) {
////            log.error("Your database connection pool configuration is incorrect!"
////                    + " Please check your Spring profile, current profiles are:"
////                    + Arrays.toString(env.getActiveProfiles()));
////            throw new ApplicationContextException("Database connection pool is not configured correctly");
////        }
////        DruidDataSource dataSource = new DruidDataSource();
////        dataSource.setUrl(properties.getUrl());
////
////        log.info("properties.getUrl(): " + properties.getUrl() + "=============");
////        log.info("properties.getUsername(): " + properties.getUsername() + "=============");
////
////        dataSource.setUsername(properties.getUsername());
////
////        String password = properties.getPassword();
////        if (!StringUtils.isEmpty(properties.getPublicKey())) {
////            try {
////                password = ConfigTools.decrypt(properties.getPublicKey(), password);
////            } catch (Exception e) {
////                log.error("密码解密失败", e);
////            }
////        }
////        dataSource.setPassword(password);
////
////        if (properties.getInitialSize() > 0) {
////            dataSource.setInitialSize(properties.getInitialSize());
////        }
////        if (properties.getMinIdle() > 0) {
////            dataSource.setMinIdle(properties.getMinIdle());
////        }
////        if (properties.getMaxActive() > 0) {
////            dataSource.setMaxActive(properties.getMaxActive());
////        }
////        if (properties.getTimeBetweenEvictionRunsMillis() > 0) {
////            dataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
////        }
////        if (properties.getMinEvictableIdleTimeMillis() > 0) {
////            dataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
////        }
////        dataSource.setValidationQuery(properties.getValidationQuery());
////        dataSource.setTestWhileIdle(properties.isTestWhileIdle());
////        dataSource.setTestOnBorrow(properties.isTestOnBorrow());
////        dataSource.setTestOnReturn(properties.isTestOnReturn());
////        try {
////            dataSource.setFilters(properties.getFilters());
////            dataSource.setConnectionProperties(properties.getConnectionProperties());
////            //            dataSource.init();
////        } catch (SQLException e) {
////            //            log.error("datasource connect failed. Errors: {}", e);
////            throw new RuntimeException(e);
////        }
////        dataSource.setConnectionProperties(properties.getConnectionProperties());
////
////        return dataSource;
////    }
//
//
//    @Bean(name = "primaryJdbcTemplate")
//    public JdbcTemplate primaryJdbcTemplate(@Qualifier("dynamicDataSource") DruidDataSource dataSource) {
//        return new JdbcTemplate(dataSource);
//    }

    @Primary
    @Bean("sqlSessionFactory")
    public SqlSessionFactory db1SqlSessionFactory(DataSource dataSource) throws Exception {
        /**
         * 使用 mybatis plus 配置
         */
        MybatisSqlSessionFactoryBean b1 = new MybatisSqlSessionFactoryBean();
        System.out.println("dataSourceLyz" + dataSource.toString());
        b1.setDataSource(dataSource);
        b1.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/**/*Mapper.xml"));
        return b1.getObject();
    }
}

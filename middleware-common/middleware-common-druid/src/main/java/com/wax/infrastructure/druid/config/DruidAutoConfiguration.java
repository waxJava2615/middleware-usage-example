package com.wax.infrastructure.druid.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@AutoConfiguration(after = DataSourceAutoConfiguration.class)
@ConditionalOnClass(DruidDataSource.class)
@EnableConfigurationProperties(DruidProperties.class)
public class DruidAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource(DruidProperties properties) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        dataSource.setDriverClassName(properties.getDriverClassName());

        dataSource.setInitialSize(properties.getInitialSize());
        dataSource.setMinIdle(properties.getMinIdle());
        dataSource.setMaxActive(properties.getMaxActive());
        dataSource.setMaxWait(properties.getMaxWait());

        dataSource.setValidationQuery(properties.getValidationQuery());
        dataSource.setFilters(properties.getFilters());
        log.info("==> DruidDataSource init success");
        return dataSource;
    }
}
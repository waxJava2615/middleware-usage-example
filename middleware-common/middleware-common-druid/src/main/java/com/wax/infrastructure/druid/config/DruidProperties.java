package com.wax.infrastructure.druid.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Desription 数据库连接池配置
 * @Author wax
 * @Date 2025/10/14
 */
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "druid")
public class DruidProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName = "com.mysql.cj.jdbc.Driver"; // 默认 MySQL 驱动

    private String validationQuery = "SELECT 1";
    // 连接池配置
    private int initialSize = 5;
    private int minIdle = 5;
    private int maxActive = 20;
    private long maxWait = 60000; // 毫秒

    private String filters = "stat,wall,log4j";

    // getter/setter 省略
}

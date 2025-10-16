package com.wax.infrastructure.redis.config;

import com.wax.infrastructure.redis.handler.KeyPrefixHandler;
import com.wax.infrastructure.redis.config.properties.RedissonProperties;
import com.wax.infrastructure.redis.lock.RedissonLockTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * redis配置
 *
 * @author Lion Li
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(RedissonClient.class) // 当RedissonClient类存在时生效
@EnableConfigurationProperties(RedissonProperties.class)
public class RedisConfig {

    @Bean
    @ConditionalOnMissingBean // 仅当容器中没有RedissonClient实例时创建
    public RedissonClient redissonClient(RedissonProperties redissonProperties) {
        Config config = new Config();
        RedissonProperties.SingleServerConfig singleServerConfig = redissonProperties.getSingleServerConfig();
        RedissonProperties.ClusterServersConfig clusterServersConfig = redissonProperties.getClusterServersConfig();

        if (ObjectUtils.allNotNull(singleServerConfig)) {
            SingleServerConfig singleConfig = config.useSingleServer();
            singleConfig
                    .setNameMapper(new KeyPrefixHandler(redissonProperties.getKeyPrefix()))
                    .setDatabase(redissonProperties.getDatabase())
                    .setTimeout(singleServerConfig.getTimeout())
                    .setClientName(singleServerConfig.getClientName())
                    .setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())
                    .setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize())
                    .setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize())
                    .setConnectionPoolSize(singleServerConfig.getConnectionPoolSize());
            if (redissonProperties.getPassword() != null && !redissonProperties.getPassword().isEmpty()) {
                singleConfig.setPassword(redissonProperties.getPassword());
            }
        } else if (ObjectUtils.allNotNull(clusterServersConfig)) {
            ClusterServersConfig clusterConfig = config.useClusterServers();
            clusterConfig
                    // 设置redis key前缀
                    .setNameMapper(new KeyPrefixHandler(redissonProperties.getKeyPrefix()))
                    .setTimeout(clusterServersConfig.getTimeout())
                    .setClientName(clusterServersConfig.getClientName())
                    .setIdleConnectionTimeout(clusterServersConfig.getIdleConnectionTimeout())
                    .setSubscriptionConnectionPoolSize(clusterServersConfig.getSubscriptionConnectionPoolSize())
                    .setMasterConnectionMinimumIdleSize(clusterServersConfig.getMasterConnectionMinimumIdleSize())
                    .setMasterConnectionPoolSize(clusterServersConfig.getMasterConnectionPoolSize())
                    .setSlaveConnectionMinimumIdleSize(clusterServersConfig.getSlaveConnectionMinimumIdleSize())
                    .setSlaveConnectionPoolSize(clusterServersConfig.getSlaveConnectionPoolSize())
                    .setReadMode(clusterServersConfig.getReadMode())
                    .setSubscriptionMode(clusterServersConfig.getSubscriptionMode());
            if (redissonProperties.getPassword() != null && !redissonProperties.getPassword().isEmpty()) {
                clusterConfig.setPassword(redissonProperties.getPassword());
            }
        }
        log.info("初始化 redis 配置");
        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedissonLockTemplate redissonTemplate(RedissonClient redissonClient) {
        return new RedissonLockTemplate(redissonClient);
    }

    /**
     * redis集群配置 yml
     *
     * --- # redis 集群配置(单机与集群只能开启一个另一个需要注释掉)
     * spring.data:
     *   redis:
     *     cluster:
     *       nodes:
     *         - 192.168.0.100:6379
     *         - 192.168.0.101:6379
     *         - 192.168.0.102:6379
     *     # 密码
     *     password:
     *     # 连接超时时间
     *     timeout: 10s
     *     # 是否开启ssl
     *     ssl.enabled: false
     *
     * redisson:
     *   # 线程池数量
     *   threads: 16
     *   # Netty线程池数量
     *   nettyThreads: 32
     *   # 集群配置
     *   clusterServersConfig:
     *     # 客户端名称
     *     clientName: ${ruoyi.name}
     *     # master最小空闲连接数
     *     masterConnectionMinimumIdleSize: 32
     *     # master连接池大小
     *     masterConnectionPoolSize: 64
     *     # slave最小空闲连接数
     *     slaveConnectionMinimumIdleSize: 32
     *     # slave连接池大小
     *     slaveConnectionPoolSize: 64
     *     # 连接空闲超时，单位：毫秒
     *     idleConnectionTimeout: 10000
     *     # 命令等待超时，单位：毫秒
     *     timeout: 3000
     *     # 发布和订阅连接池大小
     *     subscriptionConnectionPoolSize: 50
     *     # 读取模式
     *     readMode: "SLAVE"
     *     # 订阅模式
     *     subscriptionMode: "MASTER"
     */

}

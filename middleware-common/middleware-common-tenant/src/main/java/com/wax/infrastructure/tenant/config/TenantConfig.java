package com.wax.infrastructure.tenant.config;

import cn.dev33.satoken.dao.SaTokenDao;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.wax.infrastructure.redis.config.RedisConfig;
import com.wax.infrastructure.tenant.config.propreties.TenantProperties;
import com.wax.infrastructure.tenant.handle.PlusTenantLineHandler;
import com.wax.infrastructure.tenant.manager.TenantSpringCacheManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;


/**
 * 租户配置类
 *
 * @author Lion Li
 */
@EnableConfigurationProperties(TenantProperties.class)
@AutoConfiguration(after = {RedisConfig.class})
@ConditionalOnProperty(value = "tenant.enable", havingValue = "true")
public class TenantConfig {

    @ConditionalOnClass(TenantLineInnerInterceptor.class)
    @AutoConfiguration
    static class MybatisPlusConfiguration {

        /**
         * 多租户插件
         */
        @Bean
        public TenantLineInnerInterceptor tenantLineInnerInterceptor(TenantProperties tenantProperties) {
            return new TenantLineInnerInterceptor(new PlusTenantLineHandler(tenantProperties));
        }

    }

    /**
     * 多租户缓存管理器
     */
    @Primary
    @Bean
    public CacheManager tenantCacheManager() {
        return new TenantSpringCacheManager();
    }

    /**
     * 多租户鉴权dao实现
     */
    @Primary
    @Bean
    public SaTokenDao tenantSaTokenDao() {
        return new TenantSaTokenDao();
    }

}

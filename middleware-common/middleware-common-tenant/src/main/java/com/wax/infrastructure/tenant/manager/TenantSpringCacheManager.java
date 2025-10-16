package com.wax.infrastructure.tenant.manager;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.wax.infrastructure.core.constant.CommonConstants;
import com.wax.infrastructure.redis.manager.PlusSpringCacheManager;
import com.wax.infrastructure.tenant.helper.TenantHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;

/**
 * 重写 cacheName 处理方法 支持多租户
 *
 * @author Lion Li
 */
@Slf4j
public class TenantSpringCacheManager extends PlusSpringCacheManager {

    public TenantSpringCacheManager() {
    }

    @Override
    public Cache getCache(String name) {
        if (InterceptorIgnoreHelper.willIgnoreTenantLine("")) {
            return super.getCache(name);
        }
        if (StringUtils.contains(name, CommonConstants.GLOBAL_REDIS_KEY)) {
            return super.getCache(name);
        }
        String tenantId = TenantHelper.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            log.error("无法获取有效的租户id -> Null");
        }
        if (StringUtils.startsWith(name, tenantId)) {
            // 如果存在则直接返回
            return super.getCache(name);
        }
        return super.getCache(tenantId + ":" + name);
    }

}

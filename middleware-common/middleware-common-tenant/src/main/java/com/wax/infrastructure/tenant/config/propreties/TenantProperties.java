package com.wax.infrastructure.tenant.config.propreties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Desription 住户配置
 * @Author wax
 * @Date 2025/10/15
 */
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "tenant")
public class TenantProperties {

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 排除表
     */
    private List<String> excludes;
}

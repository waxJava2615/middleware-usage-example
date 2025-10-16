package com.wax.infrastructure.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Desription mybatis配置
 * @Author wax
 * @Date 2025/10/14
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * mybatis-plus插件配置
     * 可参考 https://baomidou.com/plugins/tenant/
     *
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 这个是多租户插件
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return new LongValue(1);
            }

            // 此处设置 需要忽略不添加租户id的表
            @Override
            public boolean ignoreTable(String tableName) {
                return !"sys_user".equalsIgnoreCase(tableName);
            }
        }));
        // 防止全表更新和删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        // 如果配置多个插件, 切记分页最后添加
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}

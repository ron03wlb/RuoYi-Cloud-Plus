package org.dromara.resource.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * 集成测试专用配置类
 * <p>
 * 排除 Dubbo 相关配置，避免与 Testcontainers 的生命周期冲突
 *
 * @author Lion Li
 * @since 2025-11-10
 */
@Configuration
@EnableAutoConfiguration(excludeName = {
    "org.apache.dubbo.spring.boot.autoconfigure.DubboAutoConfiguration",
    "org.apache.dubbo.spring.boot.autoconfigure.DubboRelaxedBindingAutoConfiguration",
    "com.alibaba.cloud.nacos.NacosConfigAutoConfiguration",
    "com.alibaba.cloud.nacos.NacosDiscoveryAutoConfiguration",
    "com.alibaba.cloud.nacos.NacosServiceRegistryAutoConfiguration"
})
@MapperScan("org.dromara.**.mapper")
@ComponentScan(
    basePackages = {
        "org.dromara.resource.service",
        "org.dromara.resource.mapper",
        "org.dromara.common.redis",
        "org.dromara.common.oss",
        "org.dromara.common.mybatis"
    },
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org.dromara.resource.dubbo.*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org.dromara.common.dubbo.*")
    }
)
public class TestConfiguration {

    /**
     * MyBatis-Plus 插件配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}

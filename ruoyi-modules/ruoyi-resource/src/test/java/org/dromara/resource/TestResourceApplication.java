package org.dromara.resource;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 集成测试专用的轻量级启动类
 * <p>
 * 与 RuoYiResourceApplication 不同，此类：
 * <ul>
 *   <li>排除 Dubbo 自动配置，避免服务注册和远程调用</li>
 *   <li>排除 Nacos 配置，使用本地测试配置</li>
 *   <li>排除消息队列配置，避免不必要的连接</li>
 *   <li>仅扫描必要的组件，减少 Bean 冲突可能</li>
 * </ul>
 *
 * @author Lion Li
 * @since 2025-11-11
 */
@SpringBootConfiguration
@EnableAutoConfiguration(
    excludeName = {
        // 排除 Dubbo 自动配置
        "org.apache.dubbo.spring.boot.autoconfigure.DubboAutoConfiguration",
        "org.apache.dubbo.spring.boot.autoconfigure.DubboRelaxedBindingAutoConfiguration",
        // 排除 Nacos 配置
        "com.alibaba.cloud.nacos.NacosConfigAutoConfiguration",
        "com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration",
        "com.alibaba.cloud.nacos.NacosServiceRegistryAutoConfiguration",
        // 排除消息队列（如果不需要）
        "org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration",
        "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
        // 排除 Sa-Token Redis 自动配置，使用测试配置的内存实现
        "cn.dev33.satoken.dao.alone.SaAloneDaoConfiguration",
        "cn.dev33.satoken.spring.auto.config.SaTokenAutoConfiguration"
    }
)
@ComponentScan(
    basePackages = {
        "org.dromara.resource.service",      // Service 层
        "org.dromara.resource.mapper",       // Mapper 层
        "org.dromara.resource.domain",       // Domain 对象
        "org.dromara.common.mybatis",        // MyBatis 支持
        "org.dromara.common.redis",          // Redis 支持
        "org.dromara.common.oss",            // OSS 支持
        "org.dromara.common.core",           // 核心工具
        "org.dromara.common.satoken",        // Sa-Token 支持
        "org.dromara.common.tenant",         // 租户支持
        "org.dromara.common.web"             // Web 支持
    },
    excludeFilters = {
        // 排除 Dubbo 服务实现
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org.dromara.resource.dubbo.*"),
        // 排除通用 Dubbo 配置
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org.dromara.common.dubbo.*")
    }
)
public class TestResourceApplication {
    // 测试专用的最小化启动类，不需要任何方法
}

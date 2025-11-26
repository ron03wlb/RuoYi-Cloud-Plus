package org.dromara.auth.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * 测试自动配置类
 *
 * <p>排除生产环境的 Redisson/Lock4j 自动配置
 *
 * @author Test Team
 */
@Configuration
@EnableAutoConfiguration(
        excludeName = {
            "org.apache.dubbo.spring.boot.autoconfigure.DubboAutoConfiguration",
            "org.redisson.spring.starter.RedissonAutoConfigurationV2",
            "org.dromara.common.redis.config.RedisConfiguration",
            "com.baomidou.lock.spring.boot.autoconfigure.LockAutoConfiguration"
        })
public class TestAutoConfiguration {
    // 这个类用于排除不需要的自动配置
}

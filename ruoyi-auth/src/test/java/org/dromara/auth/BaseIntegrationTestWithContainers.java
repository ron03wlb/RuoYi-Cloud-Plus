package org.dromara.auth;

import org.dromara.auth.config.AuthTestConfig;
import org.dromara.auth.config.TestAutoConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 集成测试基类（使用本地Redis）
 * <p>
 * 提供完整的 Spring Boot 测试环境 + 本地 Redis
 * </p>
 *
 * <p>特性:</p>
 * <ul>
 *   <li>完整 Spring Boot 上下文</li>
 *   <li>本地 Redis (localhost:6379)</li>
 *   <li>提供 MockMvc 用于 Controller 测试</li>
 *   <li>使用 test profile 配置</li>
 *   <li>Mock Dubbo 服务</li>
 * </ul>
 *
 * <p>前置条件:</p>
 * <ul>
 *   <li>需要本地 Redis 运行: docker run -d --name test-redis -p 6379:6379 redis:7-alpine</li>
 * </ul>
 *
 * <p>适用场景:</p>
 * <ul>
 *   <li>Controller 层集成测试</li>
 *   <li>Service 层需要 Spring 容器的测试</li>
 *   <li>需要验证 Redis 缓存行为的测试</li>
 * </ul>
 *
 * @author Test Team
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.nacos.discovery.enabled=false",
        "spring.cloud.nacos.config.enabled=false",
        "spring.cloud.config.enabled=false",
        // 完全禁用 Dubbo
        "dubbo.application.qos-enable=false",
        "dubbo.registry.address=N/A",
        "dubbo.protocol.port=-1",
        "dubbo.consumer.check=false",
        "dubbo.provider.register=false",
        // Redis 配置 (使用本地 Redis)
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379"
    }
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({AuthTestConfig.class, TestAutoConfiguration.class})
public abstract class BaseIntegrationTestWithContainers {

    @Autowired
    protected MockMvc mockMvc;

    /**
     * 测试前初始化
     * <p>
     * 子类可以覆盖此方法进行自定义初始化
     * </p>
     */
    @BeforeEach
    public void baseSetUp() {
        // 子类可以覆盖此方法进行额外的初始化
    }
}

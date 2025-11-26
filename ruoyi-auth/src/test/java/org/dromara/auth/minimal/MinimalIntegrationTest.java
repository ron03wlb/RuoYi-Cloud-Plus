package org.dromara.auth.minimal;

import static org.assertj.core.api.Assertions.assertThat;

import org.dromara.auth.config.TestAutoConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * 最小集成测试 - 用于诊断Spring容器启动问题
 *
 * <p>TODO: 诊断测试,已完成使命,暂时禁用
 */
@org.junit.jupiter.api.Disabled("Diagnostic test - mission accomplished")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "spring.cloud.nacos.discovery.enabled=false",
            "spring.cloud.nacos.config.enabled=false",
            "spring.cloud.config.enabled=false",
            "dubbo.application.qos-enable=false",
            "dubbo.registry.address=N/A",
            "dubbo.protocol.port=-1",
            "dubbo.consumer.check=false",
            "dubbo.provider.register=false",
            // Redis 配置
            "spring.data.redis.host=localhost",
            "spring.data.redis.port=6379"
        })
@ActiveProfiles("test")
@Import(TestAutoConfiguration.class)
@DisplayName("最小集成测试 - Spring容器启动诊断")
class MinimalIntegrationTest {

    @Test
    @DisplayName("Spring 容器应该能够成功启动")
    void shouldStartSpringContext() {
        // 如果容器启动成功,这个测试就会通过
        assertThat(true).isTrue();
    }
}

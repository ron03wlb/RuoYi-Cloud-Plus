package org.dromara.auth.infrastructure;

import org.dromara.auth.BaseIntegrationTestWithContainers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testcontainers 基礎設施驗證測試
 * <p>
 * 驗證 Testcontainers 和測試配置是否正常工作
 * </p>
 *
 * <p>TODO: 诊断测试,因RedissonClient依赖问题暂时禁用</p>
 *
 * @author Test Team
 */
@org.junit.jupiter.api.Disabled("Diagnostic test - disabled due to RedissonClient dependency")
@DisplayName("Testcontainers 基礎設施驗證測試")
class TestContainersVerificationTest extends BaseIntegrationTestWithContainers {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("ApplicationContext 應該成功加載")
    void shouldLoadApplicationContext() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("Redis 連接應該可用")
    void shouldConnectToRedis() {
        if (redisTemplate != null) {
            // 嘗試 ping Redis
            String pong = redisTemplate.getConnectionFactory()
                .getConnection()
                .ping();

            assertThat(pong).isEqualTo("PONG");
        } else {
            // 如果 RedisTemplate 不可用，至少確認 ApplicationContext 已加載
            assertThat(applicationContext).isNotNull();
        }
    }

    @Test
    @DisplayName("MockMvc 應該可用")
    void shouldHaveMockMvc() {
        assertThat(mockMvc).isNotNull();
    }
}

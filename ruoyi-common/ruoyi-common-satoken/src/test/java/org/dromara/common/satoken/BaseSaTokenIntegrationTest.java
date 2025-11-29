package org.dromara.common.satoken;

import org.dromara.common.satoken.config.SaTokenTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Sa-Token 集成測試基類.
 *
 * <p>提供完整的 Spring Boot 測試環境 + 真實的 Redis 容器
 *
 * <p>特性:
 *
 * <ul>
 *   <li>完整 Spring Boot 上下文
 *   <li>Testcontainers Redis 容器
 *   <li>Sa-Token 完整功能
 *   <li>使用 test profile 配置
 * </ul>
 *
 * <p>適用場景:
 *
 * <ul>
 *   <li>需要真實 Redis 環境的測試
 *   <li>Sa-Token 登錄/權限測試
 *   <li>需要驗證 Token 存儲行為的測試
 * </ul>
 *
 * @author Test Team
 */
@SpringBootTest(
    classes = BaseSaTokenIntegrationTest.TestApplication.class,
    properties = {
      "spring.cloud.nacos.discovery.enabled=false",
      "spring.cloud.nacos.config.enabled=false",
      "spring.cloud.config.enabled=false"
    })
@ActiveProfiles("test")
@Testcontainers
@Import(SaTokenTestConfig.class)
public abstract class BaseSaTokenIntegrationTest {

  /**
   * Redis 容器.
   *
   * <p>使用 Redis 7 Alpine 版本，輕量快速
   */
  @Container
  static GenericContainer<?> redis =
      new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);

  /**
   * 動態配置屬性.
   *
   * <p>從 Testcontainers 獲取動態端口並配置到 Spring
   */
  @DynamicPropertySource
  static void registerDynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
  }

  /**
   * 測試前初始化.
   *
   * <p>子類可以覆蓋此方法進行自定義初始化
   */
  @BeforeEach
  public void baseSetUp() {
    // 子類可以覆蓋此方法進行額外的初始化
  }

  /**
   * 測試應用配置.
   *
   * <p>最小化 Spring Boot 應用，僅包含 Sa-Token 必需的組件
   */
  @SpringBootApplication(scanBasePackages = "org.dromara.common")
  static class TestApplication {
    // 測試應用入口，Spring Boot 會自動掃描並加載 Sa-Token 配置
  }
}

package org.dromara.common.test;

import org.dromara.common.test.config.TestContainersConfig;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * 集成测试基类.
 *
 * <p>提供完整的集成测试环境，包括：
 *
 * <ul>
 *   <li>PostgreSQL 数据库（Testcontainers）
 *   <li>Redis 缓存（Testcontainers）
 *   <li>MinIO 对象存储（Testcontainers）
 *   <li>Spring Boot 上下文
 *   <li>自动配置数据源和缓存连接
 * </ul>
 *
 * <h3>使用示例：</h3>
 *
 * <pre>{@code
 * @SpringBootTest
 * class MyServiceIntegrationTest extends BaseIntegrationTest {
 *
 *     @Autowired
 *     private MyService myService;
 *
 *     @Test
 *     @DisplayName("应该成功插入数据")
 *     void shouldInsertData() {
 *         // 测试逻辑
 *     }
 * }
 * }</pre>
 *
 * <h3>重要说明：</h3>
 *
 * <p>本类使用 <b>手动容器启动</b> 方式（方案 A）以解决 Dubbo + Testcontainers 时序冲突问题。 容器在静态初始化块中启动，确保在 Spring Boot 的
 * BeanFactoryPostProcessor 阶段之前容器已经运行。 这样 Dubbo 的 DubboContextPostProcessor 扫描环境属性时，容器端口已经可用.
 *
 * @author Lion Li
 * @since 2025-11-09
 */
@SpringBootTest(properties = {"spring.main.web-application-type=servlet"})
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

  private static final Logger log = LoggerFactory.getLogger(BaseIntegrationTest.class);

  /**
   * PostgreSQL 容器.
   *
   * <p>在静态初始化块中手动启动，确保在 Spring 容器初始化之前容器已运行
   */
  protected static final PostgreSQLContainer<?> POSTGRES_CONTAINER;

  /**
   * Redis 容器.
   *
   * <p>在静态初始化块中手动启动，确保在 Spring 容器初始化之前容器已运行
   */
  protected static final GenericContainer<?> REDIS_CONTAINER;

  /**
   * MinIO 容器.
   *
   * <p>在静态初始化块中手动启动，确保在 Spring 容器初始化之前容器已运行
   */
  protected static final GenericContainer<?> MINIO_CONTAINER;

  /*
   * 静态初始化块 - 在类加载时执行（Spring 容器初始化之前）
   * 手动启动所有 Testcontainers，解决 Dubbo 时序冲突问题
   */
  static {
    log.info("=== 开始启动 Testcontainers（方案 A：手动容器启动）===");

    // 创建并启动 PostgreSQL 容器
    POSTGRES_CONTAINER = TestContainersConfig.PostgreSQL.createContainer();
    POSTGRES_CONTAINER.start();
    log.info("✅ PostgreSQL 容器已启动: {}", POSTGRES_CONTAINER.getJdbcUrl());

    // 创建并启动 Redis 容器
    REDIS_CONTAINER = TestContainersConfig.Redis.createContainer();
    REDIS_CONTAINER.start();
    log.info(
        "✅ Redis 容器已启动: {}:{}",
        REDIS_CONTAINER.getHost(),
        REDIS_CONTAINER.getMappedPort(TestContainersConfig.Redis.getPort()));

    // 创建并启动 MinIO 容器
    MINIO_CONTAINER = TestContainersConfig.MinIO.createContainer();
    MINIO_CONTAINER.start();
    log.info(
        "✅ MinIO 容器已启动: {}:{}",
        MINIO_CONTAINER.getHost(),
        MINIO_CONTAINER.getMappedPort(TestContainersConfig.MinIO.getApiPort()));

    log.info("=== 所有 Testcontainers 已成功启动 ===");
  }

  /**
   * 动态配置数据源和 Redis 连接属性.
   *
   * <p>容器已在静态初始化块中启动，此方法将容器配置注入到 Spring Boot 属性中。 由于容器已经运行，{@code getMappedPort()} 等方法可以安全调用.
   *
   * @param registry 动态属性注册器
   */
  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    // 配置数据源
    registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

    // 配置 Redis
    registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    registry.add(
        "spring.data.redis.port",
        () -> REDIS_CONTAINER.getMappedPort(TestContainersConfig.Redis.getPort()));

    // 配置 Redisson
    registry.add(
        "redisson.single-server-config.address",
        () ->
            "redis://"
                + REDIS_CONTAINER.getHost()
                + ":"
                + REDIS_CONTAINER.getMappedPort(TestContainersConfig.Redis.getPort()));

    // 配置 MinIO
    registry.add(
        "oss.endpoint",
        () ->
            "http://"
                + MINIO_CONTAINER.getHost()
                + ":"
                + MINIO_CONTAINER.getMappedPort(TestContainersConfig.MinIO.getApiPort()));
    registry.add("oss.accessKey", TestContainersConfig.MinIO::getAccessKey);
    registry.add("oss.secretKey", TestContainersConfig.MinIO::getSecretKey);

    // 配置 MyBatis-Plus
    registry.add(
        "mybatis-plus.configuration.log-impl", () -> "org.apache.ibatis.logging.stdout.StdOutImpl");

    // 禁用 Nacos 配置（集成测试不需要）
    registry.add("spring.cloud.nacos.config.enabled", () -> "false");
    registry.add("spring.cloud.nacos.discovery.enabled", () -> "false");
  }

  /**
   * 在所有测试前执行的初始化逻辑.
   *
   * <p>子类可以重写此方法添加自定义初始化
   */
  @BeforeAll
  static void setUpIntegrationTest() {
    // 确保容器已启动
    if (!POSTGRES_CONTAINER.isRunning()) {
      throw new IllegalStateException("PostgreSQL container is not running");
    }
    if (!REDIS_CONTAINER.isRunning()) {
      throw new IllegalStateException("Redis container is not running");
    }
    if (!MINIO_CONTAINER.isRunning()) {
      throw new IllegalStateException("MinIO container is not running");
    }
  }

  /** 获取 PostgreSQL JDBC URL. */
  protected static String getPostgresJdbcUrl() {
    return POSTGRES_CONTAINER.getJdbcUrl();
  }

  /** 获取 PostgreSQL 用户名. */
  protected static String getPostgresUsername() {
    return POSTGRES_CONTAINER.getUsername();
  }

  /** 获取 PostgreSQL 密码. */
  protected static String getPostgresPassword() {
    return POSTGRES_CONTAINER.getPassword();
  }

  /** 获取 Redis 主机. */
  protected static String getRedisHost() {
    return REDIS_CONTAINER.getHost();
  }

  /** 获取 Redis 端口. */
  protected static Integer getRedisPort() {
    return REDIS_CONTAINER.getMappedPort(TestContainersConfig.Redis.getPort());
  }

  /** 获取 MinIO 主机. */
  protected static String getMinioHost() {
    return MINIO_CONTAINER.getHost();
  }

  /** 获取 MinIO 端口. */
  protected static Integer getMinioPort() {
    return MINIO_CONTAINER.getMappedPort(TestContainersConfig.MinIO.getApiPort());
  }

  /** 获取 MinIO Endpoint (host:port). */
  protected static String getMinioEndpoint() {
    return getMinioHost() + ":" + getMinioPort();
  }

  /** 获取 MinIO URL (http://host:port). */
  protected static String getMinioUrl() {
    return "http://" + getMinioEndpoint();
  }

  /** 获取 MinIO Access Key. */
  protected static String getMinioAccessKey() {
    return TestContainersConfig.MinIO.getAccessKey();
  }

  /** 获取 MinIO Secret Key. */
  protected static String getMinioSecretKey() {
    return TestContainersConfig.MinIO.getSecretKey();
  }
}

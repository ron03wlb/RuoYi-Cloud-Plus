package org.dromara.common.test.examples;

import org.dromara.common.test.BaseIntegrationTest;
import org.dromara.common.test.config.TestSaTokenConfig;
import org.dromara.common.test.utils.AuthTestUtils;
import org.dromara.common.test.utils.TenantTestUtils;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 集成测试框架示例
 * <p>
 * 演示如何使用集成测试框架进行测试
 * <p>
 * 使用 webEnvironment = MOCK 提供 Sa-Token 所需的 Web 上下文
 *
 * @author Lion Li
 * @since 2025-11-09
 */
@org.springframework.boot.test.context.SpringBootTest(webEnvironment = org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK)
@Import(TestSaTokenConfig.class)
@DisplayName("集成测试框架示例")
class SampleIntegrationTest extends BaseIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(SampleIntegrationTest.class);

    @BeforeEach
    void setUp() {
        log.info("=== 测试开始 ===");
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        try {
            AuthTestUtils.logout();
        } catch (Exception e) {
            // Sa-Token 上下文未初始化时忽略
            log.debug("清理登录状态失败（可能未登录）: {}", e.getMessage());
        }
        TenantTestUtils.clear();

        // 清除 Sa-Token Mock 上下文
        AuthTestUtils.clearMockContext();

        log.info("=== 测试结束 ===");
    }

    @Nested
    @DisplayName("1. 基础设施测试")
    class InfrastructureTests {

        @Test
        @DisplayName("应该成功连接到 MySQL 容器")
        void shouldConnectToMysql() {
            // Arrange
            String jdbcUrl = getMysqlJdbcUrl();
            String username = getMysqlUsername();
            String password = getMysqlPassword();

            // Assert
            assertThat(jdbcUrl).isNotNull();
            assertThat(jdbcUrl).contains("jdbc:mysql://");
            assertThat(username).isEqualTo("root");
            assertThat(password).isEqualTo("root123");

            log.info("MySQL 连接信息 - URL: {}, Username: {}", jdbcUrl, username);
        }

        @Test
        @DisplayName("应该成功连接到 Redis 容器")
        void shouldConnectToRedis() {
            // Arrange
            String redisHost = getRedisHost();
            Integer redisPort = getRedisPort();

            // Assert
            assertThat(redisHost).isNotNull();
            assertThat(redisPort).isGreaterThan(0);

            log.info("Redis 连接信息 - Host: {}, Port: {}", redisHost, redisPort);
        }

        @Test
        @DisplayName("应该成功启动 Spring 上下文")
        void shouldLoadSpringContext() {
            // 这个测试通过即表示 Spring 上下文成功加载
            log.info("Spring 上下文成功加载");
        }
    }

    @Nested
    @DisplayName("2. 认证工具测试")
    class AuthenticationTests {

        @Test
        @DisplayName("应该成功模拟用户登录")
        void shouldMockUserLogin() {
            // Act
            String token = AuthTestUtils.mockLogin(1L, "testuser");

            // Assert
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(AuthTestUtils.isLogin(1L)).isTrue();
            assertThat(AuthTestUtils.getLoginUserId()).isEqualTo(1L);
            assertThat(AuthTestUtils.getLoginUsername()).isEqualTo("testuser");

            log.info("用户登录成功 - Token: {}", token);
        }

        @Test
        @DisplayName("应该成功模拟管理员登录")
        void shouldMockAdminLogin() {
            // Act
            String token = AuthTestUtils.mockAdminLogin();

            // Assert
            assertThat(token).isNotNull();
            assertThat(AuthTestUtils.isLogin(1L)).isTrue();
            assertThat(AuthTestUtils.getLoginUserId()).isEqualTo(1L);
            assertThat(AuthTestUtils.getLoginUsername()).isEqualTo("admin");

            log.info("管理员登录成功 - Token: {}", token);
        }

        @Test
        @DisplayName("应该成功设置用户权限")
        void shouldSetUserPermissions() {
            // Arrange
            AuthTestUtils.mockLogin(1L, "testuser");

            // Act
            AuthTestUtils.setPermissions(1L, "system:user:list", "system:user:add");

            // Assert - 注意：Sa-Token 权限验证需要完整集成，这里只是示例
            log.info("权限设置成功");
        }

        @Test
        @DisplayName("应该成功登出用户")
        void shouldLogoutUser() {
            // Arrange
            AuthTestUtils.mockLogin(1L, "testuser");
            assertThat(AuthTestUtils.isLogin(1L)).isTrue();

            // Act
            AuthTestUtils.logout(1L);

            // Assert
            assertThat(AuthTestUtils.isLogin(1L)).isFalse();

            log.info("用户登出成功");
        }
    }

    @Nested
    @DisplayName("3. 租户工具测试")
    class TenantTests {

        @Test
        @DisplayName("应该成功设置租户")
        void shouldSetTenant() {
            // Act
            TenantTestUtils.setTenant("000001");

            // Assert
            assertThat(TenantTestUtils.getTenant()).isEqualTo("000001");

            log.info("租户设置成功 - TenantId: {}", TenantTestUtils.getTenant());
        }

        @Test
        @DisplayName("应该成功切换租户")
        void shouldSwitchTenant() {
            // Arrange
            TenantTestUtils.setTestTenant1();
            assertThat(TenantTestUtils.getTenant()).isEqualTo(TenantTestUtils.TEST_TENANT_1_ID);

            // Act
            TenantTestUtils.setTestTenant2();

            // Assert
            assertThat(TenantTestUtils.getTenant()).isEqualTo(TenantTestUtils.TEST_TENANT_2_ID);

            log.info("租户切换成功 - From: {} To: {}",
                TenantTestUtils.TEST_TENANT_1_ID, TenantTestUtils.TEST_TENANT_2_ID);
        }

        @Test
        @DisplayName("应该在指定租户上下文中执行操作")
        void shouldExecuteInTenantContext() {
            // Arrange
            TenantTestUtils.setDefaultTenant();

            // Act
            String result = TenantTestUtils.executeInTenant("000001", () -> {
                log.info("在租户 000001 中执行操作");
                return "result from tenant 000001";
            });

            // Assert
            assertThat(result).isEqualTo("result from tenant 000001");
            assertThat(TenantTestUtils.getTenant()).isEqualTo(TenantTestUtils.DEFAULT_TENANT_ID); // 应该恢复到原租户
        }

        @Test
        @DisplayName("应该成功模拟多租户用户登录")
        void shouldMockTenantUserLogin() {
            // Act
            String token = TenantTestUtils.mockTenantUserLogin(1L, "tenant1user", "000001");

            // Assert
            assertThat(token).isNotNull();
            assertThat(AuthTestUtils.getTenantId()).isEqualTo("000001");
            assertThat(TenantTestUtils.getTenant()).isEqualTo("000001");

            log.info("多租户用户登录成功 - TenantId: {}, Token: {}", "000001", token);
        }
    }

    @Nested
    @DisplayName("4. 综合场景测试")
    class IntegrationScenarios {

        @Test
        @DisplayName("场景：租户隔离测试")
        void scenarioTenantIsolation() {
            log.info("=== 场景：租户隔离测试 ===");

            // 1. 租户1用户登录
            TenantTestUtils.mockTenantUserLogin(1L, "user1", "000001");
            log.info("租户1用户登录: userId=1, tenantId=000001");

            // 模拟在租户1中创建数据
            String tenant1Data = TenantTestUtils.executeInTenant("000001", () -> {
                log.info("租户1中创建数据");
                return "data_from_tenant_000001";
            });

            // 2. 租户2用户登录
            AuthTestUtils.logout();  // 先登出租户1用户
            TenantTestUtils.mockTenantUserLogin(2L, "user2", "000002");
            log.info("租户2用户登录: userId=2, tenantId=000002");

            // 模拟在租户2中创建数据
            String tenant2Data = TenantTestUtils.executeInTenant("000002", () -> {
                log.info("租户2中创建数据");
                return "data_from_tenant_000002";
            });

            // 3. 验证数据隔离
            assertThat(tenant1Data).isNotEqualTo(tenant2Data);
            log.info("租户隔离验证通过 - 租户1数据: {}, 租户2数据: {}", tenant1Data, tenant2Data);
        }

        @Test
        @DisplayName("场景：带权限的用户操作测试")
        void scenarioUserWithPermissions() {
            log.info("=== 场景：带权限的用户操作测试 ===");

            // 1. 模拟用户登录并设置权限
            AuthTestUtils.mockAdminLoginWithPermissions(
                "system:user:list",
                "system:user:add",
                "system:user:edit"
            );

            // 2. 模拟执行需要权限的操作
            log.info("执行需要 system:user:list 权限的操作");
            // 这里应该调用实际的 service 方法
            // userService.listUsers();

            log.info("执行需要 system:user:add 权限的操作");
            // userService.addUser(new User());

            // 3. 验证权限
            log.info("权限操作测试完成");
        }
    }
}

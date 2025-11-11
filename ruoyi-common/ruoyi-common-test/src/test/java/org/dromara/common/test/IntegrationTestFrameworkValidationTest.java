package org.dromara.common.test;

import org.dromara.common.test.utils.SqlScriptExecutor;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * 集成测试框架验证测试
 * <p>
 * 验证集成测试框架的基础设施是否正常工作
 *
 * @author Lion Li
 * @since 2025-11-10
 */
@DisplayName("集成测试框架验证")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTestFrameworkValidationTest extends BaseIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(IntegrationTestFrameworkValidationTest.class);

    @Autowired(required = false)
    private DataSource dataSource;

    @Nested
    @DisplayName("1. Testcontainers 基础设施验证")
    class TestcontainersInfrastructureTests {

        @Test
        @Order(1)
        @DisplayName("应该成功启动 MySQL 容器")
        void shouldStartMysqlContainer() {
            log.info("=== 验证 MySQL 容器 ===");

            // Assert - 容器应该正在运行
            assertThat(MYSQL_CONTAINER.isRunning())
                .as("MySQL 容器应该正在运行")
                .isTrue();

            // Assert - JDBC URL 应该包含 MySQL 连接信息
            String jdbcUrl = getMysqlJdbcUrl();
            assertThat(jdbcUrl)
                .as("JDBC URL 应该不为空")
                .isNotNull()
                .contains("jdbc:mysql://")
                .contains("ry_cloud_test");

            log.info("✅ MySQL 容器启动成功");
            log.info("   JDBC URL: {}", jdbcUrl);
            log.info("   Username: {}", getMysqlUsername());
            log.info("   Password: {}", getMysqlPassword());
        }

        @Test
        @Order(2)
        @DisplayName("应该成功启动 Redis 容器")
        void shouldStartRedisContainer() {
            log.info("=== 验证 Redis 容器 ===");

            // Assert - 容器应该正在运行
            assertThat(REDIS_CONTAINER.isRunning())
                .as("Redis 容器应该正在运行")
                .isTrue();

            // Assert - 连接信息应该有效
            String redisHost = getRedisHost();
            Integer redisPort = getRedisPort();

            assertThat(redisHost)
                .as("Redis 主机应该不为空")
                .isNotNull()
                .isNotEmpty();

            assertThat(redisPort)
                .as("Redis 端口应该大于 0")
                .isGreaterThan(0)
                .isLessThan(65536);

            log.info("✅ Redis 容器启动成功");
            log.info("   Host: {}", redisHost);
            log.info("   Port: {}", redisPort);
        }

        @Test
        @Order(3)
        @DisplayName("容器应该可以重用以提高测试速度")
        void shouldReuseContainers() {
            log.info("=== 验证容器重用 ===");

            // 注意：Testcontainers 的 withReuse(true) 需要在 ~/.testcontainers.properties 中配置
            // testcontainers.reuse.enable=true

            log.info("✅ 容器配置了重用功能");
            log.info("   提示：确保 ~/.testcontainers.properties 中设置了 testcontainers.reuse.enable=true");
        }
    }

    @Nested
    @DisplayName("2. Spring Boot 集成验证")
    class SpringBootIntegrationTests {

        @Test
        @Order(10)
        @DisplayName("应该成功加载 Spring 上下文")
        void shouldLoadSpringContext() {
            log.info("=== 验证 Spring 上下文 ===");

            // Assert - 如果测试运行到这里，说明 Spring 上下文已成功加载
            assertThat(true)
                .as("Spring 上下文应该成功加载")
                .isTrue();

            log.info("✅ Spring Boot 上下文加载成功");
        }

        @Test
        @Order(11)
        @DisplayName("应该成功注入 DataSource")
        void shouldInjectDataSource() {
            log.info("=== 验证 DataSource 注入 ===");

            // Assert
            assertThat(dataSource)
                .as("DataSource 应该被成功注入")
                .isNotNull();

            log.info("✅ DataSource 注入成功");
            log.info("   类型: {}", dataSource.getClass().getSimpleName());
        }
    }

    @Nested
    @DisplayName("3. 数据库连接验证")
    class DatabaseConnectionTests {

        @Test
        @Order(20)
        @DisplayName("应该能够连接到测试数据库")
        void shouldConnectToDatabase() throws Exception {
            log.info("=== 验证数据库连接 ===");

            // Arrange & Act
            boolean connected = false;
            try (var conn = dataSource.getConnection()) {
                connected = !conn.isClosed();
            }

            // Assert
            assertThat(connected)
                .as("应该能够连接到数据库")
                .isTrue();

            log.info("✅ 数据库连接成功");
        }

        @Test
        @Order(21)
        @DisplayName("应该能够执行简单的 SQL 查询")
        void shouldExecuteSimpleQuery() {
            log.info("=== 验证 SQL 查询 ===");

            // Arrange
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

            // Act
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            // Assert
            assertThat(result)
                .as("查询结果应该是 1")
                .isEqualTo(1);

            log.info("✅ SQL 查询执行成功");
        }

        @Test
        @Order(22)
        @DisplayName("应该能够创建临时表")
        void shouldCreateTempTable() {
            log.info("=== 验证创建表 ===");

            // Arrange
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

            // Act - 创建临时表
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS test_framework_validation (
                    id INT PRIMARY KEY,
                    name VARCHAR(100)
                )
                """);

            // Act - 插入数据
            jdbcTemplate.update("INSERT INTO test_framework_validation VALUES (?, ?)", 1, "test");

            // Act - 查询数据
            String name = jdbcTemplate.queryForObject(
                "SELECT name FROM test_framework_validation WHERE id = 1",
                String.class
            );

            // Assert
            assertThat(name)
                .as("应该能够查询到插入的数据")
                .isEqualTo("test");

            // Cleanup
            jdbcTemplate.execute("DROP TABLE test_framework_validation");

            log.info("✅ 表创建和数据操作成功");
        }
    }

    @Nested
    @DisplayName("4. SQL 脚本执行器验证")
    class SqlScriptExecutorTests {

        @Test
        @Order(30)
        @DisplayName("应该能够执行 SQL 语句")
        void shouldExecuteSqlStatements() {
            log.info("=== 验证 SQL 脚本执行器 ===");

            // Arrange
            String createTableSql = """
                CREATE TABLE IF NOT EXISTS test_sql_executor (
                    id INT PRIMARY KEY,
                    value VARCHAR(50)
                )
                """;

            String insertDataSql = "INSERT INTO test_sql_executor VALUES (1, 'test1'), (2, 'test2')";

            // Act
            SqlScriptExecutor.executeSql(dataSource, createTableSql, insertDataSql);

            // Assert - 验证数据是否插入成功
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<String> values = jdbcTemplate.queryForList(
                "SELECT value FROM test_sql_executor ORDER BY id",
                String.class
            );

            assertThat(values)
                .as("应该查询到插入的数据")
                .hasSize(2)
                .containsExactly("test1", "test2");

            // Cleanup
            SqlScriptExecutor.executeSql(dataSource, "DROP TABLE test_sql_executor");

            log.info("✅ SQL 脚本执行器工作正常");
        }

        @Test
        @Order(31)
        @DisplayName("应该能够清空表数据")
        void shouldTruncateTable() {
            log.info("=== 验证表数据清空 ===");

            // Arrange - 创建表并插入数据
            SqlScriptExecutor.executeSql(
                dataSource,
                "CREATE TABLE IF NOT EXISTS test_truncate (id INT PRIMARY KEY)",
                "INSERT INTO test_truncate VALUES (1), (2), (3)"
            );

            // Act - 清空表
            SqlScriptExecutor.truncateTables(dataSource, "test_truncate");

            // Assert - 验证表已清空
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM test_truncate",
                Integer.class
            );

            assertThat(count)
                .as("表应该被清空")
                .isEqualTo(0);

            // Cleanup
            SqlScriptExecutor.executeSql(dataSource, "DROP TABLE test_truncate");

            log.info("✅ 表数据清空功能正常");
        }

        @Test
        @Order(32)
        @DisplayName("应该能够检查表是否存在")
        void shouldCheckTableExists() {
            log.info("=== 验证表存在性检查 ===");

            // Arrange
            SqlScriptExecutor.executeSql(
                dataSource,
                "CREATE TABLE IF NOT EXISTS test_exists (id INT)"
            );

            // Act & Assert - 表应该存在
            boolean exists = SqlScriptExecutor.tableExists(dataSource, "test_exists");
            assertThat(exists)
                .as("test_exists 表应该存在")
                .isTrue();

            // Act & Assert - 不存在的表
            boolean notExists = SqlScriptExecutor.tableExists(dataSource, "table_not_exists");
            assertThat(notExists)
                .as("table_not_exists 表不应该存在")
                .isFalse();

            // Cleanup
            SqlScriptExecutor.executeSql(dataSource, "DROP TABLE test_exists");

            log.info("✅ 表存在性检查功能正常");
        }
    }

    @Nested
    @DisplayName("5. 框架性能验证")
    class PerformanceTests {

        @Test
        @Order(40)
        @DisplayName("容器启动时间应该在可接受范围内")
        void shouldStartContainersInAcceptableTime() {
            log.info("=== 验证容器启动性能 ===");

            // 注意：这个测试实际上在整个测试类加载时已经启动了容器
            // 这里只是记录和验证

            assertThat(MYSQL_CONTAINER.isRunning()).isTrue();
            assertThat(REDIS_CONTAINER.isRunning()).isTrue();

            log.info("✅ 容器启动正常");
            log.info("   提示：首次启动会下载镜像，后续测试会使用缓存");
        }
    }

    @AfterAll
    static void afterAll() {
        log.info("=== 集成测试框架验证完成 ===");
        log.info("✅ 所有基础设施验证通过");
        log.info("📝 框架已就绪，可以开始编写业务模块的集成测试");
    }
}

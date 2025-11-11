# ruoyi-common-test - 集成测试框架

> 📦 **模块**: RuoYi-Cloud-Plus 集成测试框架
> 🎯 **用途**: 提供完整的集成测试基础设施
> ✅ **状态**: 已完成
> 📅 **创建日期**: 2025-11-09

---

## 📋 目录

- [简介](#简介)
- [快速开始](#快速开始)
- [核心组件](#核心组件)
- [使用示例](#使用示例)
- [最佳实践](#最佳实践)
- [常见问题](#常见问题)

---

## 简介

`ruoyi-common-test` 是 RuoYi-Cloud-Plus 项目的集成测试框架，提供以下功能：

### ✨ 核心特性

- ✅ **Testcontainers 集成** - MySQL、Redis、MinIO 等容器自动管理
- ✅ **BaseIntegrationTest 基类** - 统一的集成测试基础
- ✅ **认证测试工具** - 模拟用户登录、权限设置
- ✅ **租户测试工具** - 租户切换、隔离验证
- ✅ **Spring Boot Test** - 完整的 Spring 上下文支持
- ✅ **自动配置** - 数据源、Redis 等自动配置

### 🎯 适用场景

- Service 层 `insertByBo()` / `updateByBo()` 方法测试（MapstructUtils 依赖）
- Dubbo 远程服务调用测试
- 多租户数据隔离测试
- 完整业务流程测试
- OSS 文件操作测试
- 工作流引擎测试

---

## 快速开始

### 1. 添加依赖

在需要编写集成测试的模块的 `build.gradle.kts` 中添加：

```kotlin
dependencies {
    // 集成测试框架
    testImplementation(project(":ruoyi-common:ruoyi-common-test"))
}
```

### 2. 创建测试类

```java
package org.dromara.system.service;

import org.dromara.common.test.BaseIntegrationTest;
import org.dromara.common.test.utils.AuthTestUtils;
import org.dromara.common.test.utils.TenantTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DisplayName("用户服务集成测试")
class SysUserServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SysUserService userService;

    @Test
    @DisplayName("应该成功插入用户")
    void shouldInsertUser() {
        // Arrange - 模拟登录
        AuthTestUtils.mockAdminLogin();
        TenantTestUtils.setDefaultTenant();

        SysUserBo userBo = new SysUserBo();
        userBo.setUserName("testuser");
        userBo.setNickName("测试用户");
        userBo.setEmail("test@example.com");

        // Act - 执行插入
        Boolean result = userService.insertByBo(userBo);

        // Assert - 验证结果
        assertThat(result).isTrue();
    }
}
```

### 3. 运行测试

```bash
# 运行单个模块的集成测试
./gradlew :ruoyi-modules:ruoyi-system:test

# 运行所有集成测试
./gradlew test
```

---

## 核心组件

### 1. BaseIntegrationTest

**位置**: `org.dromara.common.test.BaseIntegrationTest`

集成测试基类，提供：

- MySQL 容器（自动启动和配置）
- Redis 容器（自动启动和配置）
- Spring Boot Test 上下文
- 动态属性配置

**使用方法**:

```java
@SpringBootTest
class MyTest extends BaseIntegrationTest {
    // 自动获得 MySQL 和 Redis 支持
}
```

**可用方法**:

- `getMysqlJdbcUrl()` - 获取 MySQL 连接 URL
- `getMysqlUsername()` - 获取 MySQL 用户名
- `getMysqlPassword()` - 获取 MySQL 密码
- `getRedisHost()` - 获取 Redis 主机
- `getRedisPort()` - 获取 Redis 端口

---

### 2. TestContainersConfig

**位置**: `org.dromara.common.test.config.TestContainersConfig`

Testcontainers 配置类，提供各种容器的创建方法。

**支持的容器**:

#### MySQL 容器

```java
MySQLContainer<?> mysql = TestContainersConfig.MySQL.createContainer();
mysql.start();
```

#### Redis 容器

```java
GenericContainer<?> redis = TestContainersConfig.Redis.createContainer();
redis.start();
```

#### MinIO 容器

```java
GenericContainer<?> minio = TestContainersConfig.MinIO.createContainer();
minio.start();

String endpoint = "http://" + minio.getHost() + ":" +
    minio.getMappedPort(TestContainersConfig.MinIO.getApiPort());
```

#### PostgreSQL 容器

```java
PostgreSQLContainer<?> postgres = TestContainersConfig.PostgreSQL.createContainer();
postgres.start();
```

---

### 3. AuthTestUtils

**位置**: `org.dromara.common.test.utils.AuthTestUtils`

认证测试工具类，提供用户登录、权限设置等功能。

**核心方法**:

#### 模拟用户登录

```java
// 模拟普通用户登录
String token = AuthTestUtils.mockLogin(1L, "testuser");

// 模拟管理员登录
String token = AuthTestUtils.mockAdminLogin();

// 模拟带租户的用户登录
String token = AuthTestUtils.mockLogin(1L, "testuser", "000001");
```

#### 设置权限和角色

```java
// 设置用户权限
AuthTestUtils.setPermissions(1L, "system:user:list", "system:user:add");

// 设置用户角色
AuthTestUtils.setRoles(1L, "admin", "user");
```

#### 获取登录信息

```java
// 获取当前登录用户ID
Long userId = AuthTestUtils.getLoginUserId();

// 获取当前登录用户名
String username = AuthTestUtils.getLoginUsername();

// 获取当前租户ID
String tenantId = AuthTestUtils.getTenantId();
```

#### 登出

```java
// 登出指定用户
AuthTestUtils.logout(1L);

// 登出当前用户
AuthTestUtils.logout();
```

#### 便捷方法

```java
// 带权限的管理员登录
String token = AuthTestUtils.mockAdminLoginWithPermissions(
    "system:user:list",
    "system:user:add"
);

// 带角色的管理员登录
String token = AuthTestUtils.mockAdminLoginWithRoles("admin", "superadmin");

// 普通用户登录
String token = AuthTestUtils.mockNormalUserLogin(2L, "normaluser");
```

---

### 4. TenantTestUtils

**位置**: `org.dromara.common.test.utils.TenantTestUtils`

租户测试工具类，提供租户切换、隔离验证等功能。

**核心方法**:

#### 设置租户

```java
// 设置当前租户
TenantTestUtils.setTenant("000001");

// 设置为主租户
TenantTestUtils.setDefaultTenant();

// 设置为测试租户1
TenantTestUtils.setTestTenant1();

// 设置为测试租户2
TenantTestUtils.setTestTenant2();
```

#### 租户上下文执行

```java
// 在指定租户上下文中执行操作（有返回值）
List<User> users = TenantTestUtils.executeInTenant("000001", () -> {
    return userService.listAll();
});

// 在指定租户上下文中执行操作（无返回值）
TenantTestUtils.executeInTenant("000001", () -> {
    userService.saveData(data);
});
```

#### 验证租户隔离

```java
TenantTestUtils.verifyTenantIsolation(
    "000001",  // 租户1 ID
    "000002",  // 租户2 ID
    () -> userService.listAll(),  // 租户1操作
    () -> userService.listAll(),  // 租户2操作
    (result1, result2) -> {       // 验证器
        assertThat(result1).isNotEqualTo(result2);
    }
);
```

#### 多租户用户登录

```java
String token = TenantTestUtils.mockTenantUserLogin(
    1L,        // 用户ID
    "user1",   // 用户名
    "000001"   // 租户ID
);
```

---

## 使用示例

### 示例1: Service 层集成测试

测试 `insertByBo()` 方法（需要 MapstructUtils）：

```java
@SpringBootTest
@DisplayName("客户端服务集成测试")
class SysClientServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SysClientServiceImpl clientService;

    @Test
    @DisplayName("应该成功插入客户端")
    void shouldInsertClient() {
        // Arrange
        AuthTestUtils.mockAdminLogin();
        TenantTestUtils.setDefaultTenant();

        SysClientBo clientBo = new SysClientBo();
        clientBo.setClientKey("test_client");
        clientBo.setClientSecret("secret123");
        clientBo.setGrantType("password,refresh_token");
        clientBo.setDeviceType("pc");

        // Act
        Boolean result = clientService.insertByBo(clientBo);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("应该成功更新客户端")
    void shouldUpdateClient() {
        // Arrange
        AuthTestUtils.mockAdminLogin();
        TenantTestUtils.setDefaultTenant();

        // 先插入
        SysClientBo clientBo = new SysClientBo();
        clientBo.setClientKey("test_client");
        clientBo.setClientSecret("secret123");
        clientService.insertByBo(clientBo);

        // 查询获取ID
        SysClientVo client = clientService.queryByClientId("test_client");

        // Act - 更新
        clientBo.setId(client.getId());
        clientBo.setClientSecret("new_secret456");
        Boolean result = clientService.updateByBo(clientBo);

        // Assert
        assertThat(result).isTrue();
        SysClientVo updated = clientService.queryById(client.getId());
        assertThat(updated.getClientSecret()).isEqualTo("new_secret456");
    }
}
```

### 示例2: 租户隔离测试

```java
@Test
@DisplayName("应该隔离不同租户的数据")
void shouldIsolateTenantData() {
    // 准备：在租户1中创建数据
    String tenant1Data = TenantTestUtils.executeInTenant("000001", () -> {
        AuthTestUtils.mockLogin(1L, "user1", "000001");

        SysUserBo userBo = new SysUserBo();
        userBo.setUserName("tenant1_user");
        userBo.setNickName("租户1用户");
        userService.insertByBo(userBo);

        return "tenant1_user";
    });

    // 准备：在租户2中创建数据
    String tenant2Data = TenantTestUtils.executeInTenant("000002", () -> {
        AuthTestUtils.mockLogin(2L, "user2", "000002");

        SysUserBo userBo = new SysUserBo();
        userBo.setUserName("tenant2_user");
        userBo.setNickName("租户2用户");
        userService.insertByBo(userBo);

        return "tenant2_user";
    });

    // 验证：租户1只能查到自己的数据
    List<SysUserVo> tenant1Users = TenantTestUtils.executeInTenant("000001", () -> {
        return userService.selectUserList(new SysUserBo());
    });

    // 验证：租户2只能查到自己的数据
    List<SysUserVo> tenant2Users = TenantTestUtils.executeInTenant("000002", () -> {
        return userService.selectUserList(new SysUserBo());
    });

    // 断言：两个租户的数据完全隔离
    assertThat(tenant1Users).noneMatch(u -> u.getUserName().equals("tenant2_user"));
    assertThat(tenant2Users).noneMatch(u -> u.getUserName().equals("tenant1_user"));
}
```

### 示例3: 权限测试

```java
@Test
@DisplayName("应该拒绝无权限用户的操作")
void shouldDenyUnauthorizedOperation() {
    // Arrange - 创建无权限的普通用户
    AuthTestUtils.mockNormalUserLogin(2L, "normaluser");
    // 注意：mockNormalUserLogin 只设置了 common:read 权限

    // Act & Assert - 尝试执行需要 system:user:add 权限的操作
    SysUserBo userBo = new SysUserBo();
    userBo.setUserName("newuser");

    // 这里应该抛出权限不足异常
    // 具体的权限验证取决于 Service 实现
}
```

### 示例4: MinIO 文件操作测试

```java
@SpringBootTest
class OssServiceIntegrationTest extends BaseIntegrationTest {

    @Container
    static GenericContainer<?> minioContainer = TestContainersConfig.MinIO.createContainer();

    @Autowired
    private SysOssService ossService;

    @Test
    @DisplayName("应该成功上传文件到 MinIO")
    void shouldUploadFileToMinIO() {
        // Arrange
        AuthTestUtils.mockAdminLogin();

        MultipartFile file = createMockFile("test.txt", "test content");

        // Act
        SysOss oss = ossService.upload(file);

        // Assert
        assertThat(oss.getOssId()).isNotNull();
        assertThat(oss.getFileName()).isEqualTo("test.txt");
    }
}
```

---

## 最佳实践

### 1. 测试隔离

**每个测试后清理状态**:

```java
@AfterEach
void tearDown() {
    // 清理登录状态
    AuthTestUtils.logout();

    // 清理租户上下文
    TenantTestUtils.clear();

    // 清理测试数据（如需要）
    cleanupTestData();
}
```

### 2. 测试数据管理

**使用 @BeforeEach 准备测试数据**:

```java
@BeforeEach
void setUp() {
    // 准备通用测试数据
    AuthTestUtils.mockAdminLogin();
    TenantTestUtils.setDefaultTenant();

    // 创建测试所需的基础数据
    prepareTestData();
}
```

### 3. 使用 @Nested 组织测试

```java
@Nested
@DisplayName("查询方法测试")
class QueryTests {
    @Test
    void shouldQueryById() { }

    @Test
    void shouldQueryList() { }
}

@Nested
@DisplayName("新增方法测试")
class InsertTests {
    @Test
    void shouldInsert() { }
}
```

### 4. 描述性命名

```java
@Test
@DisplayName("应该在租户1中成功创建用户")
void shouldCreateUserInTenant1_WhenUserDataValid() {
    // 测试逻辑
}
```

### 5. 容器复用

Testcontainers 已配置 `withReuse(true)`，多个测试会共享同一个容器实例，加快测试速度。

---

## 常见问题

### Q1: 测试启动很慢怎么办？

**A**: Testcontainers 首次启动需要拉取 Docker 镜像，后续会复用容器。建议：

- 提前拉取镜像：`docker pull mysql:8.0 redis:7-alpine`
- 使用容器复用（已默认开启）
- 减少不必要的容器

### Q2: 如何调试 Testcontainers？

**A**: 查看容器日志：

```java
@Container
static MySQLContainer<?> mysql = TestContainersConfig.MySQL.createContainer()
    .withLogConsumer(new Slf4jLogConsumer(log));  // 已默认配置
```

### Q3: 如何禁用某些自动配置？

**A**: 在 `application-test.yml` 中配置：

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.某些AutoConfiguration
```

### Q4: 测试中如何访问 MySQL/Redis？

**A**: 通过 BaseIntegrationTest 提供的方法：

```java
String jdbcUrl = getMysqlJdbcUrl();
String redisHost = getRedisHost();
Integer redisPort = getRedisPort();
```

### Q5: 如何为特定模块添加额外容器？

**A**: 在测试类中添加：

```java
@Container
static GenericContainer<?> customContainer =
    new GenericContainer<>("image:tag")
        .withExposedPorts(8080);
```

---

## 📚 相关资源

- [Testcontainers 官方文档](https://www.testcontainers.org/)
- [Spring Boot Test 文档](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [JUnit 5 文档](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ 文档](https://assertj.github.io/doc/)

---

**创建时间**: 2025-11-09
**维护者**: Test Team
**状态**: ✅ 完成并可用

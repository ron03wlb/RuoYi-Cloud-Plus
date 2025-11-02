# RuoYi-Cloud-Plus 测试指南

> **完整的单元测试与集成测试实施手册**
>
> **版本:** 2.0
> **更新日期:** 2025-10-29
> **适用范围:** 所有 ruoyi-* 模块

---

## 📑 文档导航

### 核心章节

- [1. 快速开始](#1-快速开始) - 5分钟上手测试编写
- [2. 测试基础设施](#2-测试基础设施) - Gradle、JaCoCo、TestContainers配置
- [3. 测试编写规范](#3-测试编写规范) - 命名、结构、最佳实践
- [4. 分层测试指南](#4-分层测试指南) - Mapper、Service、Controller、工具类
- [5. 特殊场景测试](#5-特殊场景测试) - 多租户、认证、Dubbo、分布式事务
- [6. 模块实施清单](#6-模块实施清单) - 按模块组织的测试任务清单

### 附录

- [附录A: 测试依赖管理](#附录a-测试依赖管理) - 完整的依赖配置说明
- [附录B: 进度追踪](#附录b-进度追踪) - 查看当前测试实施进度

### 快速命令

```bash
# 运行所有测试
./gradlew test

# 运行单元测试（快速）
./gradlew unitTest

# 生成覆盖率报告
./gradlew test jacocoTestReport

# 查看进度报告
cat docs/TESTING-PROGRESS-REPORT.md
```

---

## 1. 快速开始

### 1.1 第一个测试示例

```java
package org.dromara.system.service;

import org.dromara.system.domain.SysUser;
import org.dromara.system.mapper.SysUserMapper;
import org.dromara.system.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class SysUserServiceTest {

    @Mock
    private SysUserMapper userMapper;

    @InjectMocks
    private SysUserServiceImpl userService;

    private SysUser testUser;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testUser = new SysUser();
        testUser.setUserId(1L);
        testUser.setUserName("test_user");
        testUser.setNickName("测试用户");
        testUser.setEmail("test@example.com");
    }

    @Test
    void shouldReturnUserWhenValidIdProvided() {
        // Arrange (准备)
        Long userId = 1L;
        when(userMapper.selectById(userId)).thenReturn(testUser);

        // Act (执行)
        SysUser result = userService.selectUserById(userId);

        // Assert (断言)
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getUserName()).isEqualTo("test_user");

        // 验证交互
        verify(userMapper, times(1)).selectById(userId);
    }

    @Test
    void shouldReturnNullWhenInvalidIdProvided() {
        // Arrange
        Long invalidUserId = 9999L;
        when(userMapper.selectById(invalidUserId)).thenReturn(null);

        // Act
        SysUser result = userService.selectUserById(invalidUserId);

        // Assert
        assertThat(result).isNull();
        verify(userMapper).selectById(invalidUserId);
    }
}
```

### 1.2 运行测试

```bash
# 运行所有测试
./gradlew test

# 运行特定模块的测试
./gradlew :ruoyi-modules:ruoyi-system:test

# 运行单个测试类
./gradlew test --tests "SysUserServiceTest"

# 运行单个测试方法
./gradlew test --tests "SysUserServiceTest.shouldReturnUserWhenValidIdProvided"

# 生成覆盖率报告
./gradlew test jacocoTestReport
# 报告位置: build/reports/jacoco/test/html/index.html
```

---

## 2. 测试基础设施

### 2.1 全局测试依赖配置

项目已在根 `build.gradle.kts` 中配置了全局测试依赖，所有子模块自动继承：

```kotlin
// 位置：build.gradle.kts (第 176-179 行)
subprojects {
    dependencies {
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}
```

#### 当前测试依赖版本

| 依赖库                      | 版本     | 来源              | 说明                         |
|--------------------------|--------|-----------------|----------------------------|
| spring-boot-starter-test | 3.5.6  | Spring Boot BOM | 包含 JUnit, Mockito, AssertJ |
| junit-jupiter            | 5.12.2 | Spring Boot BOM | JUnit 5 核心引擎               |
| junit-platform-launcher  | 1.12.2 | Spring Boot BOM | JUnit 平台启动器                |
| mockito-core             | 5.17.0 | Spring Boot BOM | Mockito 模拟框架               |
| assertj-core             | 3.27.4 | Spring Boot BOM | 流式断言库                      |

> 📌 **注意**: 所有版本由 Spring Boot BOM (3.5.6) 统一管理，确保兼容性。
> 详细依赖分析请参见 [附录A: 测试依赖管理](#附录a-测试依赖管理)

### 2.2 模块级测试依赖扩展

如果某个模块需要额外的测试依赖，在模块的 `build.gradle.kts` 中添加：

```kotlin
dependencies {
    // ========== 特定模块测试依赖 ==========

    // TestContainers (集成测试)
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.3"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testImplementation("com.redis.testcontainers:testcontainers-redis:2.0.1")

    // H2 内存数据库
    testImplementation("com.h2database:h2")

    // Spring Security Test
    testImplementation("org.springframework.security:spring-security-test")
}
```

### 2.3 JaCoCo 覆盖率配置

```kotlin
plugins {
    id("jacoco")
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    // 排除不需要覆盖的类
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/domain/**",      // POJO
                    "**/dto/**",
                    "**/vo/**",
                    "**/bo/**",
                    "**/*Application.class",  // 主程序
                    "**/config/**AutoConfiguration.class",
                    "**/constant/**",    // 常量
                    "**/enums/**"
                )
            }
        })
    )
}

// 覆盖率验证
tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)

    violationRules {
        rule {
            limit {
                minimum = "0.85".toBigDecimal() // 85% 总体覆盖率
            }
        }

        rule {
            element = "PACKAGE"
            includes = listOf("org.dromara.*.service.*")
            limit {
                counter = "LINE"
                minimum = "0.90".toBigDecimal() // Service 层 90%
            }
        }
    }
}
```

### 2.4 测试配置文件

**src/test/resources/application-test.yml**

```yaml
# 测试环境配置
spring:
  profiles:
    active: test

  # 数据源配置（集成测试时由 TestContainers 动态设置）
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 5
      minimum-idle: 2
      connection-timeout: 3000

  # Redis 配置（集成测试时由 TestContainers 动态设置）
  data:
    redis:
      timeout: 10s

  # 禁用 Nacos（测试时不需要）
  cloud:
    nacos:
      discovery:
        enabled: false
      config:
        enabled: false
        import-check:
          enabled: false

  # 禁用 Dubbo 注册（测试时使用 Mock）
  dubbo:
    protocol:
      port: -1
    registry:
      address: N/A
    consumer:
      check: false
    provider:
      register: false

# Sa-Token 配置（测试时放宽限制）
sa-token:
  token-name: Authorization
  timeout: 2592000 # 30天
  is-log: false
  check-same-token: false
  check-id-token: false

# MyBatis-Plus 配置
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl # 测试时输出 SQL
  global-config:
    banner: false

# 日志配置
logging:
  level:
    root: INFO
    org.dromara: DEBUG
    org.springframework.test: DEBUG
```

### 2.5 测试基类

**src/test/java/org/dromara/common/test/BaseUnitTest.java**

```java
package org.dromara.common.test;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 单元测试基类
 * - 纯单元测试，所有依赖都使用 Mock
 * - 不加载 Spring 容器
 * - 执行速度快
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
public abstract class BaseUnitTest {
    // 可添加通用的测试工具方法
}
```

**src/test/java/org/dromara/common/test/BaseIntegrationTest.java**

```java
package org.dromara.common.test;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * 集成测试基类
 * - 加载完整 Spring Boot 上下文
 * - 使用 TestContainers 提供真实的外部依赖
 * - 执行速度较慢
 */
@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public abstract class BaseIntegrationTest {

    /**
     * MySQL 容器（所有集成测试共享，提高性能）
     */
    @Container
    protected static final MySQLContainer<?> MYSQL_CONTAINER =
        new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass")
            .withReuse(true); // 重用容器

    /**
     * Redis 容器
     */
    @Container
    protected static final GenericContainer<?> REDIS_CONTAINER =
        new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)
            .withReuse(true);

    /**
     * 动态设置 Spring 属性
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // 数据源配置
        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);

        // Redis 配置
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port",
            () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }
}
```

---

## 3. 测试编写规范

### 3.1 命名规范

#### 测试类命名

```java
// 单元测试
{ClassName}Test.java
// 示例: UserServiceTest.java, StringUtilsTest.java

// 集成测试
{ClassName}IntegrationTest.java
// 示例: UserServiceIntegrationTest.java, UserMapperTest.java
```

#### 测试方法命名

**模式:** `should{ExpectedBehavior}When{StateUnderTest}`

```java
// ✅ 正确示例
@Test
void shouldReturnUserWhenValidIdProvided() { }

@Test
void shouldThrowExceptionWhenUserNotFound() { }

@Test
void shouldUpdateUserSuccessfullyWhenDataValid() { }

// ❌ 错误示例
@Test
void testGetUser() { }  // 不清楚测试什么

@Test
void getUserById() { }  // 不是测试命名

@Test
void test1() { }  // 完全无意义
```

### 3.2 AAA 测试结构

每个测试方法应遵循 AAA 模式：

```java
@Test
void shouldCalculateTotalPriceCorrectly() {
    // ========== Arrange (准备) ==========
    // 设置测试数据和环境
    List<OrderItem> items = Arrays.asList(
        new OrderItem("item1", 10.0, 2),  // 20.0
        new OrderItem("item2", 15.5, 1)   // 15.5
    );
    Order order = new Order(items);

    // ========== Act (执行) ==========
    // 执行被测试的方法
    double totalPrice = order.calculateTotalPrice();

    // ========== Assert (断言) ==========
    // 验证结果
    assertThat(totalPrice).isEqualTo(35.5);
    assertThat(order.getItemCount()).isEqualTo(3);
}
```

### 3.3 断言规范

**优先使用 AssertJ 流式断言**

```java
// ✅ 推荐：AssertJ 流式断言
assertThat(user)
    .isNotNull()
    .extracting("userName", "email")
    .containsExactly("test_user", "test@example.com");

assertThat(userList)
    .isNotEmpty()
    .hasSize(3)
    .extracting(SysUser::getUserName)
    .containsExactlyInAnyOrder("user1", "user2", "user3");

assertThatThrownBy(() -> service.deleteUser(9999L))
    .isInstanceOf(ServiceException.class)
    .hasMessage("用户不存在");

// ❌ 不推荐：JUnit 原生断言（可读性差）
assertNotNull(user);
assertEquals("test_user", user.getUserName());
```

### 3.4 测试数据准备

**使用工厂方法或 Builder 模式**

```java
/**
 * 测试数据工厂
 */
public class TestDataFactory {

    public static SysUser createDefaultUser() {
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUserName("test_user");
        user.setNickName("测试用户");
        user.setEmail("test@example.com");
        user.setStatus("0");
        return user;
    }

    public static SysUser createUser(Long userId, String userName) {
        SysUser user = createDefaultUser();
        user.setUserId(userId);
        user.setUserName(userName);
        return user;
    }
}
```

---

## 4. 分层测试指南

### 4.1 Mapper 层测试

Mapper 层使用**集成测试**（需要真实数据库）。

```java
package org.dromara.system.mapper;

import org.dromara.common.test.BaseIntegrationTest;
import org.dromara.system.domain.SysUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 用户 Mapper 集成测试
 */
@Sql("/sql/data/user-data.sql") // 加载测试数据
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SysUserMapperTest extends BaseIntegrationTest {

    @Autowired
    private SysUserMapper userMapper;

    @Test
    void shouldSelectUserByIdSuccessfully() {
        // Act
        SysUser user = userMapper.selectById(1L);

        // Assert
        assertThat(user).isNotNull();
        assertThat(user.getUserName()).isEqualTo("admin");
    }

    @Test
    void shouldInsertUserSuccessfully() {
        // Arrange
        SysUser newUser = new SysUser();
        newUser.setUserName("new_user");
        newUser.setNickName("新用户");

        // Act
        int rows = userMapper.insert(newUser);

        // Assert
        assertThat(rows).isEqualTo(1);
        assertThat(newUser.getUserId()).isNotNull(); // 验证主键已回填
    }
}
```

### 4.2 Service 层测试

#### 单元测试（Mock 依赖）

```java
package org.dromara.system.service;

import org.dromara.common.test.BaseUnitTest;
import org.dromara.system.domain.SysUser;
import org.dromara.system.mapper.SysUserMapper;
import org.dromara.system.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 */
class SysUserServiceTest extends BaseUnitTest {

    @Mock
    private SysUserMapper userMapper;

    @InjectMocks
    private SysUserServiceImpl userService;

    @Test
    void shouldReturnUserWhenValidIdProvided() {
        // Arrange
        SysUser testUser = new SysUser();
        testUser.setUserId(1L);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // Act
        SysUser result = userService.selectUserById(1L);

        // Assert
        assertThat(result).isNotNull();
        verify(userMapper, times(1)).selectById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDuplicateUserName() {
        // Arrange
        SysUser newUser = new SysUser();
        newUser.setUserName("existing_user");
        when(userMapper.selectByUserName("existing_user"))
            .thenReturn(new SysUser()); // 用户名已存在

        // Act & Assert
        assertThatThrownBy(() -> userService.insertUser(newUser))
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("用户名已存在");

        verify(userMapper, never()).insert(any());
    }
}
```

### 4.3 Controller 层测试

使用 **MockMvc** 进行测试。

```java
package org.dromara.system.controller;

import org.dromara.common.test.BaseUnitTest;
import org.dromara.system.service.ISysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户控制器单元测试
 */
class SysUserControllerTest extends BaseUnitTest {

    private MockMvc mockMvc;

    @Mock
    private ISysUserService userService;

    @InjectMocks
    private SysUserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void shouldReturnUserByIdSuccessfully() throws Exception {
        // Arrange
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUserName("test_user");
        when(userService.selectUserById(1L)).thenReturn(user);

        // Act & Assert
        mockMvc.perform(get("/system/user/{userId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.userId").value(1))
            .andExpect(jsonPath("$.data.userName").value("test_user"));

        verify(userService).selectUserById(1L);
    }
}
```

### 4.4 工具类测试

工具类使用**纯单元测试**（无依赖）。

```java
package org.dromara.common.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.*;

/**
 * 字符串工具类测试
 */
class StringUtilsTest {

    @ParameterizedTest
    @NullAndEmptySource
    void shouldReturnTrueWhenStringIsEmpty(String input) {
        assertThat(StringUtils.isEmpty(input)).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
        "hello, HELLO",
        "Hello, HELLO",
        "HELLO, HELLO"
    })
    void shouldConvertToUpperCase(String input, String expected) {
        assertThat(StringUtils.toUpperCase(input)).isEqualTo(expected);
    }
}
```

---

## 5. 特殊场景测试

### 5.1 多租户测试

```java
@ExtendWith(TenantContextExtension.class)
@Sql("/sql/data/multi-tenant-data.sql")
class MultiTenantUserServiceTest extends BaseIntegrationTest {

    @Autowired
    private ISysUserService userService;

    @Test
    @WithMockTenant(tenantId = "1")
    void shouldOnlyReturnTenant1Data() {
        List<SysUser> users = userService.selectUserList(new SysUser());

        assertThat(users)
            .isNotEmpty()
            .allMatch(u -> "1".equals(u.getTenantId()));
    }

    @Test
    @WithMockTenant(tenantId = "1")
    void shouldAutoSetTenantIdOnInsert() {
        SysUser newUser = new SysUser();
        newUser.setUserName("new_tenant1_user");
        // 不设置 tenantId

        userService.insertUser(newUser);

        assertThat(newUser.getTenantId()).isEqualTo("1"); // 自动设置
    }
}
```

### 5.2 Sa-Token 认证测试

```java
@AutoConfigureMockMvc
@ExtendWith(SaTokenAuthExtension.class)
class PermissionControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockSaTokenUser(permissions = "system:user:list")
    void shouldAllowAccessWithRequiredPermission() throws Exception {
        mockMvc.perform(get("/system/user/list"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockSaTokenUser(permissions = "system:user:query")
    void shouldDenyAccessWithoutRequiredPermission() throws Exception {
        mockMvc.perform(get("/system/user/list"))
            .andExpect(status().isForbidden());
    }
}
```

### 5.3 Dubbo RPC 测试

```java
class RemoteUserServiceImplTest extends BaseUnitTest {

    @Mock
    private ISysUserService userService;

    @InjectMocks
    private RemoteUserServiceImpl remoteUserService;

    @Test
    void shouldReturnRemoteUserVoWhenUserExists() {
        // Arrange
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUserName("test_user");
        when(userService.selectUserById(1L)).thenReturn(user);

        // Act
        RemoteUserVo result = remoteUserService.getUserInfo(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(userService).selectUserById(1L);
    }
}
```

---

## 6. 模块实施清单

### 6.1 通用库模块（ruoyi-common-*）

#### ✅ ruoyi-common-core

**测试重点：**

- [x] 工具类：StringUtils, DateUtils, TreeUtils, StreamUtils
- [ ] 验证器：所有自定义验证注解
- [ ] 异常处理：ServiceException, BaseException

**覆盖率目标：** 95%

---

#### ⏳ ruoyi-common-mybatis

**测试重点：**

- [ ] MyBatis-Plus 插件：租户插件、数据权限插件
- [ ] BaseMapperPlus 功能
- [ ] 自定义类型处理器

**覆盖率目标：** 90%

---

#### ⏳ ruoyi-common-redis

**测试重点：**

- [ ] Redis 操作封装
- [ ] 缓存注解
- [ ] 分布式锁 @Lock4j

**覆盖率目标：** 90%

---

### 6.2 业务模块（ruoyi-modules/*）

#### ⏳ ruoyi-auth

**测试重点：**

- [ ] 登录流程
- [ ] Token 生成
- [ ] 权限验证

**覆盖率目标：** 95%（安全关键）

---

#### ⏳ ruoyi-system

**测试重点：**

- [ ] 用户管理 CRUD
- [ ] 角色管理
- [ ] 菜单管理
- [ ] 多租户测试

**覆盖率目标：** 90%

---

### 6.3 实施进度表

| 阶段               | 模块                   | 预计工作量 | 状态     |
|------------------|----------------------|-------|--------|
| Phase 1 Week 1   | ruoyi-common-core    | 2天    | 🔄 进行中 |
| Phase 1 Week 1   | ruoyi-common-mybatis | 2天    | ⬜ 待开始  |
| Phase 1 Week 1   | ruoyi-common-redis   | 1天    | ⬜ 待开始  |
| Phase 2 Week 3   | ruoyi-auth           | 3天    | ⬜ 待开始  |
| Phase 3 Week 3-4 | ruoyi-system         | 5天    | ⬜ 待开始  |

---

## 7. 最佳实践

### 7.1 测试金字塔原则

```
        /\
       /  \
      / E2E \          10% - 端到端测试（最慢）
     /______\
    /        \
   / Integr.  \       30% - 集成测试（较慢）
  /____________\
 /              \
/   Unit Tests   \    60% - 单元测试（最快）
/________________\
```

### 7.2 FIRST 原则

- **F**ast (快速)：测试应该快速执行
- **I**ndependent (独立)：测试之间不应相互依赖
- **R**epeatable (可重复)：每次运行结果一致
- **S**elf-Validating (自我验证)：测试结果明确（通过/失败）
- **T**imely (及时)：在编写代码时或之前编写测试

### 7.3 常见陷阱

#### ❌ 不要测试框架代码

```java
// ❌ 错误：测试 Spring Data JPA 的 findById
@Test
void shouldFindById() {
    userRepository.findById(1L);
    // 这是在测试框架，不是你的代码
}

// ✅ 正确：测试你的业务逻辑
@Test
void shouldReturnActiveUsersOnly() {
    List<User> activeUsers = userRepository.findByStatus("active");
    assertThat(activeUsers).allMatch(u -> u.getStatus().equals("active"));
}
```

#### ❌ 不要依赖测试执行顺序

```java
// ❌ 错误：依赖执行顺序
@Test
@Order(1)
void createUser() {
    createdUserId = userService.create(user);
}

@Test
@Order(2)
void updateUser() {
    userService.update(createdUserId, updates); // 依赖上一个测试
}

// ✅ 正确：每个测试独立
@Test
void shouldCreateUser() {
    Long userId = userService.create(user);
    assertThat(userId).isNotNull();
}

@Test
void shouldUpdateUser() {
    Long userId = userService.create(user); // 自己准备数据
    userService.update(userId, updates);
}
```

---

## 8. 常用命令速查

```bash
# 运行所有测试
./gradlew test

# 只运行单元测试（快速）
./gradlew unitTest

# 只运行集成测试
./gradlew integrationTest

# 运行特定模块的测试
./gradlew :ruoyi-modules:ruoyi-system:test

# 运行单个测试类
./gradlew test --tests "SysUserServiceTest"

# 运行单个测试方法
./gradlew test --tests "SysUserServiceTest.shouldReturnUser*"

# 生成覆盖率报告
./gradlew jacocoTestReport

# 验证覆盖率达标
./gradlew jacocoTestCoverageVerification

# 并行测试
./gradlew test --parallel --max-workers=4

# 详细输出
./gradlew test --info
```

---

## 9. 断言与Mock速查

### 9.1 AssertJ 常用断言

```java
// 基本断言
assertThat(actual).isEqualTo(expected);
assertThat(actual).isNotNull();
assertThat(actual).isTrue();

// 字符串
assertThat(actual).contains("substring");
assertThat(actual).startsWith("prefix");
assertThat(actual).isBlank();

// 集合
assertThat(list).isEmpty();
assertThat(list).hasSize(3);
assertThat(list).contains("item1", "item2");
assertThat(list).containsExactly("item1", "item2"); // 顺序一致

// 集合提取
assertThat(users)
    .extracting("userName")
    .containsExactly("user1", "user2");

// 异常
assertThatThrownBy(() -> service.method())
    .isInstanceOf(ServiceException.class)
    .hasMessage("error message");
```

### 9.2 Mockito 速查

```java
// Mock 返回值
when(userRepository.findById(1L)).thenReturn(user);

// Mock 抛异常
when(userRepository.findById(9999L))
    .thenThrow(new EntityNotFoundException());

// 参数匹配器
when(userRepository.findById(any())).thenReturn(user);
when(userRepository.findById(anyLong())).thenReturn(user);

// 验证调用
verify(userRepository).findById(1L);
verify(userRepository, times(2)).findAll();
verify(userRepository, never()).deleteById(any());

// 捕获参数
ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
verify(userRepository).save(captor.capture());
User capturedUser = captor.getValue();
```

---

## 附录A: 测试依赖管理

### A.1 概览统计

| 统计项           | 数量                    |
|---------------|-----------------------|
| 总模块数          | 35                    |
| BOM 模块（无测试代码） | 2                     |
| 使用全局测试依赖的模块   | 32                    |
| 自定义测试依赖的模块    | 1 (ruoyi-common-core) |

### A.2 全局测试依赖配置

**配置位置:** `build.gradle.kts` (根项目，第 176-179 行)

所有非 BOM 子模块自动继承以下测试依赖：

```kotlin
dependencies {
    // Spring Boot 测试启动器（包含 JUnit, Mockito, AssertJ 等）
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // JUnit 5 (Jupiter) 核心测试引擎
    testImplementation("org.junit.jupiter:junit-jupiter")

    // JUnit 平台启动器
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
```

**版本管理方式:**

- 通过 Spring Boot BOM (`3.5.6`) 统一管理版本
- BOM 配置位置：`build.gradle.kts` 第 140-144 行

### A.3 实际解析的测试依赖版本

| 依赖库                          | 当前版本   | 最新稳定版  | 版本来源            | 状态     |
|------------------------------|--------|--------|-----------------|--------|
| **spring-boot-starter-test** | 3.5.6  | 3.5.6  | Spring Boot BOM | ✅ 最新   |
| **junit-jupiter**            | 5.12.2 | 5.14.0 | Spring Boot BOM | ⚠️ 可更新 |
| **mockito-core**             | 5.17.0 | 5.20.0 | Spring Boot BOM | ⚠️ 可更新 |
| **assertj-core**             | 3.27.4 | 3.27.4 | Spring Boot BOM | ✅ 最新   |

### A.4 标准化建议

#### 1. 移除过时依赖

**问题:** ruoyi-common-core 中的 `mockito-inline:5.2.0` 版本过旧

```kotlin
// ❌ 移除这一行（不再需要）
testImplementation("org.mockito:mockito-inline:5.2.0")

// ✅ mockito-core 已包含 inline 功能
testImplementation("org.mockito:mockito-core")
```

**原因:** Mockito 5.x 开始，inline mock maker 已成为默认功能，无需单独引入。

#### 2. 使用 Version Catalog 管理

建议在 `gradle/libs.versions.toml` 中统一管理测试依赖版本：

```toml
[versions]
junit = "5.12.2"
mockito = "5.17.0"
assertj = "3.27.4"

[libraries]
mockito-core = { module = "org.mockito:mockito-core", version.ref = "mockito" }
assertj-core = { module = "org.assertj:assertj-core", version.ref = "assertj" }
junit-jupiter = { module = "org.junit.jupiter:junit-jupiter", version.ref = "junit" }
```

### A.5 测试框架使用指南

#### JUnit 5 (Jupiter)

- **基础测试**: `@Test`, `@BeforeEach`, `@AfterEach`
- **参数化测试**: `@ParameterizedTest`, `@ValueSource`, `@CsvSource`
- **条件执行**: `@EnabledOnOs`, `@DisabledIf`

#### Mockito

- **创建 Mock**: `@Mock`, `@InjectMocks`
- **行为定义**: `when(...).thenReturn(...)`
- **验证调用**: `verify(mock).method()`

#### AssertJ

- **流式断言**: `assertThat(actual).isEqualTo(expected)`
- **集合断言**: `assertThat(list).hasSize(3)`
- **异常断言**: `assertThatThrownBy(() -> ...)`

---

## 附录B: 进度追踪

### B.1 当前进度概览

**详细进度报告:** 请查看 [docs/TESTING-PROGRESS-REPORT.md](docs/TESTING-PROGRESS-REPORT.md)

**最新统计 (2025-10-27):**

- ✅ 已完成测试用例数: **228个**
- ✅ ruoyi-common-core 模块覆盖率: **46%**
- ✅ 已完成工具类: StringUtils, DateUtils, StreamUtils, TreeBuildUtils

### B.2 查看进度报告

```bash
# 命令行查看
cat docs/TESTING-PROGRESS-REPORT.md

# 或在浏览器中打开
open docs/TESTING-PROGRESS-REPORT.md
```

### B.3 生成最新覆盖率报告

```bash
# 运行测试并生成报告
./gradlew :ruoyi-common:ruoyi-common-core:test jacocoTestReport

# 查看HTML报告
open ruoyi-common/ruoyi-common-core/build/reports/jacoco/test/html/index.html
```

### B.4 进度更新规则

进度报告 (`TESTING-PROGRESS-REPORT.md`) 应在以下情况下更新：

1. 完成一个完整工具类的测试（覆盖率 > 90%）
2. 完成一个模块的所有测试
3. 每周定期更新统计数据
4. 阶段性里程碑完成时

---

## 结语

这份测试指南涵盖了 RuoYi-Cloud-Plus 项目单元测试与集成测试的所有核心内容。

**下一步行动：**

1. ✅ 审阅本指南
2. ✅ 选择第一个模块开始实施
3. ✅ 定期查看进度报告跟踪进展
4. ✅ 持续改进和优化

**记住：**

- 测试不是负担，是投资
- 先写测试，后写代码（TDD）
- 保持测试简单、快速、可读
- 定期审查和重构测试代码

**相关资源：**

- [进度报告](docs/TESTING-PROGRESS-REPORT.md) - 实时跟踪测试进展
- [JUnit 5 文档](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito 文档](https://javadoc.io/doc/org.mockito/mockito-core/latest/)
- [AssertJ 文档](https://assertj.github.io/doc/)

---

**文档版本:** 2.0
**最后更新:** 2025-10-29
**维护者:** Test Team
**状态:** ✅ 生产就绪

祝测试顺利！🎉

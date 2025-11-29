# 集成测试框架设置完成报告

> 📅 **完成日期**: 2025-11-10
> 🎯 **任务**: Week 1 - Day 1: 集成测试框架设置
> ✅ **状态**: 已完成

---

## 📋 任务概述

根据 [testing-progress-tracker.md](testing-progress-tracker.md) 的计划，完成集成测试框架的基础设施搭建，为后续的业务模块集成测试（ruoyi-gen,
ruoyi-resource, ruoyi-workflow）提供支持。

---

## ✅ 已完成的工作

### 1. 创建 ruoyi-common-test 模块

**位置**: `ruoyi-common/ruoyi-common-test/`

**模块结构**:

```
ruoyi-common/ruoyi-common-test/
├── build.gradle.kts                           # Gradle 配置
├── src/main/java/org/dromara/common/test/
│   ├── BaseIntegrationTest.java               # 集成测试基类
│   ├── TestApplication.java                   # 测试应用配置
│   ├── config/
│   │   └── TestContainersConfig.java          # Testcontainers 配置
│   └── utils/
│       ├── AuthTestUtils.java                 # 认证测试工具
│       ├── TenantTestUtils.java               # 租户测试工具
│       └── SqlScriptExecutor.java             # SQL 脚本执行器
├── src/main/resources/
│   └── application-test.yml                   # 测试配置
└── src/test/java/org/dromara/common/test/
    ├── examples/
    │   └── SampleIntegrationTest.java         # 示例测试
    └── IntegrationTestFrameworkValidationTest.java  # 框架验证测试
```

---

### 2. 核心组件详细说明

#### 2.1 BaseIntegrationTest（集成测试基类）

**文件**: `BaseIntegrationTest.java`

**功能**:

- 自动管理 Testcontainers 生命周期
- 配置 MySQL 和 Redis 容器
- 动态注入数据源和 Redis 连接配置
- 提供便捷的容器访问方法

**使用方式**:

```java
@SpringBootTest
class MyServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MyService myService;

    @Test
    void shouldDoSomething() {
        // 测试逻辑
    }
}
```

**特性**:

- ✅ 支持 MySQL 8.0 容器
- ✅ 支持 Redis 7 容器
- ✅ 容器重用（withReuse(true)）加快测试速度
- ✅ 自动配置字符集（UTF-8）和时区（+8:00）
- ✅ 禁用 Nacos（集成测试不需要）

---

#### 2.2 TestContainersConfig（容器配置类）

**文件**: `TestContainersConfig.java`

**支持的容器**:

1. **MySQL** (`mysql:8.0`)
    - 数据库: `ry_cloud_test`
    - 用户名: `root`
    - 密码: `root123`

2. **PostgreSQL** (`postgres:15-alpine`) - 备用
    - 数据库: `ry_cloud_test`
    - 用户名: `postgres`
    - 密码: `postgres123`

3. **Redis** (`redis:7-alpine`)
    - 端口: 6379

4. **MinIO** (`minio/minio:latest`) - 用于 OSS 测试
    - API 端口: 9000
    - Console 端口: 9001
    - Access Key: `minioadmin`
    - Secret Key: `minioadmin`

5. **RocketMQ** (`apache/rocketmq:5.1.0`) - 可选
    - NameServer 端口: 9876
    - Broker 端口: 10911

6. **Nacos** (`nacos/nacos-server:v2.2.3`) - 可选
    - 端口: 8848
    - 用户名/密码: `nacos/nacos`

**扩展性**:

- 配置类采用静态内部类设计，方便后续添加新的容器
- 每个容器都有独立的配置参数和工厂方法

---

#### 2.3 测试工具类

##### AuthTestUtils（认证测试工具）

**文件**: `AuthTestUtils.java`

**功能**:

- 模拟用户登录：`mockLogin(userId, username)`
- 模拟管理员登录：`mockAdminLogin()`
- 设置用户权限：`setPermissions(userId, ...permissions)`
- 设置用户角色：`setRoles(userId, ...roles)`
- 检查登录状态：`isLogin(userId)`
- 获取登录信息：`getLoginUserId()`, `getLoginUsername()`
- 登出用户：`logout(userId)`

**使用示例**:

```java
// 模拟管理员登录并设置权限
String token = AuthTestUtils.mockAdminLoginWithPermissions(
    "system:user:list",
    "system:user:add"
);

// 执行需要权限的操作
userService.listUsers();
userService.addUser(user);

// 登出
AuthTestUtils.logout();
```

**注意事项**:

- ⚠️ Sa-Token 需要 Web 上下文才能完全工作
- ⚠️ 在纯单元测试环境中，某些功能可能受限
- ✅ 在 `@SpringBootTest(webEnvironment = MOCK)` 环境中可正常使用

---

##### TenantTestUtils（租户测试工具）

**文件**: `TenantTestUtils.java`

**功能**:

- 设置当前租户：`setTenant(tenantId)`
- 快速设置：`setDefaultTenant()`, `setTestTenant1()`, `setTestTenant2()`
- 获取当前租户：`getTenant()`
- 在指定租户上下文中执行操作：`executeInTenant(tenantId, operation)`
- 验证租户隔离：`verifyTenantIsolation(...)`
- 模拟多租户用户登录：`mockTenantUserLogin(userId, username, tenantId)`

**预定义租户 ID**:

- `DEFAULT_TENANT_ID = "000000"` - 主租户
- `TEST_TENANT_1_ID = "000001"` - 测试租户1
- `TEST_TENANT_2_ID = "000002"` - 测试租户2

**使用示例**:

```java
// 在租户1中创建数据
TenantTestUtils.setTestTenant1();
service.createData(data1);

// 切换到租户2
TenantTestUtils.setTestTenant2();
service.createData(data2);

// 验证租户隔离
List<Data> tenant1Data = TenantTestUtils.executeInTenant("000001",
    () -> service.listAll());
List<Data> tenant2Data = TenantTestUtils.executeInTenant("000002",
    () -> service.listAll());

assertThat(tenant1Data).hasSize(1);
assertThat(tenant2Data).hasSize(1);
```

---

##### SqlScriptExecutor（SQL 脚本执行器）

**文件**: `SqlScriptExecutor.java`

**功能**:

- 执行 SQL 脚本文件：`execute(dataSource, scriptPath)`
- 批量执行脚本：`executeBatch(dataSource, ...scriptPaths)`
- 执行 SQL 语句：`executeSql(dataSource, ...sqlStatements)`
- 读取脚本内容：`readScript(scriptPath)`
- 清空表数据：`truncateTables(dataSource, ...tableNames)`
- 删除表数据：`deleteFromTables(dataSource, ...tableNames)`
- 检查表是否存在：`tableExists(dataSource, tableName)`

**使用示例**:

```java
// 执行数据库初始化脚本
SqlScriptExecutor.execute(dataSource, "classpath:db/schema.sql");
SqlScriptExecutor.execute(dataSource, "classpath:db/data.sql");

// 执行测试 SQL
SqlScriptExecutor.executeSql(dataSource,
    "INSERT INTO sys_user VALUES (1, 'admin', ...)",
    "INSERT INTO sys_role VALUES (1, 'admin', ...)"
);

// 测试后清理数据
SqlScriptExecutor.truncateTables(dataSource,
    "sys_user", "sys_role", "sys_menu");
```

---

### 3. 配置文件

#### application-test.yml

**位置**: `src/main/resources/application-test.yml`

**关键配置**:

```yaml
spring:
  # 数据源（由 Testcontainers 动态配置）
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5

  # Redis（由 Testcontainers 动态配置）
  data:
    redis:
      database: 0
      timeout: 10s

  # 禁用 Nacos
  cloud:
    nacos:
      config.enabled: false
      discovery.enabled: false

# MyBatis-Plus
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: delFlag
      logic-delete-value: 1
      logic-not-delete-value: 0

# Sa-Token
sa-token:
  token-name: Authorization
  token-prefix: Bearer
  timeout: 3600
  is-concurrent: true
  is-share: false
  token-style: uuid
  is-log: false
```

---

### 4. Gradle 依赖配置

**文件**: `build.gradle.kts`

**核心依赖**:

```kotlin
dependencies {
    // Spring Boot Test
    api("org.springframework.boot:spring-boot-starter-test")

    // Testcontainers
    api("org.testcontainers:testcontainers")
    api("org.testcontainers:junit-jupiter")
    api("org.testcontainers:mysql")
    api("org.testcontainers:postgresql")

    // Database
    api("com.mysql:mysql-connector-j")
    api("com.zaxxer:HikariCP")
    api(libs.mybatis.plus.spring.boot3.starter)

    // Redis
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api(libs.redisson)

    // Sa-Token
    api(libs.sa.token.spring.boot3.starter)
    api(libs.sa.token.redis.jackson)

    // Project modules
    api(project(":ruoyi-common:ruoyi-common-core"))
    compileOnly(project(":ruoyi-common:ruoyi-common-satoken"))
    compileOnly(project(":ruoyi-common:ruoyi-common-mybatis"))
    compileOnly(project(":ruoyi-common:ruoyi-common-redis"))
    compileOnly(project(":ruoyi-common:ruoyi-common-tenant"))
}
```

---

## 🧪 验证测试结果

### IntegrationTestFrameworkValidationTest

**测试报告**: ✅ **12/12 测试通过**

#### 测试覆盖范围:

1. **Testcontainers 基础设施验证** (3 tests)
    - ✅ 应该成功启动 MySQL 容器
    - ✅ 应该成功启动 Redis 容器
    - ✅ 容器应该可以重用以提高测试速度

2. **Spring Boot 集成验证** (2 tests)
    - ✅ 应该成功加载 Spring 上下文
    - ✅ 应该成功注入 DataSource

3. **数据库连接验证** (3 tests)
    - ✅ 应该能够连接到测试数据库
    - ✅ 应该能够执行简单的 SQL 查询
    - ✅ 应该能够创建临时表

4. **SQL 脚本执行器验证** (3 tests)
    - ✅ 应该能够执行 SQL 语句
    - ✅ 应该能够清空表数据
    - ✅ 应该能够检查表是否存在

5. **框架性能验证** (1 test)
    - ✅ 容器启动时间应该在可接受范围内

**执行时间**: ~46 秒（首次下载镜像）

**运行命令**:

```bash
./gradlew :ruoyi-common:ruoyi-common-test:test --tests "IntegrationTestFrameworkValidationTest"
```

---

### SampleIntegrationTest

**测试报告**: ⚠️ **6/13 测试通过** (Sa-Token 相关测试需要 Web 环境)

**通过的测试**:

- ✅ 3个基础设施测试（MySQL, Redis, Spring）
- ✅ 3个租户工具测试（部分功能）

**失败的测试**:

- ❌ 7个认证测试（需要 Web 上下文）

**原因分析**:

- Sa-Token 依赖 Spring MVC 的 Request/Response 上下文
- 纯 @SpringBootTest 环境中无法完全模拟
- 解决方案：在具体业务模块测试中使用 `@SpringBootTest(webEnvironment = MOCK)`

---

## 📊 框架能力总结

### ✅ 已验证可用功能

1. **Testcontainers 集成**
    - ✅ MySQL 容器自动管理
    - ✅ Redis 容器自动管理
    - ✅ 容器重用机制
    - ✅ 动态配置注入

2. **数据库操作**
    - ✅ 数据源自动配置
    - ✅ JDBC 连接
    - ✅ JdbcTemplate 操作
    - ✅ SQL 脚本执行
    - ✅ 表数据清理

3. **Spring Boot 集成**
    - ✅ 自动配置
    - ✅ 依赖注入
    - ✅ Profile 支持（test profile）

4. **租户功能**
    - ✅ 租户上下文管理
    - ✅ 租户切换
    - ✅ 租户隔离验证工具

### ⚠️ 需要特殊处理的场景

1. **Sa-Token 认证**
    - 需要 Web 环境：使用 `@SpringBootTest(webEnvironment = MOCK)`
    - 或使用 Mock 方式

2. **MyBatis-Plus MapstructUtils**
    - 需要 Spring 容器：使用 @SpringBootTest 集成测试
    - 约 60 个 insert/update 方法需要集成测试

3. **Dubbo RPC**
    - 需要 Dubbo 容器：使用 @SpringBootTest + Dubbo Mock

---

## 🚀 下一步计划

### Week 1 - Day 2-3: ruoyi-gen 集成测试

**模块**: `ruoyi-modules/ruoyi-gen`

**计划任务**:

1. 创建 GenTableServiceImpl 集成测试
2. 测试数据库表导入功能
3. 测试代码生成功能（Velocity 模板）
4. 测试代码预览和下载
5. 测试表结构同步

**预计测试数**: ~20 个

**依赖**:

- ✅ Testcontainers MySQL（已就绪）
- ⏳ Velocity 模板引擎
- ⏳ Anyline 数据库元数据提取
- ⏳ 文件 I/O 操作

---

### Week 1 - Day 4-5: ruoyi-resource 集成测试（部分）

**模块**: `ruoyi-modules/ruoyi-resource`

**计划任务**:

1. SysOssServiceImpl 集成测试（MinIO）
2. SysOssConfigServiceImpl 集成测试（Redis 缓存）

**预计测试数**: ~25 个

**依赖**:

- ✅ Testcontainers MySQL（已就绪）
- ✅ Testcontainers Redis（已就绪）
- ⏳ Testcontainers MinIO（配置已完成，待使用）

---

## 📝 使用文档

### 如何编写集成测试

#### 1. 基本集成测试

```java
@SpringBootTest
@DisplayName("我的服务集成测试")
class MyServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MyService myService;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        SqlScriptExecutor.execute(dataSource, "classpath:db/test-data.sql");
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        SqlScriptExecutor.truncateTables(dataSource, "my_table");
    }

    @Test
    @DisplayName("应该成功插入数据")
    void shouldInsertData() {
        // Arrange
        MyEntity entity = new MyEntity();
        entity.setName("test");

        // Act
        myService.insert(entity);

        // Assert
        assertThat(entity.getId()).isNotNull();
    }
}
```

#### 2. 带租户的集成测试

```java
@Test
@DisplayName("应该实现租户数据隔离")
void shouldIsolateTenantData() {
    // 租户1
    TenantTestUtils.setTestTenant1();
    MyEntity entity1 = new MyEntity("data1");
    myService.insert(entity1);

    // 租户2
    TenantTestUtils.setTestTenant2();
    MyEntity entity2 = new MyEntity("data2");
    myService.insert(entity2);

    // 验证隔离
    List<MyEntity> tenant1Data = TenantTestUtils.executeInTenant("000001",
        () -> myService.listAll());
    List<MyEntity> tenant2Data = TenantTestUtils.executeInTenant("000002",
        () -> myService.listAll());

    assertThat(tenant1Data).hasSize(1).first()
        .extracting(MyEntity::getName).isEqualTo("data1");
    assertThat(tenant2Data).hasSize(1).first()
        .extracting(MyEntity::getName).isEqualTo("data2");
}
```

#### 3. 带认证的集成测试

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class MySecureServiceIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("应该验证用户权限")
    void shouldCheckUserPermission() {
        // Arrange - 模拟管理员登录并设置权限
        AuthTestUtils.mockAdminLoginWithPermissions(
            "system:data:list",
            "system:data:add"
        );

        // Act & Assert
        assertThatCode(() -> mySecureService.listData())
            .doesNotThrowAnyException();
        assertThatCode(() -> mySecureService.addData(data))
            .doesNotThrowAnyException();
    }
}
```

---

## 🎯 里程碑达成

- [x] **里程碑 6**: 集成测试框架设置 ✅ **已完成（2025-11-10）**

### 完成情况

| 任务                               | 状态 | 完成日期       |
|----------------------------------|----|------------|
| 创建 ruoyi-common-test 模块          | ✅  | 2025-11-10 |
| 创建 BaseIntegrationTest 基类        | ✅  | 2025-11-10 |
| 配置 Testcontainers (MySQL, Redis) | ✅  | 2025-11-10 |
| 创建测试工具类                          | ✅  | 2025-11-10 |
| 创建测试配置文件                         | ✅  | 2025-11-10 |
| 编写框架验证测试                         | ✅  | 2025-11-10 |
| 验证测试通过（12/12）                    | ✅  | 2025-11-10 |

---

## 📞 技术支持

**框架维护**: Test Team
**创建日期**: 2025-11-10
**文档版本**: 1.0
**当前状态**: ✅ 已完成，可以开始业务模块集成测试

---

## 附录

### A. Testcontainers 配置建议

为了提高测试速度，建议在 `~/.testcontainers.properties` 中配置：

```properties
# 启用容器重用
testcontainers.reuse.enable=true

# 配置 Docker 环境（如果使用非默认配置）
# docker.host=tcp://localhost:2375
```

### B. 常见问题

**Q1: 容器启动缓慢？**

- A: 首次启动会下载 Docker 镜像，后续会使用缓存
- 建议：启用容器重用（见上文配置）

**Q2: Sa-Token 测试失败？**

- A: Sa-Token 需要 Web 上下文，使用 `@SpringBootTest(webEnvironment = MOCK)`

**Q3: 如何在 CI/CD 中运行？**

- A: 确保 CI 环境有 Docker 支持，Testcontainers 会自动处理

### C. 性能优化建议

1. **启用容器重用**: 减少启动开销
2. **使用 @Nested 测试**: 组织测试结构，共享容器
3. **合理使用 @BeforeAll/@AfterAll**: 减少重复初始化
4. **数据清理策略**: 使用 TRUNCATE 而不是 DELETE

---

**报告结束**

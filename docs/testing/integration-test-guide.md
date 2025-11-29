# 集成测试完整指南

本指南提供了在 RuoYi-Cloud-Plus 中编写集成测试的全面参考。

## 什么是集成测试？

集成测试验证多个组件在真实环境中的协作情况。

### 特点

- **真实依赖**: 使用真实数据库、缓存等
- **较慢**: 执行时间较长（秒级或更长）
- **全面**: 测试完整的业务流程
- **复杂**: 需要更多的设置和配置

### 单元测试 vs 集成测试

| 特点     | 单元测试    | 集成测试   |
|--------|---------|--------|
| **范围** | 单个方法/类  | 多个组件协作 |
| **依赖** | Mock 对象 | 真实依赖   |
| **环境** | 隔离的     | 真实的    |
| **速度** | 快（毫秒）   | 较慢（秒级） |
| **设置** | 简单      | 复杂     |

## 集成测试框架

### Spring Boot Test

Spring Boot 提供了 `@SpringBootTest` 进行集成测试。

**核心注解**:

```java
@SpringBootTest           // 启动完整的 Spring 容器
@DataJpaTest              // 仅加载 JPA 相关配置
@DataMongoTest            // 仅加载 MongoDB 配置
@DataRedisTest            // 仅加载 Redis 配置
@WebMvcTest(Controller)   // 仅加载 MVC 相关配置
@AutoConfigureMockMvc     // 配置 MockMvc（无需启动服务器）
```

### Testcontainers

Testcontainers 使用 Docker 提供真实的外部服务（数据库、Redis、Kafka 等）。

**支持的服务**:

- MySQL、PostgreSQL、Oracle 等数据库
- Redis
- Kafka
- MongoDB
- ElasticSearch
- 以及 100+ 其他服务

## BaseIntegrationTest 基类

所有集成测试都应该继承 `BaseIntegrationTest`。

### 基本用法

```java

@SpringBootTest
@DisplayName("系统用户服务集成测试")
class SysUserServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveAndRetrieveUser() {
        // 测试代码
    }
}
```

### BaseIntegrationTest 提供

- Spring 容器初始化
- 事务管理
- 数据库事务回滚（测试隔离）
- TestEntityManager（JPA 测试工具）
- 便利的数据库操作方法

## 数据库测试

### 使用真实 MySQL

**配置**:

```yaml
# application-test.yml
spring:
    datasource:
        url: jdbc:mysql://localhost:3306/ruoyi_cloud_plus_test
        username: root
        password: 123456
        driver-class-name: com.mysql.cj.jdbc.Driver
```

**测试示例**:

```java

@SpringBootTest
@DisplayName("用户数据库操作")
class SysUserRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private SysUserMapper userMapper;

    @Test
    @DisplayName("应该保存和查询用户")
    void shouldSaveAndQueryUser() {
        // Arrange
        SysUser user = new SysUser();
        user.setUserName("testuser");
        user.setNickName("测试用户");
        user.setUserEmail("test@example.com");
        user.setDelFlag("0");

        // Act
        int result = userMapper.insert(user);

        // Assert
        assertThat(result).isEqualTo(1);

        // 查询验证
        SysUser savedUser = userMapper.selectById(user.getUserId());
        assertThat(savedUser)
            .isNotNull()
            .extracting(SysUser::getUserName, SysUser::getNickName)
            .containsExactly("testuser", "测试用户");
    }

    @Test
    @DisplayName("应该删除用户")
    void shouldDeleteUser() {
        // Arrange
        SysUser user = new SysUser();
        user.setUserName("todelete");
        user.setDelFlag("0");
        userMapper.insert(user);

        // Act
        int result = userMapper.deleteById(user.getUserId());

        // Assert
        assertThat(result).isEqualTo(1);
        assertThat(userMapper.selectById(user.getUserId()))
            .isNull();
    }
}
```

### 使用 Testcontainers

**添加依赖**:

```gradle
testImplementation 'org.testcontainers:testcontainers:1.17.6'
testImplementation 'org.testcontainers:mysql:1.17.6'
```

**配置 MySQL 容器**:

```java

@SpringBootTest
@Testcontainers
@DisplayName("使用 Testcontainers 的用户测试")
class SysUserTestcontainersTest extends BaseIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
        .withDatabaseName("ruoyi_test")
        .withUsername("root")
        .withPassword("123456");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private SysUserMapper userMapper;

    @Test
    void shouldWorkWithTestcontainers() {
        // Testcontainers 自动启动数据库
        SysUser user = new SysUser();
        user.setUserName("containertest");
        userMapper.insert(user);

        SysUser found = userMapper.selectById(user.getUserId());
        assertThat(found).isNotNull();
    }
}
```

## Service 集成测试

### 完整的 Service 流程测试

```java

@SpringBootTest
@DisplayName("系统用户服务集成测试")
class SysUserServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Nested
    @DisplayName("用户创建流程")
    class UserCreationTests {

        @Test
        @DisplayName("应该创建用户并分配角色")
        void shouldCreateUserWithRoles() {
            // Arrange
            SysUserAddBO addBO = new SysUserAddBO();
            addBO.setUserName("newuser");
            addBO.setNickName("新用户");
            addBO.setUserPassword("123456");
            addBO.setUserEmail("newuser@example.com");
            addBO.setRoleIds(Arrays.asList(1L, 2L));

            // Act
            boolean result = userService.insertByBo(addBO);

            // Assert
            assertThat(result).isTrue();

            // 验证用户被创建
            SysUser createdUser = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, "newuser"));
            assertThat(createdUser).isNotNull();

            // 验证角色被分配
            List<SysUserRole> roles = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, createdUser.getUserId()));
            assertThat(roles).hasSize(2);
        }
    }

    @Nested
    @DisplayName("用户更新流程")
    class UserUpdateTests {

        @Test
        @DisplayName("应该更新用户信息")
        void shouldUpdateUser() {
            // Arrange - 创建初始用户
            SysUser user = new SysUser();
            user.setUserName("existing");
            user.setNickName("现存用户");
            user.setDelFlag("0");
            userMapper.insert(user);

            // Act - 更新用户
            SysUserEditBO editBO = new SysUserEditBO();
            editBO.setUserId(user.getUserId());
            editBO.setNickName("更新后的用户");
            editBO.setUserEmail("updated@example.com");

            boolean result = userService.updateByBo(editBO);

            // Assert
            assertThat(result).isTrue();

            // 验证更新
            SysUser updated = userMapper.selectById(user.getUserId());
            assertThat(updated)
                .extracting(SysUser::getNickName, SysUser::getUserEmail)
                .containsExactly("更新后的用户", "updated@example.com");
        }
    }
}
```

## Redis 集成测试

### 缓存集成测试

```java

@SpringBootTest
@DisplayName("Redis 缓存集成测试")
class CacheIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SysUserService userService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("应该缓存用户查询结果")
    void shouldCacheUserQuery() {
        // Arrange
        Long userId = 1L;
        String cacheKey = "user:" + userId;

        // Act - 第一次查询（会缓存）
        userService.selectUserById(userId);

        // Assert - 验证缓存存在
        String cachedValue = redisTemplate.opsForValue().get(cacheKey);
        assertThat(cachedValue).isNotEmpty();

        // Act - 第二次查询（应该从缓存返回）
        SysUser user = userService.selectUserById(userId);
        assertThat(user).isNotNull();
    }

    @Test
    @DisplayName("应该在更新后清除缓存")
    void shouldClearCacheAfterUpdate() {
        // Arrange
        Long userId = 1L;
        String cacheKey = "user:" + userId;

        // 先缓存用户
        userService.selectUserById(userId);
        assertThat(redisTemplate.hasKey(cacheKey)).isTrue();

        // Act - 更新用户（应该清除缓存）
        SysUserEditBO editBO = new SysUserEditBO();
        editBO.setUserId(userId);
        editBO.setNickName("更新用户");
        userService.updateByBo(editBO);

        // Assert - 验证缓存被清除
        assertThat(redisTemplate.hasKey(cacheKey)).isFalse();
    }
}
```

### Testcontainers Redis

```java

@SpringBootTest
@Testcontainers
@DisplayName("Redis Testcontainers 集成测试")
class RedisTestcontainersTest extends BaseIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.0"))
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void shouldConnectToRedis() {
        redisTemplate.opsForValue().set("test", "value");
        assertThat(redisTemplate.opsForValue().get("test")).isEqualTo("value");
    }
}
```

## Web 层集成测试

### MockMvc API 测试

```java

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("用户 API 集成测试")
class SysUserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SysUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("GET 端点")
    class GetEndpointTests {

        @Test
        @DisplayName("应该获取用户列表")
        void shouldGetUserList() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/system/user/list")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        }

        @Test
        @DisplayName("应该根据 ID 获取用户")
        void shouldGetUserById() throws Exception {
            // Act & Assert
            mockMvc.perform(get("/system/user/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1));
        }
    }

    @Nested
    @DisplayName("POST 端点")
    class PostEndpointTests {

        @Test
        @DisplayName("应该创建新用户")
        void shouldCreateUser() throws Exception {
            // Arrange
            SysUserAddBO addBO = new SysUserAddBO();
            addBO.setUserName("apitest");
            addBO.setNickName("API测试用户");
            addBO.setUserPassword("123456");

            String requestBody = objectMapper.writeValueAsString(addBO);

            // Act & Assert
            mockMvc.perform(post("/system/user/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        }
    }
}
```

## 事务管理

### 测试事务隔离

```java

@SpringBootTest
@DisplayName("事务集成测试")
class TransactionIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysUserMapper userMapper;

    @Test
    @DisplayName("失败应该回滚更改")
    @Transactional
    void shouldRollbackOnFailure() {
        // Arrange
        SysUser user = new SysUser();
        user.setUserName("rollback");
        user.setDelFlag("0");
        userMapper.insert(user);

        // Act - 执行会失败的操作
        assertThatThrownBy(() -> {
            userService.processUserWithError(user.getUserId());
        }).isInstanceOf(RuntimeException.class);

        // Assert - 验证更改被回滚
        SysUser found = userMapper.selectById(user.getUserId());
        assertThat(found.getUserName()).isEqualTo("rollback");  // 未被修改
    }

    @Test
    @DisplayName("成功应该提交更改")
    void shouldCommitOnSuccess() {
        // Arrange
        SysUser user = new SysUser();
        user.setUserName("commit");
        user.setDelFlag("0");
        userMapper.insert(user);

        // Act
        userService.processUserWithoutError(user.getUserId());

        // Assert - 验证更改被提交
        SysUser found = userMapper.selectById(user.getUserId());
        assertThat(found).isNotNull();
    }
}
```

## 完整的集成测试示例

```java
@SpringBootTest
@Testcontainers
@DisplayName("完整的用户管理流程集成测试")
class CompleteUserManagementIntegrationTest extends BaseIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
        .withDatabaseName("ruoyi_test")
        .withUsername("root")
        .withPassword("123456");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("完整的用户生命周期")
    void completeUserLifecycle() throws Exception {
        // 1. 创建用户
        SysUserAddBO addBO = new SysUserAddBO();
        addBO.setUserName("lifecycle");
        addBO.setNickName("生命周期用户");
        addBO.setUserPassword("123456");

        String createResponse = mockMvc.perform(post("/system/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(addBO)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        // 2. 获取用户
        mockMvc.perform(get("/system/user/list")
                .param("userName", "lifecycle"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rows[0].userName").value("lifecycle"));

        // 3. 更新用户
        SysUserEditBO editBO = new SysUserEditBO();
        editBO.setNickName("已更新的用户");

        mockMvc.perform(put("/system/user/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editBO)))
            .andExpect(status().isOk());

        // 4. 删除用户
        mockMvc.perform(delete("/system/user/1"))
            .andExpect(status().isOk());
    }
}
```

## 最佳实践

### 1. 使用 @DisplayName 提供清晰的测试描述

```java

@DisplayName("用户创建流程")
class UserCreationTests {
    @Test
    @DisplayName("应该创建新用户并返回用户 ID")
    void shouldCreateUser() {
    }
}
```

### 2. 清理测试数据

```java

@AfterEach
void tearDown() {
    // 清理测试数据
    userMapper.delete(new LambdaQueryWrapper<SysUser>()
        .eq(SysUser::getUserName, "testuser"));
}
```

### 3. 使用 @Transactional 进行隔离

```java

@Test
@Transactional
    // 测试后自动回滚
void shouldIsolateTestData() {
    // 测试数据不会污染数据库
}
```

### 4. 验证端到端流程

```java

@Test
void shouldVerifyCompleteProcess() {
    // 1. 创建数据
    // 2. 修改数据
    // 3. 验证最终状态
}
```

## 常见问题

**Q: 集成测试很慢怎么办？**
A:

- 只在必要时运行集成测试
- 使用 `@DataJpaTest` 等部分容器测试
- 并行运行测试
- 使用 Testcontainers 的缓存功能

**Q: 如何处理 Testcontainers 的网络问题？**
A:

- 确保 Docker 已安装并运行
- 检查 Docker 权限
- 使用本地数据库代替

**Q: 集成测试中如何测试事务？**
A:

- 使用 `@Transactional` 进行隔离
- 使用 `TestTransactionManager` 验证事务状态

## 参考资源

- [Spring Boot Test 文档](https://spring.io/guides/gs/testing-web/)
- [Testcontainers 文档](https://www.testcontainers.org/)
- [MockMvc 文档](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/servlet/MockMvc.html)

---

**下一步**: 参考 [当前测试状态](/docs/testing/current-status.md) 了解项目的测试覆盖情况，并查看实际的测试代码示例。

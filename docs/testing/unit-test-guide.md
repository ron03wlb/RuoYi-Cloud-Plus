# 单元测试完整指南

本指南提供了在 RuoYi-Cloud-Plus 中编写单元测试的全面参考。

## 什么是单元测试？

单元测试是对程序中最小可测试单元（通常是一个方法或类）的验证。

### 特点

- **独立**: 不依赖外部资源（数据库、网络等）
- **快速**: 执行时间短（毫秒级）
- **可重复**: 多次运行结果相同
- **清晰**: 代码易于理解和维护

### 单元测试 vs 集成测试

| 特点       | 单元测试    | 集成测试   |
|----------|---------|--------|
| **范围**   | 单个方法/类  | 多个组件协作 |
| **依赖**   | 使用 Mock | 真实依赖   |
| **速度**   | 快（毫秒）   | 较慢（秒级） |
| **复杂度**  | 简单      | 复杂     |
| **覆盖范围** | 代码逻辑    | 业务流程   |

## 单元测试框架

### JUnit 5

JUnit 5 是现代的 Java 测试框架，本项目的推荐选择。

**核心注解**:

```java
@Test              // 标记为测试方法
@DisplayName()     // 为测试提供友好名称
@BeforeEach        // 在每个测试前执行
@AfterEach         // 在每个测试后执行
@BeforeAll         // 在所有测试前执行（静态方法）
@AfterAll          // 在所有测试后执行（静态方法）
@Nested            // 用于嵌套测试类
@ParameterizedTest // 参数化测试
```

### Mockito

Mockito 用于创建 Mock 对象，隔离被测试代码。

**核心概念**:

```java
@Mock              // 创建 Mock 对象
@InjectMocks       // 将 Mock 注入到被测类
when()             // 设置 Mock 的返回值
verify()           // 验证 Mock 被调用
ArgumentCaptor     // 捕获方法参数
```

### AssertJ

AssertJ 提供链式、易读的断言 API。

```java
assertThat(value)
    .isNotNull()
    .isEqualTo(expected)
    .contains("substring");
```

## BaseUnitTest 基类

所有单元测试都应该继承 `BaseUnitTest`。

### 基本用法

```java
@DisplayName("系统用户服务单元测试")
class SysUserServiceImplTest extends BaseUnitTest {

    @InjectMocks
    private SysUserServiceImpl userService;

    @Mock
    private SysUserMapper userMapper;

    @Test
    void shouldGetUserById() {
        // 测试代码
    }
}
```

### BaseUnitTest 提供

- Mock 框架初始化
- 常用的测试工具方法
- 标准化的配置
- 便利的 Mock 设置

## AAA 模式

所有测试都遵循 **A-A-A 模式**（Arrange-Act-Assert）。

### 模式解释

```java
@Test
@DisplayName("应该根据 ID 获取用户")
void shouldGetUserByIdWhenUserExists() {
    // ===== Arrange（准备）=====
    // 准备测试数据和 Mock 配置
    Long userId = 1L;
    SysUser expectedUser = new SysUser();
    expectedUser.setUserId(userId);
    expectedUser.setNickName("管理员");

    when(userMapper.selectById(userId))
        .thenReturn(expectedUser);

    // ===== Act（执行）=====
    // 执行要测试的操作
    SysUser actualUser = userService.selectUserById(userId);

    // ===== Assert（断言）=====
    // 验证结果是否符合预期
    assertThat(actualUser)
        .isNotNull()
        .extracting(SysUser::getUserId)
        .isEqualTo(userId);

    // ===== Verify（可选）=====
    // 验证 Mock 被正确调用
    verify(userMapper, times(1)).selectById(userId);
}
```

## 命名规范

### 测试类命名

```java
// 遵循 {ClassName}Test 规范
public class SysUserServiceImplTest { }
public class SysRoleServiceImplTest { }
```

### 测试方法命名

使用 `should...When...` 或 `should...` 命名规范：

```java
// 推荐的命名格式
@Test
void shouldGetUserByIdWhenUserExists() { }

@Test
void shouldThrowExceptionWhenUserNotFound() { }

@Test
void shouldReturnEmptyListWhenNoUsersExist() { }

// 也可以使用更描述性的名称配合 @DisplayName
@Test
@DisplayName("当用户存在时应该返回用户信息")
void test_getUser_exists() { }
```

## 常见测试场景

### 场景 1：测试成功路径

```java
@Nested
@DisplayName("创建用户")
class CreateUserTests {

    @Test
    @DisplayName("应该成功创建用户")
    void shouldCreateUserSuccessfully() {
        // Arrange
        SysUserAddBO addBO = new SysUserAddBO();
        addBO.setNickName("新用户");
        addBO.setUserName("newuser");
        addBO.setUserPassword("123456");

        when(userMapper.insert(any(SysUser.class)))
            .thenReturn(1);

        // Act
        boolean result = userService.insertByBo(addBO);

        // Assert
        assertThat(result).isTrue();

        // Verify
        verify(userMapper, times(1)).insert(any(SysUser.class));
    }
}
```

### 场景 2：测试异常情况

```java
@Nested
@DisplayName("删除用户")
class DeleteUserTests {

    @Test
    @DisplayName("当用户不存在时应该抛出异常")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        Long userId = 999L;
        when(userMapper.selectById(userId))
            .thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUser(userId))
            .isInstanceOf(ServiceException.class)
            .hasMessage("用户不存在");
    }
}
```

### 场景 3：测试边界条件

```java
@Nested
@DisplayName("验证用户名")
class ValidateUserNameTests {

    @Test
    @DisplayName("当用户名为空时应该返回 false")
    void shouldReturnFalseWhenUserNameIsEmpty() {
        // Arrange
        String userName = "";

        // Act
        boolean result = userService.validateUserName(userName);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("当用户名长度超过限制时应该返回 false")
    void shouldReturnFalseWhenUserNameTooLong() {
        // Arrange
        String userName = "a".repeat(100);

        // Act
        boolean result = userService.validateUserName(userName);

        // Assert
        assertThat(result).isFalse();
    }
}
```

### 场景 4：参数化测试

```java
@Nested
@DisplayName("参数化测试")
class ParameterizedTests {

    @ParameterizedTest
    @ValueSource(strings = {"user1", "user2", "user3"})
    @DisplayName("应该验证多个用户名")
    void shouldValidateMultipleUserNames(String userName) {
        // Act
        boolean result = userService.validateUserName(userName);

        // Assert
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
        "1,10,11",
        "2,20,22",
        "-1,5,4"
    })
    @DisplayName("应该正确计算年龄")
    void shouldCalculateAge(int year1, int year2, int expectedResult) {
        // Act
        int result = year1 + year2;

        // Assert
        assertThat(result).isEqualTo(expectedResult);
    }
}
```

## 使用 Mock

### Mock 基础

```java
@Mock
private SysUserMapper userMapper;

@InjectMocks
private SysUserServiceImpl userService;

@Test
void testWithMock() {
    // 设置 Mock 的返回值
    when(userMapper.selectById(1L))
        .thenReturn(new SysUser());

    // 当调用被测试代码时，会使用 Mock 的返回值
    SysUser user = userService.selectUserById(1L);

    // 验证 Mock 被正确调用
    verify(userMapper).selectById(1L);
}
```

### 常用的 Mock 配置

```java
// 返回指定值
when(userMapper.selectById(1L))
    .thenReturn(new SysUser());

// 返回多个值（多次调用）
when(userMapper.selectById(1L))
    .thenReturn(new SysUser())
    .thenReturn(null);

// 抛出异常
when(userMapper.insert(any()))
    .thenThrow(new RuntimeException("插入失败"));

// 使用 ArgumentMatcher 进行灵活匹配
when(userMapper.selectByUsername(argThat(s -> s.startsWith("admin"))))
    .thenReturn(new SysUser());

// 使用 ArgumentCaptor 捕获参数
ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
verify(userMapper).insert(captor.capture());
SysUser capturedUser = captor.getValue();
```

### 验证 Mock 调用

```java
// 验证被调用
verify(userMapper).selectById(1L);

// 验证调用次数
verify(userMapper, times(1)).selectById(1L);
verify(userMapper, times(2)).selectById(any());

// 验证从未调用
verify(userMapper, never()).deleteById(1L);

// 验证调用顺序
InOrder inOrder = inOrder(userMapper, roleMapper);
inOrder.verify(userMapper).selectById(1L);
inOrder.verify(roleMapper).selectByUserId(1L);

// 验证调用之后没有其他调用
verify(userMapper).selectById(1L);
verifyNoMoreInteractions(userMapper);
```

## 断言

### 基本断言

```java
// 相等性检查
assertThat(actual).isEqualTo(expected);
assertThat(actual).isNotEqualTo(expected);

// Null 检查
assertThat(actual).isNull();
assertThat(actual).isNotNull();

// 布尔值检查
assertThat(value).isTrue();
assertThat(value).isFalse();

// 大小比较
assertThat(value).isGreaterThan(0);
assertThat(value).isLessThan(100);
assertThat(value).isBetween(0, 100);

// 集合检查
assertThat(list).isEmpty();
assertThat(list).isNotEmpty();
assertThat(list).hasSize(3);
assertThat(list).contains("item1", "item2");
assertThat(list).doesNotContain("item3");
```

### 异常断言

```java
// 验证异常被抛出
assertThatThrownBy(() -> userService.deleteUser(999L))
    .isInstanceOf(ServiceException.class)
    .hasMessage("用户不存在");

// 验证异常及其原因
assertThatThrownBy(() -> userService.deleteUser(999L))
    .isInstanceOf(ServiceException.class)
    .hasCause(new SQLException());

// 验证没有异常
assertThatCode(() -> userService.deleteUser(1L))
    .doesNotThrowAnyException();
```

### 链式断言

```java
// 使用链式调用进行多个检查
assertThat(user)
    .isNotNull()
    .extracting(SysUser::getUserId, SysUser::getNickName)
    .containsExactly(1L, "管理员");

// 对集合进行链式断言
assertThat(users)
    .hasSize(3)
    .extracting(SysUser::getUserName)
    .contains("admin", "user1", "user2");
```

## 测试数据

### 使用 TestDataFactory

项目提供了 `TestDataFactory` 来快速创建测试数据：

```java
@Test
void shouldProcessUser() {
    // 使用工厂方法创建用户
    SysUser user = TestDataFactory.createSysUser()
        .withUserName("testuser")
        .withNickName("测试用户")
        .build();

    // 使用创建的数据
    userService.processUser(user);
}
```

### 手动创建测试数据

```java
@Test
void shouldSaveUser() {
    // Arrange
    SysUser user = new SysUser();
    user.setUserId(1L);
    user.setUserName("admin");
    user.setNickName("管理员");
    user.setUserEmail("admin@example.com");
    user.setUserPhone("13800138000");
    user.setUserPassword("123456");
    user.setUserType("0");
    user.setUserStatus("0");
    user.setDelFlag("0");

    // Act & Assert
    assertThat(user).isNotNull();
}
```

## 嵌套测试

使用 `@Nested` 组织相关的测试：

```java
@DisplayName("系统用户服务")
class SysUserServiceImplTest extends BaseUnitTest {

    @InjectMocks
    private SysUserServiceImpl userService;

    @Mock
    private SysUserMapper userMapper;

    @Nested
    @DisplayName("查询用户")
    class QueryUserTests {

        @Test
        @DisplayName("应该根据 ID 获取用户")
        void shouldGetUserById() { }

        @Test
        @DisplayName("应该根据用户名获取用户")
        void shouldGetUserByName() { }
    }

    @Nested
    @DisplayName("创建用户")
    class CreateUserTests {

        @Test
        @DisplayName("应该创建新用户")
        void shouldCreateUser() { }

        @Test
        @DisplayName("应该验证用户名唯一性")
        void shouldValidateUserNameUniqueness() { }
    }

    @Nested
    @DisplayName("删除用户")
    class DeleteUserTests {

        @Test
        @DisplayName("应该删除现存用户")
        void shouldDeleteExistingUser() { }
    }
}
```

## 最佳实践

### 1. 一个测试一个断言（或相关的断言）

```java
// 好的做法
@Test
void shouldReturnUserWhenUserExists() {
    assertThat(user).isNotNull();
}

@Test
void shouldReturnNullWhenUserNotExists() {
    assertThat(user).isNull();
}

// 避免在一个测试中测试多个完全不相关的内容
@Test
void testEverything() {  // 不好！
    // 测试用户查询
    // 测试角色删除
    // 测试权限验证
}
```

### 2. 测试应该是独立的

```java
@Test
void test1() {
    userService.createUser(user1);
}

@Test
void test2() {
    // 不能依赖 test1 的副作用！
    // 应该在 setUp() 中创建所需数据
}
```

### 3. 使用描述性的名称

```java
// 好的命名
void shouldThrowExceptionWhenPasswordIsTooShort() { }

// 避免这样的命名
void test() { }
void testPassword() { }
void test_1() { }
```

### 4. 遵循 AAA 模式

```java
@Test
void shouldCalculateDiscount() {
    // Arrange - 准备数据
    Order order = new Order();
    order.addItem(new Item("Apple", 10, 5));

    // Act - 执行操作
    int discount = order.calculateDiscount();

    // Assert - 验证结果
    assertThat(discount).isEqualTo(5);
}
```

### 5. 避免 Test Fixtures

```java
// 避免在测试类级别初始化重量级对象
private static Database db = new Database();  // 不好

// 使用 @BeforeEach 为每个测试创建独立的环境
@BeforeEach
void setUp() {
    db = new InMemoryDatabase();
}
```

## 调试失败的测试

### 获取详细的失败信息

```java
// 使用 as() 添加失败消息
assertThat(actualValue)
    .as("用户 ID 应该等于预期值")
    .isEqualTo(expectedValue);

// 检查实际值与期望值
SoftAssertions.assertSoftly(soft -> {
    soft.assertThat(user.getId()).isEqualTo(1L);
    soft.assertThat(user.getName()).isEqualTo("admin");
    soft.assertThat(user.getEmail()).isEqualTo("admin@example.com");
});
```

### 在 IDE 中调试

1. 在断言处设置断点
2. 右键点击测试方法 → "Debug 'testMethodName'"
3. 查看变量值和执行流程

### 查看完整堆栈跟踪

在 Gradle 中：

```bash
./gradlew test --info -x ignoreFailures
```

## 完整示例

### 复杂的 Service 测试

```java
@DisplayName("系统用户服务综合测试")
class SysUserServiceImplComplexTest extends BaseUnitTest {

    @InjectMocks
    private SysUserServiceImpl userService;

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SysRoleMapper roleMapper;

    @Mock
    private SysUserRoleMapper userRoleMapper;

    @Nested
    @DisplayName("用户权限管理")
    class UserRoleManagementTests {

        @Test
        @DisplayName("应该为用户分配多个角色")
        void shouldAssignMultipleRolesToUser() {
            // Arrange
            Long userId = 1L;
            List<Long> roleIds = Arrays.asList(1L, 2L, 3L);
            SysUser user = new SysUser();
            user.setUserId(userId);

            when(userMapper.selectById(userId))
                .thenReturn(user);
            when(roleMapper.selectBatchIds(roleIds))
                .thenReturn(createRoles(roleIds));
            when(userRoleMapper.insert(any()))
                .thenReturn(1);

            // Act
            boolean result = userService.assignRolesToUser(userId, roleIds);

            // Assert
            assertThat(result).isTrue();

            // Verify
            verify(userRoleMapper, times(3)).insert(any());
        }

        private List<SysRole> createRoles(List<Long> roleIds) {
            return roleIds.stream()
                .map(id -> {
                    SysRole role = new SysRole();
                    role.setRoleId(id);
                    return role;
                })
                .collect(Collectors.toList());
        }
    }
}
```

## 常见问题

**Q: 如何处理静态方法的 Mock？**
A: 使用 `MockedStatic` 或考虑重构为实例方法。

**Q: 如何测试异步方法？**
A: 使用 `awaitility` 库或 `@EnableAsync` + `@Test` 的超时机制。

**Q: 如何测试私有方法？**
A: 通常应该通过公有方法间接测试。如确实需要，可使用反射。

## 参考资源

- [JUnit 5 官方文档](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito 官方文档](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ 官方文档](https://assertj.github.io/assertj-core-features-highlight)

---

**下一步**: 学习 [集成测试完整指南](/docs/testing/integration-test-guide.md) 进行更复杂的测试。

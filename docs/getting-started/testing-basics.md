# 测试基础

本指南将教会您如何在 RuoYi-Cloud-Plus 中运行和编写测试。项目包含 3,271+ 个测试，通过率达 99.05%。

## 为什么需要测试？

测试能够：

- 确保代码功能正确
- 防止回归（修改代码时发现问题）
- 提高代码质量和可维护性
- 快速定位 Bug
- 为重构提供安全网

## 测试类型

| 类型        | 说明        | 示例                              | 运行速度 |
|-----------|-----------|---------------------------------|------|
| **单元测试**  | 测试单个方法或类  | Service 方法逻辑                    | 快速   |
| **集成测试**  | 测试多个组件的协作 | Service + Repository + Database | 中等   |
| **端到端测试** | 测试完整的请求流程 | API 端点的完整流程                     | 较慢   |

## 运行测试

### 运行所有测试

```bash
# 使用 Gradle
./gradlew test

# 使用 Maven
mvn test

# 查看详细输出
./gradlew test --info
```

### 运行特定模块的测试

```bash
# 运行 ruoyi-common-core 模块的测试
./gradlew :ruoyi-common-core:test

# 运行 ruoyi-system 模块的测试
./gradlew :ruoyi-system:test
```

### 运行特定测试类

```bash
# 运行单个测试类
./gradlew test --tests SysUserServiceImplTest

# 运行特定包下的所有测试
./gradlew test --tests "cn.lion.system.service.*"

# 运行特定测试方法
./gradlew test --tests SysUserServiceImplTest.shouldGetUserByIdWhenUserExists
```

### 运行测试并生成报告

```bash
# 运行测试并生成 HTML 报告
./gradlew test

# 查看测试报告（报告位置）
# build/reports/tests/test/index.html

# 使用浏览器打开
open build/reports/tests/test/index.html
```

## 查看测试结果

### 命令行输出

```bash
# 运行测试后，会看到类似输出
> Task :ruoyi-system:test

Test suite 'Gradle Test Executor' started
SysUserServiceImplTest > should...Get...WhenUserExists PASSED
SysUserServiceImplTest > should...Delete...WhenUserNotExists FAILED

16 tests completed, 15 passed, 1 failed
```

### 测试报告

生成的 HTML 报告包含：

- 测试总数和通过率
- 按类分组的测试结果
- 失败测试的堆栈跟踪
- 测试执行时间

**打开报告**:

```bash
# macOS / Linux
open build/reports/tests/test/index.html

# Windows
start build/reports/tests/test/index.html
```

### IDE 内查看

在 IntelliJ IDEA 中：

1. 右键点击测试类或方法
2. 选择 "Run" 或 "Debug"
3. 在下方的 "Run" 窗口中查看结果
4. 绿色✓ 表示通过，红色✗ 表示失败

## 编写第一个测试

### 测试示例：Service 单元测试

```java
@DisplayName("系统用户服务测试")
class SysUserServiceImplTest extends BaseUnitTest {

    @InjectMocks
    private SysUserServiceImpl userService;

    @Mock
    private SysUserMapper userMapper;

    @Nested
    @DisplayName("查询用户")
    class QueryUserTests {

        @Test
        @DisplayName("应该根据 ID 获取用户信息")
        void shouldGetUserByIdWhenUserExists() {
            // Arrange（准备）
            Long userId = 1L;
            SysUser expectedUser = new SysUser();
            expectedUser.setUserId(userId);
            expectedUser.setNickName("管理员");

            when(userMapper.selectById(userId)).thenReturn(expectedUser);

            // Act（执行）
            SysUser actualUser = userService.selectUserById(userId);

            // Assert（断言）
            assertThat(actualUser)
                .isNotNull()
                .extracting(SysUser::getUserId, SysUser::getNickName)
                .containsExactly(userId, "管理员");

            // Verify（验证 Mock 被正确调用）
            verify(userMapper).selectById(userId);
        }

        @Test
        @DisplayName("当用户不存在时应该返回 null")
        void shouldReturnNullWhenUserNotExists() {
            // Arrange
            Long userId = 999L;
            when(userMapper.selectById(userId)).thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> userService.selectUserById(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("用户不存在");
        }
    }
}
```

### 使用 BaseUnitTest 基类

所有单元测试都应该继承 `BaseUnitTest`：

```java
public abstract class BaseUnitTest {
    // 提供常用的测试工具和配置
}
```

**优点**：

- 统一的 Mock 配置
- 内置常用的测试工具
- 标准化的测试结构

## 最佳实践

### 1. 使用 AAA 模式

所有测试都应该遵循 **A-A-A 模式**（Arrange-Act-Assert）:

```java
@Test
void shouldCalculateTotalPrice() {
    // Arrange（准备测试数据）
    Order order = new Order();
    order.addItem(new Item("苹果", 10, 5));  // 5 个苹果，单价 10

    // Act（执行要测试的操作）
    int total = order.calculateTotal();

    // Assert（验证结果）
    assertThat(total).isEqualTo(50);
}
```

### 2. 描述性测试名称

使用 `should...When...` 命名规范：

```java
// 好的命名
@DisplayName("当用户存在时应该返回用户信息")
void shouldReturnUserWhenUserExists() { }

@DisplayName("当密码错误时应该抛出异常")
void shouldThrowExceptionWhenPasswordIsWrong() { }

// 避免这样的命名
void test1() { }
void testUser() { }
```

### 3. 每个测试只测试一个场景

```java
// 好的做法：一个方法只测试一个场景
@Test
void shouldReturnUserByIdWhenUserExists() { ... }

@Test
void shouldReturnNullWhenUserNotExists() { ... }

// 避免：一个方法测试多个场景
@Test
void shouldHandleAllUserQueries() {
    // 测试多个场景
}
```

### 4. 使用 Mock 隔离依赖

```java
@Mock
private SysUserMapper userMapper;

// Mock 不依赖真实数据库，测试更快
when(userMapper.selectById(1L))
    .thenReturn(new SysUser());
```

### 5. 验证方法调用

```java
// 验证 Mock 方法是否被正确调用
verify(userMapper).selectById(userId);

// 验证调用次数
verify(userMapper, times(1)).selectById(userId);

// 验证从未被调用
verify(userMapper, never()).delete(userId);
```

## 常见测试问题

### 问题 1：测试失败，不知道原因

**解决方案**：

```java
// 使用 assertThat().as() 添加失败消息
assertThat(actualUser.getUserId())
    .as("用户 ID 应该是 %d", expectedId)
    .isEqualTo(expectedId);
```

### 问题 2：测试之间有依赖（顺序相关）

**解决方案**：

```java
// 每个测试都应该独立，不依赖其他测试
// 使用 @BeforeEach 为每个测试准备独立的数据
@BeforeEach
void setUp() {
    // 为每个测试准备独立的测试数据
}
```

### 问题 3：测试覆盖率不足

**查看覆盖率**：

```bash
# 生成覆盖率报告
./gradlew test jacoco

# 查看报告
open build/reports/jacoco/test/html/index.html
```

## 测试命令参考

| 命令                                   | 说明      |
|--------------------------------------|---------|
| `./gradlew test`                     | 运行所有测试  |
| `./gradlew test --info`              | 显示详细日志  |
| `./gradlew test --tests ClassName`   | 运行特定测试类 |
| `./gradlew test --tests "*.method*"` | 运行特定方法  |
| `./gradlew test jacoco`              | 生成覆盖率报告 |
| `./gradlew test -x test`             | 跳过测试    |

## 进阶学习

- **单元测试**: [单元测试完整指南](/docs/testing/unit-test-guide.md)
- **集成测试**: [集成测试完整指南](/docs/testing/integration-test-guide.md)
- **当前测试状态**: [项目测试现状](/docs/testing/current-status.md)
- **更多代码示例**: 查看各模块的 `*Test.java` 文件

## 获取帮助

- 查看测试文档: `/docs/testing/`
- 搜索类似的测试代码
- 查看测试报告中的失败详情
- 提交 Issue: https://gitee.com/dromara/RuoYi-Cloud-Plus/issues

---

**下一步**: 深入学习 [单元测试完整指南](/docs/testing/unit-test-guide.md)
或 [集成测试完整指南](/docs/testing/integration-test-guide.md)。

# Vavr 函数式编程迁移指南

## 目录

- [简介](#简介)
- [核心原则](#核心原则)
- [Vavr 核心特性](#vavr-核心特性)
- [实用准则](#实用准则)
- [Spring Boot 集成](#spring-boot-集成)
- [Dubbo RPC 集成](#dubbo-rpc-集成)
- [MyBatis Plus 集成](#mybatis-plus-集成)
- [实际应用场景](#实际应用场景)
- [迁移策略](#迁移策略)
- [常见问题](#常见问题)

---

## 简介

Vavr (原 Javaslang) 是一个函数式编程库，为 Java 提供了不可变数据结构和函数式控制结构。

**版本**: 0.10.4

**核心优势**:

- 类型安全的错误处理 (Try, Either)
- 空值安全 (Option)
- 不可变集合
- 函数式控制流
- Pattern Matching

---

## 核心原则

### 1. 仅应用于新代码

**重要**: 所有 FP 实践仅应用于新编写的代码，**不重构现有代码**。

### 2. 渐进式采用

不要一次性全面改造，而是在以下场景优先使用:

- 新增功能模块
- 新增 Service 方法
- 新增工具类

### 3. 团队共识

确保团队成员理解 FP 概念后再广泛使用。

---

## Vavr 核心特性

### 1. Option - 空值安全

**替代**: `Optional<T>`, `null` 检查

**使用场景**: 方法可能返回空值时

```java
// ❌ 传统方式
public User findUserById(Long id) {
    User user = userMapper.selectById(id);
    return user; // 可能返回 null
}

// ✅ Vavr 方式
public Option<User> findUserById(Long id) {
    return Option.of(userMapper.selectById(id));
}

// 使用示例
Option<User> userOpt = userService.findUserById(1L);
String userName = userOpt
    .map(User::getUserName)
    .getOrElse("Unknown");
```

**准则**:

- 新方法优先返回 `Option<T>` 而非 `T` (可能为 null)
- 与现有代码交互时使用 `Option.of()` 包装
- 避免使用 `Option.get()`，优先使用 `getOrElse()`、`map()`、`flatMap()`

---

### 2. Try - 异常处理

**替代**: try-catch 块

**使用场景**: 方法可能抛出异常时

```java
// ❌ 传统方式
public String readFile(String path) {
    try {
        return Files.readString(Paths.get(path));
    } catch (IOException e) {
        log.error("文件读取失败", e);
        return null; // 或抛出运行时异常
    }
}

// ✅ Vavr 方式
public Try<String> readFile(String path) {
    return Try.of(() -> Files.readString(Paths.get(path)))
        .onFailure(e -> log.error("文件读取失败", e));
}

// 使用示例
String content = readFile("/path/to/file")
    .map(String::trim)
    .getOrElse("Default content");
```

**准则**:

- I/O 操作返回 `Try<T>`
- 外部 API 调用返回 `Try<T>`
- 避免在 `Try` 中捕获业务异常（使用 Either）

---

### 3. Either - 业务错误处理

**替代**: 异常抛出、错误码

**使用场景**: 业务逻辑中的成功/失败分支

```java
// ❌ 传统方式
public R<Void> updatePassword(Long userId, String oldPwd, String newPwd) {
    User user = userMapper.selectById(userId);
    if (user == null) {
        return R.fail("用户不存在");
    }
    if (!passwordMatches(oldPwd, user.getPassword())) {
        return R.fail("原密码错误");
    }
    // 更新密码...
    return R.ok();
}

// ✅ Vavr 方式
public Either<String, Void> updatePassword(Long userId, String oldPwd, String newPwd) {
    return findUserById(userId)
        .toEither("用户不存在")
        .flatMap(user -> validatePassword(oldPwd, user.getPassword()))
        .map(user -> {
            // 更新密码...
            return null;
        });
}

private Either<String, User> validatePassword(String input, String stored) {
    return passwordMatches(input, stored)
        ? Either.right(user)
        : Either.left("原密码错误");
}
```

**准则**:

- 左侧 (Left) 表示错误，右侧 (Right) 表示成功
- 错误类型可以是 `String`、自定义错误类、枚举
- 使用 `flatMap` 链式组合多个可能失败的操作

---

### 4. 不可变集合

**替代**: Java Collections

**使用场景**: 需要保证数据不可变时

```java
// ❌ 传统方式

List<User>users=userMapper.selectList(null);
    users.add(newUser); // 意外修改

// ✅ Vavr 方式
import io.vavr.collection.List;

List<User> users = List.ofAll(userMapper.selectList(null));
List<User> updatedUsers = users.append(newUser); // 返回新集合，原集合不变
```

**准则**:

- DTO/VO 中使用 Vavr 集合类型
- 内部计算逻辑使用 Vavr 集合
- 与框架交互时转换为 Java 集合: `toJavaList()`

---

### 5. Pattern Matching

**替代**: switch-case, if-else 链

**使用场景**: 多分支条件判断

```java
import static io.vavr.API.*;
import static io.vavr.Predicates.*;

// ✅ Vavr 方式
public String getUserStatus(User user) {
    return Match(user.getStatus()).of(
        Case($(0), "待激活"),
        Case($(1), "正常"),
        Case($(2), "已禁用"),
        Case($(), status -> "未知状态: " + status)
    );
}

// 类型匹配示例
public String processResult(Either<String, User> result) {
    return Match(result).of(
        Case($Left($()), error -> "错误: " + error),
        Case($Right($()), user -> "成功: " + user.getUserName())
    );
}
```

**准则**:

- 状态机逻辑使用 Pattern Matching
- 类型分发使用 Pattern Matching
- 避免过度复杂的 Match 表达式

---

## 实用准则

### ✅ 优先使用的场景

| 场景       | 使用               | 示例                                            |
|----------|------------------|-----------------------------------------------|
| 可能为空的返回值 | `Option<T>`      | `Option<User> findUserByEmail(String email)`  |
| 可能抛异常的操作 | `Try<T>`         | `Try<String> readConfig(String path)`         |
| 业务成功/失败  | `Either<L, R>`   | `Either<ErrorCode, Order> createOrder(...)`   |
| 不可变数据    | Vavr Collections | `List<String> tags = List.of("tag1", "tag2")` |
| 复杂条件分支   | Pattern Matching | 状态机、类型分发                                      |

### ❌ 避免使用的场景

| 场景                 | 原因              | 替代方案           |
|--------------------|-----------------|----------------|
| Controller 层直接返回   | 框架不支持序列化        | 转换为 R<T> 或 DTO |
| Dubbo 接口参数/返回值     | 序列化问题           | 仅在实现内部使用       |
| MyBatis Mapper 返回值 | 框架不识别           | Service 层包装    |
| Entity 字段类型        | JPA/MyBatis 不支持 | 仅在 DTO/VO 中使用  |

---

## Spring Boot 集成

### 1. Jackson 序列化配置

Vavr 提供了 Jackson 模块用于序列化 Vavr 类型。

```java
@Configuration
public class JacksonConfig {

    @Bean
    public Module vavrModule() {
        return new VavrModule();
    }
}
```

**配置说明**:

- 支持 `Option` 序列化为 `null` 或值
- 支持 `Either` 序列化为 JSON
- 支持 Vavr 集合序列化

### 2. 与现有 R<T> 响应体集成

```java
// Service 层使用 Either
public Either<String, UserDTO> createUser(UserDTO dto) {
    return validateUser(dto)
        .flatMap(this::checkDuplicate)
        .map(this::saveUser);
}

// Controller 层转换为 R<T>
@PostMapping("/users")
public R<UserDTO> createUser(@RequestBody UserDTO dto) {
    return userService.createUser(dto)
        .fold(
            error -> R.fail(error),
            user -> R.ok(user)
        );
}
```

### 3. 异常处理集成

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        // 可以使用 Try 包装异常处理逻辑
        return Try.of(() -> processException(e))
            .map(R::fail)
            .getOrElse(R.fail("系统错误"));
    }
}
```

---

## Dubbo RPC 集成

### 问题: Vavr 类型序列化

Dubbo 默认使用 Hessian2 序列化，**不支持** Vavr 类型。

### 解决方案

#### 方案 1: 仅在实现内部使用 (推荐)

```java
// Dubbo 接口定义（使用标准 Java 类型）
public interface UserService {
    UserDTO getUserById(Long id); // 可能返回 null
    boolean updateUser(UserDTO dto);
}

// Dubbo 实现（内部使用 Vavr）
@DubboService
public class UserServiceImpl implements UserService {

    @Override
    public UserDTO getUserById(Long id) {
        return findUserById(id) // 返回 Option<UserDTO>
            .getOrNull(); // 转换为可能为 null 的值
    }

    @Override
    public boolean updateUser(UserDTO dto) {
        return updateUserInternal(dto) // 返回 Either<String, UserDTO>
            .isRight(); // 转换为布尔值
    }

    // 内部方法使用 Vavr
    private Option<UserDTO> findUserById(Long id) {
        return Option.of(userMapper.selectById(id));
    }

    private Either<String, UserDTO> updateUserInternal(UserDTO dto) {
        // 业务逻辑...
    }
}
```

#### 方案 2: 自定义序列化 (高级)

如果需要在 RPC 接口中使用 Vavr 类型，可配置 JSON 序列化:

```yaml
dubbo:
  protocol:
    serialization: fastjson2 # 或 gson, jackson
```

```java
// 需要额外配置 JSON 序列化器支持 Vavr
```

**不推荐**: 增加复杂度，影响性能。

---

## MyBatis Plus 集成

### 问题: Mapper 返回值

MyBatis Plus 不识别 Vavr 类型，Mapper 只能返回标准 Java 类型。

### 解决方案: Service 层包装

```java
// Mapper 层（标准 Java）
@Mapper
public interface UserMapper extends BaseMapper<User> {
    User selectById(Long id); // 可能返回 null
    List<User> selectByStatus(Integer status);
}

// Service 层（Vavr 包装）
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public Option<User> findById(Long id) {
        return Option.of(userMapper.selectById(id));
    }

    public io.vavr.collection.List<User> findByStatus(Integer status) {
        return io.vavr.collection.List.ofAll(userMapper.selectByStatus(status));
    }

    public Either<String, User> saveUser(User user) {
        return Try.of(() -> {
            userMapper.insert(user);
            return user;
        })
        .toEither()
        .mapLeft(Throwable::getMessage);
    }
}
```

---

## 实际应用场景

### 场景 1: 用户注册流程

```java
@Service
public class UserRegistrationService {

    public Either<String, UserDTO> register(RegisterRequest request) {
        return validateRequest(request)
            .flatMap(this::checkEmailDuplicate)
            .flatMap(this::checkUsernameDuplicate)
            .map(this::hashPassword)
            .flatMap(this::saveUser)
            .flatMap(this::sendWelcomeEmail);
    }

    private Either<String, RegisterRequest> validateRequest(RegisterRequest req) {
        return req.getEmail() != null && req.getPassword() != null
            ? Either.right(req)
            : Either.left("邮箱和密码不能为空");
    }

    private Either<String, RegisterRequest> checkEmailDuplicate(RegisterRequest req) {
        return Option.of(userMapper.selectByEmail(req.getEmail()))
            .map(u -> Either.<String, RegisterRequest>left("邮箱已被注册"))
            .getOrElse(Either.right(req));
    }

    private Either<String, RegisterRequest> checkUsernameDuplicate(RegisterRequest req) {
        return Option.of(userMapper.selectByUsername(req.getUsername()))
            .map(u -> Either.<String, RegisterRequest>left("用户名已被占用"))
            .getOrElse(Either.right(req));
    }

    private RegisterRequest hashPassword(RegisterRequest req) {
        req.setPassword(BCrypt.hashpw(req.getPassword()));
        return req;
    }

    private Either<String, UserDTO> saveUser(RegisterRequest req) {
        return Try.of(() -> {
            User user = convertToEntity(req);
            userMapper.insert(user);
            return convertToDTO(user);
        })
        .toEither()
        .mapLeft(e -> "保存失败: " + e.getMessage());
    }

    private Either<String, UserDTO> sendWelcomeEmail(UserDTO user) {
        return emailService.sendWelcomeEmail(user.getEmail())
            .map(success -> user)
            .toEither("邮件发送失败");
    }
}
```

**优势**:

- 清晰的业务流程
- 每个步骤可能失败
- 自动短路（遇到错误立即返回）
- 无需手动 try-catch

---

### 场景 2: 配置加载与验证

```java
@Component
public class ConfigLoader {

    public Try<AppConfig> loadConfig(String configPath) {
        return readConfigFile(configPath)
            .flatMap(this::parseYaml)
            .flatMap(this::validateConfig)
            .onSuccess(config -> log.info("配置加载成功: {}", config))
            .onFailure(e -> log.error("配置加载失败", e));
    }

    private Try<String> readConfigFile(String path) {
        return Try.of(() -> Files.readString(Paths.get(path)));
    }

    private Try<AppConfig> parseYaml(String content) {
        return Try.of(() -> yamlMapper.readValue(content, AppConfig.class));
    }

    private Try<AppConfig> validateConfig(AppConfig config) {
        return config.isValid()
            ? Try.success(config)
            : Try.failure(new IllegalArgumentException("配置验证失败"));
    }
}
```

---

### 场景 3: 批量操作与错误收集

```java
@Service
public class BulkUserService {

    public Tuple2<io.vavr.collection.List<UserDTO>, io.vavr.collection.List<String>>
            importUsers(java.util.List<UserDTO> users) {

        io.vavr.collection.List<Either<String, UserDTO>> results =
            io.vavr.collection.List.ofAll(users)
                .map(this::validateAndSave);

        io.vavr.collection.List<UserDTO> successes = results
            .filter(Either::isRight)
            .map(Either::get);

        io.vavr.collection.List<String> errors = results
            .filter(Either::isLeft)
            .map(Either::getLeft);

        return Tuple.of(successes, errors);
    }

    private Either<String, UserDTO> validateAndSave(UserDTO user) {
        return validateUser(user)
            .flatMap(this::checkDuplicate)
            .flatMap(this::saveUser);
    }
}
```

---

### 场景 4: 缓存 + 数据库查询

```java
@Service
public class ProductService {

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private ProductMapper productMapper;

    public Option<Product> getProduct(Long id) {
        return getCachedProduct(id)
            .orElse(() -> getProductFromDB(id)
                .peek(product -> cacheProduct(id, product)));
    }

    private Option<Product> getCachedProduct(Long id) {
        return Option.of(redisson.<Product>getBucket("product:" + id).get());
    }

    private Option<Product> getProductFromDB(Long id) {
        return Option.of(productMapper.selectById(id));
    }

    private void cacheProduct(Long id, Product product) {
        redisson.<Product>getBucket("product:" + id).set(product, 1, TimeUnit.HOURS);
    }
}
```

---

## 迁移策略

### 阶段 1: 基础设施 (已完成)

- ✅ 添加 Vavr 依赖
- ✅ 配置 Jackson 序列化支持

### 阶段 2: 工具类和通用服务 (优先)

- 文件处理工具
- 配置加载器
- HTTP 客户端封装
- 缓存工具类

### 阶段 3: 新功能模块

- 新增 Service 方法使用 `Option`、`Either`、`Try`
- 新增工具方法使用 Vavr Collections

### 阶段 4: 团队培训

- 内部技术分享
- Code Review 中强化 FP 实践
- 编写最佳实践案例库

### 阶段 5: 代码规范

- 更新编码规范文档
- Checkstyle/SpotBugs 规则调整（如需要）

---

## 常见问题

### Q1: Vavr Option 与 Java Optional 如何选择？

**建议**: 新代码统一使用 `io.vavr.control.Option`

**原因**:

- Vavr Option 功能更强大（`flatMap`、`peek`、`filter` 等）
- 与 Vavr 其他类型配合更好
- 可以与集合无缝集成

**转换**:

```java
// Vavr -> Java
Option<String> vavrOpt = Option.of("value");
Optional<String> javaOpt = vavrOpt.toJavaOptional();

// Java -> Vavr
Optional<String> javaOpt = Optional.of("value");
Option<String> vavrOpt = Option.ofOptional(javaOpt);
```

---

### Q2: Controller 层如何返回 Vavr 类型？

**不推荐直接返回**，应在 Controller 层转换为标准响应。

```java
// ❌ 不推荐
@GetMapping("/users/{id}")
public Option<UserDTO> getUser(@PathVariable Long id) {
    return userService.findById(id);
}

// ✅ 推荐
@GetMapping("/users/{id}")
public R<UserDTO> getUser(@PathVariable Long id) {
    return userService.findById(id)
        .map(R::ok)
        .getOrElse(R.fail("用户不存在"));
}
```

---

### Q3: Vavr 集合与 Java 集合性能对比？

**性能**:

- Vavr 集合基于持久化数据结构（Persistent Data Structures）
- 读操作性能接近 Java 集合
- 写操作（add、remove）创建新集合，略慢于可变集合
- 适合读多写少场景

**建议**:

- 业务逻辑层使用 Vavr 集合（保证不可变）
- 性能敏感计算使用 Java 集合 + 防御性复制

---

### Q4: Try 与 Either 如何选择？

| 类型             | 使用场景           | 错误类型        |
|----------------|----------------|-------------|
| `Try<T>`       | 可能抛出**异常**的操作  | `Throwable` |
| `Either<L, R>` | 可能有**业务错误**的操作 | 自定义错误类型     |

**示例**:

```java
// Try: I/O、网络、解析等可能抛异常
Try<String> fileContent = Try.of(() -> Files.readString(path));

// Either: 业务验证、规则检查
Either<String, User> validation = validateAge(user);
```

---

### Q5: 如何与 Lombok 配合使用？

完全兼容，可以一起使用：

```java
@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private Option<String> email; // Vavr Option
    private io.vavr.collection.List<String> roles; // Vavr List
}
```

**注意**: 确保 Jackson 配置了 Vavr 模块用于序列化。

---

### Q6: 如何处理嵌套的 Option/Either？

使用 `flatMap` 展平嵌套结构：

```java
// ❌ 嵌套 Option<Option<User>>
Option<Option<User>> nested = Option.of(getUserId())
        .map(id -> findUserById(id)); // findUserById 返回 Option<User>

// ✅ 使用 flatMap 展平为 Option<User>
Option<User> flat = Option.of(getUserId())
    .flatMap(id -> findUserById(id));
```

---

### Q7: 如何调试 Vavr 链式调用？

使用 `peek` 方法打印中间值：

```java
Option<User> user = findUserById(id)
    .peek(u -> log.debug("找到用户: {}", u))
    .filter(u -> u.getStatus() == 1)
    .peek(u -> log.debug("用户状态正常"))
    .map(this::convertToDTO)
    .peek(dto -> log.debug("转换完成: {}", dto));
```

---

## 总结

### 关键要点

1. **仅用于新代码** - 不重构现有代码
2. **Service 层优先** - Controller、Mapper 层需转换
3. **类型优先级**: `Option` > `Try` > `Either` > Collections
4. **与框架集成** - 配置 Jackson、注意 Dubbo 序列化

### 下一步行动

1. ✅ 添加 Vavr 依赖（已完成）
2. 配置 Jackson Vavr 模块
3. 编写第一个使用 Vavr 的工具类
4. 在新功能中实践 FP 模式
5. 团队内部分享经验

### 参考资源

- [Vavr 官方文档](https://docs.vavr.io/)
- [Vavr User Guide](https://www.vavr.io/vavr-docs/)
- [Vavr GitHub](https://github.com/vavr-io/vavr)

---

**文档版本**: 1.0.0
**最后更新**: 2025-11-29
**维护者**: RuoYi-Cloud-Plus Team

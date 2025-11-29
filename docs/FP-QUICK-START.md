# Vavr 快速入门示例

本文档提供 Vavr 在 RuoYi-Cloud-Plus 项目中的快速使用示例。

## 目录

- [环境准备](#环境准备)
- [Option 示例](#option-示例)
- [Try 示例](#try-示例)
- [Either 示例](#either-示例)
- [实际业务场景](#实际业务场景)

---

## 环境准备

Vavr 已经集成到项目中,所有模块都可以直接使用:

```java
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.control.Either;
import io.vavr.collection.List;
```

---

## Option 示例

### 基础用法

```java
import io.vavr.control.Option;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据 ID 查找用户（可能不存在）
     */
    public Option<User> findUserById(Long id) {
        return Option.of(userMapper.selectById(id));
    }

    /**
     * 获取用户名称（带默认值）
     */
    public String getUserName(Long userId) {
        return findUserById(userId)
            .map(User::getUserName)
            .getOrElse("Unknown User");
    }

    /**
     * 获取用户邮箱（链式调用）
     */
    public Option<String> getUserEmail(Long userId) {
        return findUserById(userId)
            .map(User::getEmail)
            .filter(email -> email.contains("@"));
    }

    /**
     * 检查用户是否存在
     */
    public boolean userExists(Long userId) {
        return findUserById(userId).isDefined();
    }
}
```

### 实用模式

```java
// 模式 1: 空值处理
public String getDisplayName(Long userId) {
    return findUserById(userId)
        .map(user -> user.getNickName() != null ? user.getNickName() : user.getUserName())
        .getOrElse("访客");
}

// 模式 2: 条件过滤
public Option<User> findActiveUser(Long userId) {
    return findUserById(userId)
        .filter(user -> user.getStatus() == 1);
}

// 模式 3: flatMap 嵌套查询
public Option<Department> getUserDepartment(Long userId) {
    return findUserById(userId)
        .flatMap(user -> findDepartmentById(user.getDeptId()));
}
```

---

## Try 示例

### 文件操作

```java
import io.vavr.control.Try;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FileService {

    /**
     * 读取文件内容（自动处理异常）
     */
    public Try<String> readFile(String path) {
        return Try.of(() -> Files.readString(Paths.get(path)))
            .onSuccess(content -> log.info("文件读取成功, 长度: {}", content.length()))
            .onFailure(e -> log.error("文件读取失败: {}", path, e));
    }

    /**
     * 安全读取并处理
     */
    public String readAndProcess(String path) {
        return readFile(path)
            .map(String::trim)
            .map(String::toUpperCase)
            .getOrElse("DEFAULT_CONTENT");
    }

    /**
     * 多文件读取
     */
    public List<String> readMultipleFiles(List<String> paths) {
        return paths
            .map(this::readFile)
            .filter(Try::isSuccess)
            .map(Try::get);
    }
}
```

### HTTP 请求

```java
@Service
public class ExternalApiService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 调用外部 API（自动捕获异常）
     */
    public Try<ApiResponse> callExternalApi(String url) {
        return Try.of(() -> restTemplate.getForObject(url, ApiResponse.class))
            .onSuccess(response -> log.info("API 调用成功: {}", response))
            .onFailure(e -> log.error("API 调用失败: {}", url, e));
    }

    /**
     * 带重试的 API 调用
     */
    public Try<ApiResponse> callWithRetry(String url, int maxRetries) {
        Try<ApiResponse> result = callExternalApi(url);

        for (int i = 0; i < maxRetries && result.isFailure(); i++) {
            log.warn("重试 API 调用, 第 {} 次", i + 1);
            result = callExternalApi(url);
        }

        return result;
    }
}
```

---

## Either 示例

### 业务验证

```java
import io.vavr.control.Either;

@Service
public class OrderService {

    /**
     * 创建订单（带业务验证）
     */
    public Either<String, Order> createOrder(CreateOrderRequest request) {
        return validateRequest(request)
            .flatMap(this::checkStock)
            .flatMap(this::checkUserCredit)
            .flatMap(this::calculatePrice)
            .flatMap(this::saveOrder);
    }

    private Either<String, CreateOrderRequest> validateRequest(CreateOrderRequest req) {
        if (req.getProductId() == null) {
            return Either.left("产品 ID 不能为空");
        }
        if (req.getQuantity() <= 0) {
            return Either.left("数量必须大于 0");
        }
        return Either.right(req);
    }

    private Either<String, CreateOrderRequest> checkStock(CreateOrderRequest req) {
        int stock = productService.getStock(req.getProductId());
        if (stock < req.getQuantity()) {
            return Either.left("库存不足, 当前库存: " + stock);
        }
        return Either.right(req);
    }

    private Either<String, CreateOrderRequest> checkUserCredit(CreateOrderRequest req) {
        if (!creditService.hasSufficientCredit(req.getUserId(), req.getTotalAmount())) {
            return Either.left("用户信用额度不足");
        }
        return Either.right(req);
    }

    private Either<String, Order> calculatePrice(CreateOrderRequest req) {
        return Try.of(() -> {
            BigDecimal price = productService.getPrice(req.getProductId());
            BigDecimal total = price.multiply(BigDecimal.valueOf(req.getQuantity()));
            req.setTotalAmount(total);
            return req;
        })
        .toEither()
        .mapLeft(e -> "价格计算失败: " + e.getMessage())
        .map(this::convertToOrder);
    }

    private Either<String, Order> saveOrder(Order order) {
        return Try.of(() -> {
            orderMapper.insert(order);
            return order;
        })
        .toEither()
        .mapLeft(e -> "订单保存失败: " + e.getMessage());
    }

    private Order convertToOrder(CreateOrderRequest req) {
        // 转换逻辑...
        return new Order();
    }
}
```

### Controller 集成

```java
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单 API
     */
    @PostMapping
    public R<Order> createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request)
            .fold(
                error -> R.fail(error),
                order -> R.ok(order, "订单创建成功")
            );
    }

    /**
     * 批量创建订单
     */
    @PostMapping("/batch")
    public R<BatchResult> batchCreateOrders(@RequestBody List<CreateOrderRequest> requests) {
        io.vavr.collection.List<Either<String, Order>> results =
            io.vavr.collection.List.ofAll(requests)
                .map(orderService::createOrder);

        List<Order> successes = results
            .filter(Either::isRight)
            .map(Either::get)
            .toJavaList();

        List<String> errors = results
            .filter(Either::isLeft)
            .map(Either::getLeft)
            .toJavaList();

        BatchResult result = new BatchResult(successes, errors);
        return R.ok(result);
    }
}
```

---

## 实际业务场景

### 场景 1: 用户权限检查

```java
@Service
public class PermissionService {

    public Either<String, User> checkUserPermission(Long userId, String permission) {
        return findUserById(userId)
            .toEither("用户不存在")
            .flatMap(user -> checkUserStatus(user))
            .flatMap(user -> checkPermission(user, permission));
    }

    private Either<String, User> checkUserStatus(User user) {
        return user.getStatus() == 1
            ? Either.right(user)
            : Either.left("用户已被禁用");
    }

    private Either<String, User> checkPermission(User user, String permission) {
        return userHasPermission(user, permission)
            ? Either.right(user)
            : Either.left("用户无此权限: " + permission);
    }

    private boolean userHasPermission(User user, String permission) {
        // 权限检查逻辑...
        return true;
    }

    private Option<User> findUserById(Long userId) {
        // 查询用户...
        return Option.none();
    }
}
```

### 场景 2: 配置加载与验证

```java
@Component
public class ConfigLoader {

    public Try<AppConfig> loadAndValidateConfig() {
        return loadConfigFromNacos()
            .flatMap(this::parseYaml)
            .flatMap(this::validateConfig)
            .flatMap(this::decryptSensitiveData);
    }

    private Try<String> loadConfigFromNacos() {
        return Try.of(() -> nacosConfigService.getConfig("application.yml"))
            .onFailure(e -> log.error("从 Nacos 加载配置失败", e));
    }

    private Try<AppConfig> parseYaml(String yaml) {
        return Try.of(() -> yamlMapper.readValue(yaml, AppConfig.class))
            .onFailure(e -> log.error("解析 YAML 配置失败", e));
    }

    private Try<AppConfig> validateConfig(AppConfig config) {
        return config.isValid()
            ? Try.success(config)
            : Try.failure(new ConfigException("配置验证失败"));
    }

    private Try<AppConfig> decryptSensitiveData(AppConfig config) {
        return Try.of(() -> {
            config.setDbPassword(decrypt(config.getDbPassword()));
            config.setApiKey(decrypt(config.getApiKey()));
            return config;
        });
    }

    private String decrypt(String encrypted) {
        // 解密逻辑...
        return encrypted;
    }
}
```

### 场景 3: 缓存穿透保护

```java
@Service
public class ProductService {

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 查询产品（带缓存穿透保护）
     */
    public Option<Product> getProduct(Long productId) {
        return getFromCache(productId)
            .orElse(() -> getFromDatabase(productId)
                .peek(product -> cacheProduct(productId, product))
                .onEmpty(() -> cacheEmpty(productId)));
    }

    private Option<Product> getFromCache(Long productId) {
        return Option.of(redisson.<Product>getBucket("product:" + productId).get());
    }

    private Option<Product> getFromDatabase(Long productId) {
        return Option.of(productMapper.selectById(productId));
    }

    private void cacheProduct(Long productId, Product product) {
        redisson.<Product>getBucket("product:" + productId)
            .set(product, 1, TimeUnit.HOURS);
    }

    private void cacheEmpty(Long productId) {
        // 缓存空值,防止缓存穿透
        redisson.getBucket("product:" + productId)
            .set("null", 5, TimeUnit.MINUTES);
    }
}
```

---

## 总结

### 核心要点

1. **Option**: 处理可能为空的值
2. **Try**: 处理可能抛异常的操作
3. **Either**: 处理业务成功/失败分支

### 最佳实践

- Service 层使用 Vavr 类型
- Controller 层转换为 R<T>
- 使用 flatMap 链式组合操作
- 使用 fold/getOrElse 提供默认值

### 参考文档

- [FP 迁移指南](./FP-MIGRATION-GUIDE.md)
- [Vavr 官方文档](https://docs.vavr.io/)

---

**最后更新**: 2025-11-29

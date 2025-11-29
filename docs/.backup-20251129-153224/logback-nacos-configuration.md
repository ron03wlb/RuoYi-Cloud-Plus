# Nacos Logback 配置说明

## 问题描述

在启动应用时，出现 Logback Appender 冲突错误：

```
ERROR in ch.qos.logback.core.rolling.RollingFileAppender[CONFIG_LOG_FILE] - 'File' option has the same value "logs/ruoyi-job/nacos/config.log" as that given for appender [CONFIG_LOG_FILE] defined earlier.
ERROR - Collisions detected with FileAppender/RollingAppender instances defined earlier. Aborting.
```

同样的错误也出现在 `NAMING_LOG_FILE` 和 `REMOTE_LOG_FILE`。

## 根本原因

**双重配置冲突：**

1. **Nacos SDK 内置配置**：
    - Nacos Client SDK (通过 `nacos-logback-adapter-12:2.5.1`) 自带 `nacos-logback14.xml`
    - 定义了以下 appenders：
        - `CONFIG_LOG_FILE` → `logs/${logPath}/nacos/config.log`
        - `NAMING_LOG_FILE` → `logs/${logPath}/nacos/naming.log`
        - `REMOTE_LOG_FILE` → `logs/${logPath}/nacos/remote.log`

2. **项目自定义配置**：
    - 各模块的 `logback-plus.xml` 也定义了相同路径的 Nacos appenders
    - 使用了 `NACOS_CONFIG_LOG_FILE`、`NACOS_NAMING_LOG_FILE`、`NACOS_REMOTE_LOG_FILE` 名称
    - 但输出到相同的文件路径

**冲突机制：**

- Logback 不允许多个 appender 输出到同一个文件路径
- 即使 appender 名称不同，只要 `<file>` 路径相同就会冲突
- Nacos SDK 的配置被自动加载，与项目配置产生冲突

## 解决方案

### 方案概述

采用**覆盖 SDK 默认配置 + 删除项目重复配置**的方式：

1. 在 `ruoyi-common-nacos` 模块创建空的 `nacos-logback14.xml` 覆盖 SDK 配置
2. 删除所有模块 `logback-plus.xml` 中的 Nacos 独立 appender 配置
3. 让 Nacos 日志统一使用项目的标准日志配置

### 详细实施步骤

#### 1. 创建 Nacos Logback 覆盖文件

**文件位置：**

```
ruoyi-common/ruoyi-common-nacos/src/main/resources/nacos-logback14.xml
```

**文件内容：**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
  覆盖 Nacos SDK 内置的 nacos-logback14.xml
  禁用 Nacos 独立日志配置，避免 appender 冲突

  Nacos 日志将通过项目的标准 logback 配置输出到：
  - 控制台 (console)
  - 标准应用日志文件 (console.log, info.log, error.log)

  不再创建独立的 Nacos 日志文件 (nacos/config.log, nacos/naming.log, nacos/remote.log)
-->
<configuration debug="false" scan="false">
    <!--
      空配置文件：不定义任何 appender
      Nacos 的所有日志包将使用 root logger 和主配置文件中的 appender
    -->
</configuration>
```

**工作原理：**

- Logback 按照 classpath 顺序加载配置文件
- `ruoyi-common-nacos` 是项目依赖，优先级高于 Nacos SDK 的 JAR
- 空配置文件覆盖 SDK 的默认配置，禁用独立 appender

#### 2. 删除各模块的 Nacos Appender 配置

**涉及的模块：**

- `ruoyi-auth`
- `ruoyi-gateway`
- `ruoyi-modules/ruoyi-gen`
- `ruoyi-modules/ruoyi-job`
- `ruoyi-modules/ruoyi-resource`
- `ruoyi-modules/ruoyi-system`
- `ruoyi-modules/ruoyi-workflow`

**删除内容：**

在每个模块的 `src/main/resources/logback-plus.xml` 中删除以下部分（约 88 行）：

```xml
<!-- ====================================== -->
<!-- Nacos 独立日志配置 - 避免 appender 冲突 -->
<!-- ====================================== -->

<!-- Nacos Config 日志 -->
<appender name="NACOS_CONFIG_LOG_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${log.path}/nacos/config.log</file>
    ...
</appender>

    <!-- Nacos Naming 日志 -->
<appender name="NACOS_NAMING_LOG_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
...
</appender>

    <!-- Nacos Remote 日志 -->
<appender name="NACOS_REMOTE_LOG_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
...
</appender>

    <!-- Async Nacos Config -->
<appender name="ASYNC_NACOS_CONFIG" class="ch.qos.logback.classic.AsyncAppender">
...
</appender>

    <!-- Async Nacos Naming -->
<appender name="ASYNC_NACOS_NAMING" class="ch.qos.logback.classic.AsyncAppender">
...
</appender>

    <!-- Async Nacos Remote -->
<appender name="ASYNC_NACOS_REMOTE" class="ch.qos.logback.classic.AsyncAppender">
...
</appender>

    <!-- Nacos Config Logger -->
<logger name="com.alibaba.nacos.client.config" level="INFO" additivity="false">
...
</logger>

    <!-- Nacos Naming Logger -->
<logger name="com.alibaba.nacos.client.naming" level="INFO" additivity="false">
...
</logger>

    <!-- Nacos Remote Logger -->
<logger name="com.alibaba.nacos.common.remote" level="INFO" additivity="false">
...
</logger>

    <!-- Nacos 其他日志 (catch-all) -->
<logger name="com.alibaba.nacos" level="INFO" additivity="false">
...
</logger>
```

**删除后的配置结构：**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <!-- 日志存放路径 -->
    <property name="log.path" value="logs/${project.artifactId}"/>

    <!-- 日志输出格式 -->
    <property name="console.log.pattern"
              value="%cyan(%d{yyyy-MM-dd HH:mm:ss}) %green([%thread]) %highlight(%-5level) %boldMagenta(%logger{36}%n) - %msg%n"/>

    <!-- 控制台输出 -->
    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${console.log.pattern}</pattern>
            <charset>utf-8</charset>
        </encoder>
    </appender>

    <include resource="logback-common.xml"/>

    <!-- 可选引用：仅在生产环境或启用 logstash 依赖时加载 -->
    <include optional="true" resource="logback-logstash.xml"/>

    <!-- 可选引用：仅在生产环境或启用 skywalking 依赖时加载 -->
    <include optional="true" resource="logback-skylog.xml"/>

    <!--系统操作日志-->
    <root level="info">
        <appender-ref ref="console"/>
    </root>
</configuration>
```

#### 3. 清理构建缓存

```bash
./gradlew clean build -x test
```

## 最终效果

### 日志输出行为

**Nacos 日志现在会：**

- ✅ 输出到控制台（通过 `console` appender）
- ✅ 输出到标准应用日志文件（通过 `logback-common.xml` 定义的 appenders）
    - `logs/${project.artifactId}/console.log`
    - `logs/${project.artifactId}/info.log`
    - `logs/${project.artifactId}/error.log`

**不再：**

- ❌ 创建独立的 Nacos 日志目录 `logs/*/nacos/`
- ❌ 创建独立的 Nacos 日志文件 `config.log`、`naming.log`、`remote.log`
- ❌ 出现 appender 冲突错误

### Nacos 日志包

Nacos 相关的日志包会使用 root logger，包括：

- `com.alibaba.nacos.client.config` - 配置中心日志
- `com.alibaba.nacos.client.naming` - 服务发现日志
- `com.alibaba.nacos.common.remote` - 远程通信日志
- `com.alibaba.nacos.shaded.io.grpc` - gRPC 日志
- `com.alibaba.nacos.common.labels` - 标签日志

## 自定义配置

### 调整 Nacos 日志级别

如果需要调整 Nacos 日志级别，可以在各模块的 `logback-plus.xml` 中添加：

```xml
<!-- Nacos 日志级别控制 -->
<logger name="com.alibaba.nacos" level="WARN"/>
<logger name="com.alibaba.nacos.client.config" level="INFO"/>
<logger name="com.alibaba.nacos.client.naming" level="INFO"/>
```

### 恢复独立的 Nacos 日志文件（不推荐）

如果确实需要独立的 Nacos 日志文件，需要：

1. **删除** `ruoyi-common-nacos/src/main/resources/nacos-logback14.xml`
2. 在 `logback-common.xml` 或各模块的 `logback-plus.xml` 中添加 Nacos appender
3. **注意**：必须使用与 SDK 不同的 appender 名称和文件路径

示例（使用不同的名称和路径）：

```xml
<!-- 项目自定义 Nacos 日志 -->
<appender name="PROJECT_NACOS_LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${log.path}/custom-nacos.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>${log.path}/custom-nacos.%d{yyyy-MM-dd}.log.gz</fileNamePattern>
        <maxHistory>60</maxHistory>
    </rollingPolicy>
    <encoder>
        <pattern>${log.pattern}</pattern>
        <charset>utf-8</charset>
    </encoder>
</appender>

<logger name="com.alibaba.nacos" level="INFO" additivity="false">
<appender-ref ref="console"/>
<appender-ref ref="PROJECT_NACOS_LOG"/>
</logger>
```

## 验证方法

### 1. 检查启动日志

启动任意模块，确认**没有**以下错误：

```
❌ ERROR in ch.qos.logback.core.rolling.RollingFileAppender[CONFIG_LOG_FILE]
❌ Collisions detected with FileAppender/RollingAppender instances
```

### 2. 检查日志文件

启动后，检查日志目录结构：

```bash
logs/
├── ruoyi-gateway/
│   ├── console.log      ✅ 包含 Nacos 日志
│   ├── info.log         ✅ 包含 Nacos 日志
│   └── error.log
├── ruoyi-system/
│   ├── console.log      ✅ 包含 Nacos 日志
│   ├── info.log         ✅ 包含 Nacos 日志
│   └── error.log
└── ...

# 不应该存在以下目录
❌ logs/*/nacos/
```

### 3. 验证 Nacos 日志输出

在控制台或日志文件中搜索 Nacos 相关日志：

```bash
# 搜索 Nacos 配置加载日志
grep -r "Nacos Config" logs/*/console.log

# 搜索 Nacos 服务注册日志
grep -r "nacos registry" logs/*/console.log
```

应该能看到 Nacos 的正常运行日志。

## 注意事项

### 1. 版本兼容性

- 本配置适用于 Nacos Client `2.5.1` 版本
- 使用 `nacos-logback-adapter-12`（Logback 1.2.x）
- 如果升级 Nacos 版本，需要重新验证此配置

### 2. IDE 运行

如果使用 IDE（如 IntelliJ IDEA）直接运行模块：

- 确保已执行 `./gradlew clean build`
- IDE 会自动加载最新的依赖和资源文件
- 如果仍有问题，尝试 `Invalidate Caches / Restart`

### 3. Docker 部署

在 Docker 容器中运行时：

- 确保 `ruoyi-common-nacos-2.5.0.jar` 包含 `nacos-logback14.xml`
- 可以通过以下命令验证：
  ```bash
  jar tf ruoyi-common/ruoyi-common-nacos/build/libs/ruoyi-common-nacos-2.5.0.jar | grep nacos
  ```

### 4. 与其他日志框架集成

如果项目同时使用了其他日志框架（如 Log4j2），需要：

- 确保 SLF4J 桥接配置正确
- Nacos 会通过 SLF4J 输出日志
- 不需要额外配置

## 故障排查

### 问题 1：仍然出现 appender 冲突

**可能原因：**

- Build 缓存未清理
- IDE 缓存未刷新
- `nacos-logback14.xml` 未正确打包

**解决方法：**

```bash
# 1. 完全清理
./gradlew clean

# 2. 重新构建
./gradlew build -x test

# 3. 验证文件存在
jar tf ruoyi-common/ruoyi-common-nacos/build/libs/ruoyi-common-nacos-2.5.0.jar | grep nacos-logback14.xml

# 4. IDE 刷新缓存（IntelliJ）
File > Invalidate Caches / Restart
```

### 问题 2：Nacos 日志丢失

**可能原因：**

- Nacos 日志级别设置为 OFF 或过高
- Root logger 未配置合适的 appender

**解决方法：**

1. 检查 `logback-plus.xml` 中的 root logger：

```xml

<root level="info">
    <appender-ref ref="console"/>
    <appender-ref ref="async_info"/>
    <appender-ref ref="async_error"/>
</root>
```

2. 添加明确的 Nacos logger（可选）：

```xml

<logger name="com.alibaba.nacos" level="INFO"/>
```

### 问题 3：日志文件过大

**可能原因：**

- Nacos 的心跳、健康检查日志过于频繁

**解决方法：**

调整 Nacos 日志级别：

```xml
<!-- 降低 Nacos Naming 心跳日志级别 -->
<logger name="com.alibaba.nacos.client.naming" level="WARN"/>

    <!-- 降低 Nacos Remote 通信日志级别 -->
<logger name="com.alibaba.nacos.common.remote" level="WARN"/>

    <!-- 保留配置变更日志 -->
<logger name="com.alibaba.nacos.client.config" level="INFO"/>
```

## 相关文件清单

### 修改的文件

1. **新增文件：**
    - `ruoyi-common/ruoyi-common-nacos/src/main/resources/nacos-logback14.xml`

2. **修改的 logback-plus.xml（删除 Nacos appender 配置）：**
    - `ruoyi-auth/src/main/resources/logback-plus.xml`
    - `ruoyi-gateway/src/main/resources/logback-plus.xml`
    - `ruoyi-modules/ruoyi-gen/src/main/resources/logback-plus.xml`
    - `ruoyi-modules/ruoyi-job/src/main/resources/logback-plus.xml`
    - `ruoyi-modules/ruoyi-resource/src/main/resources/logback-plus.xml`
    - `ruoyi-modules/ruoyi-system/src/main/resources/logback-plus.xml`
    - `ruoyi-modules/ruoyi-workflow/src/main/resources/logback-plus.xml`

### 未修改的文件

以下模块的 `logback-plus.xml` 本身就没有 Nacos appender 配置，无需修改：

- `ruoyi-example/ruoyi-demo`
- `ruoyi-example/ruoyi-test-mq`
- `ruoyi-visual/ruoyi-monitor`
- `ruoyi-visual/ruoyi-snailjob-server`

## 参考资料

- [Logback 官方文档](https://logback.qos.ch/manual/)
- [Nacos Client GitHub](https://github.com/alibaba/nacos)
- [Spring Cloud Alibaba Nacos](https://github.com/alibaba/spring-cloud-alibaba/wiki/Nacos-config)

## 修订历史

| 日期         | 版本    | 修改内容                                | 修改人         |
|------------|-------|-------------------------------------|-------------|
| 2025-11-23 | 1.0.0 | 初始版本：解决 Nacos Logback Appender 冲突问题 | Claude Code |

---

**文档维护者：** RuoYi-Cloud-Plus 项目组
**最后更新：** 2025-11-23

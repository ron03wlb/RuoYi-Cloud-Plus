# Gradle 构建指南

本文档是 RuoYi-Cloud-Plus 项目的 Gradle 构建完整指南，涵盖配置说明、快速开始、常见问题等内容。

---

## 📋 目录

- [项目概述](#项目概述)
- [快速开始](#快速开始)
- [Gradle 配置文件清单](#gradle-配置文件清单)
- [Gradle vs Maven 命令对照](#gradle-vs-maven-命令对照)
- [项目结构说明](#项目结构说明)
- [关键配置说明](#关键配置说明)
- [常见问题](#常见问题)
- [重要注意事项](#重要注意事项)
- [Gradle 基础知识](#gradle-基础知识)
- [性能优化建议](#性能优化建议)
- [迁移路线图](#迁移路线图)
- [参考资源](#参考资源)

---

## 项目概述

本项目已创建 Gradle 构建配置文件，与现有 Maven 配置**并行存在**，支持 1-2 个月的过渡期。

### 关键特性

- ✅ **Kotlin DSL**：使用 `build.gradle.kts` 获得类型安全和 IDE 智能提示
- ✅ **Version Catalog**：集中管理依赖版本（`gradle/libs.versions.toml`）
- ✅ **Jib 插件**：无需 Docker Daemon 即可构建镜像
- ✅ **性能优化**：启用构建缓存、并行构建、配置缓存
- ✅ **完全依赖 Nacos**：移除构建时配置，通过环境变量 + Nacos 管理

---

## 快速开始

### 1. 初始化 Gradle Wrapper

**首次使用必须执行**（或使用提供的初始化脚本）：

#### 方式 1：使用初始化脚本（推荐）

```bash
# Unix/Linux/macOS
cd /path/to/RuoYi-Cloud-Plus
./script/gradle/init-gradle.sh
```

#### 方式 2：手动初始化

```bash
# 如果本地没有 Gradle，先安装：
# macOS: brew install gradle
# Linux: sdk install gradle 8.12
# Windows: choco install gradle

# 生成 Gradle Wrapper 文件
gradle wrapper --gradle-version 8.12 --distribution-type bin
```

执行后会生成：

- `gradlew`（Unix/Linux/macOS 可执行脚本）
- `gradlew.bat`（Windows 批处理文件）
- `gradle/wrapper/gradle-wrapper.jar`

**之后团队成员无需安装 Gradle**，直接使用 `./gradlew` 即可。

### 2. 验证 Gradle 安装

```bash
# Unix/Linux/macOS
./gradlew --version

# Windows
gradlew.bat --version
```

应该看到类似输出：

```
------------------------------------------------------------
Gradle 8.12
------------------------------------------------------------

Build time:   2024-11-22 02:35:36 UTC
Revision:     ...

Kotlin:       1.9.20
Groovy:       3.0.17
Ant:          Apache Ant(TM) version 1.10.13
JVM:          17.0.8 (Eclipse Adoptium 17.0.8+7)
OS:           Mac OS X 14.0 aarch64
```

### 3. 构建项目

```bash
# 清理构建输出
./gradlew clean

# 编译所有模块（跳过测试）
./gradlew build -x test

# 编译并运行测试
./gradlew build

# 只编译不打包
./gradlew compileJava

# 查看所有任务
./gradlew tasks
```

### 4. 运行服务

```bash
# 运行 Spring Boot 应用（以 gateway 为例）
./gradlew :ruoyi-gateway:bootRun

# 构建可执行 JAR
./gradlew :ruoyi-gateway:bootJar

# 运行生成的 JAR
java -jar ruoyi-gateway/build/libs/ruoyi-gateway.jar
```

### 5. 构建 Docker 镜像

```bash
# 使用 Jib 构建镜像到本地 Docker
./gradlew :ruoyi-gateway:jibDockerBuild

# 或使用自定义任务
./gradlew :ruoyi-gateway:buildDockerImage

# 推送到远程仓库（需先配置认证）
./gradlew :ruoyi-gateway:jib
```

---

## Gradle 配置文件清单

### 已创建文件列表

#### 1. 核心配置文件（项目根目录）

| 文件名                     | 路径                     | 用途                                |
|-------------------------|------------------------|-----------------------------------|
| **gradle.properties**   | `/gradle.properties`   | 项目属性配置（版本号、JVM 参数、性能优化设置）         |
| **settings.gradle.kts** | `/settings.gradle.kts` | 项目设置（定义所有模块、仓库配置、Version Catalog） |
| **build.gradle.kts**    | `/build.gradle.kts`    | 根项目构建脚本（定义所有子项目的通用配置）             |

#### 2. 依赖版本管理

| 文件名                    | 路径                           | 用途                             |
|------------------------|------------------------------|--------------------------------|
| **libs.versions.toml** | `/gradle/libs.versions.toml` | Version Catalog（集中管理 40+ 依赖版本） |

#### 3. Gradle Wrapper 配置

| 文件名                           | 路径                                          | 用途                       |
|-------------------------------|---------------------------------------------|--------------------------|
| **gradle-wrapper.properties** | `/gradle/wrapper/gradle-wrapper.properties` | Wrapper 配置（定义 Gradle 版本） |

> **注意**：`gradlew`、`gradlew.bat` 和 `gradle-wrapper.jar` 需要运行 `./script/gradle/init-gradle.sh` 或 `gradle wrapper`
> 生成。

#### 4. 示例模块配置文件

| 文件名                  | 路径                                                 | 模块类型           | 用途                                 |
|----------------------|----------------------------------------------------|----------------|------------------------------------|
| **build.gradle.kts** | `/ruoyi-common/ruoyi-common-bom/build.gradle.kts`  | BOM 模块         | 依赖版本管理（java-platform 插件）           |
| **build.gradle.kts** | `/ruoyi-common/ruoyi-common-core/build.gradle.kts` | Library 模块     | 普通 Java 库（不含 Spring Boot 应用）       |
| **build.gradle.kts** | `/ruoyi-gateway/build.gradle.kts`                  | Spring Boot 服务 | 完整的 Spring Boot 应用 + Jib Docker 构建 |

#### 5. 工具脚本

| 文件名                | 路径                              | 用途                                     |
|--------------------|---------------------------------|----------------------------------------|
| **init-gradle.sh** | `/script/gradle/init-gradle.sh` | Gradle Wrapper 初始化脚本（Unix/Linux/macOS） |

### 完整目录结构

```
RuoYi-Cloud-Plus/
├── build.gradle.kts                  ✅ 已创建
├── settings.gradle.kts               ✅ 已创建
├── gradle.properties                 ✅ 已创建
├── gradle.md                         ✅ 本文件
├── gradle/
│   ├── libs.versions.toml            ✅ 已创建
│   └── wrapper/
│       ├── gradle-wrapper.properties ✅ 已创建
│       └── gradle-wrapper.jar        ⏳ 待生成（运行 init-gradle.sh）
├── gradlew                           ⏳ 待生成（运行 init-gradle.sh）
├── gradlew.bat                       ⏳ 待生成（运行 init-gradle.sh）
├── script/
│   └── gradle/
│       └── init-gradle.sh            ✅ 已创建（可执行）
├── ruoyi-common/
│   ├── ruoyi-common-bom/
│   │   └── build.gradle.kts          ✅ 已创建（BOM 示例）
│   └── ruoyi-common-core/
│       └── build.gradle.kts          ✅ 已创建（Library 示例）
└── ruoyi-gateway/
    └── build.gradle.kts              ✅ 已创建（Spring Boot 服务示例）
```

### 下一步操作

#### 1. 初始化 Gradle Wrapper（必须）

如果尚未完成，运行初始化脚本生成 `gradlew`、`gradlew.bat` 和 `gradle-wrapper.jar`：

```bash
# Unix/Linux/macOS
./script/gradle/init-gradle.sh

# Windows
# 需手动运行：gradle wrapper --gradle-version 8.12 --distribution-type bin
```

#### 2. 为其他模块创建 build.gradle.kts

目前只创建了 3 个示例模块的配置文件，其余 55+ 模块需要逐步创建。建议按以下顺序：

**优先级 1：核心业务模块（1-2 天）**

- `ruoyi-auth/build.gradle.kts`
- `ruoyi-modules/ruoyi-system/build.gradle.kts`
- `ruoyi-modules/ruoyi-gen/build.gradle.kts`

**优先级 2：公共库模块（2-3 天）**

- `ruoyi-common/ruoyi-common-redis/build.gradle.kts`
- `ruoyi-common/ruoyi-common-mybatis/build.gradle.kts`
- `ruoyi-common/ruoyi-common-web/build.gradle.kts`
- `ruoyi-common/ruoyi-common-satoken/build.gradle.kts`
- 其他 20+ 个 `ruoyi-common-*/build.gradle.kts`

**优先级 3：API 和其他模块（1-2 天）**

- `ruoyi-api/ruoyi-api-system/build.gradle.kts`
- `ruoyi-api/ruoyi-api-resource/build.gradle.kts`
- `ruoyi-modules/ruoyi-job/build.gradle.kts`
- `ruoyi-modules/ruoyi-resource/build.gradle.kts`
- `ruoyi-modules/ruoyi-workflow/build.gradle.kts`

**优先级 4：可视化和示例模块（可选，1 天）**

- `ruoyi-visual/ruoyi-monitor/build.gradle.kts`
- `ruoyi-visual/ruoyi-seata-server/build.gradle.kts`
- `ruoyi-example/ruoyi-demo/build.gradle.kts`

#### 3. 创建模板

可以参考已创建的 3 个示例，根据模块类型选择模板：

- **BOM 模块**：参考 `ruoyi-common-bom/build.gradle.kts`
- **Library 模块**：参考 `ruoyi-common-core/build.gradle.kts`
- **Spring Boot 服务**：参考 `ruoyi-gateway/build.gradle.kts`

---

## Gradle vs Maven 命令对照

| 操作             | Maven 命令                                  | Gradle 命令                              |
|----------------|-------------------------------------------|----------------------------------------|
| 清理构建           | `mvn clean`                               | `./gradlew clean`                      |
| 编译代码           | `mvn compile`                             | `./gradlew compileJava`                |
| 运行测试           | `mvn test`                                | `./gradlew test`                       |
| 打包项目           | `mvn package`                             | `./gradlew build`                      |
| 安装到本地仓库        | `mvn install`                             | `./gradlew publishToMavenLocal`        |
| 跳过测试打包         | `mvn package -DskipTests`                 | `./gradlew build -x test`              |
| 清理并打包          | `mvn clean package`                       | `./gradlew clean build`                |
| 只编译单个模块        | `mvn compile -pl :ruoyi-gateway`          | `./gradlew :ruoyi-gateway:compileJava` |
| 查看依赖树          | `mvn dependency:tree`                     | `./gradlew dependencies`               |
| 更新依赖版本         | `mvn versions:display-dependency-updates` | `./gradlew dependencyUpdates`          |
| 运行 Spring Boot | `mvn spring-boot:run`                     | `./gradlew bootRun`                    |
| 构建 Docker 镜像   | `mvn spring-boot:build-image`             | `./gradlew jibDockerBuild`             |
| 查看所有任务         | `mvn help:describe -Dcmd=compile`         | `./gradlew tasks`                      |
| 构建特定模块         | `mvn package -pl :ruoyi-gateway -am`      | `./gradlew :ruoyi-gateway:build`       |

---

## 项目结构说明

### Gradle 配置文件

```
RuoYi-Cloud-Plus/
├── build.gradle.kts              # 根项目构建脚本（定义所有子项目通用配置）
├── settings.gradle.kts           # 项目设置（定义所有模块、仓库配置）
├── gradle.properties             # 项目属性（版本号、JVM 参数、性能优化）
├── gradle/
│   ├── libs.versions.toml        # Version Catalog（集中管理依赖版本）
│   └── wrapper/
│       ├── gradle-wrapper.properties  # Gradle Wrapper 配置
│       └── gradle-wrapper.jar         # Wrapper JAR（由 gradle wrapper 生成）
├── gradlew                       # Unix/Linux/macOS 启动脚本（由 gradle wrapper 生成）
├── gradlew.bat                   # Windows 启动脚本（由 gradle wrapper 生成）
└── ruoyi-*/
    └── build.gradle.kts          # 各模块的构建脚本
```

### Maven vs Gradle 对比

| 文件     | Maven                        | Gradle (Kotlin DSL)                        |
|--------|------------------------------|--------------------------------------------|
| 父项目配置  | `pom.xml`                    | `build.gradle.kts` + `settings.gradle.kts` |
| 模块配置   | `pom.xml`                    | `build.gradle.kts`                         |
| 依赖版本管理 | `<dependencyManagement>`     | `gradle/libs.versions.toml`                |
| 项目属性   | `<properties>`               | `gradle.properties`                        |
| BOM 模块 | `<packaging>pom</packaging>` | `plugins { id("java-platform") }`          |

---

## 关键配置说明

### gradle.properties 配置项

```properties
# 项目版本
version=2.5.0
# Java 版本
javaVersion=17
# 性能优化
org.gradle.caching=true              # 构建缓存
org.gradle.parallel=true             # 并行构建
org.gradle.configuration-cache=true  # 配置缓存（实验性）
org.gradle.jvmargs=-Xmx2048m         # JVM 内存
# Nacos 配置
nacosServer=127.0.0.1:8848
nacosUsername=nacos
nacosPassword=nacos
```

### libs.versions.toml 结构

```toml
[versions]          # 版本号定义
spring-boot = "3.5.6"

[libraries]         # 库定义
spring-boot-web = { module = "org.springframework.boot:...", version.ref = "spring-boot" }

[bundles]          # 库组合
spring-boot-basics = ["spring-boot-web", "spring-boot-actuator"]

[plugins]          # 插件定义
spring-boot = { id = "org.springframework.boot", version.ref = "springBootPlugin" }
```

### build.gradle.kts 模块类型

#### BOM 模块（java-platform）

```kotlin
plugins {
    `java-platform`
}

dependencies {
    constraints {
        api(project(":ruoyi-common:ruoyi-common-core"))
    }
}
```

#### Library 模块

```kotlin
dependencies {
    api(libs.spring.boot.autoconfigure)
    implementation(libs.hutool.core)
}
```

#### Spring Boot 服务

```kotlin
plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
}

dependencies {
    implementation(libs.bundles.spring.boot.web)
    implementation(project(":ruoyi-common:ruoyi-common-core"))
}

tasks.named<BootJar>("bootJar") {
    archiveFileName.set("${project.name}.jar")
}
```

### 资源文件变量替换

Gradle 通过 `ProcessResources` 任务处理变量替换：

```kotlin
tasks.withType<ProcessResources> {
    filesMatching(listOf("application*.yml", "bootstrap*.yml", "logback*.xml")) {
        expand(
            "project.version" to version,
            "nacos.server" to (project.findProperty("nacosServer") ?: "127.0.0.1:8848")
        )
    }
}
```

在 `application.yml` 中使用：

```yaml
spring:
    application:
        version: ${project.version}
nacos:
    server: ${nacos.server}
```

---

## 常见问题

### 1. Gradle Wrapper 初始化失败？

**问题**：执行 `gradle wrapper` 时提示 `gradle: command not found`

**解决**：

```bash
# macOS
brew install gradle

# Linux (使用 SDKMAN)
curl -s "https://get.sdkman.io" | bash
sdk install gradle 8.12

# Windows (使用 Chocolatey)
choco install gradle
```

### 2. 构建速度慢？

**解决**：

1. 检查 `gradle.properties` 中的性能优化配置是否启用
2. 使用华为云/阿里云 Maven 镜像（已在 `settings.gradle.kts` 中配置）
3. 启用 Gradle Daemon：`org.gradle.daemon=true`
4. 增加 JVM 内存：`org.gradle.jvmargs=-Xmx2048m`

### 3. 依赖下载失败？

**解决**：

1. 检查网络连接
2. 修改 `settings.gradle.kts` 中的仓库顺序，优先使用国内镜像
3. 清理 Gradle 缓存：`./gradlew clean --refresh-dependencies`

### 4. 如何查看某个模块的依赖？

```bash
# 查看 ruoyi-gateway 的依赖树
./gradlew :ruoyi-gateway:dependencies

# 只看编译时依赖
./gradlew :ruoyi-gateway:dependencies --configuration compileClasspath

# 只看运行时依赖
./gradlew :ruoyi-gateway:dependencies --configuration runtimeClasspath
```

### 5. 如何更新依赖版本？

1. 编辑 `gradle/libs.versions.toml` 文件
2. 修改 `[versions]` 部分的版本号
3. 运行 `./gradlew build` 验证

### 6. 如何添加新的依赖？

**方式 1：使用 Version Catalog（推荐）**

1. 在 `gradle/libs.versions.toml` 中添加版本和库定义：

```toml
[versions]
new-lib = "1.0.0"

[libraries]
new-lib = { module = "com.example:new-lib", version.ref = "new-lib" }
```

2. 在模块的 `build.gradle.kts` 中引用：

```kotlin
dependencies {
    implementation(libs.new.lib)
}
```

**方式 2：直接声明**

```kotlin
dependencies {
    implementation("com.example:new-lib:1.0.0")
}
```

### 7. Gradle 和 Maven 可以同时使用吗？

**可以！** 本项目设计为 1-2 个月的并行支持期：

- Maven 配置文件（`pom.xml`）保持不变
- Gradle 配置文件（`build.gradle.kts`）独立存在
- 团队可以自由选择使用 `mvn` 或 `./gradlew` 构建
- 建议在并行期内充分测试 Gradle 构建，确认无问题后再移除 Maven

### 8. 如何在 IntelliJ IDEA 中使用 Gradle？

1. 打开项目
2. IDEA 会自动检测到 `build.gradle.kts`
3. 选择 **View → Tool Windows → Gradle**
4. 点击刷新按钮同步项目
5. 右键模块 → **Link Gradle Project** 如果未自动识别

---

## 重要注意事项

### 1. Maven 和 Gradle 并行支持

- **保留所有 pom.xml 文件**：不要删除，确保 Maven 构建仍可用
- **独立运行**：Gradle 和 Maven 互不影响
- **过渡期**：建议 1-2 个月内完成充分测试

### 2. 依赖版本一致性

- Gradle 的依赖版本（`libs.versions.toml`）应与 Maven 的版本（`pom.xml`）**完全一致**
- 如果需要更新版本，同时修改两处（并行期内）

### 3. 注解处理器顺序

在根 `build.gradle.kts` 中已配置正确顺序：

```kotlin
annotationProcessor("com.github.therapi:therapi-runtime-javadoc-scribe:0.15.0")
annotationProcessor(libs.lombok)
annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
annotationProcessor("io.github.linpeilie:mapstruct-plus-processor:1.5.3")
annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
```

**不要改变此顺序**，否则可能导致编译错误。

---

## Gradle 基础知识

### 1. Gradle 任务（Tasks）

Gradle 的基本执行单元是"任务"。常用任务：

- `clean`：删除 `build/` 目录
- `compileJava`：编译 Java 代码
- `compileTestJava`：编译测试代码
- `test`：运行单元测试
- `build`：完整构建（编译 + 测试 + 打包）
- `bootJar`：生成 Spring Boot 可执行 JAR（需 Spring Boot 插件）
- `bootRun`：运行 Spring Boot 应用

查看所有可用任务：

```bash
./gradlew tasks --all
```

### 2. 依赖配置（Dependency Configurations）

| 配置                    | 说明            | Maven 等价                     |
|-----------------------|---------------|------------------------------|
| `implementation`      | 编译时依赖，不传递给消费者 | `<scope>compile</scope>`     |
| `api`                 | 编译时依赖，会传递给消费者 | `<scope>compile</scope>`     |
| `compileOnly`         | 仅编译时需要，运行时不需要 | `<scope>provided</scope>`    |
| `runtimeOnly`         | 仅运行时需要        | `<scope>runtime</scope>`     |
| `testImplementation`  | 测试编译时依赖       | `<scope>test</scope>`        |
| `annotationProcessor` | 注解处理器         | `<annotationProcessorPaths>` |

### 3. 多模块项目

引用其他模块：

```kotlin
dependencies {
    // 引用同项目的其他模块
    implementation(project(":ruoyi-common:ruoyi-common-core"))

    // 引用 BOM（版本管理）
    implementation(platform(project(":ruoyi-common:ruoyi-common-bom")))
}
```

### 4. Kotlin DSL vs Groovy DSL

本项目使用 **Kotlin DSL**（`build.gradle.kts`），优势：

- 类型安全，IDE 自动补全
- 重构友好
- 与 IntelliJ IDEA 深度集成
- 编译时错误检查

Groovy DSL（`build.gradle`）示例：

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
}
```

Kotlin DSL 等价写法：

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
}
```

### 5. Version Catalog

统一管理依赖版本，避免版本冲突。文件位置：`gradle/libs.versions.toml`

**定义版本**：

```toml
[versions]
spring-boot = "3.5.6"

[libraries]
spring-boot-web = { module = "org.springframework.boot:spring-boot-starter-web", version.ref = "spring-boot" }

[bundles]
spring-boot-basics = ["spring-boot-web", "spring-boot-actuator"]
```

**使用**：

```kotlin
dependencies {
    implementation(libs.spring.boot.web)          // 单个库
    implementation(libs.bundles.spring.boot.basics)  // 一组库
}
```

---

## 性能优化建议

### 1. 启用 Gradle Daemon

在 `gradle.properties` 中：

```properties
org.gradle.daemon=true
```

### 2. 启用并行构建

```properties
org.gradle.parallel=true
```

### 3. 启用构建缓存

```properties
org.gradle.caching=true
```

### 4. 配置缓存（实验性功能）

```properties
org.gradle.configuration-cache=true
```

### 5. 增加 JVM 内存

```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
```

### 6. 使用本地 Maven 缓存

Gradle 会自动使用 `~/.m2/repository` 中的依赖，无需重新下载。

---

## 迁移路线图

### 阶段 1：准备期（1-2 天）

- ✅ 创建 Gradle 配置文件
- ✅ 生成 Gradle Wrapper
- ✅ 团队成员学习 Gradle 基础

### 阶段 2：验证期（3-5 天）

- 使用 Gradle 构建所有模块
- 对比 Maven 和 Gradle 构建产物
- 运行所有测试用例
- 验证 Docker 镜像构建

### 阶段 3：并行期（1-2 个月）

- Maven 和 Gradle 同时存在
- 开发人员可选择使用任一构建工具
- 收集反馈，优化 Gradle 配置

### 阶段 4：切换期（1 周）

- 确认 Gradle 构建稳定
- 更新 CI/CD 流程
- 移除 Maven 配置文件（可选）

### 迁移进度

- ✅ 核心配置文件创建（100%）
- ✅ 示例模块配置创建（3/58 模块）
- ⏳ Gradle Wrapper 初始化（需运行 `./script/gradle/init-gradle.sh`）
- ⏳ 其余模块配置创建（0/55 模块）
- ⏳ 功能测试验证
- ⏳ Docker 镜像构建测试
- ⏳ 文档完善

---

## 参考资源

- [Gradle 官方文档](https://docs.gradle.org/current/userguide/userguide.html)
- [Gradle Kotlin DSL 指南](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
- [Spring Boot Gradle 插件](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/)
- [Jib Gradle 插件](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin)
- [Version Catalog 指南](https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog)

---

## 联系支持

如有问题，请：

1. 查阅本文档的 [常见问题](#常见问题) 部分
2. 参考 Gradle 官方文档
3. 联系项目负责人

---

**祝 Gradle 使用愉快！** 🚀

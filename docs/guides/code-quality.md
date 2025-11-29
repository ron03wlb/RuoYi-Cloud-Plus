# 代码质量保障指南

本文档详细介绍 RuoYi-Cloud-Plus 项目中的代码质量检查工具和构建流程，帮助新开发者理解构建过程和质量标准。

## 目录

- [构建流程概览](#构建流程概览)
- [质量检查工具](#质量检查工具)
    - [1. 单元测试 (Unit Tests)](#1-单元测试-unit-tests)
    - [2. Spotless (代码格式化)](#2-spotless-代码格式化)
    - [3. Checkstyle (代码风格检查)](#3-checkstyle-代码风格检查)
    - [4. PMD (静态代码分析)](#4-pmd-静态代码分析)
    - [5. SpotBugs (Bug 检测)](#5-spotbugs-bug-检测)
- [开发者工作流](#开发者工作流)
- [CI/CD 集成](#cicd-集成)
- [常见问题](#常见问题)

---

## 构建流程概览

### 完整构建命令

```bash
./gradlew clean build
```

### 构建阶段和任务执行顺序

当执行 `./gradlew clean build` 时，Gradle 按以下顺序执行任务：

```
1. clean
   └─ 删除 build/ 目录

2. compileJava
   ├─ spotlessApply (自动格式化代码)
   └─ 编译 Java 源代码

3. processResources
   └─ 处理资源文件（application.yml 等）

4. classes
   └─ 组装编译后的类文件

5. test
   └─ 运行单元测试 (JUnit 5)

6. check
   ├─ checkstyleMain (Google Java Style 检查)
   ├─ pmdMain (PMD 静态分析)
   └─ spotbugsMain (SpotBugs Bug 检测)

7. jar / bootJar
   └─ 打包成 JAR 文件

8. assemble
   └─ 组装最终产物

9. build
   └─ 完整构建流程
```

### 任务依赖关系图

```
build
 ├── check
 │    ├── test
 │    │    └── compileTestJava
 │    │         └── compileJava
 │    │              └── spotlessApply
 │    ├── checkstyleMain
 │    │    └── classes
 │    ├── pmdMain
 │    │    └── classes
 │    └── spotbugsMain
 │         └── classes
 └── assemble
      └── jar / bootJar
           └── classes
```

**关键说明**：

- `spotlessApply` 在编译前自动运行，确保代码格式正确
- `test` 在质量检查前运行，确保功能正确
- `checkstyleMain`, `pmdMain`, `spotbugsMain` 在 `check` 阶段并行运行
- 所有质量检查都配置为 `ignoreFailures = true`，不会中断构建

---

## 质量检查工具

### 1. 单元测试 (Unit Tests)

#### 目的

验证代码功能正确性，确保每个单元（方法、类）按预期工作。

#### 技术栈

- **JUnit 5** (Jupiter): 测试框架
- **Mockito**: Mock 框架，用于隔离依赖
- **Spring Boot Test**: Spring 集成测试支持
- **AssertJ**: 流式断言库

#### 配置位置

测试配置在 `build.gradle.kts` 的 `subprojects` 块中：

```kotlin
tasks.withType<Test> {
    useJUnitPlatform()  // 启用 JUnit 5

    jvmArgs = listOf(
        "-Xmx1024m",
        "-XX:MaxMetaspaceSize=256m"
    )

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
    }
}
```

测试依赖：

```kotlin
dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
```

#### 运行测试

```bash
# 运行所有测试
./gradlew test

# 运行特定模块的测试
./gradlew :ruoyi-auth:test

# 运行所有模块的测试（根项目任务）
./gradlew testAll

# 跳过测试（开发时）
./gradlew build -x test
```

#### 测试报告

测试报告位置：`<module>/build/reports/tests/test/index.html`

```bash
# 查看测试报告（macOS）
open ruoyi-auth/build/reports/tests/test/index.html
```

#### 编写测试示例

```java

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @Test
    @DisplayName("测试用户登录 - 成功场景")
    void testLogin_Success() {
        // Given
        String username = "admin";
        String password = "admin123";
        LoginUser mockUser = new LoginUser();
        mockUser.setUsername(username);

        when(userMapper.selectUserByUserName(username))
            .thenReturn(Optional.of(mockUser));

        // When
        LoginUser result = userService.login(username, password);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
        verify(userMapper, times(1)).selectUserByUserName(username);
    }
}
```

#### IDE 集成

**IntelliJ IDEA**:

- 右键测试类/方法 → Run 'TestName'
- 使用快捷键: Ctrl+Shift+F10 (Windows/Linux), Ctrl+Shift+R (macOS)
- 查看覆盖率: Run → Run 'TestName' with Coverage

---

### 2. Spotless (代码格式化)

#### 目的

自动格式化代码，确保团队代码风格一致，减少无意义的格式差异。

#### 配置位置

`build.gradle.kts` (lines 252-320):

```kotlin
plugins {
    id("com.diffplug.spotless") version "6.25.0"
}

allprojects {
    apply(plugin = "com.diffplug.spotless")

    configure<SpotlessExtension> {
        java {
            target("src/**/*.java")
            targetExclude("**/build/**", "**/target/**")

            // Google Java Format (2 空格缩进)
            googleJavaFormat("1.19.2").reflowLongStrings()

            // 导入顺序和移除未使用导入
            importOrder()
            removeUnusedImports()

            // 行尾处理
            trimTrailingWhitespace()
            endWithNewline()
        }

        kotlinGradle {
            target("*.gradle.kts", "**/*.gradle.kts")
            ktlint("1.0.1")
        }

        format("xml") {
            target("src/**/*.xml")
            trimTrailingWhitespace()
            endWithNewline()
            replaceRegex("XML indentation", "\t", "  ")
        }

        format("yaml") {
            target("src/**/*.yml", "src/**/*.yaml")
            trimTrailingWhitespace()
            endWithNewline()
            replaceRegex("YAML indentation", "\t", "  ")
        }
    }
}
```

#### 自动应用

Spotless 会在编译前自动运行：

```kotlin
tasks.named("compileJava") {
    dependsOn("spotlessApply")
}
```

#### 运行 Spotless

```bash
# 自动修复格式问题
./gradlew spotlessApply

# 检查格式但不修复
./gradlew spotlessCheck

# 对特定模块应用格式化
./gradlew :ruoyi-auth:spotlessApply
```

#### 格式化规则

**Java 文件**:

- 使用 Google Java Format (标准 2 空格缩进)
- 自动重排长字符串
- 自动排序和清理导入语句
- 移除行尾空格
- 文件末尾添加换行符

**XML/YAML 文件**:

- 统一使用 2 空格缩进（不使用 Tab）
- 移除行尾空格
- 文件末尾添加换行符

#### IDE 集成

**IntelliJ IDEA**:

1. 安装插件: Settings → Plugins → 搜索 "google-java-format"
2. 启用插件: Settings → google-java-format Settings → Enable
3. 设置快捷键: Settings → Keymap → 搜索 "Reformat Code with google-java-format"
4. 保存时自动格式化: Settings → Tools → Actions on Save → Reformat code

---

### 3. Checkstyle (代码风格检查)

#### 目的

检查代码是否符合 Google Java Style 规范，包括命名规则、注释规范、代码结构等。

#### 配置位置

`build.gradle.kts` (lines 322-372):

```kotlin
plugins {
    id("checkstyle")
}

subprojects {
    plugins.withId("java") {
        if (!project.name.contains("demo") && !project.name.contains("example")) {
            apply(plugin = "checkstyle")

            configure<CheckstyleExtension> {
                toolVersion = "12.1.2"
                configFile = rootProject.file("config/checkstyle/google_checks.xml")

                // 只检查 main 源代码
                sourceSets = listOf(project.extensions.getByType<SourceSetContainer>()["main"])

                isIgnoreFailures = true
                maxWarnings = 0
                maxErrors = 0
            }

            tasks.withType<Checkstyle> {
                reports {
                    html.required.set(true)
                    html.outputLocation.set(file("build/reports/checkstyle/main.html"))

                    xml.required.set(true)
                    xml.outputLocation.set(file("build/reports/checkstyle/main.xml"))
                }
            }
        }
    }
}
```

#### 规则集配置

配置文件: `config/checkstyle/google_checks.xml`

这是 Google Java Style 的官方 Checkstyle 配置，主要检查：

**命名规范**:

- 类名: UpperCamelCase
- 方法名: lowerCamelCase
- 常量: UPPER_SNAKE_CASE
- 包名: lowercase

**格式规范**:

- 缩进: 2 空格
- 行长度: 100 字符
- 空格使用: 运算符、关键字、括号周围的空格
- 花括号: K&R 风格

**注释规范**:

- Javadoc 必须正确格式化
- 公共类和方法需要 Javadoc

**代码结构**:

- import 语句顺序
- 修饰符顺序
- 重载方法应连续
- 一行一个语句

#### 运行 Checkstyle

```bash
# 运行所有模块的 Checkstyle 检查
./gradlew checkstyleMain

# 检查特定模块
./gradlew :ruoyi-auth:checkstyleMain

# 只运行 Checkstyle（不运行其他质量检查）
./gradlew checkstyleMain -x test -x pmdMain -x spotbugsMain
```

#### 检查报告

报告位置：`<module>/build/reports/checkstyle/main.html`

```bash
# 查看 HTML 报告
open ruoyi-auth/build/reports/checkstyle/main.html

# 查看 XML 报告（CI/CD 用）
cat ruoyi-auth/build/reports/checkstyle/main.xml
```

#### 常见违规和修复

**1. 行长度超过 100 字符**

```java
// 违规
String message = "This is a very long message that exceeds the maximum line length of 100 characters and should be broken into multiple lines";

// 修复
String message =
    "This is a very long message that exceeds the maximum line length of "
        + "100 characters and should be broken into multiple lines";
```

**2. 缺少 Javadoc**

```java
// 违规
public class UserService {
    public void deleteUser(Long userId) {
        // ...
    }
}

// 修复

/**
 * 用户服务类，提供用户管理功能。
 */
public class UserService {
    /**
     * 删除用户。
     *
     * @param userId 用户ID
     */
    public void deleteUser(Long userId) {
        // ...
    }
}
```

**3. import 语句顺序错误**

```java
// 违规

import java.util.List;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

// 修复（按字母顺序，标准库在前）
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
```

#### 抑制特定检查

使用 `@SuppressWarnings` 或注释抑制：

```java
// 使用注解抑制
@SuppressWarnings("checkstyle:MagicNumber")
public void processData() {
    int threshold = 100;  // 魔法数字
}

// 使用注释抑制
// CHECKSTYLE.SUPPRESS: LineLength for +1 lines
String veryLongUrl = "https://example.com/very/long/url/that/exceeds/100/characters/but/necessary/for/configuration";
```

#### IDE 集成

**IntelliJ IDEA**:

1. 安装插件: Settings → Plugins → 搜索 "Checkstyle-IDEA"
2. 配置规则: Settings → Tools → Checkstyle → Configuration File → Add
3. 选择配置文件: `config/checkstyle/google_checks.xml`
4. 运行检查: 右键项目 → Checkstyle → Check Project

---

### 4. PMD (静态代码分析)

#### 目的

检测常见的编程错误、未使用的代码、复杂的表达式、重复代码等潜在问题。

#### 配置位置

`build.gradle.kts` (lines 443-491):

```kotlin
plugins {
    id("pmd")
}

subprojects {
    plugins.withId("java") {
        if (!project.name.contains("demo") && !project.name.contains("example")) {
            apply(plugin = "pmd")

            configure<PmdExtension> {
                toolVersion = "7.9.0"
                sourceSets = listOf(project.extensions.getByType<SourceSetContainer>()["main"])
                ruleSetFiles = files(rootProject.file("config/pmd/ruleset.xml"))
                isIgnoreFailures = true
            }

            tasks.withType<Pmd> {
                reports {
                    html.required.set(true)
                    html.outputLocation.set(file("build/reports/pmd/main.html"))

                    xml.required.set(true)
                    xml.outputLocation.set(file("build/reports/pmd/main.xml"))
                }
            }
        }
    }
}
```

#### 规则集配置

配置文件: `config/pmd/ruleset.xml`

包含以下规则分类：

**最佳实践 (Best Practices)**:

- 避免使用 System.out.println()（应使用日志）
- 关闭资源（使用 try-with-resources）
- 避免过度使用字面量
- JUnit 测试规范

**代码风格 (Code Style)**:

- 命名规范
- 不必要的修饰符
- 过长的类/方法
- 注释规范

**设计 (Design)**:

- 避免深度嵌套
- 圈复杂度检查
- 类耦合度检查
- 数据类检查

**错误倾向 (Error Prone)**:

- 空指针检查
- 资源泄漏检查
- 字符串比较
- 异常处理

**多线程 (Multithreading)**:

- 线程安全问题
- 同步问题
- volatile 使用

**性能 (Performance)**:

- StringBuilder vs String 拼接
- 避免在循环中创建对象
- 集合使用优化

**安全 (Security)**:

- SQL 注入检查
- XSS 漏洞检查
- 硬编码密码检查

#### 运行 PMD

```bash
# 运行所有模块的 PMD 分析
./gradlew pmdMain

# 分析特定模块
./gradlew :ruoyi-auth:pmdMain

# 只运行 PMD（不运行其他检查）
./gradlew pmdMain -x test -x checkstyleMain -x spotbugsMain
```

#### 分析报告

报告位置：`<module>/build/reports/pmd/main.html`

```bash
# 查看 HTML 报告
open ruoyi-auth/build/reports/pmd/main.html
```

#### 常见问题和修复

**1. 避免使用 System.out**

```java
// 违规
System.out.println("User logged in: "+username);

// 修复
log.

info("User logged in: {}",username);
```

**2. 关闭资源**

```java
// 违规
FileInputStream fis = new FileInputStream("file.txt");
// ... 使用 fis
fis.

close();

// 修复（try-with-resources）
try(
FileInputStream fis = new FileInputStream("file.txt")){
    // ... 使用 fis
    }  // 自动关闭
```

**3. 避免字符串拼接在循环中**

```java
// 违规
String result = "";
for(
String item :items){
result +=item +",";
    }

// 修复
StringBuilder result = new StringBuilder();
for(
String item :items){
    result.

append(item).

append(",");
}
```

**4. 简化布尔表达式**

```java
// 违规
if(isValid ==true){
    return true;
    }else{
    return false;
    }

// 修复
    return isValid;
```

**5. 避免过度捕获异常**

```java
// 违规
try{
processData();
}catch(
Exception e){  // 捕获范围过大
    log.

error("Error",e);
}

// 修复
    try{

processData();
}catch(
IOException e){  // 捕获具体异常
    log.

error("IO error while processing data",e);
}
```

#### IDE 集成

**IntelliJ IDEA**:

1. 安装插件: Settings → Plugins → 搜索 "PMDPlugin"
2. 配置规则: Settings → PMD → RuleSets → Add
3. 选择规则文件: `config/pmd/ruleset.xml`
4. 运行分析: Tools → PMD → Scan Project with PMD

---

### 5. SpotBugs (Bug 检测)

#### 目的

使用静态分析检测潜在的 bug、性能问题、安全漏洞和多线程问题。

#### 配置位置

`build.gradle.kts` (lines 374-439):

```kotlin
plugins {
    id("com.github.spotbugs") version "6.0.26"
}

subprojects {
    plugins.withId("java") {
        if (!project.name.contains("demo") && !project.name.contains("example")) {
            apply(plugin = "com.github.spotbugs")

            configure<SpotBugsExtension> {
                toolVersion = "4.8.6"
                effort = Effort.DEFAULT
                reportLevel = Confidence.HIGH
                excludeFilter = rootProject.file("config/spotbugs/excludeFilter.xml")
                ignoreFailures = true
            }

            tasks.withType<SpotBugsTask> {
                sourceDirs = files(project.extensions.getByType<SourceSetContainer>()["main"].allSource.srcDirs)
                classDirs = files(project.extensions.getByType<SourceSetContainer>()["main"].output)

                reports.create("html") {
                    required.set(true)
                    outputLocation.set(file("build/reports/spotbugs/main.html"))
                    setStylesheet("fancy-hist.xsl")
                }

                reports.create("xml") {
                    required.set(true)
                    outputLocation.set(file("build/reports/spotbugs/main.xml"))
                }
            }

            dependencies {
                // fb-contrib: 额外的错误检测规则
                spotbugsPlugins("com.mebigfatguy.fb-contrib:fb-contrib:7.6.8")

                // find-sec-bugs: 安全漏洞检测
                spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.13.0")
            }
        }
    }
}
```

#### 排除过滤器配置

配置文件: `config/spotbugs/excludeFilter.xml`

排除规则包括：

**1. 排除测试代码**

```xml

<Match>
    <Source name="~.*[\\/]src[\\/]test[\\/]java[\\/].*"/>
</Match>
```

**2. 排除生成的代码**

- MapStruct 生成的 Mapper 实现
- Lombok 生成的代码 (Builder, getter/setter)

**3. 排除特定框架模式**

- Spring Bean 的字段注入（@Autowired, @Value）
- Spring 配置类的字段初始化
- 序列化相关警告

**4. 排除已知误报**

- 内部类可以是静态的（有时需要访问外部类）
- 字段应该是局部变量（Spring 注入字段）
- 循环复杂度（业务逻辑可能复杂）

完整的排除规则请查看 `config/spotbugs/excludeFilter.xml`。

#### SpotBugs 插件

**fb-contrib** (version 7.6.8):

- 额外的 400+ bug 模式检测
- 包括性能问题、风格问题、异常处理问题

**find-sec-bugs** (version 1.13.0):

- 专注于安全漏洞检测
- 包括 SQL 注入、XSS、加密问题、路径遍历等

#### 运行 SpotBugs

```bash
# 运行所有模块的 SpotBugs 分析
./gradlew spotbugsMain

# 分析特定模块
./gradlew :ruoyi-auth:spotbugsMain

# 只运行 SpotBugs（不运行其他检查）
./gradlew spotbugsMain -x test -x checkstyleMain -x pmdMain
```

#### 分析报告

报告位置：`<module>/build/reports/spotbugs/main.html`

```bash
# 查看 HTML 报告
open ruoyi-auth/build/reports/spotbugs/main.html
```

#### Bug 分类

SpotBugs 检测的 bug 分为以下优先级：

**High Priority (高优先级)**:

- 空指针解引用
- SQL 注入漏洞
- 不正确的同步
- 资源泄漏

**Medium Priority (中优先级)**:

- 可能的空指针
- 多线程问题
- 性能问题

**Low Priority (低优先级)**:

- 代码风格问题
- 命名问题
- 文档问题

当前配置只报告 **High Priority** 问题 (`reportLevel = Confidence.HIGH`)。

#### 常见问题和修复

**1. 空指针解引用 (NP_NULL_ON_SOME_PATH)**

```java
// 违规
public void processUser(User user) {
    String name = user.getName();  // user 可能为 null
}

// 修复
public void processUser(User user) {
    if (user == null) {
        return;
    }
    String name = user.getName();
}

// 或使用 Optional
public void processUser(Optional<User> userOpt) {
    userOpt.ifPresent(user -> {
        String name = user.getName();
    });
}
```

**2. SQL 注入 (SQL_INJECTION)**

```java
// 违规
String sql = "SELECT * FROM users WHERE username = '" + username + "'";
jdbcTemplate.

query(sql);

// 修复（使用参数化查询）
String sql = "SELECT * FROM users WHERE username = ?";
jdbcTemplate.

query(sql, username);
```

**3. 资源泄漏 (OS_OPEN_STREAM)**

```java
// 违规
FileInputStream fis = new FileInputStream("file.txt");
// ... 如果抛出异常，fis 不会被关闭

// 修复
try(
FileInputStream fis = new FileInputStream("file.txt")){
    // ... 使用 fis
    }  // 自动关闭
```

**4. 不正确的同步 (IS2_INCONSISTENT_SYNC)**

```java
// 违规
private int counter = 0;

public synchronized void increment() {
    counter++;
}

public int getCounter() {  // 没有同步
    return counter;
}

// 修复
public synchronized int getCounter() {
    return counter;
}

// 或使用 AtomicInteger
private AtomicInteger counter = new AtomicInteger(0);
```

**5. 暴露内部表示 (EI_EXPOSE_REP)**

```java
// 违规
private Date lastModified;

public Date getLastModified() {
    return lastModified;  // 返回可变对象的引用
}

// 修复
public Date getLastModified() {
    return new Date(lastModified.getTime());  // 返回副本
}

// 或使用不可变类型
private LocalDateTime lastModified;

public LocalDateTime getLastModified() {
    return lastModified;  // LocalDateTime 是不可变的
}
```

#### 抑制特定检查

使用 `@SuppressFBWarnings` 注解：

```java
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(
    value = "EI_EXPOSE_REP",
    justification = "This DTO is designed to expose the internal array for performance"
)
public byte[] getData() {
    return data;
}
```

或在 `excludeFilter.xml` 中添加规则。

#### IDE 集成

**IntelliJ IDEA**:

1. 安装插件: Settings → Plugins → 搜索 "SpotBugs"
2. 配置过滤器: Settings → SpotBugs → Filter → Exclude filter files → Add
3. 选择过滤文件: `config/spotbugs/excludeFilter.xml`
4. 运行分析: Tools → SpotBugs → Analyze Project Files

---

## 开发者工作流

### 日常开发流程

```bash
# 1. 拉取最新代码
git pull origin 2.X

# 2. 创建功能分支
git checkout -b feature/my-feature

# 3. 开发代码
# ... 编写代码 ...

# 4. 本地构建和测试
./gradlew build

# 5. 修复质量问题
# 查看报告，修复 Checkstyle、PMD、SpotBugs 发现的问题

# 6. 提交代码
git add .
git commit -m "feat: add my feature"

# 7. 推送到远程
git push origin feature/my-feature

# 8. 创建 Pull Request
# 等待 CI/CD 通过
```

### 快速开发模式

在开发过程中，可以跳过某些检查以加快构建速度：

```bash
# 跳过测试
./gradlew build -x test

# 跳过所有质量检查
./gradlew build -x test -x checkstyleMain -x pmdMain -x spotbugsMain

# 只运行代码格式化和编译
./gradlew spotlessApply compileJava

# 只运行测试
./gradlew test

# 快速打包（跳过检查）
./gradlew bootJar -x test -x check
```

### 只运行质量检查

```bash
# 运行所有质量检查（不重新编译）
./gradlew check

# 只运行 Checkstyle
./gradlew checkstyleMain

# 只运行 PMD
./gradlew pmdMain

# 只运行 SpotBugs
./gradlew spotbugsMain

# 只运行 Spotless 检查（不修复）
./gradlew spotlessCheck
```

### 修复质量问题的优先级

建议按以下顺序修复质量问题：

1. **SpotBugs High Priority** - 潜在的 bug 和安全漏洞，优先级最高
2. **单元测试失败** - 功能正确性问题
3. **PMD Error Prone** - 明显的错误倾向
4. **Checkstyle 违规** - 代码风格问题
5. **PMD Best Practices** - 最佳实践建议
6. **Spotless 格式** - 代码格式（可自动修复）

### 查看所有报告

```bash
# 生成所有报告后，使用脚本打开
find . -name "main.html" -path "*/reports/*" -exec open {} \;

# 或查看特定模块的所有报告
open ruoyi-auth/build/reports/tests/test/index.html
open ruoyi-auth/build/reports/checkstyle/main.html
open ruoyi-auth/build/reports/pmd/main.html
open ruoyi-auth/build/reports/spotbugs/main.html
```

---

## CI/CD 集成

### GitHub Actions 配置示例

```yaml
name: Build and Quality Check

on:
    push:
        branches: [ 2.X, develop ]
    pull_request:
        branches: [ 2.X ]

jobs:
    build:
        runs-on: ubuntu-latest

        steps:
            -   uses: actions/checkout@v3

            -   name: Set up JDK 21
                uses: actions/setup-java@v3
                with:
                    java-version: '21'
                    distribution: 'temurin'
                    cache: gradle

            -   name: Grant execute permission for gradlew
                run: chmod +x gradlew

            -   name: Build with Gradle
                run: ./gradlew clean build

            -   name: Upload Test Reports
                if: always()
                uses: actions/upload-artifact@v3
                with:
                    name: test-reports
                    path: '**/build/reports/tests/test/**'

            -   name: Upload Checkstyle Reports
                if: always()
                uses: actions/upload-artifact@v3
                with:
                    name: checkstyle-reports
                    path: '**/build/reports/checkstyle/**'

            -   name: Upload PMD Reports
                if: always()
                uses: actions/upload-artifact@v3
                with:
                    name: pmd-reports
                    path: '**/build/reports/pmd/**'

            -   name: Upload SpotBugs Reports
                if: always()
                uses: actions/upload-artifact@v3
                with:
                    name: spotbugs-reports
                    path: '**/build/reports/spotbugs/**'

            -   name: Publish Test Results
                if: always()
                uses: EnricoMi/publish-unit-test-result-action@v2
                with:
                    files: '**/build/test-results/test/*.xml'
```

### Jenkins Pipeline 配置示例

```groovy
pipeline {
    agent any

    tools {
        jdk 'JDK-21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }

        stage('Publish Reports') {
            steps {
                // JUnit 测试报告
                junit '**/build/test-results/test/*.xml'

                // Checkstyle 报告
                recordIssues(
                    enabledForFailure: true,
                    tool: checkStyle(pattern: '**/build/reports/checkstyle/*.xml')
                )

                // PMD 报告
                recordIssues(
                    enabledForFailure: true,
                    tool: pmdParser(pattern: '**/build/reports/pmd/*.xml')
                )

                // SpotBugs 报告
                recordIssues(
                    enabledForFailure: true,
                    tool: spotBugs(pattern: '**/build/reports/spotbugs/*.xml')
                )
            }
        }
    }

    post {
        always {
            // 清理工作空间
            cleanWs()
        }
    }
}
```

### GitLab CI 配置示例

```yaml
image: openjdk:21-jdk

stages:
    - build
    - test
    - quality

variables:
    GRADLE_OPTS: "-Dorg.gradle.daemon=false"

before_script:
    - chmod +x gradlew

build:
    stage: build
    script:
        - ./gradlew clean compileJava
    artifacts:
        paths:
            - build/classes
        expire_in: 1 hour

test:
    stage: test
    script:
        - ./gradlew test
    artifacts:
        when: always
        reports:
            junit: '**/build/test-results/test/TEST-*.xml'
        paths:
            - '**/build/reports/tests/test/**'
        expire_in: 1 week

quality:
    stage: quality
    script:
        - ./gradlew check -x test
    artifacts:
        when: always
        paths:
            - '**/build/reports/checkstyle/**'
            - '**/build/reports/pmd/**'
            - '**/build/reports/spotbugs/**'
        expire_in: 1 week
```

### SonarQube 集成

如果使用 SonarQube 进行代码质量管理：

```kotlin
// build.gradle.kts 添加 SonarQube 插件
plugins {
    id("org.sonarqube") version "4.4.1.3373"
}

sonarqube {
    properties {
        property("sonar.projectKey", "ruoyi-cloud-plus")
        property("sonar.projectName", "RuoYi-Cloud-Plus")
        property("sonar.host.url", "https://sonarqube.example.com")
        property("sonar.login", System.getenv("SONAR_TOKEN"))

        // 导入外部报告
        property("sonar.java.checkstyle.reportPaths", "**/build/reports/checkstyle/*.xml")
        property("sonar.java.pmd.reportPaths", "**/build/reports/pmd/*.xml")
        property("sonar.java.spotbugs.reportPaths", "**/build/reports/spotbugs/*.xml")
        property("sonar.coverage.jacoco.xmlReportPaths", "**/build/reports/jacoco/test/jacocoTestReport.xml")
    }
}
```

运行 SonarQube 分析：

```bash
./gradlew clean build sonarqube \
  -Dsonar.host.url=https://sonarqube.example.com \
  -Dsonar.login=$SONAR_TOKEN
```

---

## 常见问题

### Q1: 为什么构建不会因质量检查失败而中断？

A: 所有质量检查工具都配置了 `ignoreFailures = true`，原因：

- **渐进式改进**: 项目可能存在历史遗留问题，逐步修复比一次性修复更现实
- **不阻塞开发**: 开发者可以先实现功能，再修复质量问题
- **灵活性**: 某些警告可能是误报或在特定场景下可接受

**建议**:

- 在 CI/CD 中可以设置质量门禁，超过阈值则构建失败
- 新代码应该遵守所有质量规则
- 定期清理技术债务

### Q2: Spotless 和 Checkstyle 有什么区别？

A:

- **Spotless**: 自动格式化工具，可以自动修复问题（空格、缩进、换行、导入排序等）
- **Checkstyle**: 代码风格检查工具，只报告问题，不自动修复（命名规范、Javadoc、代码结构等）

两者配合使用：Spotless 负责自动化的格式问题，Checkstyle 负责需要人工判断的风格问题。

### Q3: PMD 和 SpotBugs 有什么区别？

A:

- **PMD**: 基于源代码的静态分析，检查代码风格、最佳实践、代码复杂度等
- **SpotBugs**: 基于字节码的静态分析，检查潜在的 bug、安全漏洞、性能问题等

两者互补：PMD 更关注代码质量和风格，SpotBugs 更关注 bug 和安全性。

### Q4: 如何禁用特定模块的质量检查？

A: 在模块的 `build.gradle.kts` 中配置：

```kotlin
// 禁用所有质量检查
tasks.named("check") {
    enabled = false
}

// 禁用特定检查
tasks.named("checkstyleMain") {
    enabled = false
}
```

或在构建时跳过：

```bash
./gradlew build -x checkstyleMain
```

### Q5: 质量检查运行很慢，如何优化？

A: 优化建议：

1. **使用 Gradle Daemon**:

```bash
# Daemon 会在后台保持 JVM 运行，加快后续构建
./gradlew build  # 默认启用 daemon
```

2. **增量构建**:

```bash
# 只检查变更的文件
./gradlew build --parallel --build-cache
```

3. **调整 PMD 配置**:

```kotlin
configure<PmdExtension> {
    isIncrementalAnalysis = true  // 启用增量分析
}
```

4. **并行执行**:

```kotlin
// gradle.properties
org.gradle.parallel = true
org.gradle.workers.max = 4
```

5. **跳过不必要的检查**:

```bash
# 开发时跳过 SpotBugs（最慢）
./gradlew build -x spotbugsMain
```

### Q6: 如何处理大量遗留代码的质量问题？

A: 渐进式改进策略：

1. **新代码零容忍**: 新提交的代码必须通过所有质量检查
2. **修改代码时修复**: 修改文件时顺便修复质量问题
3. **定期清理**: 每个迭代分配时间修复一定数量的质量问题
4. **优先修复**: 先修复高优先级问题（SpotBugs High Priority）
5. **使用抑制**: 对于短期无法修复的问题，使用抑制机制，并添加 TODO

### Q7: IDE 中的检查结果和 Gradle 不一致？

A: 原因和解决方案：

1. **配置文件不同**: 确保 IDE 使用项目的配置文件（如 `config/checkstyle/google_checks.xml`）
2. **版本不同**: IDE 插件版本和 Gradle 配置的工具版本可能不同
3. **缓存问题**: 清理 IDE 缓存和 Gradle 缓存
4. **插件配置**: 参考本文档的 "IDE 集成" 部分正确配置插件

建议以 Gradle 构建结果为准。

### Q8: 如何查看特定文件的所有质量问题？

A:

```bash
# Checkstyle
./gradlew checkstyleMain | grep "UserService.java"

# PMD
./gradlew pmdMain
# 然后在 HTML 报告中搜索文件名

# SpotBugs
./gradlew spotbugsMain
# 然后在 HTML 报告中搜索文件名

# 或使用 IDE 插件直接在文件中查看
```

### Q9: 单元测试覆盖率要求是多少？

A: 本项目未强制要求覆盖率，但建议：

- **关键业务逻辑**: 80%+ 覆盖率
- **工具类和通用组件**: 90%+ 覆盖率
- **简单的 DTO/VO**: 可以不写测试
- **Controller 层**: 集成测试覆盖主要流程

可以添加 JaCoCo 插件来生成覆盖率报告：

```kotlin
// build.gradle.kts
plugins {
    id("jacoco")
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
```

### Q10: 如何在 commit 前自动运行质量检查？

A: 使用 Git Hooks：

```bash
# 创建 pre-commit hook
cat > .git/hooks/pre-commit << 'EOF'
#!/bin/bash
echo "Running quality checks..."
./gradlew spotlessApply checkstyleMain pmdMain spotbugsMain -x test

if [ $? -ne 0 ]; then
  echo "Quality checks failed. Please fix the issues before committing."
  exit 1
fi
EOF

chmod +x .git/hooks/pre-commit
```

或使用 [Husky](https://github.com/typicode/husky) (需要 Node.js) 或 [pre-commit](https://pre-commit.com/)。

---

## 总结

RuoYi-Cloud-Plus 使用多层次的代码质量保障体系：

1. **Spotless** - 自动格式化，确保代码风格一致
2. **Checkstyle** - 检查 Google Java Style 规范
3. **PMD** - 检测代码异味和潜在问题
4. **SpotBugs** - 检测 bug 和安全漏洞
5. **JUnit 5** - 单元测试，确保功能正确

所有工具都已集成到 Gradle 构建流程中，在执行 `./gradlew clean build` 时自动运行。

**核心原则**：

- ✅ 新代码必须通过所有质量检查
- ✅ 遗留代码逐步改进
- ✅ 优先修复高优先级问题
- ✅ 使用 IDE 集成提高开发效率
- ✅ CI/CD 中设置质量门禁

**推荐工作流**：

1. 使用 IDE 插件实时检查
2. 提交前运行 `./gradlew build`
3. 修复所有质量问题
4. 提交代码并等待 CI/CD 验证

通过遵循这些质量标准，我们可以维护一个高质量、可维护的代码库。

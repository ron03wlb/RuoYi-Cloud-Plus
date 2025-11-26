# Spotless 代码格式化

本项目已集成 [Spotless](https://github.com/diffplug/spotless) 代码格式化工具，确保代码风格的一致性。

## 格式化标准

### Java 代码

- **格式化器**: Google Java Format (AOSP 变体)
- **缩进**: 2 空格
- **导入顺序**: 自动排序，移除未使用的导入
- **行尾**: LF (Unix 风格)

### Gradle Kotlin DSL

- **格式化器**: ktlint 1.0.1
- **特殊配置**: 禁用行内注释位置检查

### YAML 文件

- **处理**: 基本空白字符处理
- **缩进**: 2 空格
- **行尾**: LF

### XML 文件

- **处理**: 基本空白字符处理
- **缩进**: 2 空格
- **行尾**: LF

## 使用方式

### 手动格式化

```bash
# 检查所有文件的格式化问题
./gradlew spotlessCheck

# 自动格式化所有文件
./gradlew spotlessApply

# 只格式化 Java 文件
./gradlew spotlessJavaApply

# 只格式化 Kotlin Gradle 文件
./gradlew spotlessKotlinGradleApply
```

### 自动格式化

#### 构建时自动格式化

项目已配置为在编译 Java 代码时自动运行 `spotlessApply`。这意味着：

```bash
# 编译时会自动格式化代码
./gradlew build
./gradlew compileJava
```

#### Git Pre-commit Hook

推荐安装 Git pre-commit hook，在每次提交前自动格式化代码：

```bash
# 安装 Git hooks
./git-hooks/install-hooks.sh
```

安装后，每次 `git commit` 时会自动格式化暂存的文件。

**卸载 hook**:

```bash
# 删除 pre-commit hook
rm .git/hooks/pre-commit
```

**临时禁用 hook**:

```bash
# 重命名 hook 文件
mv .git/hooks/pre-commit .git/hooks/pre-commit.disabled
```

## 配置文件

### build.gradle.kts

Spotless 的主要配置位于根项目的 `build.gradle.kts` 文件中，包括：

- 格式化器版本
- 目标文件模式
- 排除规则
- 特定格式化选项

### .editorconfig

Java 文件的缩进已更新为 2 空格，以匹配 Google Java Format 标准。

## 注意事项

1. **首次运行**: 首次运行 `spotlessApply` 可能会格式化大量文件，建议单独提交这些格式化更改。

2. **IDE 集成**: 建议配置 IDE 使用相同的格式化标准：
    - IntelliJ IDEA: 安装 "google-java-format" 插件
    - VSCode: 安装 "Language Support for Java" 并配置 Google Java Format

3. **排除文件**: 如需排除特定文件或目录，请修改 `build.gradle.kts` 中的 `targetExclude` 配置。

4. **CI/CD**: 建议在 CI/CD 流程中添加 `./gradlew spotlessCheck`，确保所有提交的代码都符合格式化标准。

## 故障排除

### Spotless 检查失败

如果 `spotlessCheck` 失败，运行以下命令查看详细信息：

```bash
./gradlew spotlessCheck --info
```

然后运行 `spotlessApply` 修复问题：

```bash
./gradlew spotlessApply
```

### 格式化冲突

如果格式化后的代码与您的风格不符，可以：

1. 调整 `build.gradle.kts` 中的 Spotless 配置
2. 在特定代码块上使用 `// spotless:off` 和 `// spotless:on` 注释临时禁用格式化

### Git Hook 问题

如果 pre-commit hook 导致问题，可以临时跳过：

```bash
git commit --no-verify
```

## 更多信息

- [Spotless 官方文档](https://github.com/diffplug/spotless)
- [Google Java Format 指南](https://google.github.io/styleguide/javaguide.html)
- [ktlint 文档](https://pinterest.github.io/ktlint/)

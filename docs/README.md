# RuoYi-Cloud-Plus 文档中心

> 最后更新: 2025-11-29
> 新手开发者快速上手指南

---

## 新手 3 步走

### 第 1 步: 5分钟快速开始 ⏱️

阅读 [快速开始指南](getting-started/quick-start.md)，了解项目基本信息和核心命令。

### 第 2 步: 10分钟搭建环境 ⚙️

按照 [环境搭建指南](getting-started/environment-setup.md) 完成以下步骤：

1. 启动基础设施 (Docker)
2. 初始化数据库
3. 导入 Nacos 配置
4. 启动服务

### 第 3 步: 2分钟运行测试 ✅

参考 [测试基础](getting-started/testing-basics.md)，运行项目测试确保环境正常。

---

## 文档导航

### 新手入门

| 文档                                           | 说明           | 时长   |
|----------------------------------------------|--------------|------|
| [快速开始](getting-started/quick-start.md)       | 项目概览、核心命令    | 5分钟  |
| [环境搭建](getting-started/environment-setup.md) | 完整的开发环境初始化步骤 | 10分钟 |
| [测试基础](getting-started/testing-basics.md)    | 运行测试、编写测试    | 2分钟  |

### 操作指南

| 指南                                          | 说明                         |
|---------------------------------------------|----------------------------|
| [Nacos 配置导入](guides/nacos-config-import.md) | 自动/手动导入配置                  |
| [数据库初始化](guides/database-initialization.md) | PostgreSQL 初始化             |
| [服务启动顺序](guides/service-startup-order.md)   | 服务依赖和启动顺序                  |
| [代码质量](guides/code-quality.md)              | Checkstyle、SpotBugs、PMD 使用 |
| [测试指南](guides/testing-guide.md)             | 测试框架和最佳实践                  |

### 配置说明

| 文档                                                               | 说明                     |
|------------------------------------------------------------------|------------------------|
| [PostgreSQL 迁移](configuration/postgresql-migration.md)           | 从 MySQL 迁移到 PostgreSQL |
| [Logback Nacos 配置](configuration/logback-nacos-configuration.md) | 日志配置                   |
| [Gradle vs Maven](configuration/gradle-vs-maven.md)              | 构建工具对比                 |

### 架构设计

| 文档                                             | 说明           |
|------------------------------------------------|--------------|
| [项目概览](architecture/claude.md)                 | 完整的项目架构和开发指南 |
| [Gradle 构建](architecture/gradle.md)            | Gradle 配置和命令 |
| [Docker 部署](architecture/docker-deployment.md) | Docker 部署指南  |
| [开发环境](architecture/development-setup.md)      | 详细环境搭建说明     |

### 测试文档

| 文档                                          | 说明         |
|---------------------------------------------|------------|
| [测试概览](testing/README.md)                   | 测试整体情况     |
| [当前状态](testing/current-status.md)           | 测试覆盖率和质量指标 |
| [单元测试指南](testing/unit-test-guide.md)        | 如何编写单元测试   |
| [集成测试指南](testing/integration-test-guide.md) | 如何编写集成测试   |

---

## 常用命令

### 构建和运行

```bash
# 构建项目
./gradlew build -x test

# 运行测试
./gradlew test

# 启动服务 (在 IntelliJ IDEA 中使用 .run 配置)
# 或使用 Gradle
./gradlew bootRun
```

### Docker 快速启动

```bash
# 启动基础设施 (PostgreSQL, Redis, Nacos, MinIO)
cd script/docker
./dev-start.sh

# 构建并启动所有服务
./build-and-deploy.sh all
```

---

## 贡献指南

### 提交代码前

1. 运行代码质量检查: `./gradlew checkstyleMain spotlessCheck`
2. 运行所有测试: `./gradlew test`
3. 确保测试通过率 > 95%

### 文档更新

- 新功能需要更新对应的文档
- 重大变更需要更新 [项目概览](architecture/claude.md)

---

## 历史文档

如果你需要查看测试历史报告或归档文档，请访问 [archive](archive/) 目录。

**注意**: archive/ 目录主要用于历史追溯，**新手开发可以安全地忽略这个目录**。

---

## 获取帮助

- 查看 [常见问题](guides/README.md)
- 阅读 [项目概览](architecture/claude.md) 了解详细架构
- 访问 [官方文档](https://plus-doc.dromara.org)

---

**文档结构**: 新手优先 | 快速开始为主 | 历史归档分离
**维护**: 每个重要变更后更新 | [变更记录](archive/)

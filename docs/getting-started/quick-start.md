# 快速开始

欢迎来到 RuoYi-Cloud-Plus！本指南将在 5 分钟内让您快速开始使用这个强大的微服务平台。

## 项目简介

RuoYi-Cloud-Plus 是一个基于 Spring Cloud 的**开源微服务通用权限管理系统**，是对原 RuoYi-Cloud
的全方位升级和重写。它提供了一整套完整的企业级解决方案，包括权限管理、微服务架构、工作流支持等功能。

### 核心特性

- **微服务架构**: 基于 Spring Cloud 和 Alibaba Nacos 的分布式服务治理
- **权限管理**: 采用 Sa-Token 和 JWT，支持灵活的权限认证和授权
- **RPC 远程调用**: 使用 Apache Dubbo 3.X，提供高效的服务间通信
- **工作流引擎**: 支持复杂审批流程、转办、委派、加减签等功能
- **多数据源支持**: 支持 MySQL、PostgreSQL 等多种数据库的异构切换
- **分布式缓存**: 基于 Redis 的分布式缓存解决方案
- **消息队列**: 支持 Kafka、RocketMQ、RabbitMQ 等消息中间件
- **代码生成**: 一键生成 CRUD 代码和页面，降低 80% 的开发量

## 技术栈

| 技术项        | 版本信息            | 说明             |
|------------|-----------------|----------------|
| **JDK**    | 17 / 21         | Java 开发工具包     |
| **Spring** | Spring Boot 3.4 | 现代化的 Java 开发框架 |
| **微服务**    | Spring Cloud    | 分布式微服务架构       |
| **注册中心**   | Nacos           | 服务注册与发现        |
| **RPC 框架** | Dubbo 3.X       | 分布式服务调用        |
| **ORM**    | MyBatis-Plus    | 高效的持久化框架       |
| **权限**     | Sa-Token        | 轻量级权限认证框架      |
| **缓存**     | Redis 5-7       | 分布式缓存存储        |
| **文件存储**   | MinIO           | 分布式文件存储        |
| **工作流**    | Warm-Flow       | 流程审批引擎         |
| **消息队列**   | Kafka/RocketMQ  | 异步消息处理         |

## 5 分钟快速命令

### 1. 克隆项目

```bash
# 使用 Gitee (国内推荐)
git clone https://gitee.com/dromara/RuoYi-Cloud-Plus.git

# 或使用 GitHub
git clone https://github.com/dromara/RuoYi-Cloud-Plus.git

# 进入项目目录
cd RuoYi-Cloud-Plus
```

### 2. 切换到开发分支

```bash
# 查看可用分支
git branch -a

# 切换到 2.X 分支
git checkout 2.X
```

### 3. 构建项目

```bash
# 使用 Gradle 构建（推荐，速度更快）
./gradlew build

# 或使用 Maven 构建
mvn clean install

# 仅编译不运行测试
./gradlew build -x test
mvn clean install -DskipTests
```

### 4. 启动基础设施（Docker）

```bash
# 如果已安装 Docker，可一键启动所有依赖服务
# 进入 docker 目录
cd docker

# 启动 Nacos、MySQL、Redis、MinIO 等服务
docker-compose up -d
```

### 5. 启动第一个服务

```bash
# 启动 Auth 服务（权限认证服务）
cd ruoyi-auth
java -jar target/ruoyi-auth-*.jar

# 或在 IDE 中右键运行 AuthApplication.java
```

验证启动成功：

- Nacos 控制台: http://localhost:8848/nacos
- Auth 服务: http://localhost:9200/doc.html

## 下一步建议

1. **了解项目结构**: 阅读 [项目架构文档](/docs/architecture/README.md)
2. **配置开发环境**: 查看 [环境设置指南](/docs/getting-started/environment-setup.md)
3. **学习权限系统**: 了解 Sa-Token 的基本用法
4. **编写第一个测试**: 学习 [测试基础](/docs/getting-started/testing-basics.md)
5. **查看代码示例**: 在 `ruoyi-system` 模块中查看实际业务实现
6. **阅读官方文档**: https://plus-doc.dromara.org

## 常见问题

**Q: 项目需要什么版本的 JDK？**
A: 推荐使用 JDK 17 或 JDK 21，不支持 JDK 8 或更低版本。

**Q: 可以在 Windows 上开发吗？**
A: 可以。虽然我们推荐使用 macOS 或 Linux，但 Windows 也完全支持。

**Q: 如何快速启动所有服务？**
A: 使用 Docker Compose 是最快的方式。详见 [环境设置指南](/docs/getting-started/environment-setup.md)。

**Q: 在哪里找到项目文档？**
A: 官方文档: https://plus-doc.dromara.org，国内镜像: https://plus-doc.top

## 获取帮助

- 官方文档: https://plus-doc.dromara.org
- Gitee 仓库: https://gitee.com/dromara/RuoYi-Cloud-Plus
- 提交 Issue: https://gitee.com/dromara/RuoYi-Cloud-Plus/issues
- 加入交流群: 详见项目 README

---

**下一步**: 完成环境设置后，按照 [环境设置指南](/docs/getting-started/environment-setup.md) 继续操作。

# 项目文档

本目录包含 RuoYi-Cloud-Plus 项目的核心文档。

---

## 文档列表

### 核心指南

| 文档 | 说明 | 推荐度 |
|------|------|--------|
| [**CLAUDE.md**](./CLAUDE.md) | Claude Code 项目指南 - 项目概述、架构原则、开发模式、最佳实践 | ⭐⭐⭐⭐⭐ |
| [**GRADLE.md**](./GRADLE.md) | Gradle 构建完整指南 - 快速开始、命令对照、配置说明、常见问题 | ⭐⭐⭐⭐⭐ |
| [**DEVELOPMENT-SETUP.md**](./DEVELOPMENT-SETUP.md) | 开发环境初始化指南 - 环境要求、基础设施启动、服务启动、验证步骤（中文） | ⭐⭐⭐⭐ |

### 专题文档

| 文档 | 说明 |
|------|------|
| [**DOCKER-DEPLOYMENT.md**](./DOCKER-DEPLOYMENT.md) | Docker 部署专题 - 开发模式、完整部署、故障排查、最佳实践 |

---

## 快速导航

| 我想... | 查看文档 |
|---------|---------|
| 快速了解项目架构和开发规范 | [CLAUDE.md](./CLAUDE.md) |
| 使用 Gradle 构建项目 | [GRADLE.md](./GRADLE.md) |
| 初始化开发环境（中文） | [DEVELOPMENT-SETUP.md](./DEVELOPMENT-SETUP.md) |
| Docker 部署项目 | [DOCKER-DEPLOYMENT.md](./DOCKER-DEPLOYMENT.md) |
| 具体操作步骤（Nacos、数据库等） | [操作指南](../guides/) |
| 测试相关文档 | [测试文档中心](../) |

---

## 文档概览

### CLAUDE.md

**适合**: 所有开发者，特别是新加入项目的成员

**内容**:
- 项目概述和技术栈
- 项目结构
- 架构原则（插件化、服务通信、数据架构）
- 构建和开发命令
- Docker 部署
- 配置管理
- 开发模式和最佳实践

### GRADLE.md

**适合**: 需要了解 Gradle 构建系统的开发者

**内容**:
- Gradle 快速开始
- 配置文件清单
- Gradle vs Maven 命令对照
- 关键配置说明
- 常见问题解答
- 迁移路线图

### DEVELOPMENT-SETUP.md（中文）

**适合**: 首次搭建开发环境的开发者

**内容**:
- 环境要求（JDK、Maven、Docker）
- 基础设施启动
- 数据库初始化
- Nacos 配置导入
- 业务服务启动
- 验证步骤
- 常见问题

### DOCKER-DEPLOYMENT.md

**适合**: 需要使用 Docker 部署的开发者/运维

**内容**:
- 部署模式对比（开发模式 vs 完整部署）
- 基础设施和业务服务部署
- 常用命令
- 端口说明
- 故障排查
- 最佳实践

---

## 相关资源

### 操作指南

- [Nacos 配置导入](../guides/nacos-config-import.md)
- [数据库初始化](../guides/database-initialization.md)
- [服务启动顺序](../guides/service-startup-order.md)

### 测试文档

- [测试文档中心](../)
- [测试状态总览](../ACTIVE/TESTING-MASTER-STATUS.md)

### 外部资源

- [官方文档](https://plus-doc.dromara.org)
- [前端仓库](https://gitee.com/JavaLionLi/plus-ui)
- [项目仓库](https://gitee.com/JavaLionLi/RuoYi-Cloud-Plus)

---

**最后更新**: 2025-11-18

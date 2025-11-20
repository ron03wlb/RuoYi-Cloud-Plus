# Docker 本地构建部署指南

> **注意：本文档已整合到统一的文档系统中。**

本目录包含了从本地源码构建和部署 RuoYi-Cloud-Plus 的 Docker 配置文件。

完整的 Docker 部署指南请参阅：**[Docker 部署指南](../../docs/project/docker-deployment.md)**

---

## 快速参考

### 开发模式（推荐）

基础设施在 Docker，业务服务在 IDE：

```bash
cd script/docker

# 使用官方 Nacos（推荐，最快）
./dev-start.sh

# 按照提示导入 Nacos 配置并在 IDE 中运行服务
```

### 完整部署

所有服务在 Docker：

```bash
cd script/docker
./build-and-deploy.sh all
```

---

## 文件说明

### Docker Compose 配置文件

| 文件                             | 说明          |
|--------------------------------|-------------|
| `docker-compose.yml`           | 生产部署（远程镜像）  |
| `docker-compose-build.yml`     | 本地构建（从源码构建） |
| `docker-compose-dev-infra.yml` | 开发环境（仅基础设施） |

### Shell 脚本

| 脚本                       | 说明            |
|--------------------------|---------------|
| `dev-start.sh`           | 开发环境快速启动      |
| `build-and-deploy.sh`    | 自动化构建和部署      |
| `import-nacos-config.sh` | 自动导入 Nacos 配置 |

---

## 服务端口

### 基础设施
- PostgreSQL: 5432
- MySQL (Nacos): 3306
- Redis: 6379
- Nacos: 8848
- MinIO: 9000/9001

### 业务服务
- Gateway: 8080
- Auth: 9210
- System: 9201
- Gen: 9202
- Job: 9203
- Resource: 9204
- Workflow: 9205

---

## 详细文档

完整的部署指南、配置说明、故障排查等内容请参阅：

- **[Docker 部署指南](../../docs/project/docker-deployment.md)** - Docker 部署完整指南
- **[开发环境初始化](../../docs/project/development-setup.md)** - 开发环境设置
- **[Nacos 配置导入](../../docs/guides/nacos-config-import.md)** - Nacos 配置详细说明
- **[数据库初始化](../../docs/guides/database-initialization.md)** - 数据库初始化指南
- **[服务启动顺序](../../docs/guides/service-startup-order.md)** - 服务依赖和启动顺序

---

## 相关资源

- 项目主文档: [README.md](../../README.md)
- Claude Code 指南: [docs/project/claude.md](../../docs/project/claude.md)
- 官方文档: https://plus-doc.dromara.org

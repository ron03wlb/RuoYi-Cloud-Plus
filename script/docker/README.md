# Docker 脚本目录

本目录包含 Docker 相关的脚本和配置文件。

## 详细文档

请参阅：[Docker 部署指南](../../docs/project/DOCKER-DEPLOYMENT.md)

---

## 快速开始

### 开发模式（推荐）

基础设施在 Docker，业务服务在 IDE：

```bash
# 使用官方 Nacos（推荐，最快）
./dev-start.sh

# 或使用自定义 Nacos
./dev-start.sh --custom-nacos

# 按提示导入配置
./import-nacos-config.sh
```

### 完整部署

所有服务在 Docker：

```bash
./build-and-deploy.sh all
```

---

## 文件说明

### 脚本文件

| 脚本 | 说明 |
|------|------|
| `dev-start.sh` | 开发环境快速启动（推荐） |
| `build-and-deploy.sh` | 自动化构建和部署 |
| `import-nacos-config.sh` | 自动导入 Nacos 配置 |
| `maven-build-all.sh` | Maven 构建所有模块 |

### Docker Compose 配置

| 文件 | 说明 |
|------|------|
| `docker-compose.yml` | 生产部署（远程镜像） |
| `docker-compose-build.yml` | 本地构建（从源码构建） |
| `docker-compose-dev-infra.yml` | 开发环境（仅基础设施） |

---

## 常用命令

```bash
# 查看服务状态
./build-and-deploy.sh status

# 查看日志
./build-and-deploy.sh logs
./build-and-deploy.sh logs ruoyi-gateway

# 停止服务
./build-and-deploy.sh stop
```

---

## 端口速查

### 基础设施

- PostgreSQL: 5432
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

更多详情请查看 [Docker 部署指南](../../docs/project/DOCKER-DEPLOYMENT.md)。

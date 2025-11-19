# Docker 部署指南

本文档整合了 RuoYi-Cloud-Plus 项目的 Docker 部署相关内容。

---

## 目录

- [快速开始](#快速开始)
- [部署模式](#部署模式)
- [基础设施服务](#基础设施服务)
- [业务服务部署](#业务服务部署)
- [常用命令](#常用命令)
- [端口说明](#端口说明)
- [故障排查](#故障排查)
- [最佳实践](#最佳实践)

---

## 快速开始

### 开发模式（推荐用于日常开发）

基础设施运行在 Docker，业务服务运行在 IDE：

```bash
cd script/docker

# 使用官方 Nacos（推荐，最快）
./dev-start.sh

# 或使用自定义 Nacos（需要 Maven 构建）
./dev-start.sh --custom-nacos

# 按照提示完成：
# 1. 导入 Nacos 配置（./import-nacos-config.sh）
# 2. 初始化数据库
# 3. 在 IntelliJ IDEA 中运行业务服务
```

### 完整部署模式

所有服务运行在 Docker：

```bash
cd script/docker
./build-and-deploy.sh all
```

---

## 部署模式

### 开发模式 vs 完整部署

| 特性 | 开发模式 | 完整部署 |
|------|---------|---------|
| **启动方式** | `./dev-start.sh` | `./build-and-deploy.sh all` |
| **基础设施** | Docker | Docker |
| **业务服务** | IDE 运行 | Docker 运行 |
| **适用场景** | 日常开发、调试 | 集成测试、演示 |
| **启动速度** | 快（无需构建） | 慢（需要 Maven 构建） |
| **资源占用** | 少 | 多 |
| **调试便利性** | 高（IDE 断点） | 低 |

### Docker Compose 配置文件

- **docker-compose.yml** - 生产部署（远程镜像）
- **docker-compose-build.yml** - 本地构建（从源码构建）
- **docker-compose-dev-infra.yml** - 开发环境（仅基础设施）

---

## 基础设施服务

### 核心服务

| 服务 | 端口 | 默认账号 | 密码 |
|------|------|---------|------|
| PostgreSQL | 5432 | postgres | ruoyi123 |
| Redis | 6379 | - | - |
| Nacos | 8848 | nacos | nacos |
| MinIO | 9000/9001 | minioadmin | minioadmin |

### 启动基础设施

```bash
# 方式 1：使用开发脚本
./dev-start.sh

# 方式 2：使用部署脚本
./build-and-deploy.sh infra

# 方式 3：手动 Docker Compose
docker-compose -f docker-compose-build.yml up -d postgres redis nacos minio
```

### 验证基础设施

```bash
# 查看容器状态
docker-compose -f docker-compose-build.yml ps

# 访问控制台
# Nacos: http://localhost:8848/nacos
# MinIO: http://localhost:9001
```

---

## 业务服务部署

### 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| ruoyi-gateway | 8080 | API 网关 |
| ruoyi-auth | 9210 | 认证服务 |
| ruoyi-system | 9201 | 系统管理 |
| ruoyi-gen | 9202 | 代码生成 |
| ruoyi-job | 9203 | 任务调度 |
| ruoyi-resource | 9204 | 资源管理 |
| ruoyi-workflow | 9205 | 工作流 |
| ruoyi-monitor | 9100 | Spring Boot Admin |

### 启动业务服务

#### Docker 部署

```bash
# 启动所有业务服务
./build-and-deploy.sh services

# 启动单个服务
./build-and-deploy.sh ruoyi-gateway
./build-and-deploy.sh ruoyi-auth
./build-and-deploy.sh ruoyi-system
```

#### IDE 开发（推荐）

1. 在 IntelliJ IDEA 中打开项目
2. 使用 `.run/` 目录下的运行配置
3. 按顺序启动：Gateway → Auth → System → 其他服务

### 启动顺序

```
1. 基础设施: PostgreSQL, Redis, Nacos, MinIO
2. 核心服务: ruoyi-gateway → ruoyi-auth → ruoyi-system
3. 扩展服务: ruoyi-gen, ruoyi-job, ruoyi-resource, ruoyi-workflow
4. 监控服务: ruoyi-monitor (可选)
```

---

## 常用命令

### 构建和启动

```bash
# 构建并启动所有
./build-and-deploy.sh all

# 只启动基础设施
./build-and-deploy.sh infra

# 只启动业务服务
./build-and-deploy.sh services

# 启动单个服务
./build-and-deploy.sh ruoyi-gateway
```

### 查看状态和日志

```bash
# 查看所有服务状态
./build-and-deploy.sh status

# 查看所有日志
./build-and-deploy.sh logs

# 查看特定服务日志
./build-and-deploy.sh logs ruoyi-gateway
```

### 停止和清理

```bash
# 停止所有服务
./build-and-deploy.sh stop

# 停止并删除容器
docker-compose -f docker-compose-build.yml down

# 停止并删除容器及数据卷（危险！）
docker-compose -f docker-compose-build.yml down -v

# 删除构建的镜像
docker images | grep ruoyi | awk '{print $3}' | xargs docker rmi
```

### 重新构建

```bash
# 重新构建特定服务
cd ../..  # 项目根目录
mvn clean package -pl ruoyi-gateway -am

cd script/docker
docker-compose -f docker-compose-build.yml build ruoyi-gateway
docker-compose -f docker-compose-build.yml up -d ruoyi-gateway
```

---

## 端口说明

### 基础设施端口

| 服务 | 端口 | 说明 |
|------|------|------|
| PostgreSQL | 5432 | 数据库 |
| Redis | 6379 | 缓存 |
| Nacos | 8848, 9848, 9849 | 配置中心/注册中心 |
| MinIO | 9000, 9001 | 对象存储 API/控制台 |
| Seata | 7091, 8091 | 分布式事务 |
| SnailJob | 8800, 17888 | 任务调度 |

### 可选服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| Elasticsearch | 9200, 9300 | 搜索引擎 |
| Kibana | 5601 | 日志可视化 |
| RocketMQ | 9876, 10911, 19876 | 消息队列 |
| RabbitMQ | 5672, 15672 | 消息队列 |
| Kafka | 9092, 19092 | 消息队列 |
| Skywalking | 11800, 12800, 18080 | 链路追踪 |
| Prometheus | 9090 | 监控 |
| Grafana | 3000 | 监控面板 |

---

## 故障排查

### Docker 构建失败：找不到 JAR 文件

**错误信息:**
```
failed to compute cache key: "/target/ruoyi-*.jar": not found
```

**解决方案:**
```bash
# 方案 1: 使用自动化脚本
./build-and-deploy.sh all

# 方案 2: 手动构建
./maven-build-all.sh
docker-compose -f docker-compose-build.yml up -d --build
```

### Nacos 连接失败

1. 确认 Nacos 已启动: `docker ps | grep nacos`
2. 确认配置已导入: `./import-nacos-config.sh`
3. 检查网络: `docker network ls`

### 数据库连接失败

1. 确认 PostgreSQL 已启动
2. 确认数据库脚本已执行
3. 检查 Nacos 中的数据库配置

### 端口冲突

```bash
# 查看端口占用
lsof -i :8080

# 杀死进程
kill -9 <PID>

# 或修改 docker-compose-build.yml 中的端口映射
```

### 服务启动失败

```bash
# 查看服务日志
./build-and-deploy.sh logs ruoyi-gateway

# 检查服务状态
./build-and-deploy.sh status

# 重启服务
docker-compose -f docker-compose-build.yml restart ruoyi-gateway
```

---

## 最佳实践

### 开发环境

1. **使用开发模式**: 基础设施在 Docker，业务服务在 IDE
2. **自动导入配置**: 使用 `./import-nacos-config.sh`
3. **使用 IDEA 运行配置**: `.run/` 目录下的预配置

### 生产部署

1. **使用生产配置**: `MAVEN_PROFILE=prod ./build-and-deploy.sh all`
2. **配置持久化**: 使用 Docker Volume 持久化数据
3. **监控服务**: 启动 ruoyi-monitor, Prometheus, Grafana

### 资源优化

1. **调整 JVM 参数**: 在 Dockerfile 中配置 `-Xms` 和 `-Xmx`
2. **限制容器资源**: 在 docker-compose.yml 中配置 `mem_limit`
3. **使用多阶段构建**: 减少镜像大小

---

## 相关文档

- [claude.md](./claude.md) - 项目概述和开发指南
- [开发环境初始化](./development-setup.md) - 完整初始化步骤（中文）
- [Nacos 配置导入](../guides/nacos-config-import.md) - 配置导入详细指南
- [数据库初始化](../guides/database-initialization.md) - 数据库初始化指南
- [服务启动顺序](../guides/service-startup-order.md) - 服务依赖和启动顺序
- [官方文档](https://plus-doc.dromara.org) - 项目官方文档

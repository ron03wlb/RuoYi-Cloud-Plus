# Docker 本地构建部署指南

本目录包含了从本地源码构建和部署 RuoYi-Cloud-Plus 的 Docker 配置文件。

## 文件说明

### Docker Compose 配置文件

- **docker-compose.yml** - 生产部署配置（使用预构建的远程镜像）
- **docker-compose-build.yml** - 本地构建配置（从本地源码构建镜像，适合开发）
- **docker-compose-dev-infra.yml** - 开发环境基础设施配置（仅基础设施，使用官方镜像）

### Shell 脚本

- **build-and-deploy.sh** - 自动化构建和部署脚本（完整部署）
- **dev-start.sh** - 开发环境快速启动（推荐，支持官方/自定义 Nacos）
- **maven-build-all.sh** - Maven 构建所有模块
- **import-nacos-config.sh** - 自动导入 Nacos 配置

## 快速开始

### 推荐方式：使用开发环境脚本（最简单）

如果你只是想启动基础设施并在 IDE 中开发业务服务，推荐使用此方式：

```bash
cd script/docker

# 使用官方 Nacos 启动（推荐，无需构建，最快）
./dev-start.sh

# 或使用自定义 Nacos（需要 Maven 构建）
./dev-start.sh --custom-nacos

# 查看所有选项
./dev-start.sh --help

# 按照提示导入 Nacos 配置
# 然后在 IntelliJ IDEA 中运行业务服务
```

### 完整部署方式

如果需要在 Docker 中运行所有服务（包括业务服务），继续阅读下文。

### 1. 前置准备

确保已安装：
- Docker 和 Docker Compose
- JDK 17 或 21（仅完整部署需要）
- Maven 3.6+（仅完整部署需要）

### 2. 开发模式 vs 完整部署

#### 开发模式（推荐用于日常开发）

优点：
- 启动快速，默认无需 Maven 构建
- 可以在 IDE 中调试业务代码
- 资源占用少

使用脚本：
- `./dev-start.sh` - 默认使用官方 Nacos（最快）
- `./dev-start.sh --custom-nacos` - 使用自定义 Nacos（需要构建）

启动内容：
- 仅启动基础设施（MySQL, Redis, Nacos, MinIO）
- 业务服务在 IDE 中运行

#### 完整部署模式

优点：
- 所有服务在 Docker 中运行
- 更接近生产环境
- 适合集成测试

使用脚本：
- `./build-and-deploy.sh all`

启动内容：
- 基础设施 + 所有业务服务

### 3. 重要提示

**Docker 构建依赖 Maven 构建产物（JAR 文件）**

在使用 Docker Compose 构建镜像之前，必须先使用 Maven 构建项目生成 JAR 文件。Dockerfile 会从 `target/` 目录复制 JAR 文件到镜像中。

有两种方式处理：
1. **使用自动化脚本**（推荐）- `build-and-deploy.sh` 会自动检查并构建缺失的 JAR
2. **手动构建** - 先运行 `maven-build-all.sh`，再使用 Docker Compose

### 4. 完整部署流程

#### 方式一：开发环境快速启动（推荐用于开发）

```bash
# 进入 docker 目录
cd script/docker

# 启动基础设施
./dev-start.sh                    # 使用官方 Nacos（推荐，最快）
# 或
./dev-start.sh --custom-nacos     # 使用自定义 Nacos

# 按照提示操作：
# 1. 等待 Nacos 启动
# 2. 导入 Nacos 配置（可使用 ./import-nacos-config.sh 自动导入）
# 3. 初始化数据库
# 4. 在 IntelliJ IDEA 中运行业务服务
```

#### 方式二：使用自动化脚本完整部署（推荐用于测试）

```bash
# 进入 docker 目录
cd script/docker

# 构建并启动所有服务（会自动执行 Maven 构建）
./build-and-deploy.sh all

# 等待提示后，导入 Nacos 配置
# 访问 http://localhost:8848/nacos (nacos/nacos)
# 导入 script/config/nacos/ 目录下的所有配置文件
```

#### 方式三：分步部署

```bash
cd script/docker

# 1. 先启动基础设施
./build-and-deploy.sh infra

# 2. 等待 Nacos 启动完成（约30秒），然后导入配置
#    访问 http://localhost:8848/nacos
#    手动导入 script/config/nacos/ 下的配置文件

# 3. 启动业务服务
./build-and-deploy.sh services
```

#### 方式四：手动使用 Docker Compose

```bash
cd script/docker

# 1. 先构建所有 Maven 模块（重要！）
./maven-build-all.sh
# 或者手动执行
cd ../..
mvn clean package -P dev
cd script/docker

# 2. 启动基础设施
docker-compose -f docker-compose-build.yml up -d mysql redis nacos minio

# 3. 等待并导入 Nacos 配置

# 4. 启动业务服务（会自动构建 Docker 镜像）
docker-compose -f docker-compose-build.yml up -d --build \
  ruoyi-seata-server \
  ruoyi-snailjob-server \
  ruoyi-gateway \
  ruoyi-auth \
  ruoyi-system \
  ruoyi-gen \
  ruoyi-job \
  ruoyi-resource \
  ruoyi-workflow \
  ruoyi-monitor
```

## 常用命令

### Maven 构建

```bash
# 构建所有模块（使用辅助脚本）
./maven-build-all.sh

# 或手动构建
cd ../..
mvn clean package -P dev

# 构建特定模块
mvn clean package -pl ruoyi-gateway -am

# 使用生产环境配置构建
MAVEN_PROFILE=prod ./maven-build-all.sh
```

### 构建和启动

```bash
# 启动所有服务（首次会自动构建）
./build-and-deploy.sh all

# 只启动基础设施（MySQL, Redis, Nacos, MinIO）
./build-and-deploy.sh infra

# 只启动业务服务（需要先启动基础设施）
./build-and-deploy.sh services

# 构建并启动单个服务
./build-and-deploy.sh ruoyi-gateway
./build-and-deploy.sh ruoyi-auth
./build-and-deploy.sh ruoyi-system
```

### 查看状态和日志

```bash
# 查看所有服务状态
./build-and-deploy.sh status

# 查看所有服务日志
./build-and-deploy.sh logs

# 查看特定服务日志
./build-and-deploy.sh logs ruoyi-gateway
./build-and-deploy.sh logs nacos
```

### 停止服务

```bash
# 停止所有服务
./build-and-deploy.sh stop

# 停止特定服务
docker-compose -f docker-compose-build.yml stop ruoyi-gateway

# 停止并删除所有容器
docker-compose -f docker-compose-build.yml down

# 停止并删除所有容器及数据卷（危险操作！）
docker-compose -f docker-compose-build.yml down -v
```

### 重新构建单个服务

```bash
# 修改代码后重新构建特定服务
cd ../..  # 回到项目根目录
mvn clean package -pl ruoyi-gateway -am

cd script/docker
docker-compose -f docker-compose-build.yml build ruoyi-gateway
docker-compose -f docker-compose-build.yml up -d ruoyi-gateway
```

## 环境变量配置

可以通过环境变量控制构建行为：

```bash
# 使用生产环境配置构建
export MAVEN_PROFILE=prod
./build-and-deploy.sh all

# 或者直接在命令中指定
MAVEN_PROFILE=prod ./build-and-deploy.sh all
```

## Nacos 配置导入

**重要**: 首次启动后必须导入 Nacos 配置，否则业务服务无法正常启动。

### 自动导入（推荐）

```bash
cd script/docker
./import-nacos-config.sh
```

此脚本会自动将 `script/config/nacos/` 目录下的所有配置文件导入到 Nacos。

### 手动导入

1. 访问 Nacos 控制台: http://localhost:8848/nacos
2. 登录 (用户名: `nacos`, 密码: `nacos`)
3. 进入 "配置管理" -> "配置列表"
4. 点击 "导入配置"
5. 选择 `script/config/nacos/` 目录下的所有 `.yml` 文件
6. 确认导入

### 必需的配置文件

- `application-common.yml` - 所有服务的公共配置
- `ruoyi-gateway.yml` - 网关路由配置
- `ruoyi-auth.yml` - 认证服务配置
- `ruoyi-system.yml` - 系统服务配置
- `ruoyi-gen.yml` - 代码生成服务配置
- `ruoyi-job.yml` - 任务调度服务配置
- `ruoyi-resource.yml` - 资源服务配置
- `ruoyi-workflow.yml` - 工作流服务配置

## 数据库初始化

首次启动 MySQL 后，需要执行数据库初始化脚本：

```bash
# 连接到 MySQL 容器
docker exec -it mysql mysql -u root -pruoyi123

# 或者使用数据库客户端连接
# Host: localhost
# Port: 3306
# User: root
# Password: ruoyi123

# 执行 script/sql/ 目录下的 SQL 脚本
```

## 服务端口说明

### 基础设施
- MySQL: 3306
- Redis: 6379
- Nacos: 8848, 9848, 9849
- MinIO: 9000 (API), 9001 (Console)
- Seata: 7091, 8091
- SnailJob: 8800, 17888

### 业务服务
- Gateway: 8080
- Auth: 9210
- System: 9201
- Gen: 9202
- Job: 9203
- Resource: 9204
- Workflow: 9205
- Monitor: 9100

### 可选服务
- Elasticsearch: 9200, 9300
- Kibana: 5601
- Logstash: 4560
- RocketMQ NameServer: 9876
- RocketMQ Broker: 10911, 10909, 10912
- RocketMQ Console: 19876
- RabbitMQ: 5672, 15672
- Kafka: 9092
- Kafka Manager: 19092
- Skywalking OAP: 11800, 12800
- Skywalking UI: 18080
- Prometheus: 9090
- Grafana: 3000
- ShardingProxy: 3307

## 开发调试模式（推荐）

在 Docker 中运行基础设施，在 IDE 中运行业务服务（最适合日常开发）：

### 方式一：使用开发脚本（推荐）

```bash
# 使用官方 Nacos 启动基础设施（推荐，最快）
./dev-start.sh

# 或使用自定义 Nacos
./dev-start.sh --custom-nacos
```

### 方式二：使用完整部署脚本

```bash
# 只启动基础设施
./build-and-deploy.sh infra
```

### 后续步骤

1. 导入 Nacos 配置（自动）:
   ```bash
   ./import-nacos-config.sh
   ```

2. 初始化数据库：执行 `script/sql/` 下的 SQL 脚本

3. 在 IntelliJ IDEA 中运行业务服务：
   - 使用 `.run/` 目录下的运行配置
   - 推荐启动顺序：Gateway → Auth → System → 其他服务

### 优势

- 快速重启：修改代码后只需在 IDE 中重启对应服务
- 调试方便：可以直接在 IDE 中打断点调试
- 资源占用少：只运行必要的基础设施
- 启动快速：无需构建和启动所有 Docker 容器

## 故障排查

### Docker 构建失败：找不到 JAR 文件

**错误信息:**
```
failed to compute cache key: "/target/ruoyi-*.jar": not found
```

**原因:** Docker 构建时找不到 Maven 构建产物（JAR 文件）

**解决方案:**
```bash
# 方案 1: 使用自动化脚本（推荐）
./build-and-deploy.sh all  # 脚本会自动检查并构建

# 方案 2: 手动构建
./maven-build-all.sh  # 先构建所有 Maven 模块
docker-compose -f docker-compose-build.yml up -d --build  # 再启动 Docker

# 方案 3: 检查并重新构建
cd ../..
mvn clean package -P dev  # 确保所有模块都构建成功
cd script/docker
docker-compose -f docker-compose-build.yml build  # 重新构建 Docker 镜像
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

### Nacos 连接失败

1. 确认 Nacos 已启动: `docker ps | grep nacos`
2. 确认 Nacos 配置已导入
3. 检查网络连接: `docker network ls`

### 数据库连接失败

1. 确认 MySQL 已启动
2. 确认数据库脚本已执行
3. 检查 Nacos 中的数据库配置

### 端口冲突

如果遇到端口冲突，可以修改 `docker-compose-build.yml` 中的端口映射。

## 清理和重置

```bash
# 停止所有服务
./build-and-deploy.sh stop

# 删除所有容器
docker-compose -f docker-compose-build.yml down

# 删除所有容器和数据卷（会清空数据库！）
docker-compose -f docker-compose-build.yml down -v

# 删除构建的镜像
docker images | grep ruoyi | awk '{print $3}' | xargs docker rmi

# 清理 Maven 构建产物
cd ../..
mvn clean
```

## 相关文档

- 项目主文档: [README.md](../../README.md)
- Claude Code 指南: [CLAUDE.md](../../CLAUDE.md)
- 官方文档: https://plus-doc.dromara.org
- 初始化指南: https://plus-doc.dromara.org/#/ruoyi-cloud-plus/quickstart/init
- 部署指南: https://plus-doc.dromara.org/#/ruoyi-cloud-plus/quickstart/deploy

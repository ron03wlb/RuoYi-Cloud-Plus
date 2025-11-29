# RuoYi-Cloud-Plus 开发环境初始化指南

## 📋 目录

- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [详细步骤](#详细步骤)
    - [1. 基础环境准备](#1-基础环境准备)
    - [2. 基础设施启动](#2-基础设施启动)
    - [3. 数据库初始化](#3-数据库初始化)
    - [4. Nacos 配置导入](#4-nacos-配置导入)
    - [5. 业务服务启动](#5-业务服务启动)
- [验证步骤](#验证步骤)
- [常见问题](#常见问题)
- [附录](#附录)

---

## 环境要求

### 必需软件

| 软件             | 版本要求    | 说明                        |
|----------------|---------|---------------------------|
| JDK            | 17 或 21 | 推荐使用 OpenJDK 或 Oracle JDK |
| Maven          | 3.6+    | 用于项目构建                    |
| Docker         | 20.10+  | 用于运行基础设施服务                |
| Docker Compose | 1.29+   | 用于编排容器                    |
| Git            | 2.0+    | 版本控制                      |

### 推荐软件

- **IDE**: IntelliJ IDEA 2023+ (推荐旗舰版)
- **API 测试**: Postman、Apifox 或 Apipost
- **数据库客户端**: Navicat、DBeaver 或 DataGrip
- **Redis 客户端**: RedisInsight、Another Redis Desktop Manager

### 硬件要求

- **CPU**: 4核以上
- **内存**: 8GB+ (推荐16GB)
- **磁盘**: 20GB+ 可用空间

---

## 快速开始

如果您想快速启动开发环境，使用以下两种方式之一：

### 方式一：混合模式（推荐用于日常开发）

基础设施运行在 Docker，业务服务运行在 IDE：

```bash
# 1. 启动基础设施
cd script/docker
./dev-quick-start.sh

# 2. 按照提示完成：
#    - 访问 http://localhost:8848/nacos 导入配置
#    - 使用 script/sql/ 下的脚本初始化数据库
#    - 在 IntelliJ IDEA 中使用 .run/ 配置运行业务服务
```

### 方式二：完全 Docker 化

所有服务都运行在 Docker 容器中：

```bash
cd script/docker
./build-and-deploy.sh all
```

---

## 详细步骤

### 1. 基础环境准备

#### 1.1 安装 JDK

```bash
# macOS (使用 Homebrew)
brew install openjdk@17

# Linux (Ubuntu/Debian)
sudo apt update
sudo apt install openjdk-17-jdk

# 验证安装
java -version
```

#### 1.2 安装 Maven

```bash
# macOS
brew install maven

# Linux
sudo apt install maven

# 验证安装
mvn -version
```

#### 1.3 安装 Docker & Docker Compose

```bash
# macOS
brew install docker docker-compose

# Linux
# 参考官方文档: https://docs.docker.com/engine/install/

# 验证安装
docker --version
docker-compose --version
```

#### 1.4 克隆项目

```bash
git clone https://gitee.com/JavaLionLi/RuoYi-Cloud-Plus.git
cd RuoYi-Cloud-Plus
```

#### 1.5 配置 Maven Settings

确保 Maven 使用国内镜像加速（可选但推荐）：

编辑 `~/.m2/settings.xml`：

```xml
<mirrors>
    <mirror>
        <id>aliyun</id>
        <mirrorOf>central</mirrorOf>
        <name>Aliyun Maven</name>
        <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
</mirrors>
```

---

### 2. 基础设施启动

#### 2.1 使用快速启动脚本

```bash
cd script/docker
chmod +x dev-quick-start.sh
./dev-quick-start.sh
```

该脚本会启动：

- MySQL 5.7 (端口: 3306)
- Redis 6.2 (端口: 6379)
- Nacos 2.4.3 (端口: 8848)
- MinIO (端口: 9000, 控制台: 9001)

#### 2.2 手动启动（可选）

如果不使用脚本，可以手动启动：

```bash
cd script/docker
docker-compose -f docker-compose-dev-infra.yml up -d
```

#### 2.3 验证基础设施

```bash
# 查看容器状态
docker-compose -f docker-compose-dev-infra.yml ps

# 应该看到所有服务状态为 "Up"
```

访问各服务控制台验证：

| 服务            | 地址                          | 默认账号       | 密码         |
|---------------|-----------------------------|------------|------------|
| Nacos         | http://localhost:8848/nacos | nacos      | nacos      |
| MinIO Console | http://localhost:9001       | minioadmin | minioadmin |

---

### 3. 数据库初始化

#### 3.1 连接数据库

使用数据库客户端连接：

- **主机**: localhost
- **端口**: 3306
- **用户名**: root
- **密码**: password
- **数据库**: ry-cloud

#### 3.2 执行初始化脚本

根据您使用的数据库类型，执行对应的 SQL 脚本：

```bash
# 项目根目录
cd script/sql

# MySQL 用户执行
mysql/
├── ry-cloud.sql          # 主数据库脚本（必须）
├── quartz.sql            # 定时任务表（如使用 Quartz）
└── ry-config.sql         # 其他配置表
```

**执行方式：**

```bash
# 方式1: 命令行执行
mysql -h localhost -P 3306 -u root -p < mysql/ry-cloud.sql

# 方式2: 使用数据库客户端
# 在 Navicat/DBeaver 等工具中打开并执行 SQL 文件
```

#### 3.3 初始化示例模块（可选）

如果需要运行示例模块：

```bash
mysql -h localhost -P 3306 -u root -p < ruoyi-example/ruoyi-demo/test.sql
```

#### 3.4 验证数据库

确认以下数据库对象已创建：

```sql
-- 查看所有表
SHOW TABLES;

-- 应该看到以下核心表
-- sys_user, sys_role, sys_menu, sys_dept, sys_config
-- gen_table, gen_table_column
-- sys_oss, sys_oss_config
-- 等等...
```

---

### 4. Nacos 配置导入

#### 4.1 访问 Nacos 控制台

打开浏览器访问: http://localhost:8848/nacos

- **用户名**: nacos
- **密码**: nacos

#### 4.2 手动导入配置文件

进入 `配置管理` → `配置列表`，点击 `导入配置`：

需要导入的配置文件（位于 `script/config/nacos/`）：

| 配置文件                   | Data ID                | Group         | 说明       |
|------------------------|------------------------|---------------|----------|
| application-common.yml | application-common.yml | DEFAULT_GROUP | 所有服务共享配置 |
| ruoyi-gateway.yml      | ruoyi-gateway.yml      | DEFAULT_GROUP | 网关路由配置   |
| ruoyi-auth.yml         | ruoyi-auth.yml         | DEFAULT_GROUP | 认证服务配置   |
| ruoyi-system.yml       | ruoyi-system.yml       | DEFAULT_GROUP | 系统服务配置   |
| ruoyi-gen.yml          | ruoyi-gen.yml          | DEFAULT_GROUP | 代码生成服务配置 |
| ruoyi-job.yml          | ruoyi-job.yml          | DEFAULT_GROUP | 任务调度服务配置 |
| ruoyi-resource.yml     | ruoyi-resource.yml     | DEFAULT_GROUP | 资源服务配置   |
| ruoyi-workflow.yml     | ruoyi-workflow.yml     | DEFAULT_GROUP | 工作流服务配置  |

#### 4.3 使用脚本导入（推荐）

```bash
cd script/docker
chmod +x import-nacos-config.sh
./import-nacos-config.sh
```

#### 4.4 验证配置导入

在 Nacos 控制台的配置列表中，确认所有配置文件已导入：

```
配置列表应显示：
✓ application-common.yml
✓ ruoyi-gateway.yml
✓ ruoyi-auth.yml
✓ ruoyi-system.yml
✓ ruoyi-gen.yml
✓ ruoyi-job.yml
✓ ruoyi-resource.yml
✓ ruoyi-workflow.yml
```

#### 4.5 配置说明

**application-common.yml** - 关键配置项：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ry-cloud?...
    username: root
    password: password

  data:
    redis:
      host: localhost
      port: 6379
      password: # 如有密码请填写

# OSS 配置
oss:
  endpoint: http://localhost:9000
  accessKey: minioadmin
  secretKey: minioadmin
```

⚠️ **重要**: 如果修改了数据库密码或 Redis 密码，需要在此配置文件中同步修改！

---

### 5. 业务服务启动

#### 5.1 构建项目

```bash
# 在项目根目录执行
mvn clean install -DskipTests

# 或使用指定的 Maven 配置
mvn clean install -DskipTests -P dev
```

构建成功后，会在各模块的 `target/` 目录下生成 JAR 包。

#### 5.2 服务启动顺序

按以下顺序启动服务：

##### 第一层：核心服务

1. **ruoyi-gateway** (API 网关) - 端口 8080

```bash
cd ruoyi-gateway
mvn spring-boot:run

# 或使用 JAR
java -jar target/ruoyi-gateway.jar
```

2. **ruoyi-auth** (认证服务) - 端口 9210

```bash
cd ruoyi-auth
mvn spring-boot:run
```

##### 第二层：基础业务服务

3. **ruoyi-system** (系统管理) - 端口 9201

```bash
cd ruoyi-modules/ruoyi-system
mvn spring-boot:run
```

##### 第三层：扩展服务（可选）

4. **ruoyi-gen** (代码生成) - 端口 9202

```bash
cd ruoyi-modules/ruoyi-gen
mvn spring-boot:run
```

5. **ruoyi-job** (任务调度) - 端口 9203

```bash
cd ruoyi-modules/ruoyi-job
mvn spring-boot:run
```

6. **ruoyi-resource** (资源管理) - 端口 9204

```bash
cd ruoyi-modules/ruoyi-resource
mvn spring-boot:run
```

7. **ruoyi-workflow** (工作流) - 端口 9205

```bash
cd ruoyi-modules/ruoyi-workflow
mvn spring-boot:run
```

#### 5.3 使用 IntelliJ IDEA 启动（推荐）

项目已配置好 IDEA 运行配置（`.run/` 目录）：

1. 打开 IntelliJ IDEA
2. 导入项目（Maven 项目）
3. 等待依赖下载完成
4. 在右上角的运行配置下拉框中选择对应服务
5. 点击运行按钮

预配置的运行配置：

- `GatewayApplication`
- `AuthApplication`
- `SystemApplication`
- `GenApplication`
- `JobApplication`
- `ResourceApplication`
- `WorkflowApplication`

#### 5.4 查看服务注册情况

访问 Nacos 控制台: http://localhost:8848/nacos

进入 `服务管理` → `服务列表`，应该看到已注册的服务：

```
✓ ruoyi-gateway
✓ ruoyi-auth
✓ ruoyi-system
✓ ruoyi-gen (如已启动)
✓ ruoyi-job (如已启动)
✓ ruoyi-resource (如已启动)
✓ ruoyi-workflow (如已启动)
```

---

## 验证步骤

### 1. 健康检查

访问以下端点验证服务是否正常：

```bash
# Gateway 健康检查
curl http://localhost:8080/actuator/health

# Auth 服务健康检查
curl http://localhost:9210/actuator/health

# System 服务健康检查
curl http://localhost:9201/actuator/health
```

预期响应: `{"status":"UP"}`

### 2. 测试登录接口

```bash
# 通过网关访问登录接口
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

预期响应: 包含 `access_token` 的 JSON 对象

### 3. 访问系统接口

使用获取的 token 访问系统接口：

```bash
# 获取用户信息
curl http://localhost:8080/system/user/getInfo \
  -H "Authorization: Bearer <your_token>"
```

### 4. 验证 Dubbo 调用

查看服务日志，确认 Dubbo 服务已正常注册和调用。

### 5. 验证前端（可选）

如果需要运行前端项目：

```bash
# 克隆前端项目
git clone https://gitee.com/JavaLionLi/plus-ui.git
cd plus-ui

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

访问: http://localhost:80 或配置的前端端口

默认账号: `admin` / `admin123`

---

## 常见问题

### 1. 服务启动失败

**问题**: 服务启动时报错 "Connection refused" 或 "Unknown host"

**解决方案**:

- 确认基础设施（MySQL、Redis、Nacos）已启动
- 检查 `application-common.yml` 中的连接配置
- 验证网络连通性: `ping localhost`, `telnet localhost 3306`

### 2. Nacos 连接失败

**问题**: 服务日志显示无法连接到 Nacos

**解决方案**:

```bash
# 检查 Nacos 是否运行
docker ps | grep nacos

# 检查 Nacos 日志
docker logs nacos

# 验证 Nacos 可访问
curl http://localhost:8848/nacos/v1/console/health/readiness
```

### 3. 数据库连接失败

**问题**: "Access denied for user" 或 "Unknown database"

**解决方案**:

- 确认数据库已初始化
- 检查用户名密码是否正确
- 验证数据库是否存在: `SHOW DATABASES;`
- 检查 MySQL 容器日志: `docker logs mysql`

### 4. Redis 连接失败

**问题**: "Unable to connect to Redis"

**解决方案**:

```bash
# 测试 Redis 连接
docker exec -it redis redis-cli ping
# 应返回: PONG

# 如果设置了密码
docker exec -it redis redis-cli -a your_password ping
```

### 5. 端口冲突

**问题**: "Address already in use"

**解决方案**:

```bash
# 查看端口占用 (macOS/Linux)
lsof -i :8080

# 杀死占用端口的进程
kill -9 <PID>

# 或修改服务端口（在 Nacos 配置中修改）
```

### 6. Maven 构建失败

**问题**: 依赖下载失败或编译错误

**解决方案**:

```bash
# 清理缓存重新构建
mvn clean install -DskipTests -U

# 如果仍失败，删除本地仓库缓存
rm -rf ~/.m2/repository/org/dromara
mvn clean install -DskipTests
```

### 7. Dubbo 服务调用失败

**问题**: "No provider available" 或 Dubbo 超时

**解决方案**:

- 确认服务提供者已启动并注册到 Nacos
- 检查 Dubbo 版本兼容性
- 查看 Nacos 服务列表中是否有对应的 Dubbo 服务
- 检查网络和防火墙设置

### 8. 内存不足

**问题**: Java heap space 错误

**解决方案**:

```bash
# 启动时指定堆内存大小
java -Xms512m -Xmx1024m -jar ruoyi-gateway.jar

# 或在 IDEA 运行配置中添加 VM options:
-Xms512m -Xmx1024m
```

---

## 附录

### A. 默认端口列表

| 服务             | 端口   | 说明                |
|----------------|------|-------------------|
| MySQL          | 3306 | 数据库               |
| Redis          | 6379 | 缓存                |
| Nacos          | 8848 | 配置中心和注册中心         |
| MinIO          | 9000 | 对象存储 API          |
| MinIO Console  | 9001 | MinIO 控制台         |
| ruoyi-gateway  | 8080 | API 网关            |
| ruoyi-auth     | 9210 | 认证服务              |
| ruoyi-system   | 9201 | 系统管理服务            |
| ruoyi-gen      | 9202 | 代码生成服务            |
| ruoyi-job      | 9203 | 任务调度服务            |
| ruoyi-resource | 9204 | 资源管理服务            |
| ruoyi-workflow | 9205 | 工作流服务             |
| ruoyi-monitor  | 9100 | Spring Boot Admin |

### B. 默认账号密码

| 系统    | 用户名        | 密码         | 说明          |
|-------|------------|------------|-------------|
| 系统管理  | admin      | admin123   | 超级管理员       |
| Nacos | nacos      | nacos      | Nacos 控制台   |
| MinIO | minioadmin | minioadmin | MinIO 控制台   |
| MySQL | root       | password   | 数据库 root 用户 |

### C. 重要目录结构

```
RuoYi-Cloud-Plus/
├── .run/                    # IntelliJ IDEA 运行配置
├── script/
│   ├── docker/              # Docker 相关脚本
│   │   ├── dev-quick-start.sh      # 快速启动脚本
│   │   ├── build-and-deploy.sh     # 构建部署脚本
│   │   ├── docker-compose-dev-infra.yml  # 基础设施编排
│   │   └── docker-compose-build.yml      # 完整服务编排
│   ├── sql/                 # 数据库初始化脚本
│   │   ├── mysql/
│   │   ├── oracle/
│   │   ├── postgres/
│   │   └── sqlserver/
│   └── config/
│       └── nacos/           # Nacos 配置文件
├── ruoyi-gateway/           # 网关服务
├── ruoyi-auth/              # 认证服务
├── ruoyi-modules/           # 业务模块
├── ruoyi-api/               # Dubbo API 定义
├── ruoyi-common/            # 公共组件
└── ruoyi-visual/            # 可视化服务
```

### D. 有用的命令

```bash
# 查看所有容器状态
docker ps -a

# 查看服务日志
docker logs -f <container_name>

# 重启服务
docker restart <container_name>

# 进入容器
docker exec -it <container_name> bash

# 清理 Docker 资源
docker system prune -a

# Maven 依赖树
mvn dependency:tree

# Maven 强制更新依赖
mvn clean install -U

# 跳过测试构建
mvn clean package -DskipTests
```

### E. 参考资源

- **官方文档**: https://plus-doc.dromara.org
- **前端仓库**: https://gitee.com/JavaLionLi/plus-ui
- **主仓库**: https://gitee.com/JavaLionLi/RuoYi-Cloud-Plus
- **问题反馈**: https://gitee.com/JavaLionLi/RuoYi-Cloud-Plus/issues
- **Dromara 社区**: https://dromara.org

---

## 下一步

完成初始化后，您可以：

1. **熟悉项目结构**: 阅读 `claude.md` 了解项目架构和开发规范
2. **运行前端**: 克隆前端项目并启动 UI 界面
3. **代码生成**: 使用代码生成器快速创建 CRUD 功能
4. **开发新功能**: 参考现有模块开发新的业务功能
5. **学习文档**: 访问官方文档学习高级特性

**祝您开发愉快！** 🚀

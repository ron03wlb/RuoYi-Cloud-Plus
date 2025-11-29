# 环境设置指南

本指南将逐步指导您完成 RuoYi-Cloud-Plus 开发环境的配置。

## 环境要求

### 系统要求

| 要求项                | 最低版本  | 推荐版本 | 说明         |
|--------------------|-------|------|------------|
| **JDK**            | 17    | 21   | Java 开发工具包 |
| **Docker**         | 20.10 | 最新版  | 用于启动容器化服务  |
| **Docker Compose** | 2.0   | 最新版  | 容器编排工具     |
| **Gradle**         | 8.0   | 最新版  | 构建工具（可选）   |
| **Maven**          | 3.8.0 | 最新版  | 构建工具（可选）   |

### 硬件要求

- **CPU**: 最少 2 核（推荐 4 核）
- **内存**: 最少 8GB（推荐 16GB）
- **磁盘**: 最少 20GB 可用空间（推荐 50GB）

## 环境检查

启动前，请验证您的环境：

```bash
# 检查 JDK 版本
java -version

# 检查 Docker 版本
docker --version

# 检查 Docker Compose 版本
docker-compose --version
```

## 启动基础设施

### 方式一：Docker Compose（推荐）

**优点**: 一键启动所有依赖服务，无需手动配置

```bash
# 1. 进入 docker 目录
cd docker

# 2. 启动所有服务（Nacos、MySQL、Redis、MinIO 等）
docker-compose up -d

# 3. 验证服务状态
docker-compose ps

# 4. 查看服务日志（可选）
docker-compose logs -f
```

**启动后的服务地址**:

- Nacos: http://localhost:8848
- MySQL: localhost:3306 (用户: root, 密码: 123456)
- Redis: localhost:6379
- MinIO: http://localhost:9000

### 方式二：手动启动（可选）

如果不使用 Docker，您需要：

1. **安装 MySQL 5.7+**
    - 下载: https://dev.mysql.com/downloads/mysql/
    - 创建数据库: `ruoyi_cloud_plus`

2. **安装 Redis 5.0+**
    - 下载: https://redis.io/download
    - 启动 Redis 服务

3. **安装并启动 Nacos**
    - 下载: https://nacos.io/zh-cn/docs/quick-start.html
    - 启动: `./bin/startup.sh -m standalone`

## 数据库初始化

### 步骤 1：创建数据库

```sql
-- 登录 MySQL
mysql -h localhost -u root -p

-- 创建数据库
CREATE DATABASE ruoyi_cloud_plus CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ruoyi_cloud_plus;
```

### 步骤 2：导入初始化脚本

```bash
# 脚本位置
sql/ruoyi_cloud_plus.sql

# 导入方式一：命令行
mysql -h localhost -u root -p ruoyi_cloud_plus < sql/ruoyi_cloud_plus.sql

# 导入方式二：在 IDE 中执行
# 使用 IntelliJ IDEA、MySQL Workbench 等工具打开并执行 SQL 文件
```

### 步骤 3：验证数据库

```sql
-- 检查表是否导入成功
SHOW TABLES;

-- 应该看到以下表
-- sys_user, sys_role, sys_dept, sys_menu, sys_permission 等
```

## Nacos 配置导入

Nacos 是分布式配置中心，需要导入项目配置。

### 步骤 1：访问 Nacos 控制台

```
http://localhost:8848/nacos

默认用户名: nacos
默认密码: nacos
```

### 步骤 2：导入配置文件

项目中包含的配置文件位置：

```
nacos-config/
├── ruoyi-auth/
├── ruoyi-system/
├── ruoyi-gateway/
└── ruoyi-gen/
```

**自动导入方式**（推荐）:

某些模块支持自动配置导入，启动服务时会自动从配置文件导入。

**手动导入方式**:

1. 进入 Nacos 控制台
2. 配置管理 → 配置列表
3. 右上角"导入"按钮
4. 选择对应的配置文件并导入

### 步骤 3：验证配置

在 Nacos 控制台中确认以下配置已导入：

- `auth-dev.yml` - 权限服务配置
- `system-dev.yml` - 系统服务配置
- `gateway-dev.yml` - 网关配置

## 启动第一个服务

### 方式一：IDE 启动（适合开发）

**使用 IntelliJ IDEA**:

1. 打开项目根目录
2. 找到 `ruoyi-auth` 模块
3. 找到 `AuthApplication.java`
4. 右键 → Run 'AuthApplication'

**预期输出**:

```
Started AuthApplication in X.XXX seconds
```

### 方式二：命令行启动

```bash
# 1. 构建项目
./gradlew build -x test

# 2. 启动 Auth 服务
cd ruoyi-auth
java -jar build/libs/ruoyi-auth-*.jar

# 或使用 Maven
cd ruoyi-auth
java -jar target/ruoyi-auth-*.jar
```

### 方式三：Docker 启动

```bash
# 1. 构建 Docker 镜像
docker build -t ruoyi-auth:latest -f ruoyi-auth/Dockerfile .

# 2. 启动容器
docker run -d \
  --name ruoyi-auth \
  -p 9200:9200 \
  --network docker_ruoyi_network \
  ruoyi-auth:latest
```

## 验证环境

### 检查点清单

启动后，逐个验证以下项目：

- [ ] **Nacos 控制台**: http://localhost:8848/nacos
    - 检查"服务管理 → 服务列表"
    - 应该能看到已注册的服务

- [ ] **Auth 服务 API 文档**: http://localhost:9200/doc.html
    - 应该能看到 Swagger 接口文档

- [ ] **健康检查**:
  ```bash
  curl http://localhost:9200/actuator/health
  # 预期返回: {"status":"UP"}
  ```

- [ ] **数据库连接**:
  ```bash
  # 在 Auth 服务日志中检查数据库连接信息
  ```

- [ ] **Redis 连接**:
  ```bash
  # 检查 Redis 连接状态（在服务日志中）
  ```

### 常见问题排查

**问题 1: 无法连接到 MySQL**

```
解决方案:
1. 确保 MySQL 服务已启动: docker-compose ps
2. 检查密码是否正确: 默认用户 root，密码 123456
3. 检查防火墙设置
```

**问题 2: Nacos 无法启动**

```
解决方案:
1. 确保没有其他进程占用 8848 端口
2. 检查内存是否充足（至少 512MB）
3. 查看 Nacos 日志: docker-compose logs nacos
```

**问题 3: 服务无法注册到 Nacos**

```
解决方案:
1. 检查 Nacos 地址配置: nacos.server-addr=localhost:8848
2. 确保服务能够访问 Nacos（网络连接）
3. 查看服务启动日志中的错误信息
```

**问题 4: 端口被占用**

```bash
# 查看哪个进程占用了端口（以 9200 为例）
lsof -i :9200

# 或修改服务的启动端口
java -jar ruoyi-auth.jar --server.port=9201
```

## 继续学习

- 查看 [测试基础](/docs/getting-started/testing-basics.md) 学习如何运行和编写测试
- 阅读 [项目架构](/docs/architecture/README.md) 了解系统设计
- 浏览 [官方文档](https://plus-doc.dromara.org) 深入学习

## 获取帮助

- 遇到问题？查看 [Troubleshooting 指南](#常见问题排查)
- 官方文档: https://plus-doc.dromara.org
- 提交 Issue: https://gitee.com/dromara/RuoYi-Cloud-Plus/issues
- 加入社区: https://plus-doc.dromara.org/#/common/add_group

---

**下一步**: 环境验证成功后，可以开始 [运行测试](/docs/getting-started/testing-basics.md) 或开发新功能。

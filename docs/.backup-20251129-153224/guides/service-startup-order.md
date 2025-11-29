# 服务启动顺序指南

本文档说明 RuoYi-Cloud-Plus 各服务的依赖关系和正确的启动顺序。

---

## 服务依赖关系

```
基础设施层
├── PostgreSQL (数据库)
├── Redis (缓存)
├── Nacos (配置中心/注册中心)
└── MinIO (对象存储)
    │
    ▼
核心服务层
├── ruoyi-gateway (API 网关)
└── ruoyi-auth (认证服务)
    │
    ▼
业务服务层
├── ruoyi-system (系统管理) ← 其他服务依赖
├── ruoyi-gen (代码生成)
├── ruoyi-job (任务调度)
├── ruoyi-resource (资源管理)
└── ruoyi-workflow (工作流)
    │
    ▼
监控服务层（可选）
├── ruoyi-monitor (Spring Boot Admin)
├── ruoyi-seata-server (分布式事务)
└── ruoyi-snailjob-server (任务调度服务端)
```

---

## 完整启动顺序

### 第一层：基础设施

**必须首先启动**，所有服务都依赖这些基础设施。

| 顺序 | 服务         | 端口        | 说明        |
|----|------------|-----------|-----------|
| 1  | PostgreSQL | 5432      | 数据库       |
| 2  | Redis      | 6379      | 缓存        |
| 3  | Nacos      | 8848      | 配置中心/注册中心 |
| 4  | MinIO      | 9000/9001 | 对象存储      |

**启动命令**:

```bash
cd script/docker
./dev-start.sh
# 或
./build-and-deploy.sh infra
```

**验证**:

- Nacos 控制台: http://localhost:8848/nacos
- MinIO 控制台: http://localhost:9001

---

### 第二层：核心服务

**基础设施就绪后启动**。

| 顺序 | 服务            | 端口   | 依赖           |
|----|---------------|------|--------------|
| 5  | ruoyi-gateway | 8080 | Nacos        |
| 6  | ruoyi-auth    | 9210 | Nacos, Redis |

**说明**:

- Gateway 必须先启动，它是所有请求的入口
- Auth 服务处理认证，其他业务服务依赖它

---

### 第三层：核心业务服务

| 顺序 | 服务           | 端口   | 依赖                       |
|----|--------------|------|--------------------------|
| 7  | ruoyi-system | 9201 | Nacos, Redis, PostgreSQL |

**说明**:

- System 是最核心的业务服务
- 提供用户、角色、菜单、部门等基础数据
- 其他业务服务通过 Dubbo 调用 System 服务

---

### 第四层：扩展业务服务（可选）

可以根据需要选择性启动。

| 顺序 | 服务             | 端口   | 依赖            | 说明   |
|----|----------------|------|---------------|------|
| 8  | ruoyi-gen      | 9202 | System        | 代码生成 |
| 9  | ruoyi-job      | 9203 | System        | 任务管理 |
| 10 | ruoyi-resource | 9204 | System, MinIO | 资源管理 |
| 11 | ruoyi-workflow | 9205 | System        | 工作流  |

---

### 第五层：监控服务（可选）

| 顺序 | 服务                    | 端口   | 说明                   |
|----|-----------------------|------|----------------------|
| 12 | ruoyi-monitor         | 9100 | Spring Boot Admin 监控 |
| -  | ruoyi-seata-server    | 8091 | 分布式事务（如使用）           |
| -  | ruoyi-snailjob-server | 8800 | SnailJob 服务端         |

---

## IDE 启动方式

### IntelliJ IDEA

使用项目预配置的运行配置（`.run/` 目录）：

1. **按顺序启动**:
    - `GatewayApplication`
    - `AuthApplication`
    - `SystemApplication`
    - 其他服务...

2. **使用 Run Dashboard**:
    - View → Tool Windows → Services
    - 可以同时管理多个服务

### 启动配置说明

每个运行配置都已设置好必要的 VM 参数和环境变量。

---

## 验证服务状态

### 检查 Nacos 注册

访问 http://localhost:8848/nacos → 服务管理 → 服务列表

应该看到已注册的服务：

```
✓ ruoyi-gateway
✓ ruoyi-auth
✓ ruoyi-system
✓ ruoyi-gen (如已启动)
✓ ruoyi-job (如已启动)
✓ ruoyi-resource (如已启动)
✓ ruoyi-workflow (如已启动)
```

### 健康检查

```bash
# Gateway
curl http://localhost:8080/actuator/health

# Auth
curl http://localhost:9210/actuator/health

# System
curl http://localhost:9201/actuator/health
```

预期响应: `{"status":"UP"}`

### 测试登录

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

---

## 常见问题

### Q: 服务启动报 "No provider available"

**原因**: 被依赖的服务未启动

**解决**: 确保按正确顺序启动，特别是 ruoyi-system 必须在业务服务之前启动

### Q: 服务启动报 Nacos 连接失败

**原因**: Nacos 服务未就绪

**解决**:

1. 等待 Nacos 完全启动（约 30 秒）
2. 检查 Nacos 健康状态
3. 确认配置已导入

### Q: 服务注册但调用失败

**原因**: 服务未完全启动或网络问题

**解决**:

1. 检查服务日志
2. 确认 Dubbo 配置正确
3. 检查网络连通性

---

## 最佳实践

### 开发环境

1. 使用 IDE 启动业务服务便于调试
2. 基础设施使用 Docker 运行
3. 按需启动扩展服务，减少资源占用

### 生产环境

1. 使用容器编排确保启动顺序
2. 配置健康检查和依赖关系
3. 设置适当的启动延迟

### 快速调试

```bash
# 只启动最小必要服务
# 1. 基础设施
./dev-start.sh

# 2. 核心服务（IDE 中启动）
# - GatewayApplication
# - AuthApplication
# - SystemApplication
```

---

## 相关文档

- [Docker 部署指南](../project/docker-deployment.md)
- [开发环境初始化](../project/development-setup.md)
- [Nacos 配置导入](./nacos-config-import.md)
- [数据库初始化](./database-initialization.md)

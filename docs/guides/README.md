# 操作指南

本目录包含 RuoYi-Cloud-Plus 项目的具体操作步骤指南。

---

## 指南列表

| 指南 | 说明 | 适用场景 |
|------|------|---------|
| [**Nacos 配置导入**](./nacos-config-import.md) | 自动/手动导入 Nacos 配置、配置说明、常见问题 | 首次启动、配置变更 |
| [**数据库初始化**](./database-initialization.md) | PostgreSQL 初始化、脚本执行、数据验证 | 首次启动、数据库重建 |
| [**服务启动顺序**](./service-startup-order.md) | 服务依赖关系、启动顺序、健康检查 | 日常开发、故障排查 |

---

## 快速导航

| 我想... | 查看指南 |
|---------|---------|
| 导入 Nacos 配置 | [Nacos 配置导入](./nacos-config-import.md) |
| 初始化数据库 | [数据库初始化](./database-initialization.md) |
| 了解服务启动顺序 | [服务启动顺序](./service-startup-order.md) |

---

## 首次初始化流程

如果是首次初始化开发环境，建议按以下顺序操作：

### 1. 启动基础设施

```bash
cd script/docker
./dev-start.sh
```

### 2. 初始化数据库

参阅：[数据库初始化指南](./database-initialization.md)

### 3. 导入 Nacos 配置

参阅：[Nacos 配置导入指南](./nacos-config-import.md)

### 4. 启动业务服务

参阅：[服务启动顺序指南](./service-startup-order.md)

---

## 相关文档

### 项目文档

- [claude.md](../project/claude.md) - 项目概述和开发指南
- [开发环境初始化](../project/development-setup.md) - 完整初始化步骤（中文）
- [Docker 部署](../project/docker-deployment.md) - Docker 部署专题

### 测试文档

- [测试文档中心](../)

---

**最后更新**: 2025-11-18

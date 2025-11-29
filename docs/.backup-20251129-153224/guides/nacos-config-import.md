# Nacos 配置导入指南

本文档详细说明如何将配置文件导入到 Nacos 配置中心。

---

## 概述

RuoYi-Cloud-Plus 使用 Nacos 作为配置中心，所有服务的配置都存储在 Nacos 中。首次启动后必须导入配置，否则业务服务无法正常运行。

配置文件位置：`script/config/nacos/`

---

## 自动导入（推荐）

### 使用导入脚本

```bash
cd script/docker
./import-nacos-config.sh
```

此脚本会自动将 `script/config/nacos/` 目录下的所有配置文件导入到 Nacos。

### 前置条件

- Nacos 服务已启动并可访问
- 默认地址：http://localhost:8848

---

## 手动导入

### 步骤 1: 访问 Nacos 控制台

打开浏览器访问：http://localhost:8848/nacos

- **用户名**: nacos
- **密码**: nacos

### 步骤 2: 进入配置管理

1. 登录后点击左侧菜单 **配置管理**
2. 选择 **配置列表**
3. 确认命名空间为 **public**（或您配置的命名空间）

### 步骤 3: 导入配置文件

1. 点击 **导入配置** 按钮
2. 选择 `script/config/nacos/` 目录下的所有 `.yml` 文件
3. 确认导入

### 步骤 4: 验证导入

确认配置列表中显示以下配置文件：

```
✓ application-common.yml     - 所有服务共享配置
✓ ruoyi-gateway.yml          - 网关路由配置
✓ ruoyi-auth.yml             - 认证服务配置
✓ ruoyi-system.yml           - 系统服务配置
✓ ruoyi-gen.yml              - 代码生成服务配置
✓ ruoyi-job.yml              - 任务调度服务配置
✓ ruoyi-resource.yml         - 资源服务配置
✓ ruoyi-workflow.yml         - 工作流服务配置
```

---

## 配置文件说明

### application-common.yml（核心配置）

所有服务共享的公共配置，包含：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ry-cloud
    username: postgres
    password: ruoyi123

  data:
    redis:
      host: localhost
      port: 6379

# OSS 配置
oss:
  endpoint: http://localhost:9000
  accessKey: minioadmin
  secretKey: minioadmin
```

**重要**: 如果修改了数据库密码、Redis 密码或 MinIO 密码，需要在此配置文件中同步修改！

### ruoyi-gateway.yml

网关路由配置，定义了各服务的路由规则：

- `/auth/**` → ruoyi-auth
- `/system/**` → ruoyi-system
- `/code/**` → ruoyi-gen
- 等等...

### 服务专用配置

每个业务服务都有独立的配置文件，包含该服务特有的配置项。

---

## 配置修改

### 在 Nacos 控制台修改

1. 在配置列表中找到要修改的配置
2. 点击 **编辑**
3. 修改内容后点击 **发布**

### 配置实时生效

Nacos 支持配置热更新，修改后服务会自动刷新配置（部分配置需要重启服务）。

---

## 常见问题

### Q: 导入脚本执行失败

**原因**: Nacos 服务未启动或网络不通

**解决**:

```bash
# 检查 Nacos 是否运行
docker ps | grep nacos

# 检查 Nacos 健康状态
curl http://localhost:8848/nacos/v1/console/health/readiness
```

### Q: 配置文件格式错误

**原因**: YAML 格式不正确

**解决**: 使用 YAML 验证工具检查配置文件格式

### Q: 服务仍然报配置错误

**原因**: 配置未正确导入或命名空间不匹配

**解决**:

1. 确认配置已在 Nacos 控制台显示
2. 检查服务的 `bootstrap.yml` 中的命名空间配置
3. 确认 Group 为 `DEFAULT_GROUP`

---

## 相关文档

- [Docker 部署指南](../project/docker-deployment.md)
- [开发环境初始化](../project/development-setup.md)
- [数据库初始化](./database-initialization.md)

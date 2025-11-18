# Nacos 配置文件目录

本目录包含所有 Nacos 配置文件，需要导入到 Nacos 控制台。

## 详细文档

参阅：[Nacos 配置导入指南](../../../docs/guides/nacos-config-import.md)

## 快速导入

### 自动导入（推荐）

```bash
cd ../../docker
./import-nacos-config.sh
```

### 手动导入

1. 访问 http://localhost:8848/nacos
2. 登录 (nacos/nacos)
3. 配置管理 → 配置列表 → 导入配置
4. 选择本目录下的所有 .yml 文件

## 配置文件列表

| 文件 | 说明 |
|------|------|
| `application-common.yml` | 所有服务共享配置（数据库、Redis、OSS） |
| `ruoyi-gateway.yml` | 网关路由配置 |
| `ruoyi-auth.yml` | 认证服务配置 |
| `ruoyi-system.yml` | 系统服务配置 |
| `ruoyi-gen.yml` | 代码生成服务配置 |
| `ruoyi-job.yml` | 任务调度服务配置 |
| `ruoyi-resource.yml` | 资源服务配置 |
| `ruoyi-workflow.yml` | 工作流服务配置 |

## 注意事项

- 首次启动必须导入配置，否则业务服务无法正常运行
- 修改数据库/Redis/MinIO 密码后，需要同步更新 `application-common.yml`

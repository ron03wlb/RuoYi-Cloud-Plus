# 数据库初始化指南

本文档详细说明如何初始化 RuoYi-Cloud-Plus 项目的数据库。

---

## 概述

项目使用 PostgreSQL 17 作为主数据库。数据库脚本位于 `script/sql/` 目录。

---

## 前置条件

- PostgreSQL 服务已启动
- 数据库客户端工具（psql、DBeaver、Navicat 等）

### 默认连接信息

| 参数  | 值         |
|-----|-----------|
| 主机  | localhost |
| 端口  | 5432      |
| 用户名 | postgres  |
| 密码  | ruoyi123  |
| 数据库 | ry-cloud  |

---

## 数据库脚本

### 主要脚本

| 脚本文件              | 说明               | 必须 |
|-------------------|------------------|----|
| `ry_cloud.sql`    | 主应用数据库           | 是  |
| `ry_job.sql`      | SnailJob 任务调度数据库 | 是  |
| `ry_seata.sql`    | Seata 分布式事务数据库   | 可选 |
| `ry_workflow.sql` | Warm-Flow 工作流数据库 | 可选 |

### 归档脚本

MySQL 和 Oracle 的历史脚本在 `script/sql/archive/` 目录，仅供参考。

---

## 初始化步骤

### 方式 1: 使用 psql 命令行

```bash
# 连接到 PostgreSQL
psql -h localhost -p 5432 -U postgres

# 创建数据库（如果不存在）
CREATE DATABASE "ry-cloud";
CREATE DATABASE "ry-job";
CREATE DATABASE "ry-seata";
CREATE DATABASE "ry-workflow";

# 切换到数据库并执行脚本
\c ry-cloud
\i script/sql/ry_cloud.sql

\c ry-job
\i script/sql/ry_job.sql

\c ry-seata
\i script/sql/ry_seata.sql

\c ry-workflow
\i script/sql/ry_workflow.sql
```

### 方式 2: 使用数据库客户端

1. 使用 DBeaver/Navicat 连接到 PostgreSQL
2. 创建所需数据库
3. 分别在各数据库中执行对应的 SQL 脚本

### 方式 3: Docker 容器内执行

```bash
# 进入 PostgreSQL 容器
docker exec -it postgres psql -U postgres

# 然后执行上述 SQL 命令
```

---

## 验证初始化

### 检查数据库

```sql
-- 列出所有数据库
\l

-- 应该看到：
-- ry-cloud
-- ry-job
-- ry-seata
-- ry-workflow
```

### 检查核心表

```sql
-- 切换到主数据库
\c ry-cloud

-- 列出所有表
\dt

-- 应该看到以下核心表：
-- sys_user, sys_role, sys_menu, sys_dept
-- sys_config, sys_dict_type, sys_dict_data
-- sys_oss, sys_oss_config
-- gen_table, gen_table_column
-- 等等...
```

### 检查初始数据

```sql
-- 检查管理员用户
SELECT user_id, user_name, nick_name FROM sys_user WHERE user_id = 1;

-- 应该返回：
-- user_id | user_name | nick_name
-- --------+-----------+-----------
--       1 | admin     | 超级管理员
```

---

## 示例模块数据

如果需要运行示例模块（ruoyi-demo），还需要执行：

```bash
# 位置：ruoyi-example/ruoyi-demo/test.sql
psql -h localhost -p 5432 -U postgres -d ry-cloud -f ruoyi-example/ruoyi-demo/test.sql
```

---

## 常见问题

### Q: 连接被拒绝

**原因**: PostgreSQL 服务未启动

**解决**:

```bash
# 检查容器状态
docker ps | grep postgres

# 查看日志
docker logs postgres
```

### Q: 权限不足

**原因**: 用户权限配置问题

**解决**:

```sql
-- 授予权限
GRANT ALL PRIVILEGES ON DATABASE "ry-cloud" TO postgres;
```

### Q: 脚本执行报错

**原因**: 数据库版本不兼容或语法错误

**解决**:

1. 确认使用 PostgreSQL 17
2. 检查脚本文件编码（UTF-8）
3. 查看具体错误信息

### Q: 表已存在

**原因**: 数据库已初始化过

**解决**:

```sql
-- 如需重新初始化，先删除数据库
DROP DATABASE IF EXISTS "ry-cloud";
CREATE DATABASE "ry-cloud";
```

---

## 数据库配置

确保 Nacos 中的 `application-common.yml` 配置与数据库信息一致：

```yaml
spring:
  datasource:
    dynamic:
      primary: master
      datasource:
        master:
          url: jdbc:postgresql://localhost:5432/ry-cloud
          username: postgres
          password: ruoyi123
```

---

## 相关文档

- [Docker 部署指南](../project/docker-deployment.md)
- [开发环境初始化](../project/development-setup.md)
- [Nacos 配置导入](./nacos-config-import.md)

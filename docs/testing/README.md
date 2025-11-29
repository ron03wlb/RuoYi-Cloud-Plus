# 测试文档中心

欢迎来到 RuoYi-Cloud-Plus 的测试文档中心！这里汇集了项目所有的测试相关资料。

## 快速导航

### 新手必读

1. **[测试基础](/docs/getting-started/testing-basics.md)** ⭐ 推荐从这里开始
    - 如何运行测试
    - 如何编写第一个测试
    - 最佳实践

2. **[当前测试状态](/docs/testing/current-status.md)**
    - 项目当前的测试统计
    - 模块测试覆盖情况
    - 已知问题和改进计划

### 深入学习

3. **[单元测试完整指南](/docs/testing/unit-test-guide.md)**
    - 单元测试框架（JUnit 5 + Mockito）
    - BaseUnitTest 基类使用
    - AAA 模式详解
    - 命名规范
    - 完整代码示例

4. **[集成测试完整指南](/docs/testing/integration-test-guide.md)**
    - 集成测试框架设置
    - BaseIntegrationTest 使用
    - Testcontainers 配置
    - 真实数据库测试
    - 完整代码示例

## 项目测试概览

### 当前统计

```
总测试数:        3,271+
├─ 单元测试:     2,500+ (已完成)
├─ 集成测试:     700+ (进行中)
└─ 端到端测试:   71+ (计划中)

总体通过率:     99.05%
├─ 单元测试:    ~99%
└─ 集成测试:    ~98%

代码覆盖率:     ~90%
├─ 公共库:      ~95%
├─ 认证服务:    ~95%
└─ 系统服务:    ~90%
```

### 模块覆盖率

| 模块分类     | 覆盖数   | 状态     | 详情                                    |
|----------|-------|--------|---------------------------------------|
| **公共库**  | 9/9   | ✅ 完全覆盖 | [查看](/docs/testing/current-status.md) |
| **认证服务** | 1/1   | ✅ 完全覆盖 | Sa-Token 权限认证                         |
| **系统服务** | 17/17 | ✅ 完全覆盖 | 用户、角色、权限等                             |
| **资源服务** | 部分    | 🔄 进行中 | OSS、文件存储                              |
| **工作流**  | 计划中   | ⏳ 待开始  | 流程审批引擎                                |

## 测试框架

### 使用的技术栈

| 框架/库                 | 用途          | 版本    |
|----------------------|-------------|-------|
| **JUnit 5**          | 测试框架        | 5.9+  |
| **Mockito**          | Mock 对象     | 5.0+  |
| **AssertJ**          | 断言库         | 3.24+ |
| **Testcontainers**   | 容器化测试环境     | 1.17+ |
| **Spring Boot Test** | Spring 集成测试 | 3.4+  |

### 基础类

- **BaseUnitTest**: 单元测试基类
    - 内置 Mock 框架
    - 提供测试工具方法
    - 标准化测试配置

- **BaseIntegrationTest**: 集成测试基类
    - Spring Boot 容器启动
    - 真实数据库连接
    - 事务管理

## 按使用场景选择文档

### 我想...

- **快速开始写测试** → [测试基础](/docs/getting-started/testing-basics.md)
- **深入学习单元测试** → [单元测试完整指南](/docs/testing/unit-test-guide.md)
- **编写集成测试** → [集成测试完整指南](/docs/testing/integration-test-guide.md)
- **了解项目测试现状** → [当前测试状态](/docs/testing/current-status.md)
- **查看测试代码示例** → 各模块的 `*Test.java` 文件
- **学习测试最佳实践** → 相关文档中的"最佳实践"部分

## 常见问题

**Q: 应该先学单元测试还是集成测试？**
A: 先学单元测试。单元测试更简单，是学习测试的基础。

**Q: 项目的测试覆盖率是多少？**
A: 当前约 90%，详见 [当前测试状态](/docs/testing/current-status.md)。

**Q: 如何快速运行所有测试？**
A: 使用命令 `./gradlew test`，详见 [测试基础](/docs/getting-started/testing-basics.md)。

**Q: 单元测试为什么要使用 Mock？**
A: Mock 可以隔离被测试的代码，加快测试速度，详见 [单元测试完整指南](/docs/testing/unit-test-guide.md)。

**Q: 如何处理测试中的随机数据？**
A: 使用工厂方法或 TestDataFactory，详见 [单元测试完整指南](/docs/testing/unit-test-guide.md)。

## 测试贡献指南

如果您要为项目添加新的测试：

1. 查看相关的测试指南
2. 选择合适的基类（BaseUnitTest 或 BaseIntegrationTest）
3. 遵循命名规范和 AAA 模式
4. 确保测试独立且可重复运行
5. 提交测试时请附上必要的注释

## 获取帮助

- 📖 官方文档: https://plus-doc.dromara.org
- 💬 提交 Issue: https://gitee.com/dromara/RuoYi-Cloud-Plus/issues
- 👥 加入社区: https://plus-doc.dromara.org/#/common/add_group
- 📝 查看示例: 浏览 `src/test/java` 中的测试代码

## 相关文档

- [项目快速开始](/docs/getting-started/quick-start.md)
- [环境设置指南](/docs/getting-started/environment-setup.md)
- [项目架构文档](/docs/architecture/README.md)
- [归档文档](/docs/archive/README.md)

---

**下一步**: 选择上面的任一文档开始学习，或访问 [测试基础](/docs/getting-started/testing-basics.md) 进行快速入门。

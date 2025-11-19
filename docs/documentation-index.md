# 文档索引

> 📅 **最后更新**: 2025-11-18
> 🎯 **用途**: 快速找到你需要的文档
> 📊 **文档结构**: 项目文档 + 操作指南 + 测试文档

---

## 📁 项目文档

### 核心指南

| 文档                                            | 说明                                     |
|-----------------------------------------------|----------------------------------------|
| [**claude.md**](project/claude.md)            | Claude Code 项目指南 - 项目概述、架构原则、开发模式、最佳实践 |
| [**gradle.md**](project/gradle.md)            | Gradle 构建完整指南 - 快速开始、命令对照、配置说明         |
| [**开发环境初始化**](project/development-setup.md)   | 开发环境初始化指南 - 环境要求、基础设施启动、验证步骤（中文）       |
| [**Docker 部署**](project/docker-deployment.md) | Docker 部署专题 - 开发模式、完整部署、故障排查           |

### 操作指南

| 指南 | 说明 |
|------|------|
| [Nacos 配置导入](guides/nacos-config-import.md) | 自动/手动导入 Nacos 配置 |
| [数据库初始化](guides/database-initialization.md) | PostgreSQL 初始化步骤 |
| [服务启动顺序](guides/service-startup-order.md) | 服务依赖关系和启动顺序 |

---

## 📊 测试文档

## 🔍 如何使用本索引

### 按使用场景导航

| 我想...          | 查看文档                                                   | 说明                         |
|----------------|--------------------------------------------------------|----------------------------|
| **查看项目整体测试状态** | [测试状态总览](active/testing-master-status.md)              | 所有模块的完成情况、统计数据、技术债务（单元+集成） |
| **查看集成测试进度**   | [集成测试跟踪器](active/integration-test-tracker.md)          | 集成测试任务列表、进度、里程碑            |
| **了解集成测试框架**   | [集成测试框架文档](archive/integration-tests/week1-framework/) | Week 1 完整总结，包含框架、代码、障碍、方案  |
| **查看具体模块测试**   | [模块文档](#-模块测试文档)                                       | 按模块分类的详细测试文档               |
| **查看历史文档**     | [归档区](#-归档文档)                                          | 按时间/阶段组织的历史文档              |
| **开始编写新测试**    | [测试最佳实践](#-测试最佳实践)                                     | 框架使用、命名规范、模式参考             |

---

## 📊 主要文档（必读）

### 1. [测试状态总览](active/testing-master-status.md) ⭐⭐⭐⭐

**用途**: 项目测试状态的鸟瞰图
**包含**:

- 整体测试进度统计（单元测试+集成测试）
- 已完成模块列表（单元测试：100%）
- 进行中模块列表（集成测试：进行中）
- 技术债务清单
- 质量指标

**适合**: 项目经理、新团队成员、季度汇报

**注意**: 需要更新测试数量统计（1,116 → 3,271）

---

### 2. [集成测试跟踪器](active/integration-test-tracker.md) ⭐⭐⭐

**用途**: 集成测试详细进度跟踪
**包含**:

- 19 个服务的集成测试任务
- 按周/天的详细计划
- 任务优先级（P0/P1/P2/P3）
- 工作量预估
- 里程碑跟踪
- 技术债务跟踪
- 更新日志

**适合**: 开发人员、测试人员、日常工作规划

**当前状态**: 🔄 集成测试框架已就绪，首批测试进行中

---

### 3. [集成测试框架文档](archive/integration-tests/week1-framework/) ⭐⭐

**用途**: Week 1 集成测试工作的完整总结
**包含**:

- 框架设置（BaseIntegrationTest, Testcontainers）
- 测试代码（49 个测试用例）
- 技术障碍（Dubbo + Testcontainers 时序冲突）
- 解决方案（方案 A 已成功实施）
- 工作成果统计
- 经验教训

**适合**: 需要了解集成测试框架的所有人

**详细文档**:

- [框架设置详解](archive/integration-tests/week1-framework/integration-test-framework-setup.md)
- [工作总结](archive/integration-tests/week1-framework/integration-test-work-summary.md)
- [最终状态报告](archive/integration-tests/week1-framework/integration-test-final-status.md)

---

## 📁 模块测试文档（参考）

> ℹ️ 单元测试已完成，文档已归档为参考资料。当前重点为集成测试。

### Common 库模块

- [Common 模块总览](reference/modules/common/README.md)
- [ruoyi-common-core](reference/modules/common/core.md) - 208 tests, 100%
- [ruoyi-common-mybatis](reference/modules/common/mybatis.md) - 100 tests, 100%
- [ruoyi-common-satoken](reference/modules/common/satoken.md) - 85 tests, 100%
- [其他 common 模块...](reference/modules/common/)

### 认证模块

- [ruoyi-auth](reference/modules/auth/README.md) - 166 tests, 100%

### 系统管理模块

- [ruoyi-system 总览](reference/modules/system/README.md) - 305 tests, 100%
    - [核心服务](reference/modules/system/core-services.md)
    - [字典配置服务](reference/modules/system/dict-config-services.md)
    - [客户端社交服务](reference/modules/system/client-social-services.md)
    - [租户服务](reference/modules/system/tenant-services.md)

### 集成测试模块

- [ruoyi-gen](reference/modules/gen/README.md) - 🔄 代码生成，集成测试中
- [ruoyi-resource](reference/modules/resource/README.md) - 🔄 资源管理，集成测试中
- [ruoyi-workflow](reference/modules/workflow/README.md) - ⏳ 工作流，待开始

---

## 📚 归档文档

### 单元测试归档 (已完成)

**位置**: [archive/unit-tests/](archive/unit-tests/)

**按阶段**:

- [Phase 1](archive/unit-tests/phase1-common/) - ruoyi-common 模块 (11 个文件)
- [Phase 2](archive/unit-tests/phase2-auth/) - ruoyi-auth 模块 (3 个文件)
- [Phase 3](archive/unit-tests/phase3-system/) - ruoyi-system 模块 (15 个文件)
- [Phase 4](archive/unit-tests/phase4-analysis/) - 其他模块分析 (1 个文件)
- [Phase 5](archive/unit-tests/phase5-gen/) - ruoyi-gen 模块分析

**说明**: 所有单元测试工作已完成并归档，作为参考资料保留

---

### 集成测试归档

**位置
**: [archive/integration-tests/](archive/integration-tests/) + [archive/progress-reports/](archive/progress-reports/)

**Week 1 (2025-11-10)**:

- [README.md](archive/integration-tests/week1-framework/README.md) - 归档说明
- [integration-test-framework-setup.md](archive/integration-tests/week1-framework/integration-test-framework-setup.md) -
  框架详细设置 (480 行)
- [integration-test-work-summary.md](archive/integration-tests/week1-framework/integration-test-work-summary.md) -
  工作完整总结 (
  580 行)
- [integration-test-final-status.md](archive/integration-tests/week1-framework/integration-test-final-status.md) -
  最终状态报告

**成果**: 框架 100% 就绪，49 个测试用例编写完成，Dubbo 时序冲突已解决

**进度报告归档 (2025-11-11)**:

- [testing-progress-update-2025-11-11.md](archive/progress-reports/2025-11/testing-progress-update-2025-11-11.md) -
  3,271 测试执行完整分析

**成果**: Sa-Token 上下文问题修复，Resource Bean 配置问题分析

---

### 旧版总结文档

**位置**: [archive/legacy/](archive/legacy/)

**已删除文档**（内容已整合到 testing-master-status.md）:

- testing-status-summary.md
- overall-testing-status-summary.md
- testing-task-implementation-summary.md
- testing-progress-report.md
- testing-task-checklist.md

**保留文档**:

- [final-module-analysis-summary.md](archive/legacy/final-module-analysis-summary.md) - 模块分析总结

---

## 🎯 测试最佳实践

### BaseUnitTest 使用

```java

@ExtendWith(MockitoExtension.class)
class MyServiceTest extends BaseUnitTest {
    @Mock
    private MyMapper mapper;
    @InjectMocks
    private MyServiceImpl service;

    @Test
    @DisplayName("应该成功查询用户")
    void shouldQueryUser() {
        // AAA 模式
    }
}
```

### BaseIntegrationTest 使用

```java

@SpringBootTest
class MyServiceIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MyService service;

    @BeforeAll
    void initDatabase() {
        SqlScriptExecutor.executeSql(dataSource,
            "CREATE TABLE...");
    }
}
```

**详细指南**: [集成测试最终状态](integration-test-final-status.md) - "可行的解决方案" 章节

---

## 🔧 测试执行命令

```bash
# 运行所有测试
./gradlew test

# 运行单个模块
./gradlew :ruoyi-modules:ruoyi-system:test

# 生成覆盖率报告
./gradlew test jacocoTestReport

# 查看报告
open build/reports/jacoco/test/html/index.html
```

---

## 📈 文档统计

### 主要文档

| 文档                               | 大小  | 用途        |
|----------------------------------|-----|-----------|
| testing-master-status.md         | 15K | 状态总览      |
| testing-progress-tracker.md      | 16K | 进度跟踪      |
| integration-test-final-status.md | 11K | Week 1 总结 |

### 模块文档

- Common 模块: 12 个文档
- System 模块: 4 个文档
- 其他模块: 4 个文档
- **总计**: 20+ 个文档

### 归档文档

- Week 1: 3 个文档 (40K)
- Phase 1-4: 34 个文档
- Legacy: 4 个文档
- **总计**: 50+ 个文档

---

## 🔄 文档维护

### 更新频率

- **testing-master-status.md**: 每完成一批测试后更新
- **integration-test-tracker.md**: 每周更新一次
- **模块文档**: 模块测试完成时创建
- **本索引**: 结构变化时更新

### 归档策略

- 使用自动归档脚本（script/docs/archive-phase.sh）
- 每完成一个测试阶段后归档相关文档
- 保持根目录简洁（3-4 个主文档）
- 归档文档按类型和阶段组织

### 贡献指南

1. 新集成测试完成后，更新 [integration-test-tracker.md](active/integration-test-tracker.md)
2. 重要里程碑达成后，更新 [testing-master-status.md](active/testing-master-status.md)
3. 每个阶段完成后，运行归档脚本
4. 保持文档链接的有效性

---

## 📞 联系方式

**文档维护**: Test Team
**问题反馈**: 请在项目 issue 中提出
**更新建议**: 欢迎提交 PR

---

**最后更新**: 2025-11-12
**文档版本**: 3.1
**当前阶段**: Resource Bean 配置问题已解决，集成测试框架稳定运行
**文档结构**: 已重组为简洁结构（3 个主文档 + reference/ + archive/）
**最新成果**: Issue #2 (Resource Bean 冲突) 完全修复，切片测试方案成功

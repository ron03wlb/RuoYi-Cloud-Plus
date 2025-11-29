# 归档文档

## 新手开发者提示

⚠️ **如果您是新手开发者，可以安全地忽略这个目录。**

本目录包含的是项目早期版本、已过时或不再维护的文档。这些文档仅供参考，不应作为当前开发的指引。

## 归档文档说明

本目录保存了以下类型的文档：

### 为什么要保存？

- **历史参考**: 了解项目的演进过程
- **遗留问题**: 查看已知的技术债务
- **设计决策**: 理解为什么做了某些选择
- **知识库**: 保留已有的信息以防需要

### 何时查阅？

仅在以下情况下查阅本目录中的文档：

1. **调查历史问题**: "这个功能为什么这样实现？"
2. **学习项目演进**: "项目是如何发展到今天的？"
3. **理解遗留代码**: "这段代码为什么这样写？"
4. **参考已废弃的方法**: "旧版本中是怎样处理的？"

## 归档文档清单

### 单元测试报告（已整理）

- `unit-tests/` - 单元测试相关的详细报告
    - `phase1/` - Phase 1 公共库模块测试（9 个模块）
    - `phase2/` - Phase 2 认证服务测试（1 个模块）
    - `phase3/` - Phase 3 系统服务测试（17 个服务）
    - `phase4/` - Phase 4 其他服务分析（代码生成、资源、工作流）
    - `phase5-gen-module/` - Gen 模块代码生成测试

### 遗留测试文档

- `legacy/` - 早期版本的总结文档
    - `testing-status-summary.md` - 初版测试状态总结
    - `overall-testing-status-summary.md` - 综合测试状态总结
    - `final-module-analysis-summary.md` - 最终模块分析总结

### 集成测试相关

- `integration-test-week1/` - 第一周集成测试工作记录
- `integration-test-tracker.md` - 集成测试进度跟踪

## 如何使用归档文档

### 查看原始测试报告

如果需要查看某个模块的详细测试报告，可在相应的 phase 目录中查找：

```
归档/单元测试报告/
├── phase1/
│   ├── PHASE1-ruoyi-common-core-TESTING-REPORT.md
│   ├── PHASE1-ruoyi-common-mybatis-TESTING-REPORT.md
│   └── ...
├── phase2/
│   ├── PHASE2-ruoyi-auth-TESTING-REPORT.md
│   └── ...
└── phase3/
    ├── PHASE3-...
    └── ...
```

### 对比不同版本

早期版本的文档已存档，可用于对比项目的演进：

```
legacy/
├── testing-status-summary.md      (v1)
├── overall-testing-status-summary.md (v2)
└── final-module-analysis-summary.md  (v3)
```

## 从归档文档迁移到当前文档

如果在归档文档中找到对您有用的信息，建议：

1. **查看当前版本**: 在 `/docs/testing/current-status.md` 中查看最新信息
2. **参考新指南**: 使用 `/docs/testing/unit-test-guide.md` 和 `/docs/testing/integration-test-guide.md`
3. **查看示例代码**: 浏览 `src/test/java` 中的当前测试代码

## 重要提示

### 不要依赖归档文档

❌ 避免以下做法：

- 按照归档文档中的步骤操作
- 使用归档文档中的已过时的命令
- 引用归档文档中的弃用的 API
- 复制归档文档中的代码示例（可能已过时）

### 使用当前文档

✅ 应该这样做：

- 参考 `/docs/testing/` 中的当前指南
- 查看 `/docs/getting-started/` 中的快速开始
- 浏览项目的 `src/test/java` 中的最新代码示例
- 查阅 [官方文档](https://plus-doc.dromara.org)

## 清理和整理

本项目的文档定期进行清理和整理：

- **过时内容**: 被移至此目录
- **已完成的任务**: 整理为参考文档
- **遗留问题**: 保存以供未来参考

## 获取帮助

如果您在使用当前文档时有疑问：

- 📖 查看相关的当前文档：`/docs/testing/`、`/docs/getting-started/`
- 💬 提交 Issue: https://gitee.com/dromara/RuoYi-Cloud-Plus/issues
- 👥 加入社区: https://plus-doc.dromara.org/#/common/add_group
- 📞 查阅官方文档: https://plus-doc.dromara.org

## 相关文档

- [快速开始](/docs/getting-started/quick-start.md)
- [环境设置](/docs/getting-started/environment-setup.md)
- [测试文档中心](/docs/testing/README.md)
- [当前测试状态](/docs/testing/current-status.md)

---

**提示**: 如果您是新手，请忽略本目录，直接前往 [快速开始](/docs/getting-started/quick-start.md) 开始学习。

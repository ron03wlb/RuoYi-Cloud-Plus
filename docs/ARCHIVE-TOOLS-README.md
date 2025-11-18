# 测试文档归档脚本使用指南

> 📅 **最后更新**: 2025-11-10
> 🎯 **用途**: 自动化测试文档归档流程
> 📦 **包含**: 归档脚本、配置文件、使用说明

---

## 📋 文件说明

### 1. archive-phase.sh

**用途**: 自动归档测试阶段的文档

**功能**:

- 半自动检测阶段完成状态
- 提示用户确认归档操作
- 移动文档到归档目录
- 生成归档 README
- 暂存 git 更改供审查

### 2. archive-config.yml

**用途**: 定义归档规则和配置

**包含**:

- 各阶段的归档规则
- 文件匹配模式
- 归档目标目录
- 文档更新规则

---

## 🚀 快速开始

### 基本用法

```bash
# 进入脚本目录
cd script/docs

# 归档单元测试阶段
./archive-phase.sh --phase phase6 --type unit-test

# 归档集成测试阶段
./archive-phase.sh --phase integration-test-week2 --type integration-test

# 模拟归档（不实际移动文件）
./archive-phase.sh --phase phase6 --type unit-test --dry-run

# 列出所有可归档的阶段
./archive-phase.sh --list

# 查看帮助
./archive-phase.sh --help
```

---

## 📖 详细使用说明

### 归档单元测试阶段

当完成一个单元测试阶段（如 Phase 6）后：

```bash
# 1. 运行归档脚本
./archive-phase.sh --phase phase6 --type unit-test

# 2. 脚本会提示你输入要归档的文件
#    例如：PHASE6-TESTING-STATUS.md PHASE6-TESTING-REPORT.md

# 3. 确认归档操作

# 4. 脚本会：
#    - 创建 docs/archive/unit-tests/phase6/ 目录
#    - 移动指定文件到归档目录
#    - 生成归档 README
#    - 暂存 git 更改

# 5. 手动更新主文档中的链接
#    - docs/TESTING-MASTER-STATUS.md
#    - docs/DOCUMENTATION-INDEX.md
#    - docs/README.md

# 6. 审查并提交更改
git status
git diff
git commit -m "docs: archive phase6 unit tests"
```

### 归档集成测试阶段

当完成一个集成测试周（如 Week 2）后：

```bash
# 1. 运行归档脚本
./archive-phase.sh --phase integration-test-week2 --type integration-test

# 2. 按提示输入要归档的文件
#    例如：INTEGRATION-TEST-WEEK2-SUMMARY.md

# 3. 确认并完成归档

# 4. 更新集成测试跟踪器
#    编辑 docs/INTEGRATION-TEST-TRACKER.md，更新进度

# 5. 提交更改
git commit -m "docs: archive integration test week 2"
```

### 模拟运行（推荐第一次使用）

在第一次使用归档脚本时，建议先模拟运行：

```bash
# 模拟归档，查看会执行的操作
./archive-phase.sh --phase phase6 --type unit-test --dry-run

# 检查输出，确认无误后再正式运行
./archive-phase.sh --phase phase6 --type unit-test
```

---

## 🎯 归档工作流程

### 完整流程

```
1. 完成测试阶段
   ├─ 所有测试编写完成
   ├─ 测试全部通过
   └─ 文档编写完整

2. 标记阶段完成
   ├─ 在 TESTING-MASTER-STATUS.md 中标记 ✅
   └─ 或在 INTEGRATION-TEST-TRACKER.md 中标记 completed

3. 运行归档脚本
   ├─ 选择要归档的文件
   ├─ 确认归档操作
   └─ 脚本自动处理

4. 手动更新文档链接
   ├─ TESTING-MASTER-STATUS.md
   ├─ INTEGRATION-TEST-TRACKER.md
   ├─ DOCUMENTATION-INDEX.md
   └─ README.md

5. 审查并提交
   ├─ git status 查看更改
   ├─ git diff 审查详细更改
   └─ git commit 提交更改
```

---

## 📂 归档目录结构

归档后的目录结构：

```
docs/
├── archive/
│   ├── unit-tests/                    # 单元测试归档
│   │   ├── phase1/
│   │   ├── phase2/
│   │   ├── phase3/
│   │   ├── phase4/
│   │   ├── phase5-gen-module/
│   │   ├── phase6/                    # 新归档的阶段
│   │   │   ├── README.md              # 归档说明
│   │   │   ├── PHASE6-TESTING-STATUS.md
│   │   │   └── PHASE6-TESTING-REPORT.md
│   │   └── tracking/
│   │       └── TESTING-PROGRESS-TRACKER.md
│   ├── integration-test-week1/        # 集成测试归档
│   ├── integration-test-week2/        # 新归档的周
│   │   ├── README.md
│   │   └── INTEGRATION-TEST-WEEK2-SUMMARY.md
│   └── legacy/
└── reference/
    └── modules/
```

---

## ⚙️ 配置文件说明

### archive-config.yml 结构

```yaml
phases:
  phase6:                              # 阶段名称
    type: unit-test                    # 归档类型
    status_marker: "Phase 6.*✅"      # 完成标记
    files_to_archive:                  # 要归档的文件
      - PHASE6-TESTING-STATUS.md
    archive_destination:               # 归档目标
      archive/unit-tests/phase6

archive_rules:                         # 归档规则
  unit_test:
    destination_base: archive/unit-tests
    naming_pattern: "phase[0-9]+"

document_updates:                      # 文档更新
  main_documents:
    - docs/TESTING-MASTER-STATUS.md
    - docs/INTEGRATION-TEST-TRACKER.md
```

### 自定义配置

要添加新的归档阶段：

1. 编辑 `archive-config.yml`
2. 在 `phases` 部分添加新阶段
3. 定义阶段的归档规则
4. 运行脚本测试

---

## 🔧 故障排除

### 常见问题

#### 1. 权限错误

```bash
# 错误: Permission denied
# 解决: 给脚本添加可执行权限
chmod +x archive-phase.sh
```

#### 2. 文件不存在

```
# 错误: 文件不存在，跳过: XXX.md
# 解决: 检查文件名拼写，确认文件在 docs/ 目录
ls -la ../../docs/
```

#### 3. Git 暂存失败

```bash
# 错误: fatal: pathspec 'docs' did not match any files
# 解决: 确保在 Git 仓库中运行，检查文件路径
git status
```

---

## 📝 最佳实践

### 归档时机

- ✅ **单元测试**: 每完成一个 Phase 后立即归档
- ✅ **集成测试**: 每完成一周工作后归档
- ✅ **长期项目**: 建议每月归档一次历史文档

### 归档原则

1. **及时归档**: 完成阶段后尽快归档，避免积累
2. **清晰标记**: 在主文档中清晰标记阶段完成
3. **完整文档**: 确保归档的文档完整且自包含
4. **审查更改**: 归档后务必审查 git 更改
5. **保持简洁**: 根目录只保留 3-4 个主文档

### 文档链接更新

归档后，需要更新以下文档中的链接：

1. **TESTING-MASTER-STATUS.md**
    - 更新"文档生成历史"部分的链接
    - 添加新归档阶段的链接

2. **DOCUMENTATION-INDEX.md**
    - 更新"归档文档"部分
    - 添加新归档的描述

3. **README.md**
    - 更新"历史报告归档"部分
    - 添加新归档的入口链接

---

## 🔄 自动化改进

### 未来计划

- [ ] 自动检测完成标记
- [ ] 自动更新文档链接
- [ ] 集成到 CI/CD 流程
- [ ] 生成归档统计报告
- [ ] 支持批量归档

### 扩展脚本

可以基于 `archive-phase.sh` 扩展更多功能：

```bash
# 批量归档多个阶段
./archive-multiple-phases.sh phase6 phase7 phase8

# 自动检测并归档所有已完成阶段
./auto-archive-all.sh

# 生成归档统计报告
./archive-stats.sh
```

---

## 📞 支持与反馈

如有问题或建议，请联系：

- **团队**: Test Team
- **邮箱**: test-team@example.com
- **Issue**: 在项目中提交 Issue

---

**最后更新**: 2025-11-10
**文档版本**: 1.0
**维护者**: Test Team

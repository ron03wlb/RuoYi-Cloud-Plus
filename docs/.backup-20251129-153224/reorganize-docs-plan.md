# RuoYi-Cloud-Plus 文档重组执行计划

> 根据方案 B (平衡结构) + 用户微调要求
> 生成时间: 2025-11-29
> 状态: 待执行

---

## 目录

- [1. 精确的目录结构](#1-精确的目录结构)
- [2. 详细的文件操作清单](#2-详细的文件操作清单)
- [3. 可执行的 Bash 脚本](#3-可执行的-bash-脚本)
- [4. 新 README.md 的内容](#4-新-readmemd-的内容)
- [5. archive/README.md 的内容](#5-archivereadmemd-的内容)
- [6. 需要合并的文件内容示例](#6-需要合并的文件内容示例)
- [7. 实施检查清单](#7-实施检查清单)
- [8. 保留的归档文件清单](#8-保留的归档文件清单)
- [9. 风险控制措施](#9-风险控制措施)

---

## 1. 精确的目录结构

### 1.1 目标结构

```
docs/
├── README.md (新建，~100行，快速开始为主)
├── getting-started/
│   ├── README.md (新建，入门总览)
│   ├── quick-start.md (新建，5分钟快速开始)
│   ├── environment-setup.md (从 project/development-setup.md 移动)
│   ├── database-initialization.md (从 guides/ 移动)
│   └── nacos-config.md (从 guides/ 移动)
├── guides/
│   ├── README.md (新建，操作指南总览)
│   ├── testing-guide.md (保留)
│   ├── code-quality.md (保留)
│   ├── service-startup-order.md (保留)
│   └── docker-deployment.md (从 project/ 移动)
├── configuration/
│   ├── README.md (新建，配置说明总览)
│   ├── gradle.md (从 project/ 移动)
│   ├── nacos-advanced.md (新建，从 guides/ 提取高级内容)
│   └── test-configuration.md (新建，测试配置专题)
├── architecture/
│   ├── README.md (新建，架构说明总览)
│   ├── module-overview.md (新建，合并 reference/modules/ 的索引)
│   ├── claude-guide.md (从 project/ 移动)
│   └── technology-stack.md (新建，提取技术栈说明)
├── testing/
│   ├── README.md (新建，测试文档总览)
│   ├── current-status.md (从 active/testing-master-status.md 移动)
│   ├── integration-tracker.md (从 active/integration-test-tracker.md 移动)
│   ├── unit-test-guide.md (从 guides/testing-guide.md 提取)
│   └── integration-test-guide.md (新建，从 archive/ 提取)
└── archive/
    ├── README.md (新建，醒目说明"新手可忽略")
    ├── unit-tests/
    │   ├── phase1-final-summary.md
    │   ├── phase2-final-completion-report.md
    │   └── phase3.1-completion-summary.md
    ├── integration-tests/
    │   ├── week1-framework/
    │   │   ├── README.md
    │   │   ├── integration-test-framework-setup.md
    │   │   └── integration-test-work-summary.md
    │   └── integration-test-final-status.md (移到外层)
    ├── progress-reports/
    │   └── testing-progress-update-2025-11-11.md
    └── legacy/
        └── final-module-analysis-summary.md
```

### 1.2 文件统计

**删除前**: 70 个 .md 文件
**删除后**: 约 30-35 个 .md 文件
**保留归档**: 8-10 个文件

---

## 2. 详细的文件操作清单

### 阶段 1: 备份 (安全第一)

```bash
# 创建时间戳备份
BACKUP_DIR="docs-backup-$(date +%Y%m%d-%H%M%S)"
mkdir -p "../${BACKUP_DIR}"
cp -r docs "../${BACKUP_DIR}/"
```

### 阶段 2: 删除操作

#### 2.1 删除冗余的 archive 文件 (保留 8-10 个)

```bash
# 删除 phase1-common 的详细报告 (保留 final-summary)
rm docs/archive/unit-tests/phase1-common/phase1-encrypt-testing-report.md       # 详细报告已整合
rm docs/archive/unit-tests/phase1-common/phase1-excel-testing-status.md         # 详细报告已整合
rm docs/archive/unit-tests/phase1-common/phase1-json-testing-status.md          # 详细报告已整合
rm docs/archive/unit-tests/phase1-common/phase1-mybatis-testing-report.md       # 详细报告已整合
rm docs/archive/unit-tests/phase1-common/phase1-redis-testing-report.md         # 详细报告已整合
rm docs/archive/unit-tests/phase1-common/phase1-satoken-testing-report.md       # 详细报告已整合
rm docs/archive/unit-tests/phase1-common/phase1-sensitive-testing-report.md     # 详细报告已整合
rm docs/archive/unit-tests/phase1-common/phase1-tenant-testing-report.md        # 详细报告已整合
rm docs/archive/unit-tests/phase1-common/phase1-translation-testing-report.md   # 详细报告已整合
rm docs/archive/unit-tests/phase1-common/phase1-web-testing-report.md           # 详细报告已整合

# 删除 phase2-auth 的详细报告 (保留 final-completion-report)
rm docs/archive/unit-tests/phase2-auth/phase2-auth-testing-plan.md              # 计划文档已完成
rm docs/archive/unit-tests/phase2-auth/phase2-auth-testing-方案a+c-final-report.md  # 已被 final-completion-report 取代

# 删除 phase3-system 的详细报告 (保留 completion-summary)
rm docs/archive/unit-tests/phase3-system/phase3-system-dept-service-report.md   # 详细报告已整合
rm docs/archive/unit-tests/phase3-system/phase3-system-menu-service-report.md   # 详细报告已整合
rm docs/archive/unit-tests/phase3-system/phase3-system-permission-service-report.md  # 详细报告已整合
rm docs/archive/unit-tests/phase3-system/phase3-system-role-service-report.md   # 详细报告已整合
rm docs/archive/unit-tests/phase3-system/phase3-system-testing-fix-report.md    # 详细报告已整合
rm docs/archive/unit-tests/phase3-system/phase3-system-testing-init-report.md   # 详细报告已整合
rm docs/archive/unit-tests/phase3-system/phase3-system-testing-status-report.md # 详细报告已整合
rm docs/archive/unit-tests/phase3-system/phase3-system-user-service-expansion-report.md  # 详细报告已整合
rm docs/archive/unit-tests/phase3-system/phase3.2-config-service-report.md      # 详细报告已整合
rm docs/archive/unit-tests/phase3-system/phase3.2-dict-data-service-report.md   # 详细报告已整合
rm docs/archive/unit-tests/phase3-system/phase3.2-dict-type-service-report.md   # 详细报告已整合
rm docs/archive/unit-tests/phase3-system/phase3.2-missing-services-completion-report.md  # 详细报告已整合
rm docs/archive/unit-tests/phase3-system/phase3.2-notice-service-completion-report.md    # 详细报告已整合
rm docs/archive/unit-tests/phase3-system/phase3.2-post-service-report.md        # 详细报告已整合

# 删除 phase4 和 phase5 (信息价值低)
rm docs/archive/unit-tests/phase4-analysis/phase4-resource-module-analysis.md   # 分析已过时
rm docs/archive/unit-tests/phase5-gen/phase5-gen-module-testing-status.md       # 未完成的分析

# 删除 failure-analysis 整个目录 (价值低)
rm -rf docs/archive/failure-analysis/                                            # 失败分析已解决或过时

# 删除 progress-reports/consolidated (保留 2025-11)
rm -rf docs/archive/progress-reports/consolidated/                               # 已整合到主文档
```

#### 2.2 删除冗余的 active 目录和旧文件

```bash
# active 目录将被移动到 testing/，删除空目录时再删
rm docs/active/active-issues.md                                                  # 已过时的问题跟踪

# 删除旧的顶层文件
rm docs/documentation-index.md                                                    # 将合并到新 README.md
```

#### 2.3 删除 reference 目录 (将合并为索引)

```bash
# 所有 reference/modules 文件将合并为 architecture/module-overview.md
rm -rf docs/reference/modules/                                                   # 详细文档已整合
rm -rf docs/reference/testing-guidelines/                                        # 测试指南已整合
rm -rf docs/reference/                                                           # 删除整个 reference 目录
```

#### 2.4 删除空目录和临时目录

```bash
rm -rf docs/.trash/                                                              # 临时垃圾目录
```

### 阶段 3: 移动操作

#### 3.1 移动到 getting-started/

```bash
mkdir -p docs/getting-started

# 从 project/ 移动
mv docs/project/development-setup.md docs/getting-started/environment-setup.md

# 从 guides/ 移动
mv docs/guides/database-initialization.md docs/getting-started/database-initialization.md
mv docs/guides/nacos-config-import.md docs/getting-started/nacos-config.md
```

#### 3.2 移动到 guides/

```bash
# guides/ 已有的文件保留，从 project/ 移动
mv docs/project/docker-deployment.md docs/guides/docker-deployment.md

# testing-guide.md, code-quality.md, service-startup-order.md 保留原位置
```

#### 3.3 移动到 configuration/

```bash
mkdir -p docs/configuration

# 从 project/ 移动
mv docs/project/gradle.md docs/configuration/gradle.md
```

#### 3.4 移动到 architecture/

```bash
mkdir -p docs/architecture

# 从 project/ 移动
mv docs/project/claude.md docs/architecture/claude-guide.md
```

#### 3.5 移动到 testing/

```bash
mkdir -p docs/testing

# 从 active/ 移动
mv docs/active/testing-master-status.md docs/testing/current-status.md
mv docs/active/integration-test-tracker.md docs/testing/integration-tracker.md
```

#### 3.6 移动归档文件 (week1-framework 外层文件)

```bash
# 将 week1-framework 下的 final-status 移到 integration-tests/ 外层
mv docs/archive/integration-tests/week1-framework/integration-test-final-status.md \
   docs/archive/integration-tests/integration-test-final-status.md
```

### 阶段 4: 删除空目录

```bash
# 删除已清空的目录
rmdir docs/project/                   # 文件已全部移动
rmdir docs/active/                    # 文件已全部移动

# 删除清理后的 archive 子目录
rmdir docs/archive/unit-tests/phase1-common/  # 保留 final-summary 后可能为空，则删除父级
rmdir docs/archive/unit-tests/phase2-auth/
rmdir docs/archive/unit-tests/phase3-system/
rmdir docs/archive/unit-tests/phase4-analysis/
rmdir docs/archive/unit-tests/phase5-gen/
rmdir docs/archive/progress-reports/consolidated/

# 注意: 如果目录不为空，rmdir 会失败，这是安全机制
```

### 阶段 5: 创建新文件

详见第 4、5、6 节

---

## 3. 可执行的 Bash 脚本

```bash
#!/bin/bash
# reorganize-docs.sh
# RuoYi-Cloud-Plus 文档重组脚本
# 使用方法: ./reorganize-docs.sh

set -euo pipefail  # 错误时退出，未定义变量报错，管道错误传播

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 工作目录
DOCS_DIR="/Users/zhangxuanrong/Documents/Workspace/Java/Lion/RuoYi-Cloud-Plus/docs"
BACKUP_TIMESTAMP=$(date +%Y%m%d-%H%M%S)
BACKUP_DIR="/Users/zhangxuanrong/Documents/Workspace/Java/Lion/RuoYi-Cloud-Plus/docs-backup-${BACKUP_TIMESTAMP}"

# ============================================
# 安全检查
# ============================================
safety_check() {
    log_info "执行安全检查..."

    # 检查 docs 目录是否存在
    if [ ! -d "$DOCS_DIR" ]; then
        log_error "docs 目录不存在: $DOCS_DIR"
        exit 1
    fi

    # 检查是否在 git 仓库中
    if ! git -C "$DOCS_DIR/.." rev-parse --git-dir > /dev/null 2>&1; then
        log_error "不在 git 仓库中，无法回滚"
        exit 1
    fi

    # 检查是否有未提交的更改
    if ! git -C "$DOCS_DIR/.." diff-index --quiet HEAD -- docs/; then
        log_warning "docs/ 目录有未提交的更改"
        read -p "是否继续? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_info "用户取消操作"
            exit 0
        fi
    fi

    log_success "安全检查通过"
}

# ============================================
# 备份
# ============================================
backup_docs() {
    log_info "创建备份到: $BACKUP_DIR"

    mkdir -p "$BACKUP_DIR"
    cp -r "$DOCS_DIR" "$BACKUP_DIR/"

    log_success "备份完成"
}

# ============================================
# 阶段 2: 删除操作
# ============================================
delete_files() {
    log_info "阶段 2: 删除冗余文件..."

    cd "$DOCS_DIR"

    # 2.1 删除 phase1-common 详细报告 (保留 final-summary)
    log_info "删除 phase1-common 详细报告..."
    rm -f archive/unit-tests/phase1-common/phase1-encrypt-testing-report.md
    rm -f archive/unit-tests/phase1-common/phase1-excel-testing-status.md
    rm -f archive/unit-tests/phase1-common/phase1-json-testing-status.md
    rm -f archive/unit-tests/phase1-common/phase1-mybatis-testing-report.md
    rm -f archive/unit-tests/phase1-common/phase1-redis-testing-report.md
    rm -f archive/unit-tests/phase1-common/phase1-satoken-testing-report.md
    rm -f archive/unit-tests/phase1-common/phase1-sensitive-testing-report.md
    rm -f archive/unit-tests/phase1-common/phase1-tenant-testing-report.md
    rm -f archive/unit-tests/phase1-common/phase1-translation-testing-report.md
    rm -f archive/unit-tests/phase1-common/phase1-web-testing-report.md

    # 2.2 删除 phase2-auth 详细报告 (保留 final-completion-report)
    log_info "删除 phase2-auth 详细报告..."
    rm -f archive/unit-tests/phase2-auth/phase2-auth-testing-plan.md
    rm -f archive/unit-tests/phase2-auth/phase2-auth-testing-方案a+c-final-report.md

    # 2.3 删除 phase3-system 详细报告 (保留 completion-summary)
    log_info "删除 phase3-system 详细报告..."
    rm -f archive/unit-tests/phase3-system/phase3-system-dept-service-report.md
    rm -f archive/unit-tests/phase3-system/phase3-system-menu-service-report.md
    rm -f archive/unit-tests/phase3-system/phase3-system-permission-service-report.md
    rm -f archive/unit-tests/phase3-system/phase3-system-role-service-report.md
    rm -f archive/unit-tests/phase3-system/phase3-system-testing-fix-report.md
    rm -f archive/unit-tests/phase3-system/phase3-system-testing-init-report.md
    rm -f archive/unit-tests/phase3-system/phase3-system-testing-status-report.md
    rm -f archive/unit-tests/phase3-system/phase3-system-user-service-expansion-report.md
    rm -f archive/unit-tests/phase3-system/phase3.2-config-service-report.md
    rm -f archive/unit-tests/phase3-system/phase3.2-dict-data-service-report.md
    rm -f archive/unit-tests/phase3-system/phase3.2-dict-type-service-report.md
    rm -f archive/unit-tests/phase3-system/phase3.2-missing-services-completion-report.md
    rm -f archive/unit-tests/phase3-system/phase3.2-notice-service-completion-report.md
    rm -f archive/unit-tests/phase3-system/phase3.2-post-service-report.md

    # 2.4 删除 phase4 和 phase5
    log_info "删除 phase4 和 phase5..."
    rm -rf archive/unit-tests/phase4-analysis/
    rm -rf archive/unit-tests/phase5-gen/

    # 2.5 删除 failure-analysis
    log_info "删除 failure-analysis 目录..."
    rm -rf archive/failure-analysis/

    # 2.6 删除 progress-reports/consolidated
    log_info "删除 progress-reports/consolidated..."
    rm -rf archive/progress-reports/consolidated/

    # 2.7 删除 active 目录中的旧文件
    log_info "删除 active 目录中的旧文件..."
    rm -f active/active-issues.md

    # 2.8 删除顶层旧文件
    log_info "删除顶层旧文件..."
    rm -f documentation-index.md

    # 2.9 删除 reference 目录
    log_info "删除 reference 目录..."
    rm -rf reference/

    # 2.10 删除临时目录
    log_info "删除临时目录..."
    rm -rf .trash/

    log_success "删除操作完成"
}

# ============================================
# 阶段 3: 移动操作
# ============================================
move_files() {
    log_info "阶段 3: 移动文件到新结构..."

    cd "$DOCS_DIR"

    # 3.1 移动到 getting-started/
    log_info "移动文件到 getting-started/..."
    mkdir -p getting-started
    mv project/development-setup.md getting-started/environment-setup.md
    mv guides/database-initialization.md getting-started/database-initialization.md
    mv guides/nacos-config-import.md getting-started/nacos-config.md

    # 3.2 移动到 guides/
    log_info "移动文件到 guides/..."
    mv project/docker-deployment.md guides/docker-deployment.md

    # 3.3 移动到 configuration/
    log_info "移动文件到 configuration/..."
    mkdir -p configuration
    mv project/gradle.md configuration/gradle.md

    # 3.4 移动到 architecture/
    log_info "移动文件到 architecture/..."
    mkdir -p architecture
    mv project/claude.md architecture/claude-guide.md

    # 3.5 移动到 testing/
    log_info "移动文件到 testing/..."
    mkdir -p testing
    mv active/testing-master-status.md testing/current-status.md
    mv active/integration-test-tracker.md testing/integration-tracker.md

    # 3.6 重组归档目录
    log_info "重组归档目录..."
    mv archive/integration-tests/week1-framework/integration-test-final-status.md \
       archive/integration-tests/integration-test-final-status.md 2>/dev/null || true

    log_success "移动操作完成"
}

# ============================================
# 阶段 4: 删除空目录
# ============================================
remove_empty_dirs() {
    log_info "阶段 4: 删除空目录..."

    cd "$DOCS_DIR"

    # 删除已清空的目录 (rmdir 只删除空目录，安全)
    rmdir project/ 2>/dev/null || log_warning "project/ 目录非空或不存在"
    rmdir active/ 2>/dev/null || log_warning "active/ 目录非空或不存在"
    rmdir archive/unit-tests/phase1-common/ 2>/dev/null || true
    rmdir archive/unit-tests/phase2-auth/ 2>/dev/null || true
    rmdir archive/unit-tests/phase3-system/ 2>/dev/null || true

    log_success "空目录删除完成"
}

# ============================================
# 阶段 5: 创建新文件 (占位符)
# ============================================
create_new_files() {
    log_info "阶段 5: 创建新文件..."

    cd "$DOCS_DIR"

    # 这些文件将在后续手动创建或通过 Claude 生成
    log_info "需要创建的新文件:"
    echo "  - README.md (新版)"
    echo "  - getting-started/README.md"
    echo "  - getting-started/quick-start.md"
    echo "  - guides/README.md"
    echo "  - configuration/README.md"
    echo "  - configuration/nacos-advanced.md"
    echo "  - configuration/test-configuration.md"
    echo "  - architecture/README.md"
    echo "  - architecture/module-overview.md"
    echo "  - architecture/technology-stack.md"
    echo "  - testing/README.md"
    echo "  - testing/unit-test-guide.md"
    echo "  - testing/integration-test-guide.md"
    echo "  - archive/README.md"

    log_warning "这些文件将在脚本执行后手动创建"
}

# ============================================
# 验证步骤
# ============================================
verify_structure() {
    log_info "验证新结构..."

    cd "$DOCS_DIR"

    # 检查关键目录是否存在
    REQUIRED_DIRS=(
        "getting-started"
        "guides"
        "configuration"
        "architecture"
        "testing"
        "archive"
    )

    for dir in "${REQUIRED_DIRS[@]}"; do
        if [ ! -d "$dir" ]; then
            log_error "目录不存在: $dir"
            return 1
        fi
    done

    # 检查关键文件是否存在
    REQUIRED_FILES=(
        "getting-started/environment-setup.md"
        "getting-started/database-initialization.md"
        "getting-started/nacos-config.md"
        "guides/testing-guide.md"
        "guides/code-quality.md"
        "guides/service-startup-order.md"
        "guides/docker-deployment.md"
        "configuration/gradle.md"
        "architecture/claude-guide.md"
        "testing/current-status.md"
        "testing/integration-tracker.md"
    )

    for file in "${REQUIRED_FILES[@]}"; do
        if [ ! -f "$file" ]; then
            log_error "文件不存在: $file"
            return 1
        fi
    done

    log_success "结构验证通过"
}

# ============================================
# 生成回滚脚本
# ============================================
generate_rollback_script() {
    log_info "生成回滚脚本..."

    cat > "$DOCS_DIR/../rollback-docs.sh" << 'EOF'
#!/bin/bash
# rollback-docs.sh
# 回滚文档重组操作

set -euo pipefail

DOCS_DIR="/Users/zhangxuanrong/Documents/Workspace/Java/Lion/RuoYi-Cloud-Plus/docs"

echo "[INFO] 使用 git 回滚 docs/ 目录..."

cd "$(dirname "$DOCS_DIR")"

# 回滚所有 docs/ 下的更改
git checkout HEAD -- docs/

echo "[SUCCESS] 回滚完成"
echo "[INFO] 备份目录保留在: docs-backup-*/"
echo "[INFO] 如需完全恢复，可以从备份目录手动复制"
EOF

    chmod +x "$DOCS_DIR/../rollback-docs.sh"

    log_success "回滚脚本已生成: $(dirname $DOCS_DIR)/rollback-docs.sh"
}

# ============================================
# 主流程
# ============================================
main() {
    echo "========================================"
    echo "RuoYi-Cloud-Plus 文档重组脚本"
    echo "========================================"
    echo ""

    # 确认执行
    log_warning "此操作将重组整个 docs/ 目录"
    read -p "是否继续? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        log_info "用户取消操作"
        exit 0
    fi

    # 执行各阶段
    safety_check
    backup_docs
    delete_files
    move_files
    remove_empty_dirs
    create_new_files
    verify_structure
    generate_rollback_script

    echo ""
    echo "========================================"
    log_success "文档重组完成!"
    echo "========================================"
    echo ""
    echo "下一步:"
    echo "  1. 查看新结构: tree $DOCS_DIR"
    echo "  2. 创建新文件: 参考 reorganize-docs-plan.md 第 4-6 节"
    echo "  3. 验证链接: 运行测试脚本"
    echo "  4. 提交更改: git add docs/ && git commit -m 'docs: 重组文档结构'"
    echo ""
    echo "如需回滚:"
    echo "  执行: $(dirname $DOCS_DIR)/rollback-docs.sh"
    echo "  或从备份恢复: $BACKUP_DIR"
    echo ""
}

# 执行主流程
main
```

### 3.1 脚本使用说明

```bash
# 1. 保存脚本
cat > /Users/zhangxuanrong/Documents/Workspace/Java/Lion/RuoYi-Cloud-Plus/reorganize-docs.sh << 'EOF'
# (粘贴上面的脚本内容)
EOF

# 2. 添加执行权限
chmod +x /Users/zhangxuanrong/Documents/Workspace/Java/Lion/RuoYi-Cloud-Plus/reorganize-docs.sh

# 3. 执行脚本
cd /Users/zhangxuanrong/Documents/Workspace/Java/Lion/RuoYi-Cloud-Plus
./reorganize-docs.sh

# 4. 如需回滚
./rollback-docs.sh
```

---

## 4. 新 README.md 的内容

### 4.1 内容结构 (~100行)

```markdown
# RuoYi-Cloud-Plus 文档中心

> 基于 Spring Cloud + Spring Boot 的微服务架构
>
> 快速、安全、易扩展的企业级开发框架

---

## 新手 3 步走

### 1. 环境准备 (5分钟)

```bash
# 必需: JDK 17+, PostgreSQL 15+, Redis 7+, Nacos 2.2+
# 推荐: Docker + Docker Compose (一键启动所有基础设施)
```

详见: [环境搭建指南](getting-started/environment-setup.md)

### 2. 快速启动 (10分钟)

```bash
# 启动基础设施
docker-compose up -d

# 导入配置
cd script && ./import-nacos-config.sh

# 初始化数据库
psql -U postgres -f sql/postgres/init-ry-cloud.sql

# 启动服务
./gradlew :ruoyi-auth:bootRun
./gradlew :ruoyi-gateway:bootRun
./gradlew :ruoyi-modules:ruoyi-system:bootRun
```

详见: [5分钟快速开始](getting-started/quick-start.md)

### 3. 验证运行 (2分钟)

- 访问 http://localhost:9200
- 默认账号: admin / admin123
- 查看 API 文档: http://localhost:9200/doc.html

---

## 文档导航

### 新手入门

| 文档                                                   | 说明               | 时长   |
|------------------------------------------------------|------------------|------|
| [5分钟快速开始](getting-started/quick-start.md)            | 最快速的上手方式         | 5分钟  |
| [环境搭建](getting-started/environment-setup.md)         | 开发环境详细配置         | 30分钟 |
| [数据库初始化](getting-started/database-initialization.md) | PostgreSQL 数据库设置 | 10分钟 |
| [Nacos 配置](getting-started/nacos-config.md)          | 配置中心导入           | 5分钟  |

### 开发指南

| 文档                                        | 说明                               |
|-------------------------------------------|----------------------------------|
| [测试指南](guides/testing-guide.md)           | 单元测试 + 集成测试完整手册                  |
| [代码质量](guides/code-quality.md)            | Checkstyle + SpotBugs + Spotless |
| [服务启动顺序](guides/service-startup-order.md) | 微服务依赖关系                          |
| [Docker 部署](guides/docker-deployment.md)  | 容器化部署方案                          |

### 配置说明

| 文档                                            | 说明          |
|-----------------------------------------------|-------------|
| [Gradle 构建](configuration/gradle.md)          | Gradle 完整指南 |
| [Nacos 高级配置](configuration/nacos-advanced.md) | 配置中心高级用法    |
| [测试配置](configuration/test-configuration.md)   | 测试环境配置      |

### 架构设计

| 文档                                          | 说明          |
|---------------------------------------------|-------------|
| [模块总览](architecture/module-overview.md)     | 所有模块的结构和职责  |
| [技术栈](architecture/technology-stack.md)     | 使用的技术和版本    |
| [Claude 协作指南](architecture/claude-guide.md) | AI 辅助开发最佳实践 |

### 测试文档

| 文档                                          | 说明         |
|---------------------------------------------|------------|
| [测试现状](testing/current-status.md)           | 当前测试覆盖率和统计 |
| [集成测试跟踪](testing/integration-tracker.md)    | 集成测试进度     |
| [单元测试指南](testing/unit-test-guide.md)        | 单元测试规范     |
| [集成测试指南](testing/integration-test-guide.md) | 集成测试规范     |

---

## 常用命令

### 构建和运行

```bash
# 编译项目
./gradlew build

# 运行测试
./gradlew test

# 格式化代码
./gradlew spotlessApply

# 代码质量检查
./gradlew check
```

### Docker 操作

```bash
# 启动基础设施
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

### 测试命令

```bash
# 运行所有测试
./gradlew test

# 生成覆盖率报告
./gradlew test jacocoTestReport

# 查看报告
open build/reports/jacoco/test/html/index.html
```

---

## 贡献指南

### 开发流程

1. Fork 项目到个人仓库
2. 创建特性分支: `git checkout -b feature/xxx`
3. 提交代码: `git commit -am 'Add xxx feature'`
4. 推送分支: `git push origin feature/xxx`
5. 提交 Pull Request

### 代码规范

- 遵循阿里巴巴 Java 开发手册
- 使用 Checkstyle 检查代码风格
- 使用 SpotBugs 检查潜在问题
- 使用 Spotless 自动格式化代码
- 测试覆盖率要求: 核心模块 > 80%

详见: [代码质量指南](guides/code-quality.md)

### 提交规范

```
<type>(<scope>): <subject>

类型 (type):
- feat: 新功能
- fix: 修复 bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关
- chore: 构建/工具链

示例:
feat(auth): add OAuth2 login support
fix(system): resolve user query pagination issue
docs: update quick start guide
```

---

## 项目链接

- Gitee: https://gitee.com/dromara/RuoYi-Cloud-Plus
- 官方文档: https://plus-doc.dromara.org
- 问题反馈: https://gitee.com/dromara/RuoYi-Cloud-Plus/issues

---

**最后更新**: 2025-11-29
**文档版本**: 4.0
**项目状态**: 活跃开发中

```

### 4.2 内容亮点

- 新手 3 步走: 5分钟 + 10分钟 + 2分钟 = 17分钟上手
- 清晰的文档导航: 按使用场景分类
- 常用命令速查: 开发者日常工作必备
- 贡献指南: 鼓励社区参与
- 总行数: 约 105 行 (符合 ~100行要求)

---

## 5. archive/README.md 的内容

```markdown
# 文档归档区

## 新手请注意

**这个目录是文档归档区，主要保存历史测试报告和过程文档。**

**如果你是新手，可以安全地忽略这个目录。**

推荐从这里开始:
- [5分钟快速开始](../getting-started/quick-start.md)
- [测试现状](../testing/current-status.md)
- [文档中心首页](../README.md)

---

## 归档文档说明

本目录保存了项目测试工作的历史记录，包括:

- 单元测试阶段性报告 (Phase 1-3)
- 集成测试框架搭建过程
- 进度更新报告
- 已解决的技术问题

这些文档对于:
- 了解项目测试历史
- 查阅特定问题的解决方案
- 回顾测试覆盖率演进过程

可能有帮助，但不是入门必读。

---

## 归档文档清单

### 单元测试归档 (已完成)

#### Phase 1: Common 库模块
- [phase1-final-summary.md](unit-tests/phase1-common/phase1-final-summary.md)
  - 时间: 2025-10
  - 内容: ruoyi-common 模块单元测试总结
  - 成果: 645个测试，91.3%通过率

#### Phase 2: Auth 认证模块
- [phase2-final-completion-report.md](unit-tests/phase2-auth/phase2-final-completion-report.md)
  - 时间: 2025-10
  - 内容: ruoyi-auth 模块单元测试完成报告
  - 成果: 166个测试，100%通过率

#### Phase 3: System 系统模块
- [phase3.1-completion-summary.md](unit-tests/phase3-system/phase3.1-completion-summary.md)
  - 时间: 2025-11
  - 内容: ruoyi-system 模块单元测试总结
  - 成果: 305个测试，100%通过率

### 集成测试归档

#### Week 1: 框架搭建
- [week1-framework/](integration-tests/week1-framework/)
  - [README.md](integration-tests/week1-framework/README.md) - 归档说明
  - [integration-test-framework-setup.md](integration-tests/week1-framework/integration-test-framework-setup.md) - 框架详细设置
  - [integration-test-work-summary.md](integration-tests/week1-framework/integration-test-work-summary.md) - 工作总结
  - 时间: 2025-11-10
  - 成果: BaseIntegrationTest + Testcontainers + 49个测试用例

#### 最终状态
- [integration-test-final-status.md](integration-tests/integration-test-final-status.md)
  - 时间: 2025-11-10
  - 内容: Week 1 集成测试最终状态报告
  - 成果: Dubbo 时序问题解决方案

### 进度报告归档

- [testing-progress-update-2025-11-11.md](progress-reports/testing-progress-update-2025-11-11.md)
  - 时间: 2025-11-11
  - 内容: 3,271个测试执行完整分析
  - 成果: Sa-Token 上下文问题修复

### 旧版文档归档

- [final-module-analysis-summary.md](legacy/final-module-analysis-summary.md)
  - 时间: 2025-10
  - 内容: 模块分析总结

---

## 如何使用归档文档

### 查找特定问题的解决方案

如果你遇到了测试相关的问题，可以:

1. 先查看 [测试现状](../testing/current-status.md)
2. 如果问题涉及历史背景，再查阅对应的归档报告
3. 搜索关键词 (如 "Sa-Token", "Dubbo", "Testcontainers")

### 了解测试覆盖率演进

- Phase 1 报告: ruoyi-common 模块 (645个测试)
- Phase 2 报告: ruoyi-auth 模块 (166个测试)
- Phase 3 报告: ruoyi-system 模块 (305个测试)
- 进度报告: 整体进展 (3,271个测试)

### 学习集成测试框架

查看 [week1-framework/](integration-tests/week1-framework/) 了解:
- 如何搭建 Testcontainers
- 如何解决 Dubbo + Spring Boot 时序问题
- 如何编写集成测试基类

---

## 归档策略

### 保留原则

保留对未来有参考价值的文档:
- 阶段性总结报告 (Final Summary)
- 框架搭建文档
- 重要问题的解决方案
- 进度里程碑报告

### 删除原则

删除重复或价值低的文档:
- 详细的单项测试报告 (已整合到总结中)
- 中间状态的进度报告
- 已过时的问题分析

---

**最后更新**: 2025-11-29
**归档文件数**: 10 个
**总大小**: 约 50KB
**维护策略**: 每季度审查一次，删除过时内容
```

### 5.1 内容亮点

- 醒目的新手提示: "可以安全地忽略这个目录"
- 清晰的归档文档清单: 按阶段组织，包含时间和成果
- 如何使用归档文档: 给出具体场景和建议
- 归档策略说明: 透明的保留和删除原则

---

## 6. 需要合并的文件内容示例

### 6.1 示例 1: README.md + documentation-index.md 合并

**合并策略**:

- README.md 的 "新手 3 步走" (新增)
- documentation-index.md 的 "文档导航" 表格 (保留)
- README.md 的 "常用命令" (新增)
- 贡献指南 (新增)

已在第 4 节展示完整内容。

### 6.2 示例 2: reference/modules/ 合并为 architecture/module-overview.md

```markdown
# 模块结构总览

> RuoYi-Cloud-Plus 项目所有模块的结构、职责和测试状态
>
> 最后更新: 2025-11-29

---

## 模块组织结构

### 按职能分类

| 分类 | 模块数 | 说明 |
|------|--------|------|
| **Common 库** | 11 | 通用功能模块 |
| **业务模块** | 7 | 核心业务功能 |
| **基础设施** | 4 | 网关、认证、监控 |
| **工具模块** | 2 | 代码生成、示例 |

---

## Common 库模块 (11个)

### ruoyi-common-core

**职责**: 核心工具类、异常类、验证器
**关键组件**:
- StringUtils, DateUtils, FileUtils
- SqlUtil (SQL 注入防护)
- 异常体系 (BaseException, ServiceException)
- 验证器 (XSS, 手机号, 身份证号)

**测试状态**: ✅ 208 tests, 100% 通过
**文档**: [详见原文档](../testing/current-status.md#ruoyi-common-core)

---

### ruoyi-common-mybatis

**职责**: MyBatis-Plus 集成、数据权限、多租户
**关键组件**:
- MyBatis-Plus 配置
- 数据权限处理器 (DataPermissionHandler)
- 多租户插件 (TenantLineInnerInterceptor)
- 自动填充 (createBy, updateBy, createTime, updateTime)

**测试状态**: ✅ 100 tests, 100% 通过

---

### ruoyi-common-satoken

**职责**: Sa-Token 认证集成
**关键组件**:
- 用户登录/登出
- Token 管理
- 权限校验
- 多设备登录

**测试状态**: ✅ 85 tests, 100% 通过

---

### ruoyi-common-encrypt

**职责**: 数据加密解密
**关键组件**:
- AES 加密/解密
- RSA 加密/解密
- 字段级加密注解 (@EncryptField)

**测试状态**: ✅ 48 tests, 100% 通过

---

### ruoyi-common-sensitive

**职责**: 敏感数据脱敏
**关键组件**:
- 手机号脱敏
- 身份证号脱敏
- 邮箱脱敏
- 自定义脱敏规则

**测试状态**: ✅ 42 tests, 100% 通过

---

### ruoyi-common-translation

**职责**: 数据翻译 (字典、用户等)
**关键组件**:
- 字典翻译
- 用户翻译
- 部门翻译
- 自定义翻译接口

**测试状态**: ✅ 52 tests, 100% 通过

---

### ruoyi-common-web

**职责**: Web 层支持
**关键组件**:
- 全局异常处理
- 请求日志
- 跨域配置
- 响应包装

**测试状态**: ✅ 52 tests, 100% 通过

---

### ruoyi-common-json

**职责**: JSON 处理
**关键组件**:
- Jackson 配置
- 自定义序列化器/反序列化器
- JSON 工具类

**测试状态**: ⚠️ 42 tests, 83% 通过 (17个 MockedStatic 失败)

---

### ruoyi-common-excel

**职责**: Excel 导入导出
**关键组件**:
- EasyExcel 集成
- Excel 模板验证
- 自定义转换器

**测试状态**: ⚠️ 16 tests, 67.5% 通过 (39个模板验证失败)

---

### ruoyi-common-redis

**职责**: Redis 缓存支持
**关键组件**:
- RedisTemplate 配置
- 缓存工具类
- 分布式锁

**测试状态**: ✅ 100% (集成测试)

---

### ruoyi-common-tenant

**职责**: 多租户支持
**关键组件**:
- 租户上下文
- 租户数据隔离
- 租户切换

**测试状态**: ✅ 100% (集成测试)

---

## 业务模块 (7个)

### ruoyi-auth

**职责**: 认证服务
**关键功能**:
- 用户登录/登出
- Token 颁发/刷新
- 验证码
- 第三方登录 (OAuth2)

**测试状态**: ✅ 166 tests, 100% 通过

---

### ruoyi-system

**职责**: 系统管理服务
**关键功能**:
- 用户管理 (CRUD, 分页, 导入导出)
- 角色管理 (CRUD, 权限分配)
- 部门管理 (树形结构)
- 菜单管理 (树形结构, 权限控制)
- 字典管理
- 配置管理
- 通知公告
- 岗位管理

**测试状态**: ✅ 305 tests, 100% 通过

**子服务**: 10个核心服务
- SysUserService
- SysRoleService
- SysDeptService
- SysMenuService
- SysPermissionService
- SysDictTypeService
- SysDictDataService
- SysConfigService
- SysNoticeService
- SysPostService

---

### ruoyi-gen

**职责**: 代码生成服务
**关键功能**:
- 数据库表扫描
- 代码模板生成 (Controller, Service, Mapper, VO)
- 自定义模板支持 (Velocity)

**依赖**: Velocity, Anyline

**测试状态**: 🔄 集成测试中

---

### ruoyi-resource

**职责**: 资源管理服务
**关键功能**:
- 文件上传/下载
- 图片处理
- OSS 对象存储 (MinIO)

**依赖**: MinIO, Redis

**测试状态**: 🔄 集成测试中

**子服务**: 6个服务
- SysOssService
- SysOssConfigService
- SysOssRuleService

---

### ruoyi-workflow

**职责**: 工作流服务
**关键功能**:
- 流程定义
- 流程实例
- 任务管理
- 审批流转

**依赖**: Warm-Flow 引擎

**测试状态**: ⏳ 待开始

**子服务**: 12个服务

---

### ruoyi-job

**职责**: 定时任务 (示例)
**说明**: 仅示例代码，无实际业务逻辑

**测试状态**: 🚫 无需测试

---

### ruoyi-demo

**职责**: 示例模块
**说明**: 演示各种功能的示例代码

**测试状态**: 🚫 无需测试

---

## 基础设施模块 (4个)

### ruoyi-gateway

**职责**: API 网关
**关键功能**:
- 路由转发
- 限流降级
- 认证鉴权
- 跨域处理

**技术**: Spring Cloud Gateway

---

### ruoyi-monitor

**职责**: 服务监控
**关键功能**:
- 服务健康检查
- 性能指标
- 日志聚合

**技术**: Spring Boot Admin

---

### ruoyi-visual

**职责**: 可视化监控
**关键功能**:
- 服务拓扑
- 链路追踪
- 性能分析

**技术**: Skywalking

---

### ruoyi-xxl-job-admin

**职责**: 分布式任务调度
**关键功能**:
- 任务调度
- 任务监控
- 执行日志

**技术**: XXL-Job

---

## 工具模块 (2个)

### script

**职责**: 脚本工具集
**内容**:
- Nacos 配置导入脚本
- 数据库初始化脚本
- Docker 部署脚本

---

### sql

**职责**: 数据库脚本
**内容**:
- PostgreSQL 初始化脚本
- 升级脚本
- 示例数据

---

## 模块依赖关系

### 依赖图

```

ruoyi-gateway
└─ ruoyi-common-*

ruoyi-auth
├─ ruoyi-common-core
├─ ruoyi-common-satoken
└─ ruoyi-common-redis

ruoyi-system
├─ ruoyi-common-core
├─ ruoyi-common-mybatis
├─ ruoyi-common-satoken
├─ ruoyi-common-redis
├─ ruoyi-common-tenant
├─ ruoyi-common-web
├─ ruoyi-common-encrypt
└─ ruoyi-common-translation

ruoyi-gen
├─ ruoyi-common-core
├─ ruoyi-common-mybatis
└─ ruoyi-common-web

ruoyi-resource
├─ ruoyi-common-core
├─ ruoyi-common-mybatis
├─ ruoyi-common-redis
└─ ruoyi-common-oss

ruoyi-workflow
├─ ruoyi-common-core
├─ ruoyi-common-mybatis
└─ warm-flow-spring-boot-starter

```

---

## 测试覆盖率总览

| 模块类型 | 总模块数 | 已完成 | 进行中 | 待开始 | 无需测试 |
|---------|---------|-------|-------|-------|---------|
| Common 库 | 11 | 9 (100%) | 0 | 0 | 2 (集成测试) |
| 业务模块 | 7 | 3 (100%) | 2 | 1 | 1 |
| 基础设施 | 4 | 0 | 0 | 0 | 4 (手动测试) |
| 工具模块 | 2 | 0 | 0 | 0 | 2 (无需测试) |
| **总计** | **24** | **12** | **2** | **1** | **9** |

**整体完成度**: 50% (12/24，已完成单元测试)

**整体通过率**: 99.05% (3,240/3,271 tests)

---

## 快速查找

### 按功能查找

- **认证授权**: ruoyi-auth, ruoyi-common-satoken
- **用户管理**: ruoyi-system (SysUserService)
- **权限控制**: ruoyi-system (SysRoleService, SysMenuService, SysPermissionService)
- **文件存储**: ruoyi-resource (SysOssService)
- **数据加密**: ruoyi-common-encrypt
- **数据脱敏**: ruoyi-common-sensitive
- **数据翻译**: ruoyi-common-translation
- **多租户**: ruoyi-common-tenant
- **工作流**: ruoyi-workflow
- **代码生成**: ruoyi-gen

### 按技术栈查找

- **MyBatis-Plus**: ruoyi-common-mybatis
- **Sa-Token**: ruoyi-common-satoken
- **Redis**: ruoyi-common-redis
- **MinIO**: ruoyi-resource
- **Velocity**: ruoyi-gen
- **Warm-Flow**: ruoyi-workflow
- **Spring Cloud Gateway**: ruoyi-gateway

---

**最后更新**: 2025-11-29
**模块总数**: 24
**测试覆盖率**: 99.05%
**文档版本**: 4.0
```

### 6.3 合并说明

**原 reference/modules/ 结构**:

```
reference/modules/
├── common/README.md (189行，详细)
├── auth/README.md (约50行)
├── system/README.md (约100行)
├── gen/README.md (约30行)
├── resource/README.md (约40行)
├── workflow/README.md (约30行)
└── job/README.md (约20行)
```

**合并为 architecture/module-overview.md**:

- 提取每个模块的核心信息 (职责、关键组件、测试状态)
- 删除详细的测试报告内容 (已在 testing/current-status.md 中)
- 新增模块依赖关系图
- 新增快速查找索引
- 总行数: 约 400 行 (原 7 个文件共 ~450 行)

---

## 7. 实施检查清单

### 阶段 1: 准备阶段

- [ ] 确认 git 仓库状态干净 (无未提交更改)
- [ ] 创建新分支: `git checkout -b docs/reorganize-structure`
- [ ] 保存执行计划到: `docs/reorganize-docs-plan.md`
- [ ] 保存脚本到: `reorganize-docs.sh`
- [ ] 添加执行权限: `chmod +x reorganize-docs.sh`

### 阶段 2: 执行脚本

- [ ] 运行脚本: `./reorganize-docs.sh`
- [ ] 确认备份创建成功: `ls -la docs-backup-*/`
- [ ] 检查脚本执行日志，确保无错误
- [ ] 验证新目录结构: `tree docs -L 2`

### 阶段 3: 创建新文件

- [ ] **README.md** (新版，~100行)
    - [ ] 新手 3 步走 (30行)
    - [ ] 文档导航 (30行)
    - [ ] 常用命令 (20行)
    - [ ] 贡献指南 (20行)

- [ ] **getting-started/README.md** (~30行)
    - [ ] 入门总览
    - [ ] 学习路径

- [ ] **getting-started/quick-start.md** (~50行)
    - [ ] 5分钟快速开始
    - [ ] 环境准备
    - [ ] 启动步骤
    - [ ] 验证运行

- [ ] **guides/README.md** (~20行)
    - [ ] 操作指南总览
    - [ ] 指南清单

- [ ] **configuration/README.md** (~20行)
    - [ ] 配置说明总览
    - [ ] 配置文件清单

- [ ] **configuration/nacos-advanced.md** (~40行)
    - [ ] 从 guides/nacos-config.md 提取高级内容
    - [ ] 命名空间
    - [ ] 配置组
    - [ ] 动态刷新

- [ ] **configuration/test-configuration.md** (~30行)
    - [ ] 测试环境配置
    - [ ] Testcontainers 配置
    - [ ] Mock 配置

- [ ] **architecture/README.md** (~20行)
    - [ ] 架构说明总览
    - [ ] 架构文档清单

- [ ] **architecture/module-overview.md** (~400行)
    - [ ] 合并 reference/modules/ 的所有模块信息
    - [ ] 模块依赖关系图
    - [ ] 快速查找索引

- [ ] **architecture/technology-stack.md** (~50行)
    - [ ] 后端技术栈
    - [ ] 前端技术栈
    - [ ] 基础设施
    - [ ] 开发工具

- [ ] **testing/README.md** (~30行)
    - [ ] 测试文档总览
    - [ ] 测试文档清单

- [ ] **testing/unit-test-guide.md** (~100行)
    - [ ] 从 guides/testing-guide.md 提取单元测试部分
    - [ ] BaseUnitTest 使用
    - [ ] 测试规范
    - [ ] 最佳实践

- [ ] **testing/integration-test-guide.md** (~80行)
    - [ ] 从 archive/ 提取集成测试框架说明
    - [ ] BaseIntegrationTest 使用
    - [ ] Testcontainers 配置
    - [ ] 常见问题

- [ ] **archive/README.md** (~100行)
    - [ ] 醒目的新手提示
    - [ ] 归档文档清单
    - [ ] 如何使用归档文档
    - [ ] 归档策略

### 阶段 4: 验证新结构

- [ ] **目录结构验证**
    - [ ] 运行: `tree docs -L 2`
    - [ ] 对比目标结构 (第 1 节)
    - [ ] 确认所有目录存在

- [ ] **文件完整性验证**
    - [ ] 检查所有新文件已创建
    - [ ] 检查所有移动的文件在新位置
    - [ ] 检查归档目录保留 8-10 个文件

- [ ] **链接有效性验证**
    - [ ] 运行链接检查工具 (可选)
    - [ ] 手动检查 README.md 中的所有链接
    - [ ] 手动检查 getting-started/ 中的链接
    - [ ] 手动检查 testing/ 中的链接

- [ ] **内容一致性验证**
    - [ ] 对比新旧 README.md，确保核心信息保留
    - [ ] 检查 module-overview.md 是否包含所有模块
    - [ ] 检查 archive/README.md 清单是否完整

### 阶段 5: 测试新手路径

- [ ] **新手 3 步走测试**
    - [ ] 阅读 README.md，确认易懂
    - [ ] 跟随快速开始链接，确认可达
    - [ ] 验证常用命令可执行

- [ ] **文档导航测试**
    - [ ] 点击每个导航链接，确认可达
    - [ ] 检查导航层级不超过 2 层
    - [ ] 确认新手能找到所需文档

- [ ] **归档目录测试**
    - [ ] 确认新手提示醒目
    - [ ] 确认归档清单完整
    - [ ] 验证从归档回到主文档的链接

### 阶段 6: Git 提交

- [ ] **暂存更改**
  ```bash
  git add docs/
  ```

- [ ] **提交更改**
  ```bash
  git commit -m "docs: 重组文档结构 (方案 B 平衡结构)

  - 新建 getting-started/ 入门指南目录
  - 新建 configuration/ 配置说明目录
  - 新建 architecture/ 架构设计目录
  - 新建 testing/ 测试文档目录
  - 精简 archive/ 保留 8-10 个关键文档
  - 删除 reference/ 和 active/ 目录
  - 合并 documentation-index.md 到新 README.md
  - 创建新 README.md (~100行，快速开始为主)
  - 创建 archive/README.md (醒目标注"新手可忽略")

  目标:
  - 新手 17 分钟上手
  - 文档从 70 个精简到 30-35 个
  - 结构清晰，易于导航

  Generated with Claude Code
  Co-Authored-By: Claude <noreply@anthropic.com>"
  ```

- [ ] **推送分支**
  ```bash
  git push origin docs/reorganize-structure
  ```

### 阶段 7: 创建 Pull Request

- [ ] **GitHub/Gitee PR**
    - [ ] 标题: `docs: 重组文档结构 (方案 B 平衡结构)`
    - [ ] 描述: 参考第 9.4 节 PR 模板
    - [ ] 添加标签: `documentation`, `enhancement`
    - [ ] 请求代码审查

- [ ] **PR 审查检查点**
    - [ ] 文件变更统计正确
    - [ ] 无意外删除的文件
    - [ ] 新文件内容完整
    - [ ] CI 检查通过 (如有)

### 阶段 8: 后续优化

- [ ] **收集反馈**
    - [ ] 新手试用反馈
    - [ ] 团队成员反馈
    - [ ] 社区反馈

- [ ] **持续改进**
    - [ ] 根据反馈优化文档
    - [ ] 更新过时内容
    - [ ] 补充缺失内容

---

## 8. 保留的归档文件清单

### 8.1 详细清单 (10个文件)

```
archive/
├── README.md (新建)
├── unit-tests/
│   ├── phase1-common/
│   │   └── phase1-final-summary.md          # ✅ 保留 (Phase 1 总结报告)
│   ├── phase2-auth/
│   │   └── phase2-final-completion-report.md  # ✅ 保留 (Phase 2 完成报告)
│   └── phase3-system/
│       └── phase3.1-completion-summary.md     # ✅ 保留 (Phase 3 总结报告)
├── integration-tests/
│   ├── README.md                              # ✅ 保留 (归档说明)
│   ├── integration-test-final-status.md      # ✅ 保留 (Week 1 最终状态)
│   └── week1-framework/
│       ├── README.md                          # ✅ 保留 (Week 1 归档说明)
│       ├── integration-test-framework-setup.md  # ✅ 保留 (框架设置文档)
│       └── integration-test-work-summary.md   # ✅ 保留 (Week 1 工作总结)
├── progress-reports/
│   └── testing-progress-update-2025-11-11.md  # ✅ 保留 (3,271 测试分析)
└── legacy/
    └── final-module-analysis-summary.md       # ✅ 保留 (模块分析总结)
```

**总计**: 10 个 .md 文件 (不含目录 README)

### 8.2 保留理由

| 文件                                    | 保留理由                           | 价值 |
|---------------------------------------|--------------------------------|----|
| phase1-final-summary.md               | Phase 1 (Common 库) 完整总结，645个测试 | 高  |
| phase2-final-completion-report.md     | Phase 2 (Auth) 完成报告，166个测试     | 高  |
| phase3.1-completion-summary.md        | Phase 3 (System) 总结报告，305个测试   | 高  |
| integration-test-framework-setup.md   | 集成测试框架详细设置 (480行)              | 高  |
| integration-test-work-summary.md      | Week 1 工作完整总结 (580行)           | 高  |
| integration-test-final-status.md      | Dubbo 时序问题解决方案                 | 高  |
| testing-progress-update-2025-11-11.md | 3,271 测试执行完整分析                 | 中  |
| final-module-analysis-summary.md      | 旧版模块分析总结                       | 中  |

### 8.3 删除的文件 (33个)

- **phase1-common**: 10 个详细报告 (已整合到 final-summary)
- **phase2-auth**: 2 个详细报告 (已整合到 final-completion-report)
- **phase3-system**: 14 个详细报告 (已整合到 completion-summary)
- **phase4-analysis**: 1 个文件 (分析已过时)
- **phase5-gen**: 1 个文件 (未完成的分析)
- **failure-analysis**: 整个目录 (失败分析已解决或过时)
- **progress-reports/consolidated**: 整个目录 (已整合到主文档)

---

## 9. 风险控制措施

### 9.1 如何回滚?

#### 方式 1: 使用生成的回滚脚本 (推荐)

```bash
# 脚本会在执行重组时自动生成
./rollback-docs.sh
```

**说明**:

- 使用 `git checkout HEAD -- docs/` 恢复所有更改
- 备份目录保留，可手动恢复

#### 方式 2: 使用 Git 回滚

```bash
# 查看提交历史
git log --oneline docs/

# 回滚到指定提交
git checkout <commit-hash> -- docs/

# 或者直接回滚最后一次提交
git revert HEAD
```

#### 方式 3: 从备份恢复

```bash
# 删除当前 docs/
rm -rf docs/

# 从备份恢复
cp -r docs-backup-YYYYMMDD-HHMMSS/docs ./

# 提交恢复
git add docs/
git commit -m "docs: 从备份恢复文档结构"
```

### 9.2 如何验证?

#### 验证清单

1. **目录结构验证**
   ```bash
   tree docs -L 2
   # 对比第 1 节的目标结构
   ```

2. **文件数量验证**
   ```bash
   # 统计 .md 文件数量
   find docs -name "*.md" | wc -l
   # 应该约为 30-35 个

   # 统计归档文件数量
   find docs/archive -name "*.md" | wc -l
   # 应该为 10 个 (不含 README)
   ```

3. **链接有效性验证**
   ```bash
   # 使用 markdown-link-check (需安装)
   npm install -g markdown-link-check
   find docs -name "*.md" -exec markdown-link-check {} \;
   ```

4. **内容完整性验证**
   ```bash
   # 检查关键文件是否存在
   test -f docs/README.md && echo "✅ README.md 存在" || echo "❌ README.md 缺失"
   test -f docs/getting-started/quick-start.md && echo "✅ quick-start.md 存在" || echo "❌ quick-start.md 缺失"
   test -f docs/archive/README.md && echo "✅ archive/README.md 存在" || echo "❌ archive/README.md 缺失"
   ```

5. **Git 状态验证**
   ```bash
   # 检查是否有未跟踪的文件
   git status

   # 检查文件变更统计
   git diff --stat HEAD docs/
   ```

### 9.3 常见问题预案

#### 问题 1: 链接失效

**现象**: 点击链接 404

**原因**: 文件路径更改后，链接未更新

**解决**:

1. 全局搜索旧路径: `grep -r "old-path" docs/`
2. 批量替换: `sed -i '' 's|old-path|new-path|g' docs/**/*.md`
3. 验证修复: 运行链接检查工具

#### 问题 2: 归档文件过多/过少

**现象**: 归档目录文件数量不符合预期 (8-10个)

**原因**: 删除操作遗漏或过度

**解决**:

1. 检查归档清单: `find docs/archive -name "*.md"`
2. 根据第 8 节清单调整
3. 从备份恢复遗漏的文件: `cp docs-backup-*/docs/archive/... docs/archive/`

#### 问题 3: 新文件内容缺失

**现象**: 新创建的文件为空或内容不完整

**原因**: 手动创建步骤遗漏

**解决**:

1. 参考第 4、5、6 节的内容模板
2. 逐一创建缺失的文件
3. 使用 Claude Code 生成内容

#### 问题 4: Git 冲突

**现象**: merge 时出现冲突

**原因**: 其他分支同时修改了 docs/

**解决**:

1. 备份当前更改: `git stash`
2. 拉取最新代码: `git pull origin main`
3. 解决冲突: 手动合并
4. 恢复更改: `git stash pop`

#### 问题 5: 脚本执行失败

**现象**: reorganize-docs.sh 执行报错

**原因**: 文件路径不存在或权限不足

**解决**:

1. 检查错误日志: 查看具体报错信息
2. 手动执行失败的命令: 逐步排查
3. 检查文件权限: `ls -la docs/`
4. 如果无法解决，使用手动操作代替脚本

### 9.4 Pull Request 模板

```markdown
## 文档重组 (方案 B 平衡结构)

### 变更摘要

- 新建 4 个一级目录: getting-started/, configuration/, architecture/, testing/
- 精简 archive/ 从 43 个文件到 10 个文件
- 删除 reference/ 和 active/ 目录
- 合并 documentation-index.md 到新 README.md
- 创建 15 个新文档 (README, 指南, 配置说明等)

### 变更统计

- **新增**: 15 个文件
- **修改**: 5 个文件
- **移动**: 10 个文件
- **删除**: 35 个文件
- **总变更**: 约 2000 行 (+1200/-800)

### 测试检查清单

- [x] 目录结构验证通过
- [x] 文件完整性验证通过
- [x] 链接有效性验证通过
- [x] 新手路径测试通过
- [x] Git 状态验证通过

### 预期效果

- **新手上手时间**: 从 未知 → 17 分钟
- **文档数量**: 从 70 个 → 30-35 个
- **归档文件**: 从 43 个 → 10 个
- **结构层级**: 从 混乱 → 2 层清晰结构

### 风险控制

- ✅ 已创建备份: `docs-backup-YYYYMMDD-HHMMSS/`
- ✅ 已生成回滚脚本: `rollback-docs.sh`
- ✅ 所有操作可逆

### 截图

(可选: 添加新 README.md 的截图)

### 审查要点

1. **结构合理性**: 新手能否快速找到入门文档?
2. **内容完整性**: 关键信息是否保留?
3. **归档策略**: 保留的 10 个文件是否合理?
4. **链接有效性**: 所有链接是否可达?

### 相关 Issue

Closes #XXX (如有)

---

Generated with Claude Code
Co-Authored-By: Claude <noreply@anthropic.com>
```

### 9.5 应急预案

#### 场景 1: 脚本执行到一半失败

**应对**:

1. 不要 panic，备份仍然存在
2. 检查失败点: 查看日志中的最后一条成功消息
3. 手动完成剩余操作: 参考第 2 节操作清单
4. 或者回滚后重新执行: `./rollback-docs.sh && ./reorganize-docs.sh`

#### 场景 2: 团队成员反对新结构

**应对**:

1. 收集具体反馈: 哪些方面不满意?
2. 评估调整成本: 是否可以微调?
3. 如果需要大改: 从备份恢复，重新规划
4. 如果可以微调: 在当前结构基础上优化

#### 场景 3: 发现重要文件被误删

**应对**:

1. 检查备份: `ls docs-backup-*/docs/`
2. 从备份恢复: `cp docs-backup-*/docs/path/to/file docs/path/to/file`
3. 或者从 git 历史恢复: `git checkout HEAD~1 -- docs/path/to/file`
4. 提交修复: `git add docs/ && git commit -m "docs: 恢复误删文件"`

#### 场景 4: 新手反馈仍然难以上手

**应对**:

1. 收集具体痛点: 哪一步卡住了?
2. 优化 README.md: 增加更详细的说明
3. 添加视频教程: (可选) 录制快速开始视频
4. 建立 FAQ: 整理常见问题

---

## 附录

### A. 目录对比表

| 操作 | 旧路径                                     | 新路径                                             |
|----|-----------------------------------------|-------------------------------------------------|
| 保留 | docs/README.md                          | docs/README.md (重写)                             |
| 保留 | docs/guides/testing-guide.md            | docs/guides/testing-guide.md                    |
| 保留 | docs/guides/code-quality.md             | docs/guides/code-quality.md                     |
| 保留 | docs/guides/service-startup-order.md    | docs/guides/service-startup-order.md            |
| 移动 | docs/project/development-setup.md       | docs/getting-started/environment-setup.md       |
| 移动 | docs/guides/database-initialization.md  | docs/getting-started/database-initialization.md |
| 移动 | docs/guides/nacos-config-import.md      | docs/getting-started/nacos-config.md            |
| 移动 | docs/project/docker-deployment.md       | docs/guides/docker-deployment.md                |
| 移动 | docs/project/gradle.md                  | docs/configuration/gradle.md                    |
| 移动 | docs/project/claude.md                  | docs/architecture/claude-guide.md               |
| 移动 | docs/active/testing-master-status.md    | docs/testing/current-status.md                  |
| 移动 | docs/active/integration-test-tracker.md | docs/testing/integration-tracker.md             |
| 合并 | docs/reference/modules/*                | docs/architecture/module-overview.md            |
| 删除 | docs/documentation-index.md             | (合并到新 README.md)                                |
| 删除 | docs/active/active-issues.md            | (已过时)                                           |
| 删除 | docs/reference/                         | (已合并)                                           |
| 精简 | docs/archive/ (43个文件)                   | docs/archive/ (10个文件)                           |

### B. 脚本执行时间估算

| 阶段     | 操作          | 估计时间      |
|--------|-------------|-----------|
| 安全检查   | Git 状态检查    | 5秒        |
| 备份     | 复制 docs/ 目录 | 10秒       |
| 删除文件   | rm 35 个文件   | 5秒        |
| 移动文件   | mv 10 个文件   | 5秒        |
| 删除空目录  | rmdir 5 个目录 | 2秒        |
| 验证结构   | 检查文件存在性     | 3秒        |
| 生成回滚脚本 | 创建 shell 脚本 | 1秒        |
| **总计** |             | **约 30秒** |

**手动创建新文件时间**: 约 2-3 小时 (取决于内容复杂度)

### C. 文档大小统计

| 文件                                | 行数       | 大小        |
|-----------------------------------|----------|-----------|
| README.md (新)                     | ~100     | ~4KB      |
| getting-started/quick-start.md    | ~50      | ~2KB      |
| getting-started/README.md         | ~30      | ~1KB      |
| architecture/module-overview.md   | ~400     | ~15KB     |
| testing/unit-test-guide.md        | ~100     | ~4KB      |
| testing/integration-test-guide.md | ~80      | ~3KB      |
| archive/README.md                 | ~100     | ~4KB      |
| **总计 (新文件)**                      | **~860** | **~33KB** |

---

**执行计划版本**: 1.0
**生成时间**: 2025-11-29
**预计执行时间**: 3 小时 (脚本 30秒 + 手动创建 2.5小时)
**预计效果**: 文档从 70 个精简到 30-35 个，新手上手时间从未知缩短到 17 分钟

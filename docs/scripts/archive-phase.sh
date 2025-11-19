#!/bin/bash

# RuoYi-Cloud-Plus 测试文档自动归档脚本
# 用途：在完成一个测试阶段后，自动归档相关文档
# 作者：Test Team
# 最后更新：2025-11-10

set -e  # 遇到错误立即退出

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# 项目根目录
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
# 文档目录
DOCS_DIR="$PROJECT_ROOT/docs"
# 归档目录
ARCHIVE_DIR="$DOCS_DIR/archive"
# 配置文件
CONFIG_FILE="$SCRIPT_DIR/archive-config.yml"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 打印脚本帮助信息
print_help() {
    cat << EOF
RuoYi-Cloud-Plus 测试文档自动归档脚本

用法:
    $0 [选项]

选项:
    -h, --help              显示此帮助信息
    -d, --dry-run           模拟运行（不实际移动文件）
    -p, --phase <name>      指定要归档的阶段名称
    -t, --type <type>       归档类型：unit-test 或 integration-test
    --list                  列出所有可归档的阶段

示例:
    # 归档 integration-test-week2 阶段
    $0 --phase integration-test-week2 --type integration-test

    # 模拟归档（不实际移动文件）
    $0 --phase phase6 --type unit-test --dry-run

    # 列出所有可归档的阶段
    $0 --list

归档流程:
    1. 检测阶段完成标记
    2. 提示用户确认归档操作
    3. 移动文档到归档目录
    4. 更新主文档中的链接
    5. 生成归档摘要
    6. 暂存 git 更改（供用户审查）

EOF
}

# 检测阶段完成标记
check_phase_completion() {
    local phase_name=$1
    local test_type=$2

    print_info "检测阶段 '$phase_name' 的完成状态..."

    # 检查 testing-master-status.md 中的完成标记
    if grep -q "$phase_name.*✅" "$DOCS_DIR/testing-master-status.md" 2>/dev/null; then
        return 0  # 已完成
    fi

    # 检查 integration-test-tracker.md 中的完成标记
    if grep -q "$phase_name.*completed" "$DOCS_DIR/integration-test-tracker.md" 2>/dev/null; then
        return 0  # 已完成
    fi

    return 1  # 未完成
}

# 归档文档
archive_documents() {
    local phase_name=$1
    local test_type=$2
    local dry_run=$3

    print_info "开始归档阶段: $phase_name (类型: $test_type)"

    # 确定归档目标目录
    if [ "$test_type" == "unit-test" ]; then
        target_dir="$ARCHIVE_DIR/unit-tests/$phase_name"
    else
        target_dir="$ARCHIVE_DIR/$phase_name"
    fi

    if [ "$dry_run" == "true" ]; then
        print_warning "[DRY RUN] 将创建目录: $target_dir"
    else
        mkdir -p "$target_dir"
        print_success "创建归档目录: $target_dir"
    fi

    # 提示用户选择要归档的文件
    echo ""
    print_info "请选择要归档的文档（在 docs/ 根目录）:"
    echo "  例如：PHASE6-TESTING-STATUS.md"
    echo ""
    read -p "请输入要归档的文档名称（多个文件用空格分隔，留空跳过）: " files_to_archive

    if [ -n "$files_to_archive" ]; then
        for file in $files_to_archive; do
            if [ -f "$DOCS_DIR/$file" ]; then
                if [ "$dry_run" == "true" ]; then
                    print_warning "[DRY RUN] 将移动: $file → $target_dir/"
                else
                    mv "$DOCS_DIR/$file" "$target_dir/"
                    print_success "已归档: $file"
                fi
            else
                print_warning "文件不存在，跳过: $file"
            fi
        done
    else
        print_info "跳过文件归档"
    fi

    # 生成归档 README
    if [ "$dry_run" == "false" ]; then
        cat > "$target_dir/README.md" << EOF
# $phase_name 归档

> 📅 **归档日期**: $(date +%Y-%m-%d)
> 🎯 **归档类型**: $test_type
> 📊 **归档内容**: $files_to_archive

## 归档说明

本目录包含 $phase_name 阶段的测试文档，已完成并归档为参考资料。

## 归档文件

EOF
        for file in $files_to_archive; do
            echo "- $file" >> "$target_dir/README.md"
        done

        cat >> "$target_dir/README.md" << EOF

## 相关文档

- [测试状态总览](../../testing-master-status.md)
- [集成测试跟踪器](../../integration-test-tracker.md)
- [文档索引](../../documentation-index.md)

---

**归档时间**: $(date +"%Y-%m-%d %H:%M:%S")
**归档脚本**: archive-phase.sh
EOF
        print_success "已生成归档 README: $target_dir/README.md"
    fi
}

# 更新文档链接
update_document_links() {
    local phase_name=$1
    local test_type=$2
    local dry_run=$3

    print_info "更新文档中的链接..."

    if [ "$dry_run" == "true" ]; then
        print_warning "[DRY RUN] 将更新 testing-master-status.md 和 documentation-index.md 中的链接"
    else
        # 这里可以添加自动更新链接的逻辑
        # 由于复杂性，建议手动更新或使用更复杂的脚本
        print_warning "请手动更新以下文档中的链接:"
        echo "  - docs/testing-master-status.md"
        echo "  - docs/documentation-index.md"
        echo "  - docs/README.md"
    fi
}

# 生成归档摘要
generate_summary() {
    local phase_name=$1
    local test_type=$2

    echo ""
    print_info "=========================================="
    print_info "归档摘要"
    print_info "=========================================="
    echo "  阶段名称: $phase_name"
    echo "  归档类型: $test_type"
    echo "  归档日期: $(date +%Y-%m-%d)"
    echo ""
    print_success "归档完成！"
    echo ""
    print_info "下一步操作:"
    echo "  1. 检查归档目录: docs/archive/"
    echo "  2. 更新主文档中的链接"
    echo "  3. 审查 git 更改"
    echo "  4. 提交更改: git add . && git commit -m 'docs: archive $phase_name'"
    print_info "=========================================="
}

# 列出可归档的阶段
list_phases() {
    print_info "可归档的阶段:"
    echo ""
    echo "单元测试阶段:"
    echo "  - phase6, phase7, ..."
    echo ""
    echo "集成测试阶段:"
    echo "  - integration-test-week2"
    echo "  - integration-test-week3"
    echo "  - ..."
    echo ""
    print_info "使用 --phase 和 --type 参数指定要归档的阶段"
}

# 主函数
main() {
    local phase_name=""
    local test_type=""
    local dry_run="false"
    local list_mode="false"

    # 解析命令行参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                print_help
                exit 0
                ;;
            -d|--dry-run)
                dry_run="true"
                shift
                ;;
            -p|--phase)
                phase_name="$2"
                shift 2
                ;;
            -t|--type)
                test_type="$2"
                shift 2
                ;;
            --list)
                list_mode="true"
                shift
                ;;
            *)
                print_error "未知参数: $1"
                print_help
                exit 1
                ;;
        esac
    done

    # 列表模式
    if [ "$list_mode" == "true" ]; then
        list_phases
        exit 0
    fi

    # 检查必需参数
    if [ -z "$phase_name" ] || [ -z "$test_type" ]; then
        print_error "缺少必需参数"
        print_help
        exit 1
    fi

    # 验证 test_type
    if [ "$test_type" != "unit-test" ] && [ "$test_type" != "integration-test" ]; then
        print_error "无效的归档类型: $test_type（必须是 unit-test 或 integration-test）"
        exit 1
    fi

    # 打印归档信息
    echo ""
    print_info "=========================================="
    print_info "RuoYi-Cloud-Plus 文档归档工具"
    print_info "=========================================="
    echo "  阶段名称: $phase_name"
    echo "  归档类型: $test_type"
    echo "  模拟运行: $dry_run"
    echo ""

    # 检查阶段完成状态
    if check_phase_completion "$phase_name" "$test_type"; then
        print_success "阶段 '$phase_name' 已标记为完成"
    else
        print_warning "阶段 '$phase_name' 未标记为完成，继续归档"
    fi

    # 用户确认
    if [ "$dry_run" == "false" ]; then
        echo ""
        read -p "确认要归档 '$phase_name' 阶段吗？(y/N) " -n 1 -r
        echo ""
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_info "已取消归档操作"
            exit 0
        fi
    fi

    # 执行归档
    archive_documents "$phase_name" "$test_type" "$dry_run"

    # 更新链接
    update_document_links "$phase_name" "$test_type" "$dry_run"

    # 生成摘要
    generate_summary "$phase_name" "$test_type"

    # 暂存 git 更改
    if [ "$dry_run" == "false" ]; then
        print_info "暂存 git 更改..."
        git add "$DOCS_DIR"
        print_success "已暂存更改，请审查后提交"
    fi
}

# 运行主函数
main "$@"

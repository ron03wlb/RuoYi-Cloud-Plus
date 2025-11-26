#!/bin/bash
#
# 安装 Git hooks 脚本
# 将项目中的 git hooks 复制到 .git/hooks/ 目录
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
GIT_HOOKS_DIR="$PROJECT_ROOT/.git/hooks"

echo "Installing Git hooks..."
echo "Project root: $PROJECT_ROOT"
echo "Git hooks directory: $GIT_HOOKS_DIR"

# 检查 .git/hooks 目录是否存在
if [ ! -d "$GIT_HOOKS_DIR" ]; then
  echo "❌ Error: .git/hooks directory not found. Are you in a Git repository?"
  exit 1
fi

# 复制 pre-commit hook
if [ -f "$SCRIPT_DIR/pre-commit" ]; then
  cp "$SCRIPT_DIR/pre-commit" "$GIT_HOOKS_DIR/pre-commit"
  chmod +x "$GIT_HOOKS_DIR/pre-commit"
  echo "✅ Installed pre-commit hook"
else
  echo "❌ Error: pre-commit hook not found at $SCRIPT_DIR/pre-commit"
  exit 1
fi

echo ""
echo "✅ All Git hooks installed successfully!"
echo ""
echo "To uninstall, simply delete the files in .git/hooks/"
echo "To disable temporarily, rename the hook file (e.g., pre-commit -> pre-commit.disabled)"

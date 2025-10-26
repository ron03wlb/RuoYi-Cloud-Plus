#!/bin/bash

###############################################################################
# Gradle 初始化脚本
# 用于首次设置 Gradle Wrapper
###############################################################################

set -e  # 遇到错误立即退出

echo "=========================================="
echo "RuoYi-Cloud-Plus Gradle 初始化脚本"
echo "=========================================="
echo ""

# 检查是否已安装 Gradle
if ! command -v gradle &> /dev/null; then
    echo "❌ 错误：未检测到 Gradle 安装"
    echo ""
    echo "请先安装 Gradle 8.12+ ："
    echo ""
    echo "macOS:"
    echo "  brew install gradle"
    echo ""
    echo "Linux (使用 SDKMAN):"
    echo "  curl -s 'https://get.sdkman.io' | bash"
    echo "  sdk install gradle 8.12"
    echo ""
    echo "Windows (使用 Chocolatey):"
    echo "  choco install gradle"
    echo ""
    exit 1
fi

# 显示 Gradle 版本
echo "✅ 检测到 Gradle 安装："
gradle --version | head -3
echo ""

# 生成 Gradle Wrapper
echo "📦 正在生成 Gradle Wrapper..."
echo ""

gradle wrapper --gradle-version 8.12 --distribution-type bin

echo ""
echo "✅ Gradle Wrapper 生成成功！"
echo ""

# 验证 Wrapper
echo "🔍 验证 Gradle Wrapper..."
echo ""

./gradlew --version

echo ""
echo "=========================================="
echo "✅ 初始化完成！"
echo "=========================================="
echo ""
echo "后续步骤："
echo ""
echo "1. 构建项目："
echo "   ./gradlew clean build -x test"
echo ""
echo "2. 查看所有任务："
echo "   ./gradlew tasks"
echo ""
echo "3. 运行网关服务："
echo "   ./gradlew :ruoyi-gateway:bootRun"
echo ""
echo "4. 查看完整迁移文档："
echo "   cat GRADLE.md"
echo ""

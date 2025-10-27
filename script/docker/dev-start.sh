#!/bin/bash

################################################################################
# RuoYi-Cloud-Plus 开发环境启动脚本
################################################################################
#
# 功能说明:
#   - 启动开发所需的基础设施服务 (MySQL, Redis, Nacos, MinIO)
#   - 支持使用官方 Nacos 镜像或自定义构建的 Nacos
#   - 业务服务可以在 IDE 中运行和调试
#
# 使用方式:
#   ./dev-start.sh                  - 使用官方 Nacos（推荐，最快）
#   ./dev-start.sh --custom-nacos   - 使用自定义构建的 Nacos
#   ./dev-start.sh --skip-build     - 跳过 Nacos 构建检查
#   ./dev-start.sh --help           - 显示帮助信息
#
# Nacos 版本选择:
#   - 官方镜像: 无需构建，启动快速，使用稳定的官方版本
#   - 自定义版本: 需要 Gradle 构建，可以包含项目特定配置
#
################################################################################

set -e

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

# 目录定义
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# 默认配置
USE_CUSTOM_NACOS=false
SKIP_BUILD=false

# 显示帮助信息
show_help() {
    cat << EOF
${GREEN}RuoYi-Cloud-Plus 开发环境启动脚本${NC}

${YELLOW}使用方式:${NC}
  $0 [选项]

${YELLOW}选项:${NC}
  --custom-nacos  使用自定义构建的 Nacos（需要 Gradle 构建）
  --skip-build    跳过 Nacos 构建检查（仅与 --custom-nacos 一起使用）
  --help          显示此帮助信息

${YELLOW}说明:${NC}
  此脚本启动开发环境所需的基础设施服务：
    - MySQL (3306)       数据库
    - Redis (6379)       缓存/消息队列
    - Nacos (8848)       配置中心/注册中心
    - MinIO (9000/9001)  对象存储

${YELLOW}Nacos 版本选择:${NC}
  ${BLUE}官方镜像${NC}（默认，推荐）:
    - 无需 Maven 构建，启动最快
    - 使用官方稳定版本 (nacos-server:v2.4.3)
    - 适合大多数开发场景

  ${BLUE}自定义版本${NC}（--custom-nacos）:
    - 需要 Maven 构建 ruoyi-nacos 模块
    - 可以包含项目特定的配置和扩展
    - 适合需要定制 Nacos 的场景

${YELLOW}示例:${NC}
  $0                      # 使用官方 Nacos（推荐）
  $0 --custom-nacos       # 使用自定义 Nacos
  $0 --custom-nacos --skip-build  # 使用自定义 Nacos 并跳过构建检查

${YELLOW}后续步骤:${NC}
  1. 导入 Nacos 配置: ./import-nacos-config.sh
  2. 初始化数据库: 执行 script/sql/ 下的 SQL 脚本
  3. 在 IntelliJ IDEA 中运行业务服务

EOF
    exit 0
}

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        --custom-nacos)
            USE_CUSTOM_NACOS=true
            shift
            ;;
        --skip-build)
            SKIP_BUILD=true
            shift
            ;;
        --help)
            show_help
            ;;
        *)
            echo -e "${RED}未知选项: $1${NC}"
            echo "使用 --help 查看帮助信息"
            exit 1
            ;;
    esac
done

echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}RuoYi-Cloud-Plus 开发环境启动${NC}"
if [ "$USE_CUSTOM_NACOS" = true ]; then
    echo -e "${GREEN}(使用自定义 Nacos)${NC}"
else
    echo -e "${GREEN}(使用官方 Nacos 镜像)${NC}"
fi
echo -e "${GREEN}======================================${NC}"
echo ""

# 检查 Docker 是否运行
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}错误: Docker 未运行，请先启动 Docker${NC}"
    exit 1
fi

# 如果使用自定义 Nacos，检查并构建 JAR 文件
if [ "$USE_CUSTOM_NACOS" = true ]; then
    echo -e "${YELLOW}1. 检查自定义 Nacos JAR 文件...${NC}"
    if [ "$SKIP_BUILD" = false ]; then
        if [ ! -f "${PROJECT_ROOT}/ruoyi-visual/ruoyi-nacos/build/libs/ruoyi-nacos.jar" ]; then
            echo -e "${YELLOW}   Nacos JAR 不存在，开始构建...${NC}"
            cd "${PROJECT_ROOT}"
            ./gradlew :ruoyi-visual:ruoyi-nacos:bootJar -x test --no-configuration-cache
            if [ $? -ne 0 ]; then
                echo -e "${RED}   Nacos 构建失败！${NC}"
                exit 1
            fi
            echo -e "${GREEN}   ✓ Nacos 构建完成${NC}"
        else
            echo -e "${GREEN}   ✓ Nacos JAR 已存在${NC}"
        fi
    else
        echo -e "${YELLOW}   跳过 Nacos 构建检查${NC}"
    fi
    STEP=2
else
    STEP=1
fi

# 启动基础设施服务
echo -e "${YELLOW}${STEP}. 启动基础设施服务...${NC}"
cd "${SCRIPT_DIR}"

if [ "$USE_CUSTOM_NACOS" = true ]; then
    # 使用自定义 Nacos（docker-compose-build.yml）
    # 先启动不需要构建的服务
    docker-compose -f docker-compose-build.yml up -d mysql redis minio

    # 构建并启动自定义 Nacos
    docker-compose -f docker-compose-build.yml build nacos
    docker-compose -f docker-compose-build.yml up -d nacos
else
    # 使用官方 Nacos（docker-compose-dev-infra.yml）
    docker-compose -f docker-compose-dev-infra.yml up -d
fi

((STEP++))

echo ""
echo -e "${YELLOW}${STEP}. 等待服务启动...${NC}"
echo -e "   - MySQL (3306)"
echo -e "   - Redis (6379)"
if [ "$USE_CUSTOM_NACOS" = true ]; then
    echo -e "   - Nacos (8848) - ${BLUE}自定义构建版本${NC}"
else
    echo -e "   - Nacos (8848) - ${BLUE}官方镜像版本${NC}"
fi
echo -e "   - MinIO (9000/9001)"

((STEP++))

# 等待 Nacos 启动
echo ""
echo -e "${YELLOW}${STEP}. 等待 Nacos 完全启动...${NC}"
for i in {1..30}; do
    if curl -s http://localhost:8848/nacos > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Nacos 已启动${NC}"
        break
    fi
    echo -n "."
    sleep 2
done

echo ""
echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}基础设施启动完成！${NC}"
echo -e "${GREEN}======================================${NC}"
echo ""

echo -e "${YELLOW}接下来的步骤:${NC}"
echo ""
echo -e "1. ${RED}【重要】${NC} 导入 Nacos 配置:"
echo -e "   ${BLUE}自动导入:${NC}"
echo -e "     ${GREEN}./import-nacos-config.sh${NC}"
echo -e "   ${BLUE}或手动导入:${NC}"
echo -e "     - 访问: ${GREEN}http://localhost:8848/nacos${NC}"
echo -e "     - 用户名/密码: ${GREEN}nacos/nacos${NC}"
echo -e "     - 导入配置文件: ${GREEN}${PROJECT_ROOT}/script/config/nacos/*.yml${NC}"
echo ""
echo -e "2. 初始化数据库 (仅首次运行):"
echo -e "   - 连接 MySQL: ${GREEN}localhost:3306${NC}"
echo -e "   - 用户名/密码: ${GREEN}root/ruoyi123${NC}"
echo -e "   - 执行 SQL: ${GREEN}${PROJECT_ROOT}/script/sql/ry-cloud.sql${NC}"
echo ""
echo -e "3. 在 IntelliJ IDEA 中运行业务服务:"
echo -e "   - 使用 ${GREEN}.run/${NC} 目录下的运行配置"
echo -e "   - 推荐启动顺序:"
echo -e "     1) ruoyi-gateway    (端口 8080)"
echo -e "     2) ruoyi-auth       (端口 9210)"
echo -e "     3) ruoyi-system     (端口 9201)"
echo -e "     4) 其他服务按需启动"
echo ""
echo -e "4. 访问服务:"
echo -e "   - 前端地址: ${GREEN}http://localhost:80${NC}"
echo -e "   - API 网关: ${GREEN}http://localhost:8080${NC}"
echo -e "   - Nacos 控制台: ${GREEN}http://localhost:8848/nacos${NC}"
echo -e "   - MinIO 控制台: ${GREEN}http://localhost:9001${NC} (ruoyi/ruoyi123)"
echo ""

echo -e "${YELLOW}实用命令:${NC}"
if [ "$USE_CUSTOM_NACOS" = true ]; then
    COMPOSE_FILE="docker-compose-build.yml"
else
    COMPOSE_FILE="docker-compose-dev-infra.yml"
fi
echo -e "  查看状态: ${GREEN}docker-compose -f ${COMPOSE_FILE} ps${NC}"
echo -e "  查看日志: ${GREEN}docker-compose -f ${COMPOSE_FILE} logs -f [服务名]${NC}"
echo -e "  停止服务: ${GREEN}docker-compose -f ${COMPOSE_FILE} down${NC}"
echo -e "  重启服务: ${GREEN}docker-compose -f ${COMPOSE_FILE} restart [服务名]${NC}"
echo ""

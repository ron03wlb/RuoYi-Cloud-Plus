#!/bin/bash

################################################################################
# Maven 构建所有模块脚本
################################################################################
#
# 功能说明:
#   - 使用 Maven 构建项目所有模块
#   - 生成 JAR 文件供 Docker 镜像构建使用
#   - 跳过测试以加快构建速度
#
# 使用方式:
#   ./maven-build-all.sh                 - 使用默认配置 (dev) 构建
#   MAVEN_PROFILE=prod ./maven-build-all.sh - 使用生产配置构建
#
# 环境变量:
#   MAVEN_PROFILE   Maven 构建配置 (dev|prod)，默认: dev
#
# 说明:
#   此脚本在 Docker 构建前必须执行，因为 Dockerfile 需要从
#   target/ 目录复制 JAR 文件。如果使用 build-and-deploy.sh，
#   则会自动调用此脚本。
#
################################################################################

set -e

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# 目录定义
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# Maven 配置
PROFILE=${MAVEN_PROFILE:-dev}

echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}RuoYi-Cloud-Plus Maven 构建${NC}"
echo -e "${GREEN}======================================${NC}"
echo ""
echo -e "${YELLOW}项目根目录: ${PROJECT_ROOT}${NC}"
echo -e "${YELLOW}Maven Profile: ${PROFILE}${NC}"
echo ""

cd "${PROJECT_ROOT}"

echo -e "${YELLOW}开始构建所有模块...${NC}"
echo ""

mvn clean package -P "${PROFILE}" -DskipTests=true

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}======================================${NC}"
    echo -e "${GREEN}✓ 构建成功！${NC}"
    echo -e "${GREEN}======================================${NC}"
    echo ""
    echo -e "${YELLOW}生成的 JAR 文件:${NC}"
    find . -name "ruoyi-*.jar" -not -path "*/\.*" | sort
    echo ""
    echo -e "${GREEN}现在可以使用 Docker 构建了:${NC}"
    echo -e "  cd script/docker"
    echo -e "  docker-compose -f docker-compose-build.yml up -d"
    echo ""
    echo -e "或使用自动化脚本:"
    echo -e "  ./build-and-deploy.sh all"
    echo ""
else
    echo ""
    echo -e "${RED}======================================${NC}"
    echo -e "${RED}✗ 构建失败${NC}"
    echo -e "${RED}======================================${NC}"
    echo ""
    echo -e "${YELLOW}请检查错误信息并修复问题${NC}"
    exit 1
fi

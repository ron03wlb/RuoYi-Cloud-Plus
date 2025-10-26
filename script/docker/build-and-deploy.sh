#!/bin/bash

################################################################################
# RuoYi-Cloud-Plus 本地构建和部署脚本
################################################################################
#
# 功能说明:
#   - 从本地源码构建 Docker 镜像并部署所有服务
#   - 自动检查并构建缺失的 JAR 文件
#   - 支持分步部署（基础设施 + 业务服务）
#   - 支持单独构建和部署特定服务
#
# 使用方式:
#   ./build-and-deploy.sh all          - 构建并启动所有服务（基础设施 + 业务）
#   ./build-and-deploy.sh infra        - 只启动基础设施 (MySQL, Redis, Nacos, MinIO)
#   ./build-and-deploy.sh services     - 构建并启动所有业务服务
#   ./build-and-deploy.sh <服务名>     - 构建并启动指定服务
#   ./build-and-deploy.sh status       - 显示所有服务状态
#   ./build-and-deploy.sh logs [服务名] - 查看日志
#   ./build-and-deploy.sh stop         - 停止所有服务
#
# 环境变量:
#   MAVEN_PROFILE   Maven 构建配置 (dev|prod)，默认: dev
#
# 示例:
#   MAVEN_PROFILE=prod ./build-and-deploy.sh all
#
################################################################################

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 项目根目录
PROJECT_ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
echo -e "${GREEN}项目根目录: ${PROJECT_ROOT}${NC}"

# Maven profile (dev 或 prod)
PROFILE=${MAVEN_PROFILE:-dev}
echo -e "${GREEN}使用 Maven Profile: ${PROFILE}${NC}"

# 构建指定模块
build_module() {
    local module=$1
    echo -e "${YELLOW}正在构建模块: ${module}${NC}"
    cd "${PROJECT_ROOT}"
    mvn clean package -pl "${module}" -am -P "${PROFILE}" -DskipTests=true
    echo -e "${GREEN}✓ ${module} 构建完成${NC}"
}

# 构建所有服务
build_all_services() {
    echo -e "${YELLOW}开始构建所有服务...${NC}"
    cd "${PROJECT_ROOT}"
    mvn clean package -P "${PROFILE}" -DskipTests=true
    echo -e "${GREEN}✓ 所有服务构建完成${NC}"
}

# 启动基础设施服务
start_infra() {
    echo -e "${YELLOW}启动基础设施服务 (MySQL, Redis, Nacos, MinIO)...${NC}"

    # 检查是否需要构建 Nacos
    if [ ! -f "${PROJECT_ROOT}/ruoyi-visual/ruoyi-nacos/target/ruoyi-nacos.jar" ]; then
        echo -e "${YELLOW}Nacos JAR 不存在，开始构建...${NC}"
        build_module "ruoyi-visual/ruoyi-nacos"
    fi

    cd "${PROJECT_ROOT}/script/docker"
    docker-compose -f docker-compose-build.yml up -d mysql redis minio

    # 构建并启动 Nacos
    docker-compose -f docker-compose-build.yml build nacos
    docker-compose -f docker-compose-build.yml up -d nacos

    echo -e "${GREEN}✓ 基础设施服务启动完成${NC}"
    echo -e "${YELLOW}请等待 Nacos 完全启动后，手动导入配置文件 (script/config/nacos/)${NC}"
    echo -e "${YELLOW}Nacos 控制台: http://localhost:8848/nacos (nacos/nacos)${NC}"
}

# 启动业务服务
start_services() {
    echo -e "${YELLOW}启动业务服务...${NC}"

    # 检查关键服务的 JAR 文件
    local services_to_build=()

    [ ! -f "${PROJECT_ROOT}/ruoyi-visual/ruoyi-seata-server/target/ruoyi-seata-server.jar" ] && services_to_build+=("ruoyi-visual/ruoyi-seata-server")
    [ ! -f "${PROJECT_ROOT}/ruoyi-visual/ruoyi-snailjob-server/target/ruoyi-snailjob-server.jar" ] && services_to_build+=("ruoyi-visual/ruoyi-snailjob-server")
    [ ! -f "${PROJECT_ROOT}/ruoyi-gateway/target/ruoyi-gateway.jar" ] && services_to_build+=("ruoyi-gateway")
    [ ! -f "${PROJECT_ROOT}/ruoyi-auth/target/ruoyi-auth.jar" ] && services_to_build+=("ruoyi-auth")
    [ ! -f "${PROJECT_ROOT}/ruoyi-modules/ruoyi-system/target/ruoyi-system.jar" ] && services_to_build+=("ruoyi-modules/ruoyi-system")
    [ ! -f "${PROJECT_ROOT}/ruoyi-modules/ruoyi-gen/target/ruoyi-gen.jar" ] && services_to_build+=("ruoyi-modules/ruoyi-gen")
    [ ! -f "${PROJECT_ROOT}/ruoyi-modules/ruoyi-job/target/ruoyi-job.jar" ] && services_to_build+=("ruoyi-modules/ruoyi-job")
    [ ! -f "${PROJECT_ROOT}/ruoyi-modules/ruoyi-resource/target/ruoyi-resource.jar" ] && services_to_build+=("ruoyi-modules/ruoyi-resource")
    [ ! -f "${PROJECT_ROOT}/ruoyi-modules/ruoyi-workflow/target/ruoyi-workflow.jar" ] && services_to_build+=("ruoyi-modules/ruoyi-workflow")
    [ ! -f "${PROJECT_ROOT}/ruoyi-visual/ruoyi-monitor/target/ruoyi-monitor.jar" ] && services_to_build+=("ruoyi-visual/ruoyi-monitor")

    # 如果有缺失的 JAR，直接构建全部
    if [ ${#services_to_build[@]} -gt 0 ]; then
        echo -e "${YELLOW}发现 ${#services_to_build[@]} 个服务需要构建，执行完整构建...${NC}"
        build_all_services
    fi

    cd "${PROJECT_ROOT}/script/docker"

    # 构建并启动所有业务服务
    echo -e "${YELLOW}构建 Docker 镜像...${NC}"
    docker-compose -f docker-compose-build.yml build \
        ruoyi-seata-server \
        ruoyi-snailjob-server \
        ruoyi-gateway \
        ruoyi-auth \
        ruoyi-system \
        ruoyi-gen \
        ruoyi-job \
        ruoyi-resource \
        ruoyi-workflow \
        ruoyi-monitor

    echo -e "${YELLOW}启动服务...${NC}"
    docker-compose -f docker-compose-build.yml up -d \
        ruoyi-seata-server \
        ruoyi-snailjob-server \
        ruoyi-gateway \
        ruoyi-auth \
        ruoyi-system \
        ruoyi-gen \
        ruoyi-job \
        ruoyi-resource \
        ruoyi-workflow \
        ruoyi-monitor

    echo -e "${GREEN}✓ 业务服务启动完成${NC}"
}

# 启动指定服务
start_service() {
    local service=$1
    echo -e "${YELLOW}启动服务: ${service}${NC}"
    cd "${PROJECT_ROOT}/script/docker"
    docker-compose -f docker-compose-build.yml up -d "${service}"
    echo -e "${GREEN}✓ ${service} 启动完成${NC}"
}

# 显示服务状态
show_status() {
    echo -e "${YELLOW}服务状态:${NC}"
    cd "${PROJECT_ROOT}/script/docker"
    docker-compose -f docker-compose-build.yml ps
}

# 查看日志
show_logs() {
    local service=$1
    cd "${PROJECT_ROOT}/script/docker"
    if [ -z "$service" ]; then
        docker-compose -f docker-compose-build.yml logs -f
    else
        docker-compose -f docker-compose-build.yml logs -f "${service}"
    fi
}

# 停止所有服务
stop_all() {
    echo -e "${YELLOW}停止所有服务...${NC}"
    cd "${PROJECT_ROOT}/script/docker"
    docker-compose -f docker-compose-build.yml down
    echo -e "${GREEN}✓ 所有服务已停止${NC}"
}

# 主逻辑
case "${1}" in
    all)
        build_all_services
        start_infra
        echo -e "${YELLOW}等待 30 秒让 Nacos 启动...${NC}"
        sleep 30
        echo -e "${RED}请确认已导入 Nacos 配置！${NC}"
        read -p "按 Enter 继续启动业务服务，或 Ctrl+C 取消..."
        start_services
        show_status
        ;;
    infra)
        start_infra
        show_status
        ;;
    services)
        build_all_services
        start_services
        show_status
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs "${2}"
        ;;
    stop)
        stop_all
        ;;
    *)
        # 检查是否是服务名
        if docker-compose -f "${PROJECT_ROOT}/script/docker/docker-compose-build.yml" config --services | grep -q "^${1}$"; then
            # 如果是 ruoyi-* 服务，先构建
            if [[ $1 == ruoyi-* ]]; then
                case "${1}" in
                    ruoyi-gateway)
                        build_module "ruoyi-gateway"
                        ;;
                    ruoyi-auth)
                        build_module "ruoyi-auth"
                        ;;
                    ruoyi-system)
                        build_module "ruoyi-modules/ruoyi-system"
                        ;;
                    ruoyi-gen)
                        build_module "ruoyi-modules/ruoyi-gen"
                        ;;
                    ruoyi-job)
                        build_module "ruoyi-modules/ruoyi-job"
                        ;;
                    ruoyi-resource)
                        build_module "ruoyi-modules/ruoyi-resource"
                        ;;
                    ruoyi-workflow)
                        build_module "ruoyi-modules/ruoyi-workflow"
                        ;;
                    ruoyi-monitor)
                        build_module "ruoyi-visual/ruoyi-monitor"
                        ;;
                    ruoyi-nacos)
                        build_module "ruoyi-visual/ruoyi-nacos"
                        ;;
                    ruoyi-seata-server)
                        build_module "ruoyi-visual/ruoyi-seata-server"
                        ;;
                    ruoyi-snailjob-server)
                        build_module "ruoyi-visual/ruoyi-snailjob-server"
                        ;;
                esac
            fi
            start_service "${1}"
            show_status
        else
            echo -e "${RED}错误: 未知的命令或服务名${NC}"
            echo ""
            echo "使用方式:"
            echo "  ${0} all          - 构建并启动所有服务"
            echo "  ${0} infra        - 只启动基础设施(MySQL, Redis, Nacos, MinIO)"
            echo "  ${0} services     - 构建并启动所有业务服务"
            echo "  ${0} status       - 显示所有服务状态"
            echo "  ${0} logs [服务名] - 查看日志"
            echo "  ${0} stop         - 停止所有服务"
            echo "  ${0} 服务名        - 构建并启动指定服务"
            echo ""
            echo "可用的服务名:"
            docker-compose -f "${PROJECT_ROOT}/script/docker/docker-compose-build.yml" config --services
            exit 1
        fi
        ;;
esac

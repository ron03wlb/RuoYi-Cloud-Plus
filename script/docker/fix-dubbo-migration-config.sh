#!/bin/bash

# ==========================================
# 修复 Dubbo 服务发现迁移配置
# 使用正确的 YAML 格式
# ==========================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Nacos 配置
NACOS_SERVER="http://localhost:8848"
NACOS_USERNAME="nacos"
NACOS_PASSWORD="nacos"
NACOS_NAMESPACE="dev"
NACOS_GROUP="DUBBO_SERVICEDISCOVERY_MIGRATION"

# 需要配置的应用列表
APPLICATIONS=(
    "ruoyi-system"
    "ruoyi-workflow"
    "ruoyi-auth"
    "ruoyi-gateway"
    "ruoyi-resource"
    "ruoyi-gen"
    "ruoyi-job"
)

echo -e "${YELLOW}===========================================${NC}"
echo -e "${YELLOW}修复 Dubbo 服务发现迁移配置${NC}"
echo -e "${YELLOW}===========================================${NC}"

# 1. 登录获取 token
echo -e "\n${YELLOW}1. 登录 Nacos 并获取 token...${NC}"
TOKEN_RESPONSE=$(curl -s -X POST "${NACOS_SERVER}/nacos/v1/auth/login" \
    -d "username=${NACOS_USERNAME}" \
    -d "password=${NACOS_PASSWORD}")

TOKEN=$(echo "$TOKEN_RESPONSE" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}✗ 登录失败${NC}"
    echo "响应: $TOKEN_RESPONSE"
    exit 1
fi
echo -e "${GREEN}✓ 登录成功${NC}"

# 2. 删除错误的配置
echo -e "\n${YELLOW}2. 删除之前的错误配置...${NC}"
for APP in "${APPLICATIONS[@]}"; do
    DATA_ID="${APP}.migration"
    echo -e "删除: ${DATA_ID}"

    curl -s -X DELETE "${NACOS_SERVER}/nacos/v1/cs/configs" \
        -d "dataId=${DATA_ID}" \
        -d "group=${NACOS_GROUP}" \
        -d "tenant=${NACOS_NAMESPACE}" \
        -d "accessToken=${TOKEN}" > /dev/null 2>&1 || true
done
echo -e "${GREEN}✓ 清理完成${NC}"

# 3. 推送正确格式的配置
echo -e "\n${YELLOW}3. 推送正确格式的 YAML 配置...${NC}"

SUCCESS_COUNT=0
FAIL_COUNT=0

for APP in "${APPLICATIONS[@]}"; do
    DATA_ID="${APP}.migration"
    echo -e "\n${YELLOW}推送配置: ${DATA_ID}${NC}"

    # 创建 YAML 格式的配置内容
    YAML_CONTENT="key: ${APP}
step: FORCE_APPLICATION"

    response=$(curl -s -X POST "${NACOS_SERVER}/nacos/v1/cs/configs" \
        --data-urlencode "dataId=${DATA_ID}" \
        --data-urlencode "group=${NACOS_GROUP}" \
        --data-urlencode "content=${YAML_CONTENT}" \
        --data-urlencode "tenant=${NACOS_NAMESPACE}" \
        --data-urlencode "type=yaml" \
        --data-urlencode "accessToken=${TOKEN}")

    if [ "$response" == "true" ]; then
        echo -e "${GREEN}  ✓ 推送成功: ${DATA_ID}${NC}"
        echo -e "  内容: key: ${APP}, step: FORCE_APPLICATION"
        ((SUCCESS_COUNT++))
    else
        echo -e "${RED}  ✗ 推送失败: ${DATA_ID}${NC}"
        echo "  响应: $response"
        ((FAIL_COUNT++))
    fi
done

# 4. 汇总结果
echo -e "\n${YELLOW}===========================================${NC}"
echo -e "成功: ${GREEN}${SUCCESS_COUNT}${NC} 个配置"
echo -e "失败: ${RED}${FAIL_COUNT}${NC} 个配置"
echo -e "${YELLOW}===========================================${NC}"

if [ $FAIL_COUNT -eq 0 ]; then
    echo -e "\n${GREEN}✓ 所有配置推送成功！${NC}"
    echo -e "\n${YELLOW}重要提示：${NC}"
    echo -e "1. 配置已使用正确的 YAML 格式推送到 Nacos"
    echo -e "2. ${RED}必须重启服务${NC}才能生效"
    echo -e "3. 重启命令："
    echo -e "   ${GREEN}pkill -f 'ruoyi' && ./dev-start.sh${NC}"
    exit 0
else
    echo -e "\n${RED}✗ 部分配置推送失败${NC}"
    exit 1
fi

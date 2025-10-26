#!/bin/bash

# Nacos 配置自动导入脚本
# 将 script/config/nacos/ 目录下的所有配置文件导入到 Nacos

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# Nacos 服务器配置
NACOS_SERVER="http://localhost:8848"
NACOS_USERNAME="nacos"
NACOS_PASSWORD="nacos"
NACOS_NAMESPACE="dev"  # 使用 dev 命名空间
NACOS_GROUP="DEFAULT_GROUP"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
CONFIG_DIR="${SCRIPT_DIR}/../config/nacos"

echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}Nacos 配置自动导入工具${NC}"
echo -e "${GREEN}======================================${NC}"
echo ""

# 检查 Nacos 是否可访问
echo -e "${YELLOW}1. 检查 Nacos 服务状态...${NC}"
if ! curl -s -f "${NACOS_SERVER}/nacos" > /dev/null 2>&1; then
    echo -e "${RED}错误: 无法连接到 Nacos 服务器 ${NACOS_SERVER}${NC}"
    echo -e "${YELLOW}请确保 Nacos 已启动并可访问${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Nacos 服务正常${NC}"
echo ""

# 登录并获取 token
echo -e "${YELLOW}2. 登录 Nacos 并获取 token...${NC}"
TOKEN_RESPONSE=$(curl -s -X POST "${NACOS_SERVER}/nacos/v1/auth/login" \
    -d "username=${NACOS_USERNAME}" \
    -d "password=${NACOS_PASSWORD}")

ACCESS_TOKEN=$(echo "$TOKEN_RESPONSE" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -z "$ACCESS_TOKEN" ]; then
    echo -e "${RED}错误: 登录失败，无法获取 token${NC}"
    echo -e "${YELLOW}响应: ${TOKEN_RESPONSE}${NC}"
    exit 1
fi
echo -e "${GREEN}✓ 登录成功${NC}"
echo ""

# 函数：导入单个配置文件
import_config() {
    local file_path=$1
    local file_name=$(basename "$file_path")
    local data_id="${file_name}"

    # 根据文件扩展名确定配置类型
    if [[ "$file_name" == *.properties ]]; then
        config_type="properties"
    elif [[ "$file_name" == *.yml ]] || [[ "$file_name" == *.yaml ]]; then
        config_type="yaml"
    else
        config_type="text"
    fi

    echo -e "${YELLOW}导入配置: ${file_name}${NC}"

    # 发送配置到 Nacos (使用 --data-urlencode 自动处理 UTF-8 编码)
    response=$(curl -s -X POST "${NACOS_SERVER}/nacos/v1/cs/configs" \
        --data-urlencode "dataId=${data_id}" \
        --data-urlencode "group=${NACOS_GROUP}" \
        --data-urlencode "content@${file_path}" \
        --data-urlencode "type=${config_type}" \
        --data-urlencode "tenant=${NACOS_NAMESPACE}" \
        -H "accessToken: ${ACCESS_TOKEN}")

    if [[ "$response" == "true" ]]; then
        echo -e "${GREEN}  ✓ 导入成功: ${file_name}${NC}"
        return 0
    else
        echo -e "${RED}  ✗ 导入失败: ${file_name}${NC}"
        echo -e "${YELLOW}  响应: ${response}${NC}"
        return 1
    fi
}

# 导入所有配置文件
echo -e "${YELLOW}3. 开始导入配置文件...${NC}"
echo ""

success_count=0
fail_count=0

# 遍历配置目录中的所有 .yml 和 .properties 文件
for config_file in "${CONFIG_DIR}"/*.yml "${CONFIG_DIR}"/*.yaml "${CONFIG_DIR}"/*.properties; do
    # 检查文件是否存在（避免通配符无匹配时的问题）
    if [ ! -f "$config_file" ]; then
        continue
    fi

    # 跳过 README 文件
    if [[ "$(basename "$config_file")" == "README"* ]]; then
        continue
    fi

    if import_config "$config_file"; then
        ((success_count++))
    else
        ((fail_count++))
    fi
    echo ""
done

echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}导入完成！${NC}"
echo -e "${GREEN}======================================${NC}"
echo -e "成功: ${GREEN}${success_count}${NC} 个配置"
echo -e "失败: ${RED}${fail_count}${NC} 个配置"
echo ""
echo -e "${YELLOW}访问 Nacos 控制台查看配置:${NC}"
echo -e "${GREEN}${NACOS_SERVER}/nacos${NC}"
echo -e "用户名: ${GREEN}${NACOS_USERNAME}${NC}"
echo -e "密码: ${GREEN}${NACOS_PASSWORD}${NC}"
echo ""

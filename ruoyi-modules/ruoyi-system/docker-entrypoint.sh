#!/bin/bash
set -e

# ==============================================================================
# Docker Entrypoint Script Template for RuoYi-Cloud-Plus Services
# ==============================================================================
# This script handles:
# - Pre-flight validation checks
# - Conditional Skywalking APM agent loading
# - Proper signal handling for graceful shutdown
# - Startup logging for troubleshooting
# ==============================================================================

# Variables to be replaced when copying to specific services:
# - SERVICE_NAME: The service name (e.g., ruoyi-auth, ruoyi-gateway)
# - WORKDIR: Working directory (e.g., /ruoyi/auth, /ruoyi/gateway)

SERVICE_NAME="${SERVICE_NAME:-ruoyi-service}"
WORKDIR="${WORKDIR:-/ruoyi/service}"

# ==============================================================================
# Pre-flight Checks
# ==============================================================================

echo "[$(date +'%Y-%m-%d %H:%M:%S')] Starting ${SERVICE_NAME}..."

# Check if app.jar exists
if [ ! -f "${WORKDIR}/app.jar" ]; then
    echo "ERROR: ${WORKDIR}/app.jar not found!"
    exit 1
fi

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH!"
    exit 1
fi

# ==============================================================================
# Configuration Logging
# ==============================================================================

echo "Configuration:"
echo "  - Service Name: ${SERVICE_NAME}"
echo "  - Server Port: ${SERVER_PORT}"
echo "  - Skywalking Enabled: ${SKYWALKING_ENABLED}"
echo "  - Java Options: ${JAVA_OPTS}"
echo "  - Working Directory: ${WORKDIR}"

# ==============================================================================
# Build Java Command
# ==============================================================================

# Base JVM options
JAVA_CMD=(
    "java"
    "-Djava.security.egd=file:/dev/./urandom"
    "-Dserver.port=${SERVER_PORT}"
    "-XX:+HeapDumpOnOutOfMemoryError"
    "-XX:+UseZGC"
)

# Add Skywalking agent if enabled
if [ "${SKYWALKING_ENABLED}" = "true" ]; then
    echo "Skywalking APM agent is ENABLED"

    # Check if Skywalking agent exists
    if [ ! -f "/ruoyi/skywalking/agent/skywalking-agent.jar" ]; then
        echo "WARNING: Skywalking enabled but agent not found at /ruoyi/skywalking/agent/skywalking-agent.jar"
        echo "Continuing without Skywalking..."
    else
        JAVA_CMD+=(
            "-Dskywalking.agent.service_name=${SERVICE_NAME}"
            "-javaagent:/ruoyi/skywalking/agent/skywalking-agent.jar"
        )
    fi
else
    echo "Skywalking APM agent is DISABLED"
fi

# Add extra Java options if provided (service-specific)
if [ -n "${EXTRA_JAVA_OPTS}" ]; then
    read -ra EXTRA_OPTS <<< "${EXTRA_JAVA_OPTS}"
    JAVA_CMD+=("${EXTRA_OPTS[@]}")
fi

# Add custom Java options if provided
if [ -n "${JAVA_OPTS}" ]; then
    # Split JAVA_OPTS by space and add to array
    read -ra OPTS <<< "${JAVA_OPTS}"
    JAVA_CMD+=("${OPTS[@]}")
fi

# Add JAR file
JAVA_CMD+=("-jar" "app.jar")

# ==============================================================================
# Start Application
# ==============================================================================

echo "Starting Java application with command:"
echo "  ${JAVA_CMD[*]}"
echo "================================================================================"

# Execute Java process, replacing this script's process (becomes PID 1)
# This ensures proper signal handling for graceful shutdown
exec "${JAVA_CMD[@]}"

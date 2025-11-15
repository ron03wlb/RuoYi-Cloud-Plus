# 贝尔实验室 Spring 官方推荐镜像 JDK下载地址 https://bell-sw.com/pages/downloads/
ARG JDK_VERSION=21.0.8
FROM bellsoft/liberica-openjdk-rocky:${JDK_VERSION}-cds

LABEL maintainer="Lion Li"

# Create non-root user and directories
RUN groupadd -r ruoyi && useradd -r -g ruoyi ruoyi && \
    mkdir -p /ruoyi/job/logs \
    /ruoyi/job/temp \
    /ruoyi/skywalking/agent && \
    chown -R ruoyi:ruoyi /ruoyi

WORKDIR /ruoyi/job

ENV SERVER_PORT=9203 \
    SNAIL_PORT=29203 \
    LANG=C.UTF-8 \
    LC_ALL=C.UTF-8 \
    JAVA_OPTS="" \
    SKYWALKING_ENABLED=false

EXPOSE ${SERVER_PORT}
# 暴露 snail job 客户端端口 用于定时任务调度中心通信
EXPOSE ${SNAIL_PORT}

COPY --chown=ruoyi:ruoyi ./build/libs/ruoyi-job.jar ./app.jar
COPY --chown=ruoyi:ruoyi ./docker-entrypoint.sh /docker-entrypoint.sh

# Switch to non-root user
USER ruoyi

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Set service-specific environment variables for entrypoint script
ENV SERVICE_NAME=ruoyi-job \
    WORKDIR=/ruoyi/job \
    EXTRA_JAVA_OPTS="-Dsnail-job.port=${SNAIL_PORT}"

# Use exec form for better signal handling
ENTRYPOINT ["/docker-entrypoint.sh"]


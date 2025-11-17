# RuoYi Custom Nacos with PostgreSQL Support
FROM eclipse-temurin:17-jre

LABEL maintainer="RuoYi-Cloud-Plus"
LABEL description="Custom Nacos Server with PostgreSQL support"

# 设置工作目录
WORKDIR /home/nacos

# 复制构建好的 JAR 文件
COPY build/libs/ruoyi-nacos.jar /home/nacos/nacos-server.jar

# 复制 PostgreSQL 数据源插件到 plugins 目录
COPY src/main/resources/META-INF/plugins/*.jar /home/nacos/plugins/

# 创建日志和数据目录
RUN mkdir -p /home/nacos/logs /home/nacos/data

# 暴露端口
EXPOSE 8848 9848 9849

# 设置环境变量
ENV MODE=standalone \
    PREFER_HOST_MODE=hostname \
    SPRING_DATASOURCE_PLATFORM=postgresql \
    TZ=Asia/Shanghai

# 启动命令
ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Xms256m", \
            "-Xmx512m", \
            "-jar", \
            "/home/nacos/nacos-server.jar"]

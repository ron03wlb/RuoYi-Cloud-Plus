# 基于官方 Nacos 镜像，添加 PostgreSQL 支持
FROM nacos/nacos-server:v2.4.3

# 维护者信息
LABEL maintainer="RuoYi-Cloud-Plus"
LABEL description="Nacos Server with PostgreSQL support"

# 下载并安装 PostgreSQL 支持
USER root

# 创建插件目录
RUN mkdir -p /home/nacos/plugins/mysql

# 下载 PostgreSQL JDBC 驱动
RUN cd /home/nacos/plugins/mysql && \
    wget -q https://jdbc.postgresql.org/download/postgresql-42.7.4.jar && \
    chmod 644 postgresql-42.7.4.jar

# 下载 Nacos PostgreSQL 数据源插件 (from Maven Central) - 直接放在 plugins 目录
RUN cd /home/nacos/plugins && \
    wget -q https://repo1.maven.org/maven2/net/hlinfo/nacos-datasource-plugin-pgsql/2.3.0/nacos-datasource-plugin-pgsql-2.3.0.jar && \
    chmod 644 nacos-datasource-plugin-pgsql-2.3.0.jar

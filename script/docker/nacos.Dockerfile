# 基于官方 Nacos 镜像，添加 MySQL 支持
FROM nacos/nacos-server:v2.5.1

# 维护者信息
LABEL maintainer="RuoYi-Cloud-Plus"
LABEL description="Nacos Server with MySQL support"

USER root

/*
 * ===========================================
 * ruoyi-common-oss
 * OSS服务
 * ===========================================
 */

description = "ruoyi-common-oss oss服务"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-json"))
    api(project(":ruoyi-common:ruoyi-common-redis"))

    // ===========================================
    // AWS SDK BOM (Bill of Materials)
    // ===========================================
    api(platform("software.amazon.awssdk:bom:2.29.48"))

    // ===========================================
    // AWS SDK for Java 2.x - S3
    // ===========================================
    api("software.amazon.awssdk:s3") {
        // 将基于 CRT 的 HTTP 客户端从类路径中移除
        exclude(group = "software.amazon.awssdk", module = "aws-crt-client")
        // 将基于 Apache 的 HTTP 客户端从类路径中移除
        exclude(group = "software.amazon.awssdk", module = "apache-client")
        // 将配置基于 URL 连接的 HTTP 客户端从类路径中移除
        exclude(group = "software.amazon.awssdk", module = "url-connection-client")
    }

    // ===========================================
    // AWS SDK - Netty NIO Client
    // ===========================================
    // 使用基于 Netty 的 HTTP 客户端
    api("software.amazon.awssdk:netty-nio-client")

    // ===========================================
    // AWS SDK - S3 Transfer Manager
    // ===========================================
    // 基于 AWS CRT 的 S3 客户端的性能增强的 S3 传输管理器
    api("software.amazon.awssdk:s3-transfer-manager")
}

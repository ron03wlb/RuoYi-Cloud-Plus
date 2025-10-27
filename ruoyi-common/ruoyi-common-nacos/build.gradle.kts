/*
 * ===========================================
 * ruoyi-common-nacos
 * 配置中心
 * ===========================================
 */

description = "ruoyi-common-nacos 配置中心"

dependencies {
    // ===========================================
    // Nacos 服务发现 & 配置中心
    // ===========================================

    // Nacos 服务发现
    api("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")

    // Nacos 配置中心
    api("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config")
}

/*
 * ===========================================
 * ruoyi-common-dubbo
 * RPC服务
 * ===========================================
 */

description = "ruoyi-common-dubbo RPC服务"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-json"))

    // ===========================================
    // Spring Cloud
    // ===========================================
    api(libs.spring.cloud.context)
    api(libs.spring.cloud.commons)

    // ===========================================
    // Dubbo
    // ===========================================
    api(libs.dubbo.spring.boot.starter)
    api(libs.dubbo.spring.boot.actuator)

    // Dubbo Redis Metadata Report
    api(libs.dubbo.metadata.report.redis) {
        exclude(group = "redis.clients", module = "jedis")
    }

    // Jedis（显式声明版本）
    api(libs.jedis)

    // ===========================================
    // Sa-Token Dubbo 整合
    // ===========================================
    api(libs.sa.token.dubbo3) {
        exclude(group = "org.apache.dubbo", module = "dubbo")
    }
}

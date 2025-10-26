/*
 * ===========================================
 * ruoyi-common-loadbalancer
 * 自定义负载均衡(多团队开发使用)
 * ===========================================
 */

description = "ruoyi-common-loadbalancer 自定义负载均衡(多团队开发使用)"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-core"))

    // ===========================================
    // Spring Cloud LoadBalancer
    // ===========================================
    api(libs.spring.cloud.starter.loadbalancer)

    // ===========================================
    // Dubbo (provided)
    // ===========================================
    compileOnly(libs.dubbo.spring.boot.starter)
}

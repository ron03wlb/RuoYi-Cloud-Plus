/*
 * ===========================================
 * ruoyi-common-job
 * 定时任务
 * ===========================================
 */

description = "ruoyi-common-job 定时任务"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-core"))

    // ===========================================
    // Spring Boot
    // ===========================================
    api("org.springframework.boot:spring-boot-autoconfigure")

    // ===========================================
    // Spring Cloud 服务发现
    // ===========================================
    api("org.springframework.cloud:spring-cloud-commons")

    // ===========================================
    // SnailJob Client
    // ===========================================
    api(libs.snailjob.client.starter)
    api("com.aizuda:snail-job-client-job-core:1.8.0")
}

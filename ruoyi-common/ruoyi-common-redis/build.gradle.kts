/*
 * ===========================================
 * ruoyi-common-redis
 * 缓存服务
 * ===========================================
 */

description = "ruoyi-common-redis 缓存服务"

dependencies {
    // ===========================================
    // Spring 核心依赖
    // ===========================================
    api("org.springframework:spring-context")
    api("org.springframework:spring-core")
    api("org.springframework:spring-context-support")
    api("org.springframework:spring-web")

    // Spring Boot
    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")

    // ===========================================
    // Jakarta EE API
    // ===========================================
    api("jakarta.servlet:jakarta.servlet-api")

    // ===========================================
    // 日志
    // ===========================================
    api("org.slf4j:slf4j-api")

    // ===========================================
    // 工具库
    // ===========================================
    api(libs.hutool.core)

    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-core"))

    // ===========================================
    // Redisson（Redis 客户端）
    // ===========================================
    api(libs.redisson)

    // ===========================================
    // Lock4j（分布式锁）
    // ===========================================
    api(libs.lock4j)

    // ===========================================
    // Caffeine（本地缓存）
    // ===========================================
    api("com.github.ben-manes.caffeine:caffeine")

    // ===========================================
    // Jackson（序列化）
    // ===========================================
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}

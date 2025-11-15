/*
 * ===========================================
 * ruoyi-common-redis
 * 缓存服务
 * ===========================================
 */

plugins {
    id("jacoco")
}

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

    // ===========================================
    // 测试依赖
    // ===========================================
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
}

// ===========================================
// JaCoCo 配置
// ===========================================
jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    // 排除不需要覆盖的类
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/annotation/**",      // 注解类
                    "**/config/**",           // 配置类（Spring配置）
                    "**/*Application.class"   // 主程序
                )
            }
        })
    )
}

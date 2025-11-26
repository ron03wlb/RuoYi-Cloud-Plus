/*
 * ===========================================
 * ruoyi-common-satoken
 * 权限认证服务
 * ===========================================
 */

plugins {
    id("jacoco")
}

description = "ruoyi-common-satoken 权限认证服务"

dependencies {
    // ===========================================
    // Sa-Token 核心
    // ===========================================
    api(libs.sa.token.core)

    // ===========================================
    // Sa-Token 整合 JWT
    // ===========================================
    api(libs.sa.token.jwt)

    // ===========================================
    // 项目内依赖
    // ===========================================

    // API 模块
    api(project(":ruoyi-api:ruoyi-api-system"))

    // Redis 模块
    api(project(":ruoyi-common:ruoyi-common-redis"))

    // ===========================================
    // Caffeine 本地缓存
    // ===========================================
    api("com.github.ben-manes.caffeine:caffeine")

    // ===========================================
    // 測試依賴
    // ===========================================
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation") // Bean Validation
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
}

// ===========================================
// JaCoCo 配置
// ===========================================
jacoco {
    toolVersion = "0.8.11"
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
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/config/**", // 配置类
                        "**/handler/**", // 异常处理器（已测试）
                    )
                }
            },
        ),
    )
}

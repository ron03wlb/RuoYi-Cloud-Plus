/*
 * ===========================================
 * ruoyi-common-test
 * 集成测试框架
 * ===========================================
 */

plugins {
    jacoco
}

description = "ruoyi-common-test 集成测试框架"

dependencies {
    // ===========================================
    // Spring Boot Test
    // ===========================================
    api("org.springframework.boot:spring-boot-starter-test")

    // ===========================================
    // Testcontainers
    // ===========================================
    api("org.testcontainers:testcontainers")
    api("org.testcontainers:junit-jupiter")
    api("org.testcontainers:mysql")
    api("org.testcontainers:postgresql")

    // ===========================================
    // Database
    // ===========================================
    api("com.mysql:mysql-connector-j")
    api("com.zaxxer:HikariCP")

    // MyBatis Plus (for database operations)
    api(libs.mybatis.plus.spring.boot3.starter)

    // ===========================================
    // Redis
    // ===========================================
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api(libs.redisson)

    // ===========================================
    // Sa-Token (for authentication testing)
    // ===========================================
    api(libs.sa.token.spring.boot3.starter)
    api(libs.sa.token.redis.jackson)

    // ===========================================
    // Dubbo (for RPC mocking)
    // ===========================================
    compileOnly(libs.dubbo.spring.boot.starter)

    // ===========================================
    // Common dependencies from project
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-core"))
    compileOnly(project(":ruoyi-common:ruoyi-common-satoken"))
    compileOnly(project(":ruoyi-common:ruoyi-common-mybatis"))
    compileOnly(project(":ruoyi-common:ruoyi-common-redis"))
    compileOnly(project(":ruoyi-common:ruoyi-common-tenant"))

    // ===========================================
    // AOP Support
    // ===========================================
    api("org.aspectj:aspectjweaver")

    // ===========================================
    // Test utilities
    // ===========================================
    api("org.junit.jupiter:junit-jupiter")
    api("org.mockito:mockito-core")
    api("org.mockito:mockito-junit-jupiter")
    api("org.assertj:assertj-core")

    // ===========================================
    // Logging
    // ===========================================
    api("org.slf4j:slf4j-api")
    api("ch.qos.logback:logback-classic")
}

// ===========================================
// 测试任务配置
// ===========================================
tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

// ===========================================
// JaCoCo 覆盖率配置
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

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/config/**",           // 配置类
                    "**/examples/**"          // 示例类
                )
            }
        })
    )
}

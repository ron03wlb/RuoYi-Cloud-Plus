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
    // 项目内依赖 - api
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-core"))

    // ===========================================
    // 项目内依赖 - compileOnly
    // ===========================================
    compileOnly(project(":ruoyi-common:ruoyi-common-satoken"))
    compileOnly(project(":ruoyi-common:ruoyi-common-mybatis"))
    compileOnly(project(":ruoyi-common:ruoyi-common-redis"))
    compileOnly(project(":ruoyi-common:ruoyi-common-tenant"))

    // ===========================================
    // Spring Boot Test
    // ===========================================
    api("org.springframework.boot:spring-boot-starter-test")
    api("org.springframework.boot:spring-boot-starter-data-redis")

    // ===========================================
    // Testcontainers
    // ===========================================
    api("org.testcontainers:testcontainers")
    api("org.testcontainers:junit-jupiter")
    api("org.testcontainers:postgresql")

    // ===========================================
    // 数据库
    // ===========================================
    api("org.postgresql:postgresql")
    api("com.zaxxer:HikariCP")
    api(libs.mybatis.plus.spring.boot3.starter)

    // ===========================================
    // Redis
    // ===========================================
    api(libs.redisson)

    // ===========================================
    // Sa-Token
    // ===========================================
    api(libs.sa.token.spring.boot3.starter)
    api(libs.sa.token.redis.jackson)

    // ===========================================
    // AOP
    // ===========================================
    api("org.aspectj:aspectjweaver")

    // ===========================================
    // 测试工具库
    // ===========================================
    api("org.junit.jupiter:junit-jupiter")
    api("org.mockito:mockito-core")
    api("org.mockito:mockito-junit-jupiter")
    api("org.assertj:assertj-core")

    // ===========================================
    // 日志
    // ===========================================
    api("org.slf4j:slf4j-api")
    api("ch.qos.logback:logback-classic")

    // ===========================================
    // Dubbo (compileOnly)
    // ===========================================
    compileOnly(libs.dubbo.spring.boot.starter)
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
    toolVersion = "0.8.11"
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

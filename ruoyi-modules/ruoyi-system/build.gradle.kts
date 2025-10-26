/*
 * ===========================================
 * ruoyi-system
 * 系统模块
 * ===========================================
 */

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
}

description = "ruoyi-system系统模块"

dependencies {
    // Spring核心依赖
    api(libs.spring.context)
    api(libs.spring.web)
    api(libs.spring.beans)
    api(libs.spring.boot)
    api(libs.spring.boot.autoconfigure)

    // Jakarta API
    api(libs.jakarta.servlet.api)
    api(libs.jakarta.validation.api)

    // Hutool
    api(libs.hutool.core)

    // Dubbo
    implementation(libs.dubbo)

    // Sa-Token
    implementation(libs.sa.token.core)

    // RuoYi核心模块
    implementation(project(":ruoyi-common:ruoyi-common-core"))
    implementation(project(":ruoyi-common:ruoyi-common-excel"))
    implementation(project(":ruoyi-common:ruoyi-common-satoken"))
    implementation(project(":ruoyi-common:ruoyi-common-nacos"))

    // RuoYi Common Log
    implementation(project(":ruoyi-common:ruoyi-common-log"))
    implementation(project(":ruoyi-common:ruoyi-common-doc"))
    implementation(project(":ruoyi-common:ruoyi-common-web"))
    implementation(project(":ruoyi-common:ruoyi-common-mybatis"))
    implementation(project(":ruoyi-common:ruoyi-common-idempotent"))
    implementation(project(":ruoyi-common:ruoyi-common-tenant"))

    // 分布式事务（某些业务需要）
    compileOnly(project(":ruoyi-common:ruoyi-common-seata"))

    implementation(project(":ruoyi-common:ruoyi-common-security"))
    implementation(project(":ruoyi-common:ruoyi-common-translation"))
    implementation(project(":ruoyi-common:ruoyi-common-sensitive"))
    implementation(project(":ruoyi-common:ruoyi-common-encrypt"))

    // RuoYi Api System
    implementation(project(":ruoyi-api:ruoyi-api-system"))
    implementation(project(":ruoyi-api:ruoyi-api-resource"))
    implementation(project(":ruoyi-api:ruoyi-api-workflow"))
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("${project.name}.jar")
    layered {
        enabled.set(true)
    }
}

jib {
    from {
        image = "eclipse-temurin:17-jre-alpine"
        platforms {
            platform {
                architecture = "amd64"
                os = "linux"
            }
        }
    }
    to {
        image = "ruoyi-cloud-plus/${project.name}"
        tags = setOf(version.toString(), "latest")
    }
    container {
        jvmFlags = listOf("-Xms512m", "-Xmx1024m", "-XX:+UseG1GC")
        ports = listOf("9201")
        environment = mapOf("SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

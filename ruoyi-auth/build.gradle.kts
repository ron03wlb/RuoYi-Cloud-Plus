/*
 * ===========================================
 * ruoyi-auth
 * 认证授权中心
 * ===========================================
 */

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
}

description = "ruoyi-auth 认证授权中心"

dependencies {
    // Spring 核心
    api("org.springframework:spring-context")
    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")

    // 日志
    api("org.slf4j:slf4j-api")

    // RuoYi核心模块
    implementation(project(":ruoyi-common:ruoyi-common-core"))
    implementation(project(":ruoyi-common:ruoyi-common-json"))
    implementation(project(":ruoyi-common:ruoyi-common-satoken"))
    implementation(project(":ruoyi-common:ruoyi-common-nacos"))

    // API模块
    implementation(project(":ruoyi-api:ruoyi-api-system"))
    implementation(project(":ruoyi-api:ruoyi-api-resource"))

    // Sa-Token
    implementation(libs.sa.token.core)

    // JustAuth (第三方登录)
    implementation(libs.justauth)

    // Hutool验证码
    implementation(libs.hutool.captcha)

    // RuoYi Common 功能模块
    implementation(project(":ruoyi-common:ruoyi-common-security"))
    implementation(project(":ruoyi-common:ruoyi-common-social"))
    implementation(project(":ruoyi-common:ruoyi-common-log"))
    implementation(project(":ruoyi-common:ruoyi-common-doc"))
    implementation(project(":ruoyi-common:ruoyi-common-web"))
    implementation(project(":ruoyi-common:ruoyi-common-ratelimiter"))
    implementation(project(":ruoyi-common:ruoyi-common-encrypt"))
    implementation(project(":ruoyi-common:ruoyi-common-tenant"))

    // Dubbo (可选,用于RPC调用)
    implementation(libs.dubbo)
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
        jvmFlags = listOf("-Xms512m", "-Xmx512m", "-XX:+UseG1GC")
        ports = listOf("9210")
        environment = mapOf("SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

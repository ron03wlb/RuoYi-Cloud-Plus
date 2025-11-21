/*
 * ===========================================
 * ruoyi-job
 * 任务调度模块
 * ===========================================
 */

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
}

description = "ruoyi-job 任务调度模块"

dependencies {
    // ===========================================
    // Spring 核心依赖
    // ===========================================
    api("org.springframework:spring-context")
    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")

    // ===========================================
    // 日志
    // ===========================================
    api("org.slf4j:slf4j-api")

    // ===========================================
    // 工具库
    // ===========================================
    api(libs.hutool.core)

    // ===========================================
    // RuoYi 核心模块
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-json"))
    implementation(project(":ruoyi-common:ruoyi-common-nacos"))

    // ===========================================
    // RuoYi 功能模块
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-log"))
    implementation(project(":ruoyi-common:ruoyi-common-web"))
    implementation(project(":ruoyi-common:ruoyi-common-mybatis"))
    implementation(project(":ruoyi-common:ruoyi-common-job"))
    implementation(project(":ruoyi-common:ruoyi-common-security"))

    implementation(project(":ruoyi-common:ruoyi-common-tenant")) {
        exclude(group = "org.dromara", module = "ruoyi-common-mybatis")
    }

    // ===========================================
    // RuoYi API 模块
    // ===========================================
    implementation(project(":ruoyi-api:ruoyi-api-system"))

    // ===========================================
    // Dubbo
    // ===========================================
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
        ports = listOf("9203")
        environment = mapOf("SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

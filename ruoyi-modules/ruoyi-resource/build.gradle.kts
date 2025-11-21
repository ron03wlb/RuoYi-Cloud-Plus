/*
 * ===========================================
 * ruoyi-resource
 * 资源服务
 * ===========================================
 */

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
}

description = "ruoyi-resource资源服务"

dependencies {
    // ===========================================
    // RuoYi 核心模块
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-nacos"))

    // ===========================================
    // RuoYi 功能模块
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-doc"))
    implementation(project(":ruoyi-common:ruoyi-common-dubbo"))
    implementation(project(":ruoyi-common:ruoyi-common-seata"))
    implementation(project(":ruoyi-common:ruoyi-common-web"))
    implementation(project(":ruoyi-common:ruoyi-common-log"))
    implementation(project(":ruoyi-common:ruoyi-common-idempotent"))
    implementation(project(":ruoyi-common:ruoyi-common-oss"))
    implementation(project(":ruoyi-common:ruoyi-common-ratelimiter"))
    implementation(project(":ruoyi-common:ruoyi-common-mail"))
    implementation(project(":ruoyi-common:ruoyi-common-sms"))
    implementation(project(":ruoyi-common:ruoyi-common-mybatis"))
    implementation(project(":ruoyi-common:ruoyi-common-tenant"))
    implementation(project(":ruoyi-common:ruoyi-common-security"))
    implementation(project(":ruoyi-common:ruoyi-common-translation"))
    implementation(project(":ruoyi-common:ruoyi-common-websocket"))
    implementation(project(":ruoyi-common:ruoyi-common-sse"))
    implementation(project(":ruoyi-common:ruoyi-common-service-impl"))

    // ===========================================
    // RuoYi API 模块
    // ===========================================
    implementation(project(":ruoyi-api:ruoyi-api-system"))
    implementation(project(":ruoyi-api:ruoyi-api-resource"))

    // ===========================================
    // 测试依赖
    // ===========================================
    testImplementation(project(":ruoyi-common:ruoyi-common-test"))
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
        ports = listOf("9204")
        environment = mapOf("SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

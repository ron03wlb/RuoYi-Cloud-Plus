/*
 * ===========================================
 * ruoyi-demo
 * 演示模块
 * ===========================================
 */

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
}

description = "ruoyi-demo 演示模块"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-nacos"))
    implementation(project(":ruoyi-common:ruoyi-common-log"))
    implementation(project(":ruoyi-common:ruoyi-common-doc"))
    implementation(project(":ruoyi-common:ruoyi-common-security"))
    implementation(project(":ruoyi-common:ruoyi-common-web"))
    implementation(project(":ruoyi-common:ruoyi-common-mybatis"))
    implementation(project(":ruoyi-common:ruoyi-common-dubbo"))
    implementation(project(":ruoyi-common:ruoyi-common-idempotent"))
    implementation(project(":ruoyi-common:ruoyi-common-mail"))
    implementation(project(":ruoyi-common:ruoyi-common-sms"))
    implementation(project(":ruoyi-common:ruoyi-common-encrypt"))
    implementation(project(":ruoyi-common:ruoyi-common-tenant"))
    implementation(project(":ruoyi-common:ruoyi-common-elasticsearch"))
    implementation(project(":ruoyi-common:ruoyi-common-translation"))
    implementation(project(":ruoyi-common:ruoyi-common-sensitive"))

    // ===========================================
    // 测试消息总线使用 搭配 TestBusController
    // ===========================================
    // compileOnly(project(":ruoyi-api:ruoyi-api-workflow"))

    // ===========================================
    // 测试依赖
    // ===========================================
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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
        ports = listOf("9301")
        environment = mapOf("SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

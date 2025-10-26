/*
 * ===========================================
 * ruoyi-workflow
 * 工作流模块
 * ===========================================
 */

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
}

description = "工作流模块"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-nacos"))
    implementation(project(":ruoyi-common:ruoyi-common-doc"))
    implementation(project(":ruoyi-common:ruoyi-common-mybatis"))
    implementation(project(":ruoyi-common:ruoyi-common-web"))
    implementation(project(":ruoyi-common:ruoyi-common-log"))
    implementation(project(":ruoyi-common:ruoyi-common-idempotent"))
    implementation(project(":ruoyi-common:ruoyi-common-excel"))
    implementation(project(":ruoyi-common:ruoyi-common-translation"))
    implementation(project(":ruoyi-common:ruoyi-common-tenant"))
    implementation(project(":ruoyi-common:ruoyi-common-security"))
    implementation(project(":ruoyi-common:ruoyi-common-dubbo"))
    implementation(project(":ruoyi-common:ruoyi-common-seata"))
    implementation(project(":ruoyi-common:ruoyi-common-service-impl"))
    implementation(project(":ruoyi-common:ruoyi-common-bus"))

    // ===========================================
    // Warm-Flow 工作流引擎
    // ===========================================
    implementation("org.dromara.warm:warm-flow-mybatis-plus-sb3-starter:1.8.1")
    implementation("org.dromara.warm:warm-flow-plugin-ui-sb-web:1.8.1")

    // ===========================================
    // RuoYi Api
    // ===========================================
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
        ports = listOf("9205")
        environment = mapOf("SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

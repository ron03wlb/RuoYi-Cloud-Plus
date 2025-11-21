/*
 * ===========================================
 * ruoyi-gen
 * 代码生成
 * ===========================================
 */

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
}

description = "ruoyi-gen代码生成"

dependencies {
    // ===========================================
    // Spring 核心依赖
    // ===========================================
    api("org.springframework:spring-context")
    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")

    // ===========================================
    // Apache Commons
    // ===========================================
    api("org.apache.commons:commons-lang3")

    // ===========================================
    // MyBatis Plus
    // ===========================================
    api(libs.mybatis.plus.annotation)

    // ===========================================
    // RuoYi 核心模块
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-core"))
    implementation(project(":ruoyi-common:ruoyi-common-nacos"))

    // ===========================================
    // RuoYi 功能模块
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-log"))
    implementation(project(":ruoyi-common:ruoyi-common-idempotent"))
    implementation(project(":ruoyi-common:ruoyi-common-doc"))
    implementation(project(":ruoyi-common:ruoyi-common-web"))
    implementation(project(":ruoyi-common:ruoyi-common-mybatis"))
    implementation(project(":ruoyi-common:ruoyi-common-security"))

    // ===========================================
    // 代码生成引擎
    // ===========================================
    implementation(libs.velocity.engine.core)

    // ===========================================
    // Anyline (面向运行时的 D-ORM)
    // ===========================================
    implementation(libs.anyline.environment.spring.data.jdbc)
    implementation(libs.anyline.data.jdbc.postgresql)

    // ===========================================
    // Dubbo
    // ===========================================
    implementation(libs.dubbo)

    // ===========================================
    // 测试依赖
    // ===========================================
    testImplementation(project(":ruoyi-common:ruoyi-common-test"))
}

// ===========================================
// Test Configuration
// ===========================================

// 排除测试运行时的 Dubbo 依赖，避免 Dubbo 初始化问题
configurations {
    testRuntimeClasspath {
        exclude(group = "org.apache.dubbo")
        exclude(group = "org.apache.dubbo.spring.boot")
    }
}

tasks.test {
    useJUnitPlatform()
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
        ports = listOf("9202")
        environment = mapOf("SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

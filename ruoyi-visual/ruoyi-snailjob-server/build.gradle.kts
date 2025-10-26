/*
 * ===========================================
 * ruoyi-snailjob-server
 * SnailJob 定时任务服务器
 * ===========================================
 */

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
}

description = "ruoyi-snailjob-server SnailJob定时任务服务器"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-nacos"))

    // ===========================================
    // SnailJob Server
    // ===========================================
    implementation("com.aizuda:snail-job-server-starter:1.8.0") {
        exclude(group = "org.scala-lang", module = "scala-library")
    }

    // ===========================================
    // Scala Library
    // ===========================================
    implementation("org.scala-lang:scala-library:2.13.9")

    // ===========================================
    // Spring Boot Admin Client
    // ===========================================
    implementation("de.codecentric:spring-boot-admin-starter-client:3.5.0")
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
        ports = listOf("8800")
        environment = mapOf("SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

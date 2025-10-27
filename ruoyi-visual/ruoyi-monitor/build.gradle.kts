/*
 * ===========================================
 * ruoyi-monitor
 * 监控中心
 * ===========================================
 */

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
}

description = "ruoyi-monitor监控中心"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-nacos"))

    // ===========================================
    // Spring Boot Admin
    // ===========================================
    implementation("de.codecentric:spring-boot-admin-starter-server:3.5.0")

    // ===========================================
    // Spring Boot Web (使用 Undertow)
    // ===========================================
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow")

    // ===========================================
    // Spring Security
    // ===========================================
    implementation("org.springframework.boot:spring-boot-starter-security")
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
        jvmFlags = listOf("-Xms256m", "-Xmx512m", "-XX:+UseG1GC")
        ports = listOf("9100")
        environment = mapOf("SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

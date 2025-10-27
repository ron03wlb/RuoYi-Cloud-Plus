/*
 * ===========================================
 * ruoyi-test-mq
 * 消息队列测试案例项目
 * ===========================================
 */

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
}

description = "ruoyi-test-mq 案例项目"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-nacos"))
    implementation(project(":ruoyi-common:ruoyi-common-security"))
    implementation(project(":ruoyi-common:ruoyi-common-doc"))
    implementation(project(":ruoyi-common:ruoyi-common-web"))

    implementation(project(":ruoyi-common:ruoyi-common-tenant")) {
        exclude(group = "org.dromara", module = "ruoyi-common-mybatis")
    }

    // ===========================================
    // 消息队列依赖
    // ===========================================
    // RabbitMQ
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    // RocketMQ
    implementation(libs.rocketmq.spring.boot.starter)

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")
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
        ports = listOf("9302")
        environment = mapOf("SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

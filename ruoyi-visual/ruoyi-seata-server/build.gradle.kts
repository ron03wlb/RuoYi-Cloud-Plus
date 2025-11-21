/*
 * ===========================================
 * ruoyi-seata-server
 * Seata 分布式事务服务器
 * ===========================================
 *
 * 注意：
 * 1. 此模块使用 Spring Boot 2.7.18 和 Spring Framework 5.3.39
 * 2. 依赖本地 seata-server JAR 文件
 * 3. 生产环境建议使用官方 Seata Docker 镜像
 */

plugins {
    id("org.springframework.boot") version "2.7.18"
}

description = "ruoyi-seata-server Seata分布式事务服务器"

// Seata 版本
val seataVersion = "2.5.0"
val springBootForServerVersion = "2.7.18"
val springFrameworkForServerVersion = "5.3.39"
val snakeYamlForServerVersion = "2.0"
val logstashLogbackEncoderVersion = "7.2"
val jedisVersion = "3.8.0"
val kafkaClientsVersion = "3.6.1"

// 独立的依赖管理（不使用根项目的 BOM）
configurations.all {
    resolutionStrategy {
        // 强制使用特定版本
        force("org.springframework:spring-framework-bom:$springFrameworkForServerVersion")
        force("org.springframework.boot:spring-boot:$springBootForServerVersion")
        force("org.yaml:snakeyaml:$snakeYamlForServerVersion")
    }
}

dependencies {
    // ===========================================
    // Seata Server
    // ===========================================
    implementation("org.apache.seata:seata-server:$seataVersion")
    implementation("org.apache.seata:seata-spring-autoconfigure-server:$seataVersion")
    implementation("org.apache.seata:seata-core:$seataVersion")
    implementation("org.apache.seata:seata-config-all:$seataVersion") {
        exclude(group = "log4j", module = "log4j")
    }
    implementation("org.apache.seata:seata-discovery-all:$seataVersion")
    implementation("org.apache.seata:seata-serializer-all:$seataVersion")
    implementation("org.apache.seata:seata-compressor-all:$seataVersion")
    implementation("org.apache.seata:seata-metrics-all:$seataVersion")

    // ===========================================
    // Spring Boot & Spring Framework
    // ===========================================
    implementation("org.springframework.boot:spring-boot-starter:$springBootForServerVersion")
    implementation("org.springframework:spring-web:$springFrameworkForServerVersion")
    implementation("javax.servlet:javax.servlet-api")

    // ===========================================
    // 配置解析
    // ===========================================
    implementation("org.yaml:snakeyaml:$snakeYamlForServerVersion")

    // ===========================================
    // 数据库驱动
    // ===========================================
    implementation("org.postgresql:postgresql")

    // ===========================================
    // 数据库连接池
    // ===========================================
    implementation("com.alibaba:druid")
    implementation("org.apache.commons:commons-dbcp2")
    implementation("com.zaxxer:HikariCP")

    // ===========================================
    // Redis
    // ===========================================
    implementation("redis.clients:jedis:$jedisVersion")

    // ===========================================
    // 日志
    // ===========================================
    implementation("ch.qos.logback:logback-classic")
    implementation("ch.qos.logback:logback-core")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashLogbackEncoderVersion")
    implementation("com.github.danielwegener:logback-kafka-appender") {
        exclude(group = "org.apache.kafka", module = "kafka-clients")
    }
    implementation("org.codehaus.janino:janino")

    // ===========================================
    // Kafka
    // ===========================================
    implementation("org.apache.kafka:kafka-clients:$kafkaClientsVersion")

    // ===========================================
    // Raft 共识算法
    // ===========================================
    implementation("com.alipay.sofa:jraft-core") {
        exclude(group = "com.alipay.sofa", module = "bolt")
    }
    implementation("com.alipay.sofa:bolt")

    // ===========================================
    // 工具库
    // ===========================================
    implementation("com.google.guava:guava")
    implementation("com.alibaba:fastjson")
    implementation("com.beust:jcommander")
    implementation("com.bucket4j:bucket4j_jdk8-core")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("${project.name}.jar")
}

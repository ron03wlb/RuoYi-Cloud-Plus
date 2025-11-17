/*
 * ===========================================
 * ruoyi-nacos
 * Nacos 服务器
 * ===========================================
 *
 * 注意：
 * 1. 此模块使用 Spring Boot 2.7.18（与主项目不同）
 * 2. 依赖大量本地 JAR 文件（src/main/resources/lib/）
 * 3. 生产环境建议使用官方 Nacos Docker 镜像
 */

plugins {
    java
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
}

group = "org.dromara"
version = "2.5.0"

description = "ruoyi-nacos Nacos服务器"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// Nacos 版本
val nacosVersion = "2.5.1"
val snakeYamlVersion = "2.0"
val springBootAdminVersion = "2.7.11"
val nacosLibPath = file("src/main/resources/lib")

// 独立的依赖管理（不使用根项目的 BOM）
configurations.all {
    resolutionStrategy {
        // 强制使用 Spring Boot 2.7.18
        force("org.springframework.boot:spring-boot:2.7.18")
        force("org.springframework.boot:spring-boot-autoconfigure:2.7.18")
    }
}

dependencies {
    // ===========================================
    // Nacos 本地 JAR 依赖 (system scope)
    // ===========================================
    implementation(
        files(
            "$nacosLibPath/nacos-console-$nacosVersion.jar",
            "$nacosLibPath/nacos-auth-$nacosVersion.jar",
            "$nacosLibPath/nacos-cmdb-$nacosVersion.jar",
            "$nacosLibPath/nacos-config-$nacosVersion.jar",
            "$nacosLibPath/nacos-persistence-$nacosVersion.jar",
            "$nacosLibPath/nacos-consistency-$nacosVersion.jar",
            "$nacosLibPath/nacos-control-plugin-$nacosVersion.jar",
            "$nacosLibPath/nacos-config-plugin-$nacosVersion.jar",
            "$nacosLibPath/nacos-core-$nacosVersion.jar",
            "$nacosLibPath/nacos-istio-$nacosVersion.jar",
            "$nacosLibPath/nacos-naming-$nacosVersion.jar",
            "$nacosLibPath/default-auth-plugin-$nacosVersion.jar",
            "$nacosLibPath/default-control-plugin-$nacosVersion.jar",
            "$nacosLibPath/nacos-prometheus-$nacosVersion.jar",
            "$nacosLibPath/nacos-sys-$nacosVersion.jar",
            "$nacosLibPath/nacos-default-plugin-all-$nacosVersion.jar"
        )
    )

    // ===========================================
    // Nacos Maven 依赖
    // ===========================================
    implementation("com.alibaba.nacos:nacos-custom-environment-plugin:$nacosVersion")
    implementation("com.alibaba.nacos:nacos-datasource-plugin:$nacosVersion")
    implementation("com.alibaba.nacos:nacos-encryption-plugin:$nacosVersion")
    implementation("com.alibaba.nacos:nacos-trace-plugin:$nacosVersion")
    implementation("com.alibaba.nacos:nacos-common:$nacosVersion")
    implementation("com.alibaba.nacos:nacos-client:$nacosVersion")

    // ===========================================
    // Spring Boot (2.7.18)
    // ===========================================
    implementation("org.springframework.boot:spring-boot-starter-web:2.7.18") {
        exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
    }

    // 覆盖 Tomcat 版本避免 CVE-2024-24549
    implementation("org.apache.tomcat.embed:tomcat-embed-websocket:9.0.105")
    implementation("org.apache.tomcat.embed:tomcat-embed-core:9.0.105")
    implementation("org.apache.tomcat.embed:tomcat-embed-el:9.0.105")

    implementation("org.springframework.boot:spring-boot-starter-jdbc:2.7.18")
    implementation("org.springframework.boot:spring-boot-starter-aop:2.7.18")
    implementation("org.springframework.boot:spring-boot-starter-security:2.7.18")
    implementation("org.springframework.boot:spring-boot-starter-actuator:2.7.18")
    implementation("org.springframework.ldap:spring-ldap-core")

    // ===========================================
    // 其他依赖
    // ===========================================
    implementation("com.caucho:hessian:4.0.66")
    implementation("commons-collections:commons-collections:3.2.2")
    implementation("ch.qos.logback:logback-classic")
    implementation("ch.qos.logback:logback-core")
    implementation("com.mysql:mysql-connector-j:9.1.0")
    implementation("org.apache.derby:derby:10.17.1.0")
    implementation("com.alipay.sofa:jraft-core:1.3.14")
    implementation("com.alipay.sofa:sofa-rpc-all:5.12.0")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("com.google.code.gson:gson")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-registry-influx")
    implementation("io.micrometer:micrometer-registry-elastic")
    implementation("io.envoyproxy.controlplane:api:0.1.27")
    implementation("org.slf4j:jcl-over-slf4j")
    implementation("org.slf4j:jul-to-slf4j")
    implementation("org.yaml:snakeyaml:$snakeYamlVersion")
    implementation("de.codecentric:spring-boot-admin-client:$springBootAdminVersion")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("${project.name}.jar")
}

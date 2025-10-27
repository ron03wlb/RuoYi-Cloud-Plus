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
    // Spring核心依赖
    api("org.springframework:spring-context")
    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")

    // Apache Commons
    api("org.apache.commons:commons-lang3")

    // MyBatis Plus
    api(libs.mybatis.plus.annotation)

    // Dubbo
    implementation(libs.dubbo)

    // RuoYi核心
    implementation(project(":ruoyi-common:ruoyi-common-core"))
    implementation(project(":ruoyi-common:ruoyi-common-nacos"))

    // Apache Velocity (代码模板引擎)
    implementation(libs.velocity.engine.core)

    // RuoYi功能模块（保留必需的）
    implementation(project(":ruoyi-common:ruoyi-common-log"))
    implementation(project(":ruoyi-common:ruoyi-common-idempotent"))
    implementation(project(":ruoyi-common:ruoyi-common-doc"))
    implementation(project(":ruoyi-common:ruoyi-common-web"))
    implementation(project(":ruoyi-common:ruoyi-common-mybatis"))
    implementation(project(":ruoyi-common:ruoyi-common-security"))

    // Anyline (面向运行时的D-ORM依赖,支持100+种类型数据库)
    implementation(libs.anyline.environment.spring.data.jdbc)
    implementation(libs.anyline.data.jdbc.mysql)

    // anyline支持100+种类型数据库 添加对应的jdbc依赖与anyline对应数据库依赖包即可
    // compileOnly("org.anyline:anyline-data-jdbc-oracle:${anyline.version}")
    // compileOnly("org.anyline:anyline-data-jdbc-postgresql:${anyline.version}")
    // compileOnly("org.anyline:anyline-data-jdbc-mssql:${anyline.version}")
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

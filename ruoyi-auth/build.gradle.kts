/*
 * ===========================================
 * ruoyi-auth
 * 认证授权中心
 * ===========================================
 */

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
    jacoco
}

description = "ruoyi-auth 认证授权中心"

dependencies {
    // Spring 核心
    api("org.springframework:spring-context")
    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")

    // 日志
    api("org.slf4j:slf4j-api")

    // RuoYi核心模块
    implementation(project(":ruoyi-common:ruoyi-common-core"))
    implementation(project(":ruoyi-common:ruoyi-common-json"))
    implementation(project(":ruoyi-common:ruoyi-common-satoken"))
    implementation(project(":ruoyi-common:ruoyi-common-nacos"))

    // API模块
    implementation(project(":ruoyi-api:ruoyi-api-system"))
    implementation(project(":ruoyi-api:ruoyi-api-resource"))

    // Sa-Token
    implementation(libs.sa.token.core)

    // JustAuth (第三方登录)
    implementation(libs.justauth)

    // Hutool验证码
    implementation(libs.hutool.captcha)

    // RuoYi Common 功能模块
    implementation(project(":ruoyi-common:ruoyi-common-security"))
    implementation(project(":ruoyi-common:ruoyi-common-social"))
    implementation(project(":ruoyi-common:ruoyi-common-log"))
    implementation(project(":ruoyi-common:ruoyi-common-doc"))
    implementation(project(":ruoyi-common:ruoyi-common-web"))
    implementation(project(":ruoyi-common:ruoyi-common-ratelimiter"))
    implementation(project(":ruoyi-common:ruoyi-common-encrypt"))
    implementation(project(":ruoyi-common:ruoyi-common-tenant"))

    // Dubbo (可选,用于RPC调用)
    implementation(libs.dubbo)

    // ===========================================
    // 测试依赖
    // ===========================================
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Mockito 模拟框架
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.mockito:mockito-inline:5.2.0")

    // AssertJ 流式断言
    testImplementation("org.assertj:assertj-core")

    // JUnit 5 参数化测试
    testImplementation("org.junit.jupiter:junit-jupiter-params")

    // MockWebServer for HTTP mocking
    testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")

    // Embedded Redis for integration tests
    testImplementation("com.github.codemonstur:embedded-redis:1.4.3")

    // Testcontainers for integration tests
    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
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
        ports = listOf("9210")
        environment = mapOf("SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

// ===========================================
// 测试任务配置
// ===========================================
tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

// ===========================================
// JaCoCo 覆盖率配置
// ===========================================
jacoco {
    toolVersion = "0.8.11"
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/RuoYiAuthApplication.class",  // 排除启动类
                    "**/domain/**",                    // 排除 POJO
                    "**/form/**",                      // 排除表单类
                    "**/enums/**",                     // 排除枚举类
                    "**/config/**",                    // 排除配置类
                    "**/properties/**",                // 排除属性类
                    "**/listener/**"                   // 排除监听器
                )
            }
        })
    )
}

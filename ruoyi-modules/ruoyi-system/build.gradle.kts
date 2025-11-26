/*
 * ===========================================
 * ruoyi-system
 * 系统模块
 * ===========================================
 */

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
    jacoco
}

description = "ruoyi-system系统模块"

dependencies {
    // ===========================================
    // Spring 核心依赖
    // ===========================================
    api("org.springframework:spring-context")
    api("org.springframework:spring-web")
    api("org.springframework:spring-beans")
    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")

    // ===========================================
    // Jakarta EE API
    // ===========================================
    api("jakarta.servlet:jakarta.servlet-api")
    api("jakarta.validation:jakarta.validation-api")

    // ===========================================
    // 工具库
    // ===========================================
    api(libs.hutool.core)

    // ===========================================
    // RuoYi 核心模块
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-core"))
    implementation(project(":ruoyi-common:ruoyi-common-excel"))
    implementation(project(":ruoyi-common:ruoyi-common-satoken"))
    implementation(project(":ruoyi-common:ruoyi-common-nacos"))

    // ===========================================
    // RuoYi 功能模块
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-log"))
    implementation(project(":ruoyi-common:ruoyi-common-doc"))
    implementation(project(":ruoyi-common:ruoyi-common-web"))
    implementation(project(":ruoyi-common:ruoyi-common-mybatis"))
    implementation(project(":ruoyi-common:ruoyi-common-idempotent"))
    implementation(project(":ruoyi-common:ruoyi-common-tenant"))
    implementation(project(":ruoyi-common:ruoyi-common-security"))
    implementation(project(":ruoyi-common:ruoyi-common-translation"))
    implementation(project(":ruoyi-common:ruoyi-common-sensitive"))
    implementation(project(":ruoyi-common:ruoyi-common-encrypt"))

    // ===========================================
    // RuoYi API 模块
    // ===========================================
    implementation(project(":ruoyi-api:ruoyi-api-system"))
    implementation(project(":ruoyi-api:ruoyi-api-resource"))
    implementation(project(":ruoyi-api:ruoyi-api-workflow"))

    // ===========================================
    // Sa-Token 权限认证
    // ===========================================
    implementation(libs.sa.token.core)

    // ===========================================
    // Dubbo
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-dubbo"))

    // ===========================================
    // 可选依赖
    // ===========================================
    // 分布式事务（某些业务需要）
    compileOnly(project(":ruoyi-common:ruoyi-common-seata"))

    // ===========================================
    // Netty 原生库（macOS DNS 解析）
    // ===========================================
    // 注意：这些依赖已在根 build.gradle.kts 中配置
    // 但由于依赖解析问题，在模块级别明确添加以确保正确加载
    // runtimeOnly("io.netty:netty-transport-native-kqueue:${libs.versions.netty.get()}:osx-aarch_64")
    // runtimeOnly("io.netty:netty-transport-native-kqueue:${libs.versions.netty.get()}:osx-x86_64")
    // runtimeOnly("io.netty:netty-resolver-dns-native-macos:${libs.versions.netty.get()}:osx-aarch_64")
    // runtimeOnly("io.netty:netty-resolver-dns-native-macos:${libs.versions.netty.get()}:osx-x86_64")

    // ===========================================
    // 测试依赖
    // ===========================================
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.mockito.inline)
    testImplementation("org.assertj:assertj-core")
    testImplementation("com.h2database:h2")
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
        ports = listOf("9201")
        environment = mapOf("SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

// ====================
// JaCoCo 配置
// ====================
jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        // 排除配置类
                        "**/config/**",
                        "**/configuration/**",
                        // 排除启动类
                        "**/*Application.class",
                        // 排除 DTO/VO/BO
                        "**/domain/**",
                        "**/bo/**",
                        "**/vo/**",
                        "**/dto/**",
                        // 排除 Mapper XML
                        "**/mapper/**/*Mapper.class",
                        // 排除 Controller (后续可以用 MockMvc 测试)
                        "**/controller/**",
                        // 排除 Dubbo 实现 (需要 Dubbo 上下文)
                        "**/dubbo/**",
                        // 排除监听器
                        "**/listener/**",
                        // 排除转换器
                        "**/convert/**",
                    )
                }
            },
        ),
    )
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
    useJUnitPlatform()
}

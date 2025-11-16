/*
 * ===========================================
 * ruoyi-gateway
 * API 网关服务
 * ===========================================
 *
 * 说明：
 * 1. 这是一个 Spring Boot 应用模块
 * 2. 使用 Spring Cloud Gateway 实现 API 网关
 * 3. 使用 Jib 插件构建 Docker 镜像
 * 4. 响应式编程模型（WebFlux）
 */

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jib)
}

description = "ruoyi-gateway 网关模块"

dependencies {
    // ===========================================
    // Spring 核心依赖
    // ===========================================
    implementation("org.springframework.boot:spring-boot-starter")

    // ===========================================
    // Spring Cloud Gateway
    // ===========================================
    implementation("org.springframework.cloud:spring-cloud-gateway-server")

    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")

    implementation("io.projectreactor:reactor-core")

    implementation("com.github.ben-manes.caffeine:caffeine")

    // 注意：Gateway 使用 WebFlux 而不是 Web MVC
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")

    // 负载均衡
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")

    // Caffeine 缓存（LoadBalancer 需要）
    implementation("com.github.ben-manes.caffeine:caffeine")

    // ===========================================
    // Nacos 服务发现 + 配置中心
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-nacos"))

    // ===========================================
    // Spring Boot Actuator（健康检查、监控）
    // ===========================================
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // ===========================================
    // Hutool（工具库）
    // ===========================================
    implementation(libs.hutool.http)

    // ===========================================
    // Sa-Token 权限认证（Reactor 响应式集成）
    // ===========================================
    implementation(libs.sa.token.core)
    implementation(libs.sa.token.reactor.spring.boot3.starter)
    implementation(project(":ruoyi-common:ruoyi-common-satoken"))

    // ===========================================
    // Redis 缓存
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-redis"))

    // ===========================================
    // 多租户支持
    // ===========================================
    implementation(project(":ruoyi-common:ruoyi-common-tenant"))

    // ===========================================
    // 可选：其他公共模块
    // ===========================================

    // 自定义负载均衡（多团队开发使用，默认注释）
    // implementation(project(":ruoyi-common:ruoyi-common-loadbalancer"))

    // ELK 日志收集（默认注释）
    // implementation(project(":ruoyi-common:ruoyi-common-logstash"))

    // Skywalking 日志收集（默认注释）
    // implementation(project(":ruoyi-common:ruoyi-common-skylog"))

    // Prometheus 监控（默认注释）
    // implementation(project(":ruoyi-common:ruoyi-common-prometheus"))
}

// ===========================================
// Spring Boot 配置
// ===========================================
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    // 添加 JVM 参数禁用 Nacos 默认日志配置
    jvmArgs = listOf(
        "-Dnacos.logging.default.config.enabled=false"
    )
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    // 设置生成的 JAR 文件名
    archiveFileName.set("${project.name}.jar")

    // 启用分层 JAR（优化 Docker 镜像层）
    layered {
        enabled.set(true)
    }
}

// 禁用普通 JAR 任务（只生成 Boot JAR）
tasks.named<Jar>("jar") {
    enabled = false
}

// ===========================================
// Jib Docker 镜像配置
// ===========================================
jib {
    // 基础镜像
    from {
        image = "eclipse-temurin:17-jre-alpine"
        // 使用阿里云镜像加速（可选）
        // image = "registry.cn-hangzhou.aliyuncs.com/zhengqing/openjdk:17-jdk-alpine"

        platforms {
            platform {
                architecture = "amd64"
                os = "linux"
            }
            // 如果需要 ARM 架构（如 Apple Silicon），可添加：
            // platform {
            //     architecture = "arm64"
            //     os = "linux"
            // }
        }
    }

    // 目标镜像
    to {
        // 镜像名称和标签
        image = "ruoyi-cloud-plus/${project.name}"
        tags = setOf(version.toString(), "latest")

        // 如果推送到远程仓库，配置认证信息
        // auth {
        //     username = project.findProperty("dockerUsername")?.toString()
        //     password = project.findProperty("dockerPassword")?.toString()
        // }
    }

    // 容器配置
    container {
        // JVM 参数
        jvmFlags = listOf(
            "-Xms512m",
            "-Xmx512m",
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=200",
            "-Djava.security.egd=file:/dev/./urandom",
            "-Dfile.encoding=UTF-8",
            "-Duser.timezone=Asia/Shanghai",
            "-Dnacos.logging.default.config.enabled=false"
        )

        // 暴露端口
        ports = listOf("8080")

        // 环境变量
        environment = mapOf(
            "SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS",
            "TZ" to "Asia/Shanghai"
        )

        // 容器启动用户
        user = "1000:1000"

        // 工作目录
        workingDirectory = "/app"

        // 容器创建时间（使用构建时间）
        creationTime.set("USE_CURRENT_TIMESTAMP")

        // 标签
        labels.set(
            mapOf(
                "maintainer" to "RuoYi-Cloud-Plus",
                "version" to version.toString(),
                "description" to "RuoYi Gateway Service"
            )
        )
    }

    // 额外的目录（如配置文件、静态资源等）
    // extraDirectories {
    //     paths {
    //         path {
    //             setFrom("src/main/jib")
    //         }
    //     }
    //     permissions.set(
    //         mapOf(
    //             "/app/config" to "755"
    //         )
    //     )
    // }
}

// ===========================================
// 自定义任务
// ===========================================

// 构建并导出 Docker 镜像到本地 Docker Daemon
tasks.register("buildDockerImage") {
    group = "docker"
    description = "Build and load Docker image to local Docker daemon"
    dependsOn("jibDockerBuild")
}

// 构建并推送 Docker 镜像到远程仓库
tasks.register("pushDockerImage") {
    group = "docker"
    description = "Build and push Docker image to remote registry"
    dependsOn("jib")
}

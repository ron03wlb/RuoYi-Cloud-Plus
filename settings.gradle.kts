/*
 * ===========================================
 * RuoYi-Cloud-Plus Gradle Settings
 * ===========================================
 */

rootProject.name = "ruoyi-cloud-plus"

// 启用 Feature Preview（可选，用于启用 Gradle 新特性）
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// ===========================================
// 依赖管理设置
// ===========================================

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        // 华为云 Maven 镜像（中国大陆用户推荐）
        maven {
            name = "HuaweiCloud"
            url = uri("https://mirrors.huaweicloud.com/repository/maven/")
        }

        // 阿里云 Maven 镜像（备用）
        maven {
            name = "AliYun"
            url = uri("https://maven.aliyun.com/repository/public/")
        }

        // Maven 中央仓库
        mavenCentral()

        // Spring 官方仓库
        maven {
            name = "Spring Milestones"
            url = uri("https://repo.spring.io/milestone")
        }

        maven {
            name = "Spring Snapshots"
            url = uri("https://repo.spring.io/snapshot")
        }
    }

    // 注意：gradle/libs.versions.toml 会被 Gradle 自动检测并创建名为 "libs" 的 Version Catalog
    // 无需显式配置 versionCatalogs { create("libs") { ... } }
}

// ===========================================
// 插件管理
// ===========================================

pluginManagement {
    repositories {
        maven {
            name = "HuaweiCloud"
            url = uri("https://mirrors.huaweicloud.com/repository/maven/")
        }
        maven {
            name = "AliYun"
            url = uri("https://maven.aliyun.com/repository/public/")
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

// ===========================================
// 模块包含（Multi-Module Project）
// ===========================================

// ruoyi-common 公共模块（26个子模块）
include(":ruoyi-common")
include(":ruoyi-common:ruoyi-common-nacos")
include(":ruoyi-common:ruoyi-common-core")
include(":ruoyi-common:ruoyi-common-json")
include(":ruoyi-common:ruoyi-common-excel")
include(":ruoyi-common:ruoyi-common-redis")
include(":ruoyi-common:ruoyi-common-satoken")
include(":ruoyi-common:ruoyi-common-security")
include(":ruoyi-common:ruoyi-common-social")
include(":ruoyi-common:ruoyi-common-log")
include(":ruoyi-common:ruoyi-common-doc")
include(":ruoyi-common:ruoyi-common-web")
include(":ruoyi-common:ruoyi-common-ratelimiter")
include(":ruoyi-common:ruoyi-common-encrypt")
include(":ruoyi-common:ruoyi-common-dubbo")
include(":ruoyi-common:ruoyi-common-seata")
include(":ruoyi-common:ruoyi-common-mybatis")
include(":ruoyi-common:ruoyi-common-tenant")
include(":ruoyi-common:ruoyi-common-service-impl")
include(":ruoyi-common:ruoyi-common-idempotent")
include(":ruoyi-common:ruoyi-common-translation")
include(":ruoyi-common:ruoyi-common-sensitive")
include(":ruoyi-common:ruoyi-common-bus")
include(":ruoyi-common:ruoyi-common-job")
include(":ruoyi-common:ruoyi-common-oss")
include(":ruoyi-common:ruoyi-common-mail")
include(":ruoyi-common:ruoyi-common-sms")
include(":ruoyi-common:ruoyi-common-websocket")
include(":ruoyi-common:ruoyi-common-sse")
include(":ruoyi-common:ruoyi-common-loadbalancer")
include(":ruoyi-common:ruoyi-common-logstash")
include(":ruoyi-common:ruoyi-common-elasticsearch")
include(":ruoyi-common:ruoyi-common-skylog")
include(":ruoyi-common:ruoyi-common-prometheus")
include(":ruoyi-common:ruoyi-common-test")

// ruoyi-common BOM 模块（依赖管理）
include(":ruoyi-common:ruoyi-common-bom")
include(":ruoyi-common:ruoyi-common-alibaba-bom")

// ruoyi-api Dubbo API 接口模块
include(":ruoyi-api")
include(":ruoyi-api:ruoyi-api-system")
include(":ruoyi-api:ruoyi-api-resource")
include(":ruoyi-api:ruoyi-api-workflow")
include(":ruoyi-api:ruoyi-api-bom")

// 网关模块
include(":ruoyi-gateway")

// 认证模块
include(":ruoyi-auth")

// ruoyi-modules 业务服务模块
include(":ruoyi-modules")
include(":ruoyi-modules:ruoyi-system")
include(":ruoyi-modules:ruoyi-gen")
include(":ruoyi-modules:ruoyi-job")
include(":ruoyi-modules:ruoyi-resource")
include(":ruoyi-modules:ruoyi-workflow")

// ruoyi-visual 可视化监控模块（可选）
include(":ruoyi-visual")
include(":ruoyi-visual:ruoyi-monitor")
// 注意：ruoyi-seata-server 和 ruoyi-nacos 使用 Spring Boot 2.7.18，与主项目不兼容
// 已采用混合方案：使用官方 Nacos Docker 镜像（MySQL）+ PostgreSQL（业务数据）
// include(":ruoyi-visual:ruoyi-seata-server")
// include(":ruoyi-visual:ruoyi-nacos")
include(":ruoyi-visual:ruoyi-snailjob-server")

// ruoyi-example 示例模块（可选）
include(":ruoyi-example")
include(":ruoyi-example:ruoyi-demo")
include(":ruoyi-example:ruoyi-test-mq")

// ===========================================
// 项目描述（可选）
// ===========================================

rootProject.children.forEach { project ->
    project.buildFileName = "build.gradle.kts"
}

println("🚀 RuoYi-Cloud-Plus Gradle 项目初始化完成")
println("📦 共包含 ${rootProject.children.size} 个子模块")

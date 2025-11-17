/*
 * ===========================================
 * RuoYi-Nacos Standalone Build Settings
 * ===========================================
 */

rootProject.name = "ruoyi-nacos"

// 插件管理
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

// 依赖管理
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        maven {
            name = "HuaweiCloud"
            url = uri("https://mirrors.huaweicloud.com/repository/maven/")
        }
        maven {
            name = "AliYun"
            url = uri("https://maven.aliyun.com/repository/public/")
        }
        mavenCentral()
    }
}

println("🚀 RuoYi-Nacos Standalone Build Initialized")

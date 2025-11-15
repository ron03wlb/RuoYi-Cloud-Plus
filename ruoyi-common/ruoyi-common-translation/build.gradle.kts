/*
 * ===========================================
 * ruoyi-common-translation
 * 通用翻译功能
 * ===========================================
 */

plugins {
    id("jacoco")
}

description = "ruoyi-common-translation 通用翻译功能"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================

    // JSON 模块
    api(project(":ruoyi-common:ruoyi-common-json"))

    // Service 实现模块
    api(project(":ruoyi-common:ruoyi-common-service-impl"))

    // Dubbo 模块
    api(project(":ruoyi-common:ruoyi-common-dubbo"))

    // API Resource 模块
    api(project(":ruoyi-api:ruoyi-api-resource"))

    // ===========================================
    // 测试依赖
    // ===========================================
    // JUnit
    testImplementation("org.junit.jupiter:junit-jupiter")

    // AssertJ
    testImplementation("org.assertj:assertj-core")

    // Mockito
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
}

// ===========================================
// JaCoCo 配置
// ===========================================
jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    // 排除不需要覆盖的类
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/annotation/**",                    // 注解（元数据）
                    "**/core/TranslationInterface.class",  // 接口
                    "**/core/handler/**",                  // Handler（依赖Jackson）
                    "**/core/impl/**",                     // 实现类（依赖Spring服务）
                    "**/config/**",                        // 配置类
                    "**/*Application.class"                // 主程序
                )
            }
        })
    )
}

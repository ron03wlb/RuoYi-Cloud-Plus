/*
 * ===========================================
 * ruoyi-common-json
 * 序列化模块
 * ===========================================
 */

plugins {
    id("jacoco")
}

description = "ruoyi-common-json 序列化模块"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-core"))

    // ===========================================
    // Jackson JSON 处理
    // ===========================================

    // Jackson 核心
    api("com.fasterxml.jackson.core:jackson-databind")

    // Java 8 日期时间支持
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // ===========================================
    // 测试依赖
    // ===========================================
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation(libs.mockito.inline) // 支持静态方法mock
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
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/config/**", // 配置类（Spring配置）
                        "**/*Application.class", // 主程序
                    )
                }
            },
        ),
    )
}

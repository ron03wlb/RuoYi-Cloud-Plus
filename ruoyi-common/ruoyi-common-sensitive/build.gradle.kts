/*
 * ===========================================
 * ruoyi-common-sensitive
 * 脱敏模块
 * ===========================================
 */

plugins {
    id("jacoco")
}

description = "ruoyi-common-sensitive 脱敏模块"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-json"))

    // ===========================================
    // 测试依赖
    // ===========================================
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
}

// ===========================================
// JaCoCo 配置
// ===========================================
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

    // 排除不需要覆盖的类
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/annotation/**",           // 注解（元数据）
                    "**/core/SensitiveService.class", // 接口
                    "**/handler/**",              // Handler（依赖Spring + Jackson）
                    "**/*Application.class"       // 主程序
                )
            }
        })
    )
}

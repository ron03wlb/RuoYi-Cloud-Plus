/*
 * ===========================================
 * ruoyi-common-excel
 * Excel 导入导出模块
 * ===========================================
 */

plugins {
    id("jacoco")
}

description = "ruoyi-common-excel"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-json"))

    // ===========================================
    // FastExcel（Excel 处理库）
    // ===========================================
    api(libs.fastexcel)

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
                    "**/annotation/**",       // 注解（元数据）
                    "**/core/ExcelListener.class",    // 接口
                    "**/core/ExcelResult.class",      // 接口
                    "**/handler/**",          // Handler（重度依赖POI/FastExcel）
                    "**/*Application.class"   // 主程序
                )
            }
        })
    )
}

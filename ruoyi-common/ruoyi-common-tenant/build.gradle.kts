/*
 * ===========================================
 * ruoyi-common-tenant
 * 租户模块
 * ===========================================
 */

plugins {
    id("jacoco")
}

description = "ruoyi-common-tenant 租户模块"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================

    // MyBatis 模块（可选）
    compileOnly(project(":ruoyi-common:ruoyi-common-mybatis"))

    // Redis 模块
    api(project(":ruoyi-common:ruoyi-common-redis"))

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

    // 测试时需要访问BaseEntity
    testImplementation(project(":ruoyi-common:ruoyi-common-mybatis"))
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
                    "**/config/**",           // 配置类（Spring配置）
                    "**/*Application.class"   // 主程序
                )
            }
        })
    )
}

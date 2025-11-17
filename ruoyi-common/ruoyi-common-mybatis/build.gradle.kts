/*
 * ===========================================
 * ruoyi-common-mybatis
 * 数据库服务
 * ===========================================
 */

plugins {
    id("jacoco")
}

description = "ruoyi-common-mybatis 数据库服务"

dependencies {
    // ===========================================
    // Spring 依赖
    // ===========================================
    api("org.springframework:spring-context")
    api("org.springframework:spring-aop")

    // ===========================================
    // 工具库
    // ===========================================
    api(libs.hutool.core)

    // ===========================================
    // 项目内依赖
    // ===========================================

    // Sa-Token 模块
    api(project(":ruoyi-common:ruoyi-common-satoken"))

    // Dubbo 模块（可选）
    compileOnly(project(":ruoyi-common:ruoyi-common-dubbo"))

    // ===========================================
    // MyBatis Plus
    // ===========================================
    api(libs.mybatis.plus.spring.boot3.starter)
    api(libs.mybatis.plus.jsqlparser)

    // ===========================================
    // SQL 性能分析插件
    // ===========================================
    api(libs.p6spy)

    // ===========================================
    // 动态数据源
    // ===========================================
    api(libs.dynamic.datasource)

    // ===========================================
    // PostgreSQL 数据库驱动
    // ===========================================
    api("org.postgresql:postgresql")
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
                    "**/annotation/**",      // 注解类
                    "**/enums/**",            // 枚举类（已有测试覆盖）
                    "**/config/**",           // 配置类
                    "**/domain/**",           // POJO
                    "**/model/**",
                    "**/*Application.class"   // 主程序
                )
            }
        })
    )
}

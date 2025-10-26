/*
 * ===========================================
 * ruoyi-common-log
 * 日志记录
 * ===========================================
 */

description = "ruoyi-common-log 日志记录"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================

    // Sa-Token 模块
    api(project(":ruoyi-common:ruoyi-common-satoken"))

    // JSON 模块
    api(project(":ruoyi-common:ruoyi-common-json"))

    // ===========================================
    // Dubbo（可选）
    // ===========================================
    compileOnly(libs.dubbo.spring.boot.starter)
}

/*
 * ===========================================
 * ruoyi-common-security
 * 安全模块
 * ===========================================
 */

description = "ruoyi-common-security 安全模块"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-satoken"))

    // ===========================================
    // Sa-Token Spring Boot Starter
    // ===========================================
    api(libs.sa.token.spring.boot3.starter)
}

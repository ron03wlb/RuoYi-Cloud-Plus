/*
 * ===========================================
 * ruoyi-common-doc
 * 系统接口文档
 * ===========================================
 */

description = "ruoyi-common-doc 系统接口"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-core"))

    // ===========================================
    // SpringDoc OpenAPI
    // ===========================================
    api(libs.springdoc.openapi.starter.webmvc.api)

    // ===========================================
    // Therapi JavaDoc Runtime
    // ===========================================
    api(libs.therapi.runtime.javadoc)

    // ===========================================
    // Jackson Kotlin Module
    // ===========================================
    api(libs.jackson.module.kotlin)
}

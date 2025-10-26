/*
 * ===========================================
 * ruoyi-common-translation
 * 通用翻译功能
 * ===========================================
 */

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
}

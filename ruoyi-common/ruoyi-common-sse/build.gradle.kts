/*
 * ===========================================
 * ruoyi-common-sse
 * SSE (Server-Sent Events) 模块
 * ===========================================
 */

description = "ruoyi-common-sse 模块"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-core"))
    api(project(":ruoyi-common:ruoyi-common-redis"))
    api(project(":ruoyi-common:ruoyi-common-satoken"))
    api(project(":ruoyi-common:ruoyi-common-json"))

    // ===========================================
    // Spring WebMVC
    // ===========================================
    api(libs.spring.webmvc)
}

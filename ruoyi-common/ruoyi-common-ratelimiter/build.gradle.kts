/*
 * ===========================================
 * ruoyi-common-ratelimiter
 * 限流功能
 * ===========================================
 */

description = "ruoyi-common-ratelimiter 限流功能"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-core"))
    api(project(":ruoyi-common:ruoyi-common-redis"))
}

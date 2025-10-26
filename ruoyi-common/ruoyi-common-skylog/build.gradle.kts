/*
 * ===========================================
 * ruoyi-common-skylog
 * Skywalking日志收集模块
 * ===========================================
 */

description = "ruoyi-common-skylog skywalking日志收集模块"

dependencies {
    // ===========================================
    // Skywalking 整合 Logback
    // ===========================================
    api(libs.skywalking.toolkit.logback)
    api(libs.skywalking.toolkit.trace)
}

/*
 * ===========================================
 * ruoyi-common-logstash
 * Logstash日志推送模块
 * ===========================================
 */

description = "ruoyi-common-logstash logstash日志推送模块"

dependencies {
    // ===========================================
    // Logstash Logback Encoder
    // ===========================================
    api(libs.logstash.logback.encoder)
}

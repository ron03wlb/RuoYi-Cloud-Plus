/*
 * ===========================================
 * ruoyi-common-json
 * 序列化模块
 * ===========================================
 */

description = "ruoyi-common-json 序列化模块"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-core"))

    // ===========================================
    // Jackson JSON 处理
    // ===========================================

    // Jackson 核心
    api(libs.jackson.databind)

    // Java 8 日期时间支持
    api(libs.jackson.datatype.jsr310)
}

/*
 * ===========================================
 * ruoyi-common-social
 * 授权认证
 * ===========================================
 */

description = "ruoyi-common-social 授权认证"

dependencies {
    // ===========================================
    // JustAuth（第三方登录）
    // ===========================================
    api(libs.justauth)

    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-json"))
    api(project(":ruoyi-common:ruoyi-common-redis"))
}

/*
 * ===========================================
 * ruoyi-common-tenant
 * 租户模块
 * ===========================================
 */

description = "ruoyi-common-tenant 租户模块"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================

    // MyBatis 模块（可选）
    compileOnly(project(":ruoyi-common:ruoyi-common-mybatis"))

    // Redis 模块
    api(project(":ruoyi-common:ruoyi-common-redis"))
}

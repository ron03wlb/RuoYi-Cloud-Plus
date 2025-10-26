/*
 * ===========================================
 * ruoyi-common-service-impl
 * 通用service实现模块(用于处理core模块中的通用业务service类)
 * ===========================================
 */

description = "ruoyi-common-service-impl 通用service实现模块(用于处理core模块中的通用业务service类)"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================

    // Redis 模块
    api(project(":ruoyi-common:ruoyi-common-redis"))

    // API 模块
    api(project(":ruoyi-api:ruoyi-api-system"))

    // ===========================================
    // Dubbo（可选）
    // ===========================================
    compileOnly(libs.dubbo.spring.boot.starter)
}

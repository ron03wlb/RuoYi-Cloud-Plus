/*
 * ===========================================
 * ruoyi-api-bom
 * API依赖项 BOM（Bill of Materials）
 * ===========================================
 *
 * 说明：
 * 1. 这是一个 BOM 模块，使用 java-platform 插件
 * 2. 管理 ruoyi-api 所有接口模块的版本
 * 3. 其他模块通过导入此 BOM 来统一版本管理
 */

description = "ruoyi-api-bom api依赖项"

plugins {
    `java-platform`
}

// ===========================================
// 依赖管理
// ===========================================
dependencies {
    constraints {
        // 系统接口
        api(project(":ruoyi-api:ruoyi-api-system"))

        // 资源服务接口
        api(project(":ruoyi-api:ruoyi-api-resource"))

        // workflow接口
        api(project(":ruoyi-api:ruoyi-api-workflow"))
    }
}

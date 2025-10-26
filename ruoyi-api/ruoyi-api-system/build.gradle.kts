/*
 * ===========================================
 * ruoyi-api-system
 * 系统接口模块
 * ===========================================
 */

description = "ruoyi-api-system系统接口模块"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================

    // 核心模块
    api(project(":ruoyi-common:ruoyi-common-core"))

    // Excel 模块
    api(project(":ruoyi-common:ruoyi-common-excel"))
}

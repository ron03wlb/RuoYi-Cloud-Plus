/*
 * ===========================================
 * ruoyi-common-mybatis
 * 数据库服务
 * ===========================================
 */

description = "ruoyi-common-mybatis 数据库服务"

dependencies {
    // ===========================================
    // Spring 依赖
    // ===========================================
    api("org.springframework:spring-context")
    api("org.springframework:spring-aop")

    // ===========================================
    // 工具库
    // ===========================================
    api(libs.hutool.core)

    // ===========================================
    // 项目内依赖
    // ===========================================

    // Sa-Token 模块
    api(project(":ruoyi-common:ruoyi-common-satoken"))

    // Dubbo 模块（可选）
    compileOnly(project(":ruoyi-common:ruoyi-common-dubbo"))

    // ===========================================
    // MyBatis Plus
    // ===========================================
    api(libs.mybatis.plus.spring.boot3.starter)
    api(libs.mybatis.plus.jsqlparser)

    // ===========================================
    // SQL 性能分析插件
    // ===========================================
    api(libs.p6spy)

    // ===========================================
    // 动态数据源
    // ===========================================
    api(libs.dynamic.datasource)

    // ===========================================
    // MySQL 数据库驱动
    // ===========================================
    api("com.mysql:mysql-connector-j")

    // 可选的其他数据库驱动（注释掉）
    // compileOnly("com.oracle.database.jdbc:ojdbc11")
    // compileOnly("org.postgresql:postgresql")
    // compileOnly("com.microsoft.sqlserver:mssql-jdbc")
}

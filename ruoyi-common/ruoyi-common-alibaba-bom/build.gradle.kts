/*
 * ===========================================
 * ruoyi-common-alibaba-bom
 * Alibaba 依赖项 BOM（Bill of Materials）
 * ===========================================
 *
 * 说明：
 * 1. 这是一个 BOM 模块，使用 java-platform 插件
 * 2. 管理 Spring Cloud Alibaba、Nacos、Seata、Dubbo 相关依赖的版本
 * 3. 其他模块通过导入此 BOM 来统一版本管理
 */

description = "ruoyi-common-alibaba-bom alibaba依赖项"

plugins {
    `java-platform`
}

// ===========================================
// 平台配置
// ===========================================
// 允许 BOM 模块导入其他 BOM 依赖
javaPlatform {
    allowDependencies()
}

// ===========================================
// 依赖管理
// ===========================================
dependencies {
    // ===========================================
    // 导入 Spring Cloud Alibaba BOM
    // ===========================================
    // 导入 Spring Cloud Alibaba Dependencies BOM
    api(platform(libs.spring.cloud.alibaba.dependencies))

    // ===========================================
    // 自定义版本约束
    // ===========================================
    constraints {
        // Nacos 客户端
        // 注意：排除配置应该在实际使用此依赖的模块中配置
        api(libs.nacos.client)

        // Seata 分布式事务
        api(libs.seata.spring.boot.starter)
        api(libs.seata.all)

        // Apache Dubbo
        api(libs.dubbo.spring.boot.starter)
        api(libs.dubbo.spring.boot.actuator)
        api(libs.dubbo)

        // Dubbo Extensions
        api(libs.dubbo.metadata.report.redis)
        api(libs.dubbo.filter.seata)
    }
}

// ===========================================
// 发布配置（可选）
// ===========================================
// 如需发布到 Maven 仓库，取消下面注释并应用 maven-publish 插件
/*
plugins {
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["javaPlatform"])

            pom {
                name.set("RuoYi Common Alibaba BOM")
                description.set("RuoYi-Cloud-Plus Alibaba 依赖项 BOM")
            }
        }
    }
}
*/

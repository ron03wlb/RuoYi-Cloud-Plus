/*
 * ===========================================
 * ruoyi-common-bom
 * 公共模块依赖管理（BOM - Bill of Materials）
 * ===========================================
 *
 * 说明：
 * 1. 使用 java-platform 插件创建 BOM 模块
 * 2. 定义所有 ruoyi-common-* 模块的版本约束
 * 3. 其他模块通过 platform() 引用此 BOM
 */

plugins {
    `java-platform`
}

description = "ruoyi-common-bom common依赖项"

// ===========================================
// 依赖约束（Dependencies Constraints）
// ===========================================
dependencies {
    constraints {
        // 核心模块
        api(project(":ruoyi-common:ruoyi-common-core"))

        // 接口文档模块
        api(project(":ruoyi-common:ruoyi-common-doc"))

        // 安全模块
        api(project(":ruoyi-common:ruoyi-common-security"))

        // 权限认证服务
        api(project(":ruoyi-common:ruoyi-common-satoken"))

        // 日志记录
        api(project(":ruoyi-common:ruoyi-common-log"))

        // 通用 service 实现模块
        api(project(":ruoyi-common:ruoyi-common-service-impl"))

        // Excel
        api(project(":ruoyi-common:ruoyi-common-excel"))

        // 缓存服务
        api(project(":ruoyi-common:ruoyi-common-redis"))

        // Web 服务
        api(project(":ruoyi-common:ruoyi-common-web"))

        // 数据库服务
        api(project(":ruoyi-common:ruoyi-common-mybatis"))

        // 定时任务
        api(project(":ruoyi-common:ruoyi-common-job"))

        // RPC 服务
        api(project(":ruoyi-common:ruoyi-common-dubbo"))

        // 分布式事务
        api(project(":ruoyi-common:ruoyi-common-seata"))

        // 自定义负载均衡
        api(project(":ruoyi-common:ruoyi-common-loadbalancer"))

        // OSS 服务
        api(project(":ruoyi-common:ruoyi-common-oss"))

        // 限流功能
        api(project(":ruoyi-common:ruoyi-common-ratelimiter"))

        // 幂等功能
        api(project(":ruoyi-common:ruoyi-common-idempotent"))

        // 邮件模块
        api(project(":ruoyi-common:ruoyi-common-mail"))

        // 短信模块
        api(project(":ruoyi-common:ruoyi-common-sms"))

        // Logstash 日志推送模块
        api(project(":ruoyi-common:ruoyi-common-logstash"))

        // ES 搜索引擎服务
        api(project(":ruoyi-common:ruoyi-common-elasticsearch"))

        // Skywalking 日志收集模块
        api(project(":ruoyi-common:ruoyi-common-skylog"))

        // Prometheus 监控
        api(project(":ruoyi-common:ruoyi-common-prometheus"))

        // 通用翻译功能
        api(project(":ruoyi-common:ruoyi-common-translation"))

        // 脱敏模块
        api(project(":ruoyi-common:ruoyi-common-sensitive"))

        // 序列化模块
        api(project(":ruoyi-common:ruoyi-common-json"))

        // 数据加解密模块
        api(project(":ruoyi-common:ruoyi-common-encrypt"))

        // 租户模块
        api(project(":ruoyi-common:ruoyi-common-tenant"))

        // WebSocket 模块
        api(project(":ruoyi-common:ruoyi-common-websocket"))

        // 授权认证
        api(project(":ruoyi-common:ruoyi-common-social"))

        // 配置中心
        api(project(":ruoyi-common:ruoyi-common-nacos"))

        // 消息总线模块
        api(project(":ruoyi-common:ruoyi-common-bus"))

        // SSE 模块
        api(project(":ruoyi-common:ruoyi-common-sse"))
    }
}

// ===========================================
// 发布配置（可选，用于发布到 Maven 仓库）
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
                name.set("RuoYi Common BOM")
                description.set("RuoYi-Cloud-Plus 公共模块依赖管理")
                url.set("https://gitee.com/JavaLionLi/RuoYi-Cloud-Plus")
            }
        }
    }
}
*/

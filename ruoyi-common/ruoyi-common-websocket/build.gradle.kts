/*
 * ===========================================
 * ruoyi-common-websocket
 * WebSocket 模块
 * ===========================================
 */

description = "ruoyi-common-websocket 模块"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-core"))
    api(project(":ruoyi-common:ruoyi-common-redis"))
    api(project(":ruoyi-common:ruoyi-common-satoken"))
    api(project(":ruoyi-common:ruoyi-common-json"))

    // ===========================================
    // Spring Boot WebSocket
    // ===========================================
    api("org.springframework.boot:spring-boot-starter-websocket") {
        // 排除 Tomcat，使用 Undertow
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
}

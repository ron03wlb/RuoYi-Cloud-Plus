/*
 * ===========================================
 * ruoyi-common-web
 * web服务
 * ===========================================
 */

description = "ruoyi-common-web web服务"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-json"))

    // ===========================================
    // Spring Boot Web
    // ===========================================
    api("org.springframework.boot:spring-boot-starter-web") {
        // 排除 Tomcat，使用 Undertow
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }

    // Undertow（性能更强的 Web 容器）
    api("org.springframework.boot:spring-boot-starter-undertow")

    // ===========================================
    // Spring Boot Actuator
    // ===========================================
    api("org.springframework.boot:spring-boot-starter-actuator")

    // ===========================================
    // Nacos Discovery（可选）
    // ===========================================
    compileOnly("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")

    // ===========================================
    // Mica Metrics
    // ===========================================
    api(libs.mica.metrics) {
        exclude(group = "net.dreamlu", module = "mica-core")
    }

    // Mica Core（provided）
    compileOnly(libs.mica.core)
}

/*
 * ===========================================
 * ruoyi-common-prometheus
 * Prometheus监控
 * ===========================================
 */

description = "ruoyi-common-prometheus prometheus监控"

dependencies {
    // ===========================================
    // Spring Boot Actuator
    // ===========================================
    api("org.springframework.boot:spring-boot-starter-actuator")

    // ===========================================
    // Micrometer Prometheus Registry
    // ===========================================
    api("io.micrometer:micrometer-registry-prometheus")
}

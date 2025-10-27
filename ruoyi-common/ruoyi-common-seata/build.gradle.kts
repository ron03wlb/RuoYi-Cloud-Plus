/*
 * ===========================================
 * ruoyi-common-seata
 * 分布式事务
 * ===========================================
 */

description = "ruoyi-common-seata 分布式事务"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-core"))

    // ===========================================
    // Dubbo Filter Seata
    // ===========================================
    api(libs.dubbo.filter.seata)

    // ===========================================
    // Spring Cloud Alibaba Seata
    // ===========================================
    api("com.alibaba.cloud:spring-cloud-starter-alibaba-seata") {
        // 排除 Log4j
        exclude(group = "org.apache.logging.log4j", module = "*")
        // 排除旧版 dubbo-filter-seata
        exclude(group = "org.apache.dubbo.extensions", module = "dubbo-filter-seata")
        // 排除旧版 seata-spring-boot-starter
        exclude(group = "io.seata", module = "seata-spring-boot-starter")
    }

    // ===========================================
    // Seata Spring Boot Starter
    // ===========================================
    api(libs.seata.spring.boot.starter)
}

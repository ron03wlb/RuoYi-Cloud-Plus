/*
 * ===========================================
 * ruoyi-common-bus
 * 消息总线模块
 * ===========================================
 */

description = "ruoyi-common-bus 消息总线模块"

dependencies {
    // ===========================================
    // Spring Cloud Bus (RabbitMQ)
    // ===========================================
    api("org.springframework.cloud:spring-cloud-starter-bus-amqp")

    // 如需使用 Kafka，取消下面注释
    // api("org.springframework.cloud:spring-cloud-starter-bus-kafka")

    // 如需使用 RocketMQ，取消下面注释
    // api("com.alibaba.cloud:spring-cloud-starter-bus-rocketmq")
}

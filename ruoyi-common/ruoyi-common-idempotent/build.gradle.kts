/*
 * ===========================================
 * ruoyi-common-idempotent
 * 幂等功能
 * ===========================================
 */

description = "ruoyi-common-idempotent 幂等功能"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-json"))
    api(project(":ruoyi-common:ruoyi-common-redis"))

    // ===========================================
    // Hutool Crypto
    // ===========================================
    api(libs.hutool.crypto)

    // ===========================================
    // Sa-Token Core
    // ===========================================
    api(libs.sa.token.core)

    // ===========================================
    // TransmittableThreadLocal（虚拟线程支持）
    // ===========================================
    api(libs.transmittable.thread.local)
}

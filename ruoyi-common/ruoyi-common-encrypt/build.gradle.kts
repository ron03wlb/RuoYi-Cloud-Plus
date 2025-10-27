/*
 * ===========================================
 * ruoyi-common-encrypt
 * 数据加解密模块
 * ===========================================
 */

description = "ruoyi-common-encrypt 数据加解密模块"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-core"))

    // ===========================================
    // BouncyCastle 加密库
    // ===========================================
    api(libs.bouncycastle.jdk15to18)

    // ===========================================
    // Hutool Crypto
    // ===========================================
    api(libs.hutool.crypto)

    // ===========================================
    // Spring WebMVC
    // ===========================================
    api("org.springframework:spring-webmvc")

    // ===========================================
    // MyBatis Plus（可选）
    // ===========================================
    compileOnly(libs.mybatis.plus.spring.boot3.starter) {
        exclude(group = "org.mybatis", module = "mybatis-spring")
    }
}

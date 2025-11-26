/*
 * ===========================================
 * ruoyi-common-encrypt
 * 数据加解密模块
 * ===========================================
 */

plugins {
    id("jacoco")
}

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

    // ===========================================
    // 测试依赖
    // ===========================================
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
}

// ===========================================
// JaCoCo 配置
// ===========================================
jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    // 排除不需要覆盖的类
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/annotation/**", // 注解类
                        "**/config/**", // 配置类
                        "**/filter/**", // 过滤器
                        "**/interceptor/**", // 拦截器
                        "**/*Application.class", // 主程序
                    )
                }
            },
        ),
    )
}

/*
 * ===========================================
 * ruoyi-common-core
 * 核心公共模块
 * ===========================================
 *
 * 说明：
 * 1. 这是一个 Java Library 模块（不是 Spring Boot 应用）
 * 2. 提供核心工具类、常量、异常处理等基础功能
 * 3. 其他模块通过 api() 或 implementation() 依赖此模块
 */

plugins {
    jacoco
}

description = "ruoyi-common-core 核心模块"

dependencies {
    // ===========================================
    // Spring 核心依赖
    // ===========================================
    api("org.springframework:spring-context")
    api("org.springframework:spring-core")
    api("org.springframework:spring-beans")

    // Spring Web 模块
    api("org.springframework:spring-web")

    // Spring Boot 自动配置
    api("org.springframework.boot:spring-boot-autoconfigure")

    // ===========================================
    // Jakarta EE API（Jakarta 命名空间，不是 javax）
    // ===========================================

    // Validation API
    api("jakarta.validation:jakarta.validation-api")

    // Hibernate Validator（验证框架实现）
    api("org.hibernate.validator:hibernate-validator")

    // Jakarta Annotation API（@Resource, @PostConstruct 等）
    api("jakarta.annotation:jakarta.annotation-api")

    // Servlet API
    api("jakarta.servlet:jakarta.servlet-api")

    // ===========================================
    // 日志
    // ===========================================
    api("org.slf4j:slf4j-api")

    // ===========================================
    // 工具库
    // ===========================================

    // Apache Commons Lang3
    api("org.apache.commons:commons-lang3")

    // Hutool 工具库
    api(libs.hutool.core)
    api(libs.hutool.http)
    api(libs.hutool.extra)

    // Lombok（compileOnly + annotationProcessor 在根 build.gradle.kts 中已配置）
    // 这里无需重复声明

    // ===========================================
    // 对象映射
    // ===========================================

    // MapStruct Plus（对象转换工具）
    api(libs.mapstruct.plus.spring.boot.starter)

    // ===========================================
    // 其他工具
    // ===========================================

    // 离线 IP 地址定位库
    api(libs.ip2region)

    // Spring Boot Properties Migrator（运行时依赖）
    // 版本由 Spring Boot BOM 管理
    runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")

    // ===========================================
    // 测试依赖
    // ===========================================
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Mockito 模拟框架
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation(libs.mockito.inline)  // 支持 mock 静态方法和 final 类

    // AssertJ 流式断言
    testImplementation("org.assertj:assertj-core")

    // JUnit 5 参数化测试
    testImplementation("org.junit.jupiter:junit-jupiter-params")

    // AspectJ（用于集成测试的 AOP 支持）
    testImplementation(libs.aspectjweaver)

    // Jakarta EL（用于 Hibernate Validator 消息插值）
    testImplementation(libs.jakarta.el)
}

// ===========================================
// 测试任务配置
// ===========================================
tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

// ===========================================
// JaCoCo 覆盖率配置
// ===========================================
jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/constant/**",     // 排除常量类
                    "**/enums/**",        // 排除枚举类
                    "**/domain/**",       // 排除简单 POJO
                    "**/config/**",       // 排除配置类
                    "**/validate/AddGroup.class",
                    "**/validate/EditGroup.class",
                    "**/validate/QueryGroup.class"
                )
            }
        })
    )
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
            from(components["java"])

            pom {
                name.set("RuoYi Common Core")
                description.set("RuoYi-Cloud-Plus 核心公共模块")
            }
        }
    }
}
*/
